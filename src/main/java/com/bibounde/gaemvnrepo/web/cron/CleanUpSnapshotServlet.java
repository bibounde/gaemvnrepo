package com.bibounde.gaemvnrepo.web.cron;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;

public class CleanUpSnapshotServlet extends AbstractCronServlet {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CleanUpSnapshotServlet.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Override
    protected void execute() throws TechnicalException, BusinessException {
        List<String> repos = this.repositoryService.findSnapshotsReposiroryNames();
        for (String repo : repos) {
            this.repositoryService.cleanUp(repo);
        }
    }

}
