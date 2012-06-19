package com.bibounde.gaemvnrepo.web.admin.tools;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfirmationDialogUtil {

    /**
     * Shows confirmation dialog
     * @param parent parent window
     * @param action action name
     * @param message confirmation message
     * @param listener confirmation listener
     */
    public static void showConfirmationDialog(final Window parent, String action, String message, final ConfirmationDialogListener listener) {
        final Window subWindow = new Window(action);
        subWindow.setModal(true);
        subWindow.setResizable(false);
        
        VerticalLayout layout = (VerticalLayout) subWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        
        HorizontalLayout messageLayout = new HorizontalLayout();
        messageLayout.setSpacing(true);
        
        Embedded icon = new Embedded();
        icon.setSource(new ExternalResource("/static/icons/info-32.png"));
        //icon.setWidth(34, Label.UNITS_PIXELS);
        //icon.setHeight(34, Label.UNITS_PIXELS);
        messageLayout.addComponent(icon);
        messageLayout.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);
        
        Label label = new Label(message + "?");
        messageLayout.addComponent(label);
        messageLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        //messageLayout.setExpandRatio(label, 1.0f);
        
        layout.addComponent(messageLayout);
        layout.setExpandRatio(messageLayout, 1.0f);
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        layout.addComponent(buttonLayout);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth(100, HorizontalLayout.UNITS_PERCENTAGE);
        
        NativeButton yesButton = new NativeButton(Messages.INSTANCE.getString("ConfirmationDialogUtil.yes", layout.getLocale()));
        buttonLayout.addComponent(yesButton);
        buttonLayout.setExpandRatio(yesButton, 1.0f);
        buttonLayout.setComponentAlignment(yesButton, Alignment.BOTTOM_RIGHT);
        
        yesButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                parent.removeWindow(subWindow);
                listener.onConfirmation();
            }
        });
        
        NativeButton noButton = new NativeButton(Messages.INSTANCE.getString("ConfirmationDialogUtil.no", layout.getLocale()));
        buttonLayout.addComponent(noButton);
        buttonLayout.setComponentAlignment(noButton, Alignment.BOTTOM_RIGHT);
        noButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                parent.removeWindow(subWindow);
            }
        });
        
        parent.addWindow(subWindow);
    }
    
    
    public static interface ConfirmationDialogListener extends Serializable {
        
        /**
         * Method called when user confirms
         */
        public void onConfirmation();
    }
}
