package com.bibounde.gaemvnrepo.shared.service;

import java.io.Serializable;
import java.util.List;

import com.bibounde.gaemvnrepo.shared.domain.migration.MigrationResponse;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.migration.MigrationOperation;

public interface MigrationService extends Serializable {

    /**
     * Returns true if application is locked
     * @return
     * @throws TechnicalException 
     */
    boolean isLocked() throws TechnicalException;
    
    /**
     * Perform migrations
     * @param migrationOperations migration operation names
     * @throws TechnicalException 
     */
    void performMigration(List<MigrationOperation> migrationOperations) throws TechnicalException;
    
    /**
     * Retrieves migration status
     * @return migration status
     * @throws TechnicalException
     */
    MigrationResponse getMigrationStatus() throws TechnicalException;
    
    /**
     * Starts migration
     * @throws Exception 
     */
    void startMigration() throws Exception;
    
}
