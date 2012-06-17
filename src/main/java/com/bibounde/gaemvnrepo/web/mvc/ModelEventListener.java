package com.bibounde.gaemvnrepo.web.mvc;

import java.io.Serializable;

public interface ModelEventListener extends Serializable {

    /**
     * Method called when model changed
     * @param event
     */
    void modelChanged(ModelEvent event);
}
