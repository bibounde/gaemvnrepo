package com.bibounde.gaemvnrepo.server;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.DevService;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.bibounde.gaemvnrepo.shared.service.UserService;

public class Initializer {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DevService devService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    public void init() {
        logger.info("Initialization started");
        try {
            if (ConfigurationUtil.Mode.DEV == ConfigurationUtil.INSTANCE.getMode()) {
                logger.info("------- DEV mode start ------");
                
                logger.info("Deletes all entities");
                this.devService.deleteAll();
                
                logger.info("Creates test users");
                Random rd = new Random();
                for (int i = 0; i < 55; i++) {
                    User user = new User();
                    user.setActive(rd.nextBoolean());
                    user.setEmail("email-" + user.hashCode() + "gmail.com");
                    user.setLocale(Locale.ENGLISH.getLanguage() + "_" + Locale.ENGLISH.getCountry());
                    user.setLogin("login" + user.hashCode());
                    user.setPassword("fe01ce2a7fbac8fafaed7c982a04e229");
                    user.setRole(rd.nextBoolean() ? Role.MANAGER : Role.UPLOADER);
                    
                    userService.createUser(user);
                }
                
                logger.info("------- DEV mode end ------");
            }
            
            if (!userService.userAdminExists()) {
                userService.createUserAdmin();
                logger.info("User admin created");
            } else {
                logger.info("User admin already created");
            }
            
            if (repositoryService.findByName("snapshots") == null) {
                repositoryService.createRepository("snapshots", true);
                logger.info("Repository snapshots created");
            } else {
                logger.info("Repository snapshots already created");
            }
            
        } catch (TechnicalException e) {
            logger.error("Unable to perform initialization", e);
        }
        logger.info("Initialization done");
    }
}
