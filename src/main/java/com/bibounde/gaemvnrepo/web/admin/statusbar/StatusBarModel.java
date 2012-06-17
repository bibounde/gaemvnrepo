package com.bibounde.gaemvnrepo.web.admin.statusbar;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class StatusBarModel implements Model {

    public static final String GRAVATAR_URL_CHANGED_EVENT = StatusBar.class.getName() + "event.gravatarurlchanged";
    public static final String LOGIN_CHANGED_EVENT = StatusBar.class.getName() + "event.loginchanged";
    
    private String gravaterUrl;
    private String login;
    
    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    
    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        this.listeners.remove(listener);
    }
    
    /**
     * @return the gravaterUrl
     */
    public String getGravaterUrl() {
        return gravaterUrl;
    }
    /**
     * @param gravaterUrl the gravaterUrl to set
     */
    public void setGravaterUrl(String gravaterUrl) {
        this.gravaterUrl = gravaterUrl;
        ModelEvent event = new ModelEvent(GRAVATAR_URL_CHANGED_EVENT);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
        ModelEvent event = new ModelEvent(LOGIN_CHANGED_EVENT);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }
}
