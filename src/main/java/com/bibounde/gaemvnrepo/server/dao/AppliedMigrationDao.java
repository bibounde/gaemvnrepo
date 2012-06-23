package com.bibounde.gaemvnrepo.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface AppliedMigrationDao extends Serializable {

    /**
     * Retrieve applied migration names
     * @param pm persistence panel
     * @return applied migrations
     * @throws TechnicalException
     */
    List<String> getAppliedMigrationNames(PersistenceManager pm) throws TechnicalException;
}
