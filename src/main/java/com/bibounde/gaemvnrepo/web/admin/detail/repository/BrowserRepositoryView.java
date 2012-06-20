package com.bibounde.gaemvnrepo.web.admin.detail.repository;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.NavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.admin.detail.user.BrowserUserView;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class BrowserRepositoryView extends VerticalLayout implements View {

    public static final String ACTION_NODE_EXPANDED = BrowserRepositoryView.class.getName() + ".action.node.expanded";
    
    public static final String ID = BrowserRepositoryView.class.getName();
    
    private BrowserRepositoryModel model;
    private Controller controller;
    private Tree tree;
    
    public BrowserRepositoryView() {
        this.model = new BrowserRepositoryModel();
        this.model.addModelEventListener(this);
        
        this.tree = new Tree();
        this.tree.addListener(new ExpandListener() {
            
            @Override
            public void nodeExpand(ExpandEvent event) {
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_NODE_EXPANDED, event.getItemId());
                controller.actionPerformed(actionEvent);
            }
        });
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("BrowserRepositoryView.title", this.getLocale()), "/static/icons/browse-repository-32.png"));
        
        Panel treePanel = new Panel();
        treePanel.setStyleName(Reindeer.PANEL_LIGHT);
        treePanel.addStyleName("gaemvnrepo-tree-panel");
        treePanel.setSizeFull();
        
//        VerticalLayout treePanelLayout = (VerticalLayout) treePanel.getContent();
//        treePanelLayout.addStyleName("gaemvnrepo-tree-panel");
//        treePanelLayout.setMargin(true);
        
        this.addComponent(treePanel);
        this.setExpandRatio(treePanel, 1.0f);
        
        //this.tree.addStyleName("gaemvnrepo-tree-panel");
        treePanel.addComponent(this.tree);
    }
    
    @Override
    public void modelChanged(ModelEvent event) {
        if (BrowserRepositoryModel.REPOSITORY_INITIALIZED.equals(event.getType())) {
            this.tree.removeAllItems();
            for (RepositoryNavigationNode repoNode : this.model.getNodes()) {
                tree.addItem(repoNode);
                tree.setItemCaption(repoNode, repoNode.name);
                
                if (repoNode.children != null) {
                    for (FileNavigationNode fileNode : repoNode.children) {
                        this.createItem(repoNode, fileNode);
                        this.computeFileNodeChildren(fileNode);
                    }
                }
            }
        } else if (BrowserRepositoryModel.CHILDREN_ADDED.equals(event.getType())) {
            this.computeFileNodeChildren(this.model.getLastExpandedNode());
        }
    }
    
    /**
     * Computes nodes recursively
     * @param fileNode
     */
    private void computeFileNodeChildren(NavigationNode parentNode) {
        if (parentNode.children != null) {
            for (FileNavigationNode fileNode : parentNode.children) {
                this.createItem(parentNode, fileNode);
                this.computeFileNodeChildren(fileNode);
            }
        }
    }
    
    private void createItem(NavigationNode parentNode, FileNavigationNode childNode) {
        tree.addItem(childNode);
        tree.setParent(childNode, parentNode);
        tree.setItemCaption(childNode, childNode.name);
        if (childNode.file) {
            tree.setChildrenAllowed(childNode, false);
            tree.setItemIcon(childNode, this.getFileIcon(childNode.name));
        } else {
            tree.setChildrenAllowed(childNode, true);
        }
    }
    
    private ExternalResource getFileIcon(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String img = null;
        if (".xml".equals(extension)) {
            img = "file-xml-16.png";
        } else if (".pom".equals(extension)) {
            img = "file-pom-16.png";
        } else if (".md5".equals(extension)) {
            img = "file-md5-16.png";
        } else if (".sha1".equals(extension)) {
            img = "file-sha1-16.png";
        } else {
            img = "file-unknown-16.png";
        }
        return new ExternalResource("/static/icons/" + img);
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

}
