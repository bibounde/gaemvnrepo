package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.model.AppliedMigration;
import com.bibounde.gaemvnrepo.model.MigrationStatus;
import com.bibounde.gaemvnrepo.model.MigrationStatus.Status;
import com.bibounde.gaemvnrepo.server.PMF;
import com.bibounde.gaemvnrepo.server.dao.AppliedMigrationDao;
import com.bibounde.gaemvnrepo.server.dao.MigrationStatusDao;
import com.bibounde.gaemvnrepo.server.migration.DevelopmentMigrationOperation;
import com.bibounde.gaemvnrepo.server.migration.InitMigrationOperation;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil.Mode;
import com.bibounde.gaemvnrepo.shared.domain.migration.MigrationResponse;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.migration.MigrationOperation;
import com.bibounde.gaemvnrepo.shared.service.MigrationService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Service("migrationService")
public class MigrationServiceImpl implements MigrationService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MigrationServiceImpl.class);
    
    private static final String[] MIGRATIONS = {InitMigrationOperation.FQN};
    
    @Autowired
    private MigrationStatusDao migrationStatusDao;
    
    @Autowired
    private AppliedMigrationDao appliedMigrationDao;
    
    public void init() {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            
            List<String> appliedMigrationNames = this.appliedMigrationDao.getAppliedMigrationNames(pm);
            List<String> toPerform = new ArrayList<String>();
            if (appliedMigrationNames != null) {
                if (ConfigurationUtil.INSTANCE.getMode() == Mode.DEV) {
                    if (!appliedMigrationNames.contains(DevelopmentMigrationOperation.FQN)) {
                        toPerform.add(DevelopmentMigrationOperation.FQN);
                    }
                }
                for (String migrationName : MIGRATIONS) {
                    if (!appliedMigrationNames.contains(migrationName)) {
                        toPerform.add(migrationName);
                    }
                }
            }
            if (!toPerform.isEmpty()) {
                tx = pm.currentTransaction();
                tx.begin();
                
                logger.debug("Migration is necessary");
                MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
                if (status == null) {
                    logger.debug("Migration status no found in DB. Need to create");
                    status = new MigrationStatus();
                }
                status.setCurrentMigrationCount(0);
                status.setStatus(Status.IN_PROGESS);
                status.setMigrationNamesToPerform(toPerform);
                
                this.migrationStatusDao.saveOrUpdateMigrationStatus(status, pm);
                tx.commit();
            } else {
                logger.debug("Migrations already done");
            }
            
        } catch (Exception e) {
            logger.error("Unable to perform initilization", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (pm != null) {
                pm.close();
            }
        }
    }
    
    @Override
    public boolean isLocked() throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
            return status.getStatus() == Status.IN_PROGESS || status.getStatus() == Status.PAUSED;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to retrieve status", e);
        } finally {
            if (pm != null) {
                pm.close();
            }
        }
    }

    @Override
    public void startMigration() throws Exception {
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
            MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
            status.setStatus(Status.IN_PROGESS);
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < status.getMigrationNamesToPerform().size(); i++) {
                String name = status.getMigrationNamesToPerform().get(i);
                if (i > 0) {
                    params.append(",");
                }
                params.append(name);
            }
            
            tx.commit();
            
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/tasks/migration").param("names", params.toString()));
        } catch (TechnicalException e) {
            throw e;
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
    public void performMigration(List<MigrationOperation> migrationOperations) {
        long maxRequestTime = System.currentTimeMillis() + 15000;
        try {
            float max = migrationOperations.size();
            for (int i = 0; i < max; i++) {
                if (System.currentTimeMillis() > maxRequestTime) {
                    this.pauseMigration();
                    logger.debug("Migration paused");
                    return;
                }
                
                MigrationOperation operation = migrationOperations.get(i);
                operation.run();
                this.updateMigrationStatus(operation);
            }
            logger.debug("Migration succesfull");
        } catch (Exception e) {
            //TODO: manage errors
            logger.error("Unable to perform operation", e);
        }
    }
    
    private void updateMigrationStatus(MigrationOperation operation) throws TechnicalException {
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
            MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
            
            AppliedMigration appliedMigration = new AppliedMigration();
            appliedMigration.setName(operation.getName());
            status.getAppliedMigrations().add(appliedMigration);
            
            int count = status.getCurrentMigrationCount() + 1;
            if (count >= status.getMigrationNamesToPerform().size()) {
                //End of migration
                status.setStatus(Status.DONE);
                status.setMigrationNamesToPerform(Collections.EMPTY_LIST);
                status.setCurrentMigrationCount(0);
            } else {
                status.setCurrentMigrationCount(count);
            }
            tx.commit();
        
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
    
    private void pauseMigration() throws TechnicalException {
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
            MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
            status.setStatus(Status.PAUSED);
            tx.commit();
        
        } catch (TechnicalException e) {
            throw e;
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
    public MigrationResponse getMigrationStatus() throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            MigrationStatus status = this.migrationStatusDao.getMigrationStatus(pm);
            MigrationResponse ret = new MigrationResponse();
            ret.status = com.bibounde.gaemvnrepo.shared.domain.migration.MigrationResponse.Status.valueOf(status.getStatus().name());
            
            if (status.getStatus() == Status.DONE) {
                ret.progression = 1f;
            } else {
                ret.progression = Integer.valueOf(status.getCurrentMigrationCount()).floatValue() / Integer.valueOf(status.getMigrationNamesToPerform().size()).floatValue();
            }
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to retrieve status", e);
        } finally {
            if (pm != null) {
                pm.close();
            }
        }
    }
}
