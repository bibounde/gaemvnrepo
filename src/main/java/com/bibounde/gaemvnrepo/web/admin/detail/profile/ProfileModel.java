package com.bibounde.gaemvnrepo.web.admin.detail.profile;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class ProfileModel implements Model{

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

}
