package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.shared.domain.Role;

public class UserEditQuery implements Serializable {

    public String login, email, password, locale;
    public Role role;
    
    
}
