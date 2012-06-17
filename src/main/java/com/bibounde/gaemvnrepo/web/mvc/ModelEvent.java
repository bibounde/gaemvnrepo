package com.bibounde.gaemvnrepo.web.mvc;

import java.io.Serializable;

public class ModelEvent implements Serializable {

    private String type;

    public ModelEvent(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}
