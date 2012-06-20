package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.shared.domain.Role;

public class UserEditResponse implements Serializable {

    public long id;
    public String login, email, locale;
    public Role role;
    public boolean active;
    
}
