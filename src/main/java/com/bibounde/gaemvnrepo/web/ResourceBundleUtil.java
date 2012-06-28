package com.bibounde.gaemvnrepo.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public enum ResourceBundleUtil {

    INSTANCE;
    
    private String[] supportedLanguages = {"en_GB", "fr_FR"};
    
    private Map<ApplicationData, ResourceBundle> resourceBundleMap = new HashMap<ApplicationData, ResourceBundle>();
    
    public void put(ApplicationData applicationData, Locale locale) {
        System.out.println("PUT ---------------------------------------> " + applicationData);
        this.resourceBundleMap.put(applicationData, ResourceBundle.getBundle("messages", locale));
    }
    
    public void remove(ApplicationData applicationData) {
        this.resourceBundleMap.remove(applicationData);
    }
    
    public String getString(ApplicationData applicationData, String key) {
        System.out.println("GET ---------------------------------------> " + applicationData);
        String ret = this.resourceBundleMap.get(applicationData).getString(key);
        System.out.println("OK ----------------------------------------->");
        return ret;
    }

    /**
     * @return the supportedLanguages
     */
    public String[] getSupportedLanguages() {
        return supportedLanguages;
    }
}
