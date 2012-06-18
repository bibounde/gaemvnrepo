package com.bibounde.gaemvnrepo.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
//Not supported by GAE @Unique(name="USER_UNIQUE_IDX", members={"email"})
public class User implements Disposable {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent(nullValue=NullValue.EXCEPTION)
    private String email;
    
    @Persistent(nullValue=NullValue.EXCEPTION)
    private String login;
    
    @Persistent(nullValue=NullValue.EXCEPTION)
    private String password;
    
    @Persistent(nullValue=NullValue.EXCEPTION)
    private boolean active;
    
    @Persistent
    private Role role = Role.UPLOADER;
    
    @Persistent
    private String locale;
    
    @Persistent
    private long created;
    
    @Persistent
    private boolean disposable;
    
    public enum Role {
        ADMIN, MANAGER, UPLOADER, USER;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * @return the created
     */
    public long getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(long created) {
        this.created = created;
    }

    /**
     * @return the disposable
     */
    public boolean isDisposable() {
        return disposable;
    }

    /**
     * @param disposable the disposable to set
     */
    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }
    
    
}
