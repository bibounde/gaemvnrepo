package com.bibounde.gaemvnrepo.web.mvc;

public class ActionEvent {

    private String sourceId, action;
    private Object[] params;
    
    public ActionEvent(String sourceId, String action) {
        this.sourceId = sourceId;
        this.action = action;
    }
    
    public ActionEvent(String sourceId, String action, Object ... params) {
        this(sourceId, action);
        this.params = params;
    }

    /**
     * @return the source id
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @return the params
     */
    public Object[] getParams() {
        return params;
    }
    
    
    
}
