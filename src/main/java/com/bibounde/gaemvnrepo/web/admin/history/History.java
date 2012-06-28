package com.bibounde.gaemvnrepo.web.admin.history;

import java.util.Map;

import com.google.gson.Gson;

public class History {

    private static Gson gson = new Gson();
    
    private String viewId;
    private String action;
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    private Map<String, String> params;
    /**
     * @return the viewId
     */
    public String getViewId() {
        return viewId;
    }
    /**
     * @param viewId the viewId to set
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
    /**
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }
    /**
     * @param params the params to set
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    
    /**
     * Encodes history object
     * @param history
     * @return encoded String
     */
    public static String encode(History history) {
        return gson.toJson(history);
    }
    
    /**
     * Decodes encoded history string
     * @param encoded
     * @return history
     */
    public static History decode(String encoded) {
        return gson.fromJson(encoded, History.class);
    }
}
