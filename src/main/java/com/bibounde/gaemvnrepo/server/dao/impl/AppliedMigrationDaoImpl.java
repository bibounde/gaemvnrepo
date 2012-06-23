package com.bibounde.gaemvnrepo.server.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.AppliedMigration;
import com.bibounde.gaemvnrepo.server.dao.AppliedMigrationDao;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Component
public class AppliedMigrationDaoImpl implements AppliedMigrationDao {

    public List<String> getAppliedMigrationNames(PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(AppliedMigration.class);

            List<AppliedMigration> migrations = (List<AppliedMigration>) query.execute();
            List<String> ret = new ArrayList<String>();
            
            for (AppliedMigration appliedMigration : migrations) {
                ret.add(appliedMigration.getName());
            }
            
            return ret;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }
}
