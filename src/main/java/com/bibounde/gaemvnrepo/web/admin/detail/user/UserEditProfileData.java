package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;

public class UserEditProfileData implements Serializable {

    public static final String LOGIN_PROPERTY = "login";
    public static final String EMAIL_PROPERTY = "email";
    public static final String LOCALE_PROPERTY = "locale";
    public static final String ACTIVE_PROPERTY = "active";
    public static final String ADMINISTRATOR_PROPERTY = "administrator";
    
    
    private String login = "", email = "", locale;
    private boolean active, administrator;
    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }
    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }
    /**
     * @param locale the locale to set
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }
    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * @return the administrator
     */
    public boolean isAdministrator() {
        return administrator;
    }
    /**
     * @param administrator the administrator to set
     */
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
    
}
