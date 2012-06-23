package com.bibounde.gaemvnrepo.server.dao;

import com.bibounde.gaemvnrepo.model.AppliedMigration;

public interface MigrationDao {

    /**
     * Saves or updates migration data
     * @param migration migration data
     */
    void saveOrUpdate(AppliedMigration migration);
}
