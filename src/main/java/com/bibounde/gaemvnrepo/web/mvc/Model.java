package com.bibounde.gaemvnrepo.web.mvc;

import java.io.Serializable;

public interface Model extends Serializable{

    /**
     * Adds model event listener
     * @param listener
     */
    void addModelEventListener(ModelEventListener listener);
    
    /**
     * Removes model event listener
     * @param listener
     */
    void removeModelEventListener(ModelEventListener listener);
}
