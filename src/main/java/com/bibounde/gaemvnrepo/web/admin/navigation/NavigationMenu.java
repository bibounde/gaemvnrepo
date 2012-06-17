package com.bibounde.gaemvnrepo.web.admin.navigation;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.web.admin.navigation.NavigationMenuModel.MenuItem;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class NavigationMenu extends Panel implements View {
    
    public static final String ACTION_SELECTION = NavigationMenu.class.getName() + ".action.selection";
    
    public static final String ID = NavigationMenu.class.getName();
    
    private NavigationMenuModel model;
    private Embedded appEngine;
    private VerticalLayout linkLayout;
    private List<Button> links = new ArrayList<Button>();
    private Resource selectedIcon, unselectedIcon;

    private Controller controller;

    public NavigationMenu() {
        this.model = new NavigationMenuModel();
        this.model.addModelEventListener(this);
        
        this.linkLayout = new VerticalLayout();
        
        this.appEngine = new Embedded();
        this.appEngine.setType(Embedded.TYPE_IMAGE);
        this.appEngine.setSource(new ExternalResource("/static/icons/appengine-noborder-120x30.gif"));
        
        this.selectedIcon = new ExternalResource("/static/icons/menu-selected-16.png");
        this.unselectedIcon = new ExternalResource("/static/icons/transparent-16.png");
        
        this.initLayout();
    }
    
    private void initLayout() {
        VerticalLayout layout = (VerticalLayout) this.getContent();
        layout.setSpacing(true);
        layout.setMargin(true);
        
        this.addComponent(this.linkLayout);
        this.linkLayout.setSizeFull();
        this.linkLayout.setSpacing(true);
        
        Label separator = new Label();
        separator.setHeight(50, UNITS_PIXELS);
        this.addComponent(separator);
        
        this.addComponent(this.appEngine);
        layout.setComponentAlignment(this.appEngine, Alignment.BOTTOM_CENTER);
    }
    
    @Override
    public void modelChanged(ModelEvent event) {
        if (NavigationMenuModel.MENU_ITEM_CHANGED.equals(event.getType())) {
            this.linkLayout.removeAllComponents();
            this.links.clear();
            for (int i = 0; i < this.model.getMenuItems().size(); i++) {
                MenuItem item = this.model.getMenuItems().get(i);
                Button link = new Button(Messages.INSTANCE.getString("NavigationMenu." + item.name().toLowerCase(), this.getLocale()));
                link.setStyleName(BaseTheme.BUTTON_LINK);
                link.addListener(this.createSelectionListener(i));
                this.linkLayout.addComponent(link);
                this.linkLayout.setComponentAlignment(link, Alignment.MIDDLE_LEFT);
                
                this.links.add(link);
            }
        } else if (NavigationMenuModel.SELECTION_CHANGED.equals(event.getType())){
            int selectedIndex = ((NavigationMenuModel) this.getModel()).getSelectedIndex();
            for (int i = 0; i < this.links.size(); i++) {
                Button link = this.links.get(i);
                if (i == selectedIndex) {
                    link.setIcon(this.selectedIcon);
                } else {
                    link.setIcon(this.unselectedIcon);
                }
            }
        }
    }
    
    private ClickListener createSelectionListener(final int index) {
        ClickListener ret = new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_SELECTION, index);
                NavigationMenu.this.controller.actionPerformed(actionEvent);
            }
        };
        return ret;
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
