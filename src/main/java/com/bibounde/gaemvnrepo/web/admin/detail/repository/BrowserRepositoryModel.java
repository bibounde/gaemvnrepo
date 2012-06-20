package com.bibounde.gaemvnrepo.web.admin.detail.repository;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.NavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class BrowserRepositoryModel implements Model{

    public static final String REPOSITORY_INITIALIZED = BrowserRepositoryModel.class.getName() + ".event.repositoryinitialized";
    public static final String CHILDREN_ADDED = BrowserRepositoryModel.class.getName() + ".event.childrenadded";
    
    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    private List<RepositoryNavigationNode> nodes;
    private NavigationNode lastExpandedNode;
    
    /**
     * @return the nodes
     */
    public List<RepositoryNavigationNode> getNodes() {
        return nodes;
    }

    /**
     * @param nodes
     */
    public void set(List<RepositoryNavigationNode> nodes) {
        this.nodes = nodes;
        ModelEvent event = new ModelEvent(REPOSITORY_INITIALIZED);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }
    
    public void expandNode(NavigationNode parent, List<FileNavigationNode> children) {
        parent.children = children;
        this.lastExpandedNode = parent;
        ModelEvent event = new ModelEvent(CHILDREN_ADDED);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }
    
    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        this.removeModelEventListener(listener);
    }

    /**
     * @return the lastExpandedNode
     */
    public NavigationNode getLastExpandedNode() {
        return lastExpandedNode;
    }

}
