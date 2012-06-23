package com.bibounde.gaemvnrepo.server.dao.impl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.MigrationStatus;
import com.bibounde.gaemvnrepo.server.dao.MigrationStatusDao;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Component
public class MigrationStatusDaoImpl implements MigrationStatusDao {

    @Override
    public void saveOrUpdateMigrationStatus(MigrationStatus status, PersistenceManager pm) throws TechnicalException {
        try {
            pm.makePersistent(status);
        } catch (Exception e) {
            throw new TechnicalException("Unable to create migration stauts db", e);
        }
    }

    @Override
    public MigrationStatus getMigrationStatus(PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(MigrationStatus.class);

            List<MigrationStatus> status = (List<MigrationStatus>) query.execute();

            return status.isEmpty() ? null : status.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

}
