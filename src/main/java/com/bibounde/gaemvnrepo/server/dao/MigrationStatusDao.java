package com.bibounde.gaemvnrepo.server.dao;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import com.bibounde.gaemvnrepo.model.MigrationStatus;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface MigrationStatusDao extends Serializable {

    
    /**
     * Save or update migration status
     * @param status
     * @param persistence manager
     * @throws TechnicalException
     */
    void saveOrUpdateMigrationStatus(MigrationStatus status, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieve migration status
     * @param persistence manager
     * @return migration status
     */
    MigrationStatus getMigrationStatus(PersistenceManager pm) throws TechnicalException;
}
