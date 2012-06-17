package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.Date;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class UserEditView extends VerticalLayout implements View {

    public static final String ID = UserEditView.class.getName();
    
    private UserEditModel model;
    private Controller controller;
    
    public UserEditView() {
        this.model = new UserEditModel();
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("UserEditView.title", this.getLocale()), "/static/icons/user-edit-32.png"));
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
