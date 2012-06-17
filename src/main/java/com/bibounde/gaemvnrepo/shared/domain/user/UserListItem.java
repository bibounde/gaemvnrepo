package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;
import java.util.Date;

public class UserListItem implements Serializable{

    public long id;
    public String gravatarURL, login, email;
    public boolean administrator, active;
    public Date created;
}
