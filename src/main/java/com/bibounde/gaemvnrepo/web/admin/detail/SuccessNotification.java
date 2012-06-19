package com.bibounde.gaemvnrepo.web.admin.detail;

import com.vaadin.ui.Window.Notification;

public class SuccessNotification extends Notification {

    public SuccessNotification(String caption, String message) {
        super(caption, message);
        this.setStyleName("gaemvnrepo-notification-success");
    }

}
