package com.bibounde.gaemvnrepo.shared.domain.authentication;

import java.util.Locale;

import com.bibounde.gaemvnrepo.shared.domain.Role;

public class AuthenticatedUserInfo {

    public String login, gravatarUrl;
    public Role role;
    public Locale locale;
}
