package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class UserEditModel implements Model{

    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    
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

}
