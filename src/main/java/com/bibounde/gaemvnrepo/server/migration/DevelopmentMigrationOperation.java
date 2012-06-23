package com.bibounde.gaemvnrepo.server.migration;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.migration.MigrationOperation;
import com.bibounde.gaemvnrepo.shared.service.GaeService;
import com.bibounde.gaemvnrepo.shared.service.UserService;

@Component
public class DevelopmentMigrationOperation implements MigrationOperation {

    public static final String FQN = DevelopmentMigrationOperation.class.getName();
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DevelopmentMigrationOperation.class);
    
    @Autowired
    private GaeService gaeService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public String getLocalizedName() {
        return "$$Applied Migration de dev";
    }

    @Override
    public void run() throws TechnicalException {
        logger.info("------- DEV mode start ------");
        
        logger.info("Deletes all entities");
        this.gaeService.deleteAll();
        
        logger.info("Creates test users");
        Random rd = new Random();
        for (int i = 0; i < 26; i++) {
            User user = new User();
            user.setActive(rd.nextBoolean());
            user.setEmail("email-" + user.hashCode() + "@gmail.com");
            user.setLocale("en_GB");
            user.setLogin("login" + user.hashCode());
            user.setPassword("fe01ce2a7fbac8fafaed7c982a04e229");
            user.setRole(rd.nextBoolean() ? Role.USER : Role.UPLOADER);
            
            userService.createUser(user);
        }
        
        logger.info("------- DEV mode end ------");
    }

    @Override
    public String getName() {
        return FQN;
    }

}
