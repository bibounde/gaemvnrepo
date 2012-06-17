package com.bibounde.gaemvnrepo.web.admin.detail;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class HeaderComponent extends HorizontalLayout {

    private Label titleLabel;
    
    public HeaderComponent(String title, String iconURL) {
        this.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        this.setHeight(40, Sizeable.UNITS_PIXELS);
        this.addStyleName("gaemvnrepo-detail-title");
        
        this.titleLabel = new Label(title);
        this.addComponent(titleLabel);
        this.setComponentAlignment(titleLabel, Alignment.MIDDLE_LEFT);
        this.setExpandRatio(titleLabel, 5f);
        
        Embedded icon = new Embedded();
        icon.setType(Embedded.TYPE_IMAGE);
        icon.setSource(new ExternalResource(iconURL));
        this.addComponent(icon);
        this.setComponentAlignment(icon, Alignment.MIDDLE_RIGHT);
        this.setExpandRatio(icon, 1f);
    }
    
    public void setTitle(String title) {
        this.titleLabel.setValue(title);
    }
}
