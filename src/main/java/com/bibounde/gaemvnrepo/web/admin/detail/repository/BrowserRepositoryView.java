package com.bibounde.gaemvnrepo.web.admin.detail.repository;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.shared.domain.repository.FileNavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.NavigationNode;
import com.bibounde.gaemvnrepo.shared.domain.repository.RepositoryNavigationNode;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.admin.tools.ConfirmationDialogUtil;
import com.bibounde.gaemvnrepo.web.admin.tools.ConfirmationDialogUtil.ConfirmationDialogListener;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

public class BrowserRepositoryView extends VerticalLayout implements View, Action.Handler {

    public static final String ACTION_NODE_EXPANDED = BrowserRepositoryView.class.getName() + ".action.node.expanded";
    public static final String ACTION_NODE_DELETED = BrowserRepositoryView.class.getName() + ".action.node.deleted";
    public static final String ACTION_REFRESH = BrowserRepositoryView.class.getName() + ".action.refresh";
    
    public static final String ID = BrowserRepositoryView.class.getName();
    
    private BrowserRepositoryModel model;
    private Controller controller;
    private Tree tree;
    private Action deleteAction;
    private Button refreshButton;
    private Label statusLabel, statusIcon;
    
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
        this.tree.addActionHandler(this);

        this.deleteAction = new Action(Messages.INSTANCE.getString("BrowserRepositoryView.delete", this.getLocale()), new ExternalResource("/static/icons/delete-16.png"));
        
        this.refreshButton = new Button(Messages.INSTANCE.getString("BrowserRepositoryView.refresh", getLocale()));
        this.refreshButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.refreshButton.setIcon(new ExternalResource("/static/icons/refresh-16.png"));
        this.refreshButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_REFRESH);
                controller.actionPerformed(actionEvent);
            }
        });
        
        this.statusIcon = new Label();
        this.statusIcon.setWidth(16, UNITS_PIXELS);
        this.statusLabel = new Label();
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("BrowserRepositoryView.title", this.getLocale()), "/static/icons/browse-repository-32.png"));
        
        HorizontalLayout toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth(100, UNITS_PERCENTAGE);
        toolBarLayout.setSpacing(true);
        this.addComponent(toolBarLayout);
        
        toolBarLayout.addComponent(this.statusIcon);
        toolBarLayout.addComponent(this.statusLabel);
        toolBarLayout.setExpandRatio(this.statusLabel, 1.0f);
        toolBarLayout.addComponent(this.refreshButton);
        toolBarLayout.setComponentAlignment(this.refreshButton, Alignment.MIDDLE_RIGHT);
        
        Panel treePanel = new Panel();
        treePanel.setStyleName(Reindeer.PANEL_LIGHT);
        treePanel.addStyleName("gaemvnrepo-tree-panel");
        treePanel.setSizeFull();
        
        this.addComponent(treePanel);
        this.setExpandRatio(treePanel, 1.0f);
       
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
        } else if (BrowserRepositoryModel.NODE_DELETED.equals(event.getType())) {
            NavigationNode nodeToDelete = this.model.getDeletedNode();
            NavigationNode parent = (NavigationNode) this.tree.getParent(nodeToDelete);
            if (parent != null) {
                parent.children = null;
            }
            this.deleteNode(nodeToDelete);
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
    
    private void deleteNode(NavigationNode node) {
        if (node.children != null) {
            for (FileNavigationNode child : node.children) {
                this.deleteNode(child);
            }
        }
        this.tree.removeItem(node);
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


    @Override
    public Action[] getActions(Object target, Object sender) {
        if (this.tree == sender && target instanceof FileNavigationNode) {
            return new Action[]{this.deleteAction};
        }
        return new Action[0];
        
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (this.deleteAction == action) {
            final FileNavigationNode node = (FileNavigationNode) target;
            ConfirmationDialogUtil.showConfirmationDialog(getWindow(), Messages.INSTANCE.getString("BrowserRepositoryView.delete", getLocale()), Messages.INSTANCE.getString("BrowserRepositoryView.delete.message", getLocale()).replace("{}", node.name), new ConfirmationDialogListener() {
                
                @Override
                public void onConfirmation() {
                    ActionEvent event = new ActionEvent(ID, ACTION_NODE_DELETED, node);
                    controller.actionPerformed(event);
                }
            });
        }
    }
}
