package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;

public class UserEditResponse implements Serializable {

    public long id;
    public String login, email, locale;
    public boolean active, administrator;
    
}
