package com.bibounde.gaemvnrepo.web.admin.detail.system;

import com.bibounde.gaemvnrepo.web.ApplicationData;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.ui.VerticalLayout;

public class SystemConfigurationView extends VerticalLayout implements View {

    public static final String ID = SystemConfigurationView.class.getName();
    
    private SystemConfigurationModel model;
    private Object controller;
    
    public SystemConfigurationView() {
        this.model = new SystemConfigurationModel();
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(ApplicationData.getMessage("SystemConfigurationView.title"), "/static/icons/system-configuration-32.png"));
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
