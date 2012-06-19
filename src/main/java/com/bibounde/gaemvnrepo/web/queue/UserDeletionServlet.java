package com.bibounde.gaemvnrepo.web.queue;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;
import com.bibounde.gaemvnrepo.shared.service.UserService;

public class UserDeletionServlet extends AbstractQueueServlet {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserDeletionServlet.class);
    
    @Autowired
    UserService userService;
    
    @Override
    protected void execute(Map parameterMap) throws TechnicalException, BusinessException {
        String[] id = (String[]) parameterMap.get("id");
        
        if (id == null || id.length == 0) {
            throw new BusinessException("Queue parameter is invalid. Unbale to find \"id\"");
        }
        this.userService.deleteUser(Long.valueOf(id[0]));
    }

}
