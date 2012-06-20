package com.bibounde.gaemvnrepo.shared.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.model.Repository;
import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface RepositoryService extends Serializable{
    
    /**
     * Creates repository
     * @param name repository name
     * @throws TechnicalException
     */
    void createRepository(String name) throws TechnicalException;
    
    /**
     * Creates repository
     * @param name repository name
     * @param snapshots true means a snapshot repository
     * @throws TechnicalException
     */
    void createRepository(String name, boolean snapshots) throws TechnicalException;
    
    /**
     * Retrieves repository
     * @param name repository name
     * @return repository
     * @throws TechnicalException
     */
    Repository findByName(String name) throws TechnicalException;
    
    /**
     * Uploads file path in specified repositry
     * @param name repository name
     * @param filePath file path (reponame/xxx/xxx/xx.xx)
     * @param content file content
     * @param mime mime content
     * @throws TechnicalException, BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager', 'uploader')")
    void uploadFile(String name, String filePath, byte[] content, String mime) throws TechnicalException, BusinessException;
    
    /**
     * Retrieves file from file path or null if file does not exist
     * @param name repository name
     * @param filePath file path
     * @return file
     * @throws TechnicalException
     */
    File findFileByPath(String name, String filePath) throws TechnicalException;
    
    /**
     * Retrieves all files that dir contains  (recursively)
     * @param name repository name
     * @param dirPath dir path 
     * @return file list
     * @throws TechnicalException
     * @throws BusinessException 
     */
    List<File> findAllFiles(String name, String dirPath) throws TechnicalException, BusinessException;
    
    /**
     * Deletes file
     * @param name repository name
     * @param filePath file path
     * @throws TechnicalException
     * @throws BusinessException
     */
    void deleteFile(String name, String filePath) throws TechnicalException, BusinessException;
    
    /**
     * Retrieves snapshots repository
     * @return repository name list
     * @throws TechnicalException
     */
    List<String> findSnapshotsReposiroryNames() throws TechnicalException;
    
    /**
     * Retrieves repository navigation nodes
     * @return navigation node list
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    List<RepositoryNavigationNode> getRepositoryNavigationNodes() throws TechnicalException, BusinessException;
    
    /**
     * Retrieves file navigation nodes
     * @param parent path
     * @param parent depth
     * @return file node list
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    List<FileNavigationNode> getFileNavigationNodes(String parentPath, int parentDepth) throws TechnicalException, BusinessException;
}
