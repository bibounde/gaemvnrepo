package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;

public class UsereditAuthData implements Serializable{

    public static final String PASSWORD_PROPERTY = "password";
    public static final String REPASSWORD_PROPERTY = "repassword";
    
    private String password = "", repassword = "";

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the repassword
     */
    public String getRepassword() {
        return repassword;
    }

    /**
     * @param repassword the repassword to set
     */
    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
    
}
