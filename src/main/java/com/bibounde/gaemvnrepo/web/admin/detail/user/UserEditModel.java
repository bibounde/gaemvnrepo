package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class UserEditModel implements Model{

    public static final String USER_SAVED = UserEditModel.class.getName() + ".event.usersaved";
    public static final String USER_CHANGED = UserEditModel.class.getName() + ".event.userchanged";
    public static final String USER_ACTIVATION_CHANGED = UserEditModel.class.getName() + ".event.useractivationchanged";
    
    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    
    private boolean editionMode = false;
    private UserEditResponse user;
    
    /**
     * @return the editionMode
     */
    public boolean isEditionMode() {
        return editionMode;
    }

    /**
     * @return the user
     */
    public UserEditResponse getUser() {
        return user;
    }
    
    public void initUser(UserEditResponse user){
        this.user = user;
        this.editionMode = this.user != null;
        
        ModelEvent event = new ModelEvent(USER_CHANGED);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }
    
    public void saveUser(UserEditResponse user){
        this.user = user;
        this.editionMode = true;
        ModelEvent event = new ModelEvent(USER_SAVED);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }
    
    public void activeUser(boolean active) {
        this.user.active = active;
        ModelEvent event = new ModelEvent(USER_ACTIVATION_CHANGED);
        for (ModelEventListener listener : this.listeners) {
            listener.modelChanged(event);
        }
    }

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

}
