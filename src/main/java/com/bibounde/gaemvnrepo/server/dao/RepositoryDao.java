package com.bibounde.gaemvnrepo.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.model.Repository;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface RepositoryDao extends Serializable {

    /**
     * Retrieves repository by name
     * @param name repository name
     * @param pm persistence manager
     * @return repository or null if no repository with specified name exists
     * @throws TechnicalException
     */
    Repository findByName(String name, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves file from its path
     * 
     * @param path
     * @param pm persistence manager
     * @return file object or null if does not exist
     * @throws TechnicalException
     */
    File findFileByPath(String path, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves file from its path and depth
     * @param parentPath
     * @param parentDepth
     * @param pm persistence manager
     * @return file object or null if does not exist
     * @throws TechnicalException
     */
    List<File> findFileByParentPath(String parentPath, int parentDepth, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves all files contained in dir contains (recursively)
     * @param dirPath dir path
     * @param inclusive true means that dir with dipath is retrieved
     * @param pm persistence manager
     * @return file list
     * @throws TechnicalException
     * @throws BusinessException 
     */
    List<File> findAllFiles(String dirPath, boolean inclusive, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves file whose path starts with prefix
     * @param prefix
     * @param pm persistence manager
     * @return file list
     * @throws TechnicalException
     */
    List<File> findFileStartWith(String prefix, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves snapshots repository
     * @param pm persistence manager
     * @return repository list
     * @throws TechnicalException
     */
    List<Repository> findSnapshotsReposirories(PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves repositories
     * @param pm persistence manager
     * @return repository list
     * @throws TechnicalException
     */
    List<Repository> findReposirories(PersistenceManager pm) throws TechnicalException;
}
