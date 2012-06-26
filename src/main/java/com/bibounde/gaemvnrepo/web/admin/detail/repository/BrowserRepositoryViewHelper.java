package com.bibounde.gaemvnrepo.web.admin.detail.repository;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.NavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNodeQuery;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.RepositoryService;

public class BrowserRepositoryViewHelper implements Serializable {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(BrowserRepositoryViewHelper.class);

    private BrowserRepositoryView view;    
    
    public void setView(BrowserRepositoryView view) {
        this.view = view;
    }
    
    private BrowserRepositoryModel getModel() {
        this.checkState();
        return (BrowserRepositoryModel) this.view.getModel();
    }
    
    public void initView(RepositoryNavigationNodeQuery query, RepositoryService repositoryService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("Initializes {} data", view.getId());
        if (query == null) {
            List<RepositoryNavigationNode> repos = repositoryService.getRepositoryNavigationNodes();
            this.getModel().set(repos);
        } else {
            throw new TechnicalException("Not implemented yet", null);
        }
    }
    
    public void refresh(RepositoryNavigationNodeQuery query, RepositoryService repositoryService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("Refresh {} data", view.getId());
        if (query == null) {
            List<RepositoryNavigationNode> repos = repositoryService.getRepositoryNavigationNodes();
            this.getModel().set(repos);
        } else {
            throw new TechnicalException("Not implemented yet", null);
        }
    }
    
    public void nodeExpanded(NavigationNode node, RepositoryService repositoryService) throws TechnicalException, BusinessException {
        this.checkState();
        
        if (node.children != null) {
            logger.debug("Node {} already expanded. Not necessary to retrieves children", node.path);
        } else {
            if (node instanceof RepositoryNavigationNode) {
                RepositoryNavigationNode repoNode = (RepositoryNavigationNode) node;
                logger.debug("Repository {} expanded", repoNode.name);
                this.getModel().expandNode(repoNode, repositoryService.getFileNavigationNodes(repoNode.path, 0));
            } else if (node instanceof FileNavigationNode) {
                FileNavigationNode fileNode = (FileNavigationNode) node;
                logger.debug("File {} expanded", fileNode.name);
                this.getModel().expandNode(fileNode, repositoryService.getFileNavigationNodes(fileNode.path, fileNode.depth + 1));
            } else {
                throw new TechnicalException(node.getClass().getName() + " is not supported", null);
            }
        }
    }
    
    public void nodeDeleted(FileNavigationNode node, RepositoryService repositoryService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("File {} deleted", node.name);
        repositoryService.deleteAllFiles(node.path);
        this.getModel().deleteNode(node);
    }
    
    private void checkState() {
        if (this.view == null) {
            throw new IllegalStateException(BrowserRepositoryView.class.getSimpleName()  + " must be initialized");
        }
    }
}
