package com.bibounde.gaemvnrepo.web.admin.exception;

public class WebException extends Exception{

    private String i18nKey;

    public WebException(String i18nKey, Throwable cause) {
        super(cause);
        this.i18nKey = i18nKey;
    }

    /**
     * @return the i18nKey
     */
    public String getI18nKey() {
        return i18nKey;
    }
    
}
