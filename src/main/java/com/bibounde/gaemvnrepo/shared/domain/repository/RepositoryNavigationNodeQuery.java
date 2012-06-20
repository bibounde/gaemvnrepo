package com.bibounde.gaemvnrepo.shared.domain.repository;

import java.util.List;

public class RepositoryNavigationNodeQuery {

    public long id;
    public List<FileQuery> children;
    
    public static class FileQuery {
        public String path;
        public List<FileQuery> children;
    }
}
