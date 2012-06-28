package com.bibounde.gaemvnrepo.server.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.migration.MigrationOperation;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.bibounde.gaemvnrepo.shared.service.UserService;

@Component
public class InitMigrationOperation implements MigrationOperation {

    public static final String FQN = InitMigrationOperation.class.getName();
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(InitMigrationOperation.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public String getName() {
        return FQN;
    }

    @Override
    public void run() throws TechnicalException {
        userService.createUserAdmin();
        logger.info("User admin created");

        repositoryService.createRepository("snapshots", true);
        logger.info("Repository snapshots created");

        repositoryService.createRepository("releases", false);
        logger.info("Repository releases created");
    }

}
