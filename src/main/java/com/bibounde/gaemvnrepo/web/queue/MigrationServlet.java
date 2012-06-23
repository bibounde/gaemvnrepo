package com.bibounde.gaemvnrepo.web.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.server.migration.DevelopmentMigrationOperation;
import com.bibounde.gaemvnrepo.server.migration.InitMigrationOperation;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.migration.MigrationOperation;
import com.bibounde.gaemvnrepo.shared.service.MigrationService;

public class MigrationServlet extends AbstractQueueServlet{

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MigrationServlet.class);
    
    @Autowired
    private DevelopmentMigrationOperation  developmentMigrationOperation;

    @Autowired
    private InitMigrationOperation initMigrationOperation;
    
    @Autowired
    private MigrationService migrationService;
    
    
    @Override
    protected void execute(Map parameterMap) throws TechnicalException, BusinessException {
        String[] nameParam = (String[]) parameterMap.get("names");
        
        if (nameParam == null || nameParam.length == 0) {
            throw new BusinessException("Queue parameter is invalid. Unbale to find \"filepath\"");
        }
        String[] names = nameParam[0].split(",");
        List<MigrationOperation> toPerform = new ArrayList<MigrationOperation>();
        for (int i = 0; i < names.length; i++) {
            String migrationName = names[i];
            if (DevelopmentMigrationOperation.FQN.equals(migrationName)) {
                toPerform.add(developmentMigrationOperation);
            } else if (InitMigrationOperation.FQN.equals(migrationName)) {
                toPerform.add(initMigrationOperation);
            } else {
                logger.warn("{} migration name is not supported", migrationName);
            }
        }
        
        this.migrationService.performMigration(toPerform);
    }

}
