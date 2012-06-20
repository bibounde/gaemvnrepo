package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.shared.domain.Role;

public class UserEditProfileData implements Serializable {

    public static final String LOGIN_PROPERTY = "login";
    public static final String EMAIL_PROPERTY = "email";
    public static final String LOCALE_PROPERTY = "locale";
    public static final String ROLE_PROPERTY = "role";
    
    
    private String login = "", email = "", locale;
    private Role role;
    
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
     * @return the role
     */
    public Role getRole() {
        return role;
    }
    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
