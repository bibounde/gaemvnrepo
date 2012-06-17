package com.bibounde.gaemvnrepo.web.admin.detail.repository;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.ui.VerticalLayout;

public class BrowseRepositoryView extends VerticalLayout implements View {

    public static final String ID = BrowseRepositoryView.class.getName();
    
    private BrowseRepositoryModel model;
    private Object controller;
    
    public BrowseRepositoryView() {
        this.model = new BrowseRepositoryModel();
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("BrowseRepositoryView.title", this.getLocale()), "/static/icons/browse-repository-32.png"));
    }
    
    @Override
    public void modelChanged(ModelEvent event) {
        
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
