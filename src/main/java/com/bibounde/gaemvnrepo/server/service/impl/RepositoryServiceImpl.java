package com.bibounde.gaemvnrepo.server.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.model.Repository;
import com.bibounde.gaemvnrepo.server.PMF;
import com.bibounde.gaemvnrepo.server.dao.RepositoryDao;
import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {

    private static final String SNAPSHOTS_FILE_REGEX = "(.*/.*-SNAPSHOT/.*-)([0-9]{8}\\.[0-9]+-[0-9]+)(.*)";
    private static final String SNAPSHOTS_TIMESTAMP_REGEX = "[0-9]{8}\\.[0-9]+-[0-9]+";

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private RepositoryDao repositoryDao;

    @Override
    public void createRepository(String name) throws TechnicalException {
        this.createRepository(name, false);
    }

    @Override
    public void createRepository(String name, boolean snapshots) throws TechnicalException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            Repository repo = new Repository();
            repo.setName(name);
            repo.setSnapshots(snapshots);
            pm.makePersistent(repo);
            tx.commit();
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public Repository findByName(String name) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        try {
            return this.repositoryDao.findByName(name, pm);
        } finally {
            pm.close();
        }
    }

    @Override
    public void uploadFile(String name, String filePath, byte[] content, String contentType) throws TechnicalException, BusinessException {
        
        String[] splittedPath = filePath.split("/");
        String fileName = splittedPath[splittedPath.length - 1];
        
        BlobKey blobKey = null;
        if (content != null) {
            blobKey = this.storeFileContent(fileName, content, contentType);
            logger.debug("Blob key of {} is {}", fileName, blobKey.getKeyString());
        } else {
            logger.warn("Upload file without content");
        }
        
        String creatorName = SecurityContextHolder.getContext().getAuthentication().getName();

        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        String blobKeyToDelete = null;

        try {
            tx.begin();

            Repository repository = this.repositoryDao.findByName(name, pm);
            if (repository == null) {
                throw new BusinessException(name + " is not a valid repository");
            }

            this.checkFileQueryParam(repository, filePath, pm);

            // Check if filePath exist
            File found = this.repositoryDao.findFileByPath(filePath, pm);

            if (found != null) {
                logger.debug("File {} exists. Need to delete first", filePath);
                blobKeyToDelete = found.getContentKey().getKeyString();
                repository.getFiles().remove(found);
            } else {
                // Check if parent creation is needed
                logger.debug("Checks if parent of {} must be created", filePath);
                StringBuilder dirPath = null;
                for (int i = 1; i < splittedPath.length - 1; i++) {
                    String dir = splittedPath[i];
                    if (dirPath != null) {
                        dirPath.append("/").append(dir);
                    } else {
                        dirPath = new StringBuilder(repository.getName() + "/" + dir);
                    }
                    logger.trace("Checks if directory {} exists.", dirPath.toString());
                    if (this.repositoryDao.findFileByPath(dirPath.toString(), pm) == null) {
                        logger.trace("Creates directory {}", dirPath.toString());
                        File dirFile = new File();
                        dirFile.setCreationDate(System.currentTimeMillis());
                        dirFile.setCreator(creatorName);
                        dirFile.setDepth(i - 1);
                        dirFile.setFile(false);
                        dirFile.setName(dir);
                        dirFile.setPath(dirPath.toString());
                        repository.getFiles().add(dirFile);
                    }
                }
            }

            logger.debug("Creates file {}", filePath); 
            File file = new File();
            file.setCreationDate(System.currentTimeMillis());
            file.setCreator(creatorName);
            file.setDepth(splittedPath.length - 2);
            file.setFile(true);
            file.setName(fileName);
            file.setPath(filePath);
            file.setContentKey(blobKey);
            repository.getFiles().add(file);
            
            // Snapshots management
            List<File> filesToDelete = new ArrayList<File>();
            if (repository.isSnapshots() && filePath.matches(SNAPSHOTS_FILE_REGEX)) {
                logger.debug("{} is a snapshot. Need to deprecate old files", filePath);
                String dirPath = this.getDirPath(filePath);
                List<File> toCheck = this.repositoryDao.findAllFiles(dirPath, false, pm);
                if (!toCheck.isEmpty()) {
                    for (File f : toCheck) {
                        if (!f.equals(found) && this.isSnaspshotsOfSameFile(filePath, f.getPath())) {
                            logger.trace("{} must be deleted", f.getPath());
                            filesToDelete.add(f);
                        }
                    }   
                }
            }
            
            for (File toDelete : filesToDelete) {
                repository.getFiles().remove(toDelete);
            }

            tx.commit();
        } catch (BusinessException e) {
            throw e;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to upload", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
        
        //Not in the transaction because blob and repo are not in the same group
        try {
            Queue queue = QueueFactory.getDefaultQueue();
            if (blobKeyToDelete != null) {
                queue.add(TaskOptions.Builder.withUrl("/tasks/delete/blob").param("blobkey", blobKeyToDelete));
            }
        } catch (Exception e) {
            throw new TechnicalException("Unable to perform queue operation", e);
        }
    }
    
    /**
     * Store file content in Blobstore
     * @param fileName file name
     * @param content file content
     * @param contentType content type
     * @return BlobKey
     * @throws TechnicalException
     */
    private BlobKey storeFileContent(String fileName, byte[] content, String contentType) throws TechnicalException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        try {
            tx.begin();
            FileService fileService = FileServiceFactory.getFileService();
            AppEngineFile blobFile = fileService.createNewBlobFile(contentType, fileName);
            // Open a channel to write to it
            boolean lock = true;
            FileWriteChannel writeChannel = null;
            writeChannel = fileService.openWriteChannel(blobFile, lock);
            BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(content));
            byte[] buffer = new byte[BlobstoreService.MAX_BLOB_FETCH_SIZE];
            int read;
            while( (read = in.read(buffer)) > 0 ){ //-1 means EndOfStream
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, read);
                writeChannel.write(bb);
            }
            writeChannel.closeFinally();
            tx.commit();
            return fileService.getBlobKey(blobFile);
        } catch (Exception e) {
            throw new TechnicalException("Unable to store blob", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public File findFileByPath(String name, String filePath) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            Repository repository = this.repositoryDao.findByName(name, pm);
            if (repository == null) {
                throw new BusinessException(name + " is not a valid repository");
            }
            this.checkFileQueryParam(repository, filePath, pm);

            return this.repositoryDao.findFileByPath(filePath, pm);
        } catch (BusinessException e) {
            return null;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<File> findAllFiles(String name, String dirPath) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            Repository repository = this.repositoryDao.findByName(name, pm);
            if (repository == null) {
                throw new BusinessException(name + " is not a valid repository");
            }
            this.checkFileQueryParam(repository, dirPath, pm);

            return this.repositoryDao.findAllFiles(dirPath, false, pm);
        } catch (BusinessException e) {
            return null;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    /**
     * Checks if file query parameters are valid. If not, throws exception
     * 
     * @param repository
     *            repository
     * @param filePath
     *            file path
     * @param pm
     *            persistence manager
     * @throws BusinessException
     *             throwed if paramaters are not valid
     */
    private void checkFileQueryParam(Repository repository, String filePath, PersistenceManager pm) throws BusinessException {
        String mandatoryFilePrefix = repository.getName() + "/";
        if (filePath == null || !filePath.startsWith(mandatoryFilePrefix)) {
            throw new BusinessException(filePath + " must start with " + mandatoryFilePrefix);
        }
        //TODO: checks if it is really necessary
        /*
        String[] splitted = filePath.split("/");
        if (splitted.length < 4) {
            throw new BusinessException("The depth of " + filePath + " is invalid. It must be greather than 4 (found " + splitted.length + ")");
        }
        */
    }
    
    /**
     * Retrieves directory path of specied file path
     * @param filePath file path
     * @return directory path
     * @throws BusinessException
     */
    private String getDirPath(String filePath) throws BusinessException {
        return filePath.substring(0, filePath.lastIndexOf("/"));
    }
    
    /**
     * Returns true if filePath1 and filePath2 corresponds of snapshot version of same file 
     * @param filePath1 file path
     * @param filePath2 file path
     * @return true if filePath1 and filePath2 corresponds of snapshot version of same file
     */
    private boolean isSnaspshotsOfSameFile(String filePath1, String filePath2) {
        logger.trace("Check if ...{} and {} are a snapshot version of same file", filePath1.substring(filePath1.length()-40), filePath2.substring(filePath2.length()-40));
        Pattern pattern = Pattern.compile(SNAPSHOTS_FILE_REGEX);
        Matcher matcher = pattern.matcher(filePath1);
        boolean ret = false;
        if (matcher.find()) {
            String regex = matcher.replaceAll(matcher.group(1) + SNAPSHOTS_TIMESTAMP_REGEX + matcher.group(3));
            ret = filePath2.matches(regex); 
        }
        logger.trace("...{} and ...{} are {} a snapshot version of same file", new Object[]{filePath1.substring(filePath1.length()-40), filePath2.substring(filePath2.length()-40), !ret ? "NOT " : "" });
        return ret;
    }

    @Override
    public List<String> getSnapshotsReposiroryNames() throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            List<Repository> repositories = this.repositoryDao.findSnapshotsReposirories(pm);
            List<String> ret = new ArrayList<String>();
            for (Repository repo : repositories) {
                ret.add(repo.getName());
            }
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }
    
    @Override
    public List<String> getReposiroryNames() throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            List<Repository> repositories = this.repositoryDao.findReposirories(pm);
            List<String> ret = new ArrayList<String>();
            for (Repository repo : repositories) {
                ret.add(repo.getName());
            }
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteFile(String name, String filePath) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            Repository repository = this.repositoryDao.findByName(name, pm);
            if (repository == null) {
                throw new BusinessException(name + " is not a valid repository");
            }
            this.checkFileQueryParam(repository, filePath, pm);

            File file = this.repositoryDao.findFileByPath(filePath, pm);
            if (file != null) {
                repository.getFiles().remove(file);
            }
            
            tx.commit();
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public List<RepositoryNavigationNode> getRepositoryNavigationNodes() throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            List<Repository> repositories = this.repositoryDao.findReposirories(pm);
            List<RepositoryNavigationNode> ret = new ArrayList<RepositoryNavigationNode>();
            for (Repository repository : repositories) {
                RepositoryNavigationNode node = new RepositoryNavigationNode();
                node.id = repository.getId();
                node.path = repository.getName();
                node.name = repository.getName();
                node.snapshots = repository.isSnapshots();
                ret.add(node);
            }
            
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<FileNavigationNode> getFileNavigationNodes(String parentPath, int parentDepth) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            List<File> files = this.repositoryDao.findFileByParentPath(parentPath, parentDepth, pm);
            List<FileNavigationNode> ret = new ArrayList<FileNavigationNode>();
            for (File file : files) {
                FileNavigationNode node = new FileNavigationNode();
               
                node.file = file.isFile();
                node.created = new Date(file.getCreationDate());
                node.creator = file.getCreator();
                node.name = file.getName();
                node.path = file.getPath();
                node.depth = file.getDepth();
                
                ret.add(node);
            }
            
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<String> getFileNames(String parentPath, int parentDepth) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            List<File> files = this.repositoryDao.findFileByParentPath(parentPath, parentDepth, pm);
            List<String> ret = new ArrayList<String>();
            for (File file : files) {
                ret.add(file.getName());
            }
            
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to read in db", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteAllFiles(String path) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        List<String> blobKeyToDelete = new ArrayList<String>();
        
        try {
            tx.begin();
            List<File> files = this.repositoryDao.findAllFiles(path, true, pm);
            for (File file : files) {
                if (file.isFile()) {
                    blobKeyToDelete.add(file.getContentKey().getKeyString());
                }
                pm.deletePersistent(file);
            }
            tx.commit();
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
        
        //Not in the transaction because blobs and files are not in the same group
        try {
            Queue queue = QueueFactory.getDefaultQueue();
            for (String key : blobKeyToDelete) {
                queue.add(TaskOptions.Builder.withUrl("/tasks/delete/blob").param("blobkey", key));
            }
        } catch (Exception e) {
            throw new TechnicalException("Unable to perform queue operation", e);
        }
    }
    
}
