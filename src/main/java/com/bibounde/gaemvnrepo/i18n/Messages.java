package com.bibounde.gaemvnrepo.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Messages {

    INSTANCE;
    
    private ResourceBundle resourceBundle;
    private String[] supportedLanguages = {"en_GB"};
    
    private Messages() {
        this.resourceBundle = ResourceBundle.getBundle("messages");
        
    }
    
    /**
     * Returns translated value of key
     * @param key
     * @param locale
     * @return translated text
     */
    public String getString(String key, Locale locale) {
        return this.resourceBundle.getString(key);
    }
    
    public String[] getSupportedLanguages() {
        return this.supportedLanguages;
    }
}
