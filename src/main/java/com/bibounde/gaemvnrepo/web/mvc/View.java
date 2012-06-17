package com.bibounde.gaemvnrepo.web.mvc;

import com.vaadin.ui.Component;

public interface View extends ModelEventListener, Component {
    
    /**
     * Retrieves model
     * @return model
     */
    Model getModel();
    
    /**
     * Retrieves view id
     * @return
     */
    String getId();
    
    /**
     * Sets the controller
     * @param controller
     */
    void setController(Controller controller);
}
