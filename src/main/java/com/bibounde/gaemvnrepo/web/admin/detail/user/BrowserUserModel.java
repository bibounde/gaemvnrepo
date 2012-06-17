package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.shared.domain.user.UserListResponse;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class BrowserUserModel implements Model{

    public static final String USER_LIST_INITIALIZED = BrowserUserModel.class.getName() + ".event.userlistinitialized";
    public static final String USER_LIST_CHANGED = BrowserUserModel.class.getName() + ".event.userlistchanged";
    
    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    private UserListResponse userList;
    
    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        this.removeModelEventListener(listener);
    }

    /**
     * @return the userList
     */
    public UserListResponse getUserList() {
        return userList;
    }

    /**
     * @param userList the userList to set
     */
    public void setUserList(UserListResponse userList) {
        this.userList = userList;
        if (userList == null) {
            ModelEvent event = new ModelEvent(USER_LIST_INITIALIZED);
            for (ModelEventListener listener : this.listeners) {
                listener.modelChanged(event);
            }
        } else {
            ModelEvent event = new ModelEvent(USER_LIST_CHANGED);
            for (ModelEventListener listener : this.listeners) {
                listener.modelChanged(event);
            }
        }
        
    }

    
}
