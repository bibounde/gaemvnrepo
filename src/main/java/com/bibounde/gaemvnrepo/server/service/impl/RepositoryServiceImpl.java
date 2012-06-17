package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;
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
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;

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
    public void uploadFile(String name, String filePath, byte[] content, String mime) throws TechnicalException, BusinessException {
        String creatorName = SecurityContextHolder.getContext().getAuthentication().getName();

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

            String[] splittedPath = filePath.split("/");

            Repository repository = this.repositoryDao.findByName(name, pm);
            if (repository == null) {
                throw new BusinessException(name + " is not a valid repository");
            }

            this.checkFileQueryParam(repository, filePath, pm);

            // Check if filePath exist
            File found = this.repositoryDao.findFileByPath(filePath, pm);

            if (found != null) {
                logger.debug("File {} exists. Need to delete first", filePath);
                pm.deletePersistent(found);
            } else {
                // Check if parent creation is needed
                logger.debug("Checks if parent of {} must be created", filePath);
                StringBuilder dirPath = null;
                for (int i = 1; i < splittedPath.length - 1; i++) {
                    String dir = splittedPath[i];
                    if (dirPath != null) {
                        dirPath.append("/").append(dir);
                    } else {
                        dirPath = new StringBuilder(dir);
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
            file.setDeprecated(false);
            file.setFile(false);
            file.setName(splittedPath[splittedPath.length - 1]);
            file.setPath(filePath);
            file.setContent(content);
            file.setMime(mime);
            repository.getFiles().add(file);
            
            // Snapshots management
            if (repository.isSnapshots() && filePath.matches(SNAPSHOTS_FILE_REGEX)) {
                logger.debug("{} is a snapshot. Need to deprecate old files", filePath);
                String dirPath = this.getDirPath(filePath);
                List<File> toCheck = this.repositoryDao.findAllFiles(dirPath, pm);
                if (!toCheck.isEmpty()) {
                    for (File f : toCheck) {
                        if (!f.equals(found) && !f.isDeprecated() && this.isSnaspshotsOfSameFile(filePath, f.getPath())) {
                            logger.trace("{} will be deprecated", f.getPath());
                            f.setDeprecated(true);
                        }
                    }   
                }
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

            return this.repositoryDao.findAllFiles(dirPath, pm);
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

        String[] splitted = filePath.split("/");
        if (splitted.length < 4) {
            throw new BusinessException("The depth of " + filePath + " is invalid. It must be greather than 4 (found " + splitted.length + ")");
        }
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
        logger.trace("...{} and ...{} are {}a snapshot version of same file", new Object[]{filePath1.substring(filePath1.length()-40), filePath2.substring(filePath2.length()-40), !ret ? "NOT " : "" });
        return ret;
    }

    @Override
    public void cleanUp(String name) throws TechnicalException, BusinessException {
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
            if (repository == null || !repository.isSnapshots()) {
                throw new BusinessException(name + " does not exist or not a snapshot repository");
            }

            List<File> deprecatedFiles = this.repositoryDao.findDeprecatedFiles(name, pm);
            for (File file : deprecatedFiles) {
                logger.debug("Delete deprecated file {}", file.getPath());
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
    }

    @Override
    public List<String> findSnapshotsReposiroryNames() throws TechnicalException {
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

}
