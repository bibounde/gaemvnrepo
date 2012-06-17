package com.bibounde.gaemvnrepo.web.admin;

import com.bibounde.gaemvnrepo.web.admin.navigation.NavigationMenu;
import com.bibounde.gaemvnrepo.web.admin.statusbar.StatusBar;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class MainPage extends VerticalLayout {

    private Controller controller;
    private StatusBar statusBar;
    private NavigationMenu navigationMenu;
    private Panel mainView;
    private View currentView;
    
    public MainPage(Controller controller) {
        this.controller = controller;
        
        this.statusBar = new StatusBar();
        this.controller.add(this.statusBar);
        
        this.navigationMenu = new NavigationMenu();
        this.controller.add(this.navigationMenu);
        
        mainView = new Panel();
        
        this.initLayout();
    }

    private void initLayout() {
        this.addStyleName(Reindeer.LAYOUT_BLUE);
        this.setMargin(false);
        this.setSpacing(true);
        this.setSizeFull();
        
        this.addComponent(this.statusBar);
        this.setComponentAlignment(this.statusBar, Alignment.TOP_CENTER);
        
        HorizontalLayout mainLayout = new HorizontalLayout();
        this.addComponent(mainLayout);
        this.setExpandRatio(mainLayout, 1.0f);
        
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        
        mainLayout.addComponent(this.navigationMenu);
        mainLayout.setComponentAlignment(this.navigationMenu, Alignment.TOP_LEFT);
        mainLayout.setExpandRatio(this.navigationMenu, 1f);
        
        this.mainView.setSizeFull();
        //this.mainView.setHeight(100, UNITS_PERCENTAGE);
        mainLayout.addComponent(this.mainView);
        mainLayout.setComponentAlignment(this.mainView, Alignment.TOP_LEFT);
        mainLayout.setExpandRatio(this.mainView, 4f);
    }
    
    /**
     * Shows view in main panel
     * @param view
     */
    public void showView(View view) {
        if (view != null && this.currentView != view) {
            if (this.currentView != null) {
                this.mainView.setContent(null);
            }
            
            //Use this code in order to obtain 100% height
            VerticalLayout viewLayout = new VerticalLayout();
            viewLayout.setMargin(true);
            viewLayout.addComponent(view);
            viewLayout.setExpandRatio(view, 1.0f);
            viewLayout.setHeight(100, UNITS_PERCENTAGE);
            this.mainView.setContent(viewLayout);
            this.currentView = view;
        }
    }
    
}
