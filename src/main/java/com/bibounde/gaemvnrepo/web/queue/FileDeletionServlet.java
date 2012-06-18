package com.bibounde.gaemvnrepo.web.queue;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;

public class FileDeletionServlet extends AbstractQueueServlet {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(FileDeletionServlet.class);
    
    @Autowired
    RepositoryService repositoryService;
    
    @Override
    protected void execute(Map parameterMap) throws TechnicalException, BusinessException {
        String[] filePath = (String[]) parameterMap.get("filepath");
        String[] repository = (String[]) parameterMap.get("repository");
        
        if (filePath == null || filePath.length == 0) {
            throw new BusinessException("Queue parameter is invalid. Unbale to find \"filepath\"");
        }
        if (repository == null || repository.length == 0) {
            throw new BusinessException("Queue parameter is invalid. Unbale to find \"repository\"");
        }
        this.repositoryService.deleteFile(repository[0], filePath[0]);
    }

}
