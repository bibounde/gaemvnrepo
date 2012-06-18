package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;

public class UserEditQuery implements Serializable {

    public String login, email, password, locale;
    public boolean active, administrator;
    
    
}
