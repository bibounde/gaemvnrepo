package com.bibounde.gaemvnrepo.shared.domain.repository;

import java.io.Serializable;
import java.util.List;

public class NavigationNode implements Serializable {
    
    public String path;
    public List<FileNavigationNode> children;
}
