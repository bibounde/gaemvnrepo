package com.bibounde.gaemvnrepo.shared.domain.repository;

import java.util.Date;

public class FileNavigationNode extends NavigationNode {

    public boolean file;
    public String name, mime, creator;
    public Date created;
    public int depth;
}
