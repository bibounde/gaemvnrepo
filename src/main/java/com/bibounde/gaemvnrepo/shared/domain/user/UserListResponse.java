package com.bibounde.gaemvnrepo.shared.domain.user;

import java.io.Serializable;
import java.util.List;

public class UserListResponse implements Serializable {

    public List<UserListItem> users;
    public int total, currentPage, pageSize;
    public UserSort sort;
    public boolean ascending;
}
