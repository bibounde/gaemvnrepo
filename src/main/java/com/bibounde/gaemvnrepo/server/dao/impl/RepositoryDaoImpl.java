package com.bibounde.gaemvnrepo.server.dao.impl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.File;
import com.bibounde.gaemvnrepo.model.Repository;
import com.bibounde.gaemvnrepo.server.dao.RepositoryDao;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Component
public class RepositoryDaoImpl implements RepositoryDao {

    @Override
    public Repository findByName(String name, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(Repository.class);
            query.setFilter("name == nameParam && disposable == false");
            query.declareParameters("String nameParam");

            List<Repository> repositories = (List<Repository>) query.execute(name);

            return repositories.isEmpty() ? null : repositories.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public File findFileByPath(String path, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(File.class);
            query.setFilter("path == pathParam && disposable == false");
            query.declareParameters("String pathParam");

            List<File> files = (List<File>) query.execute(path);

            return files.isEmpty() ? null : files.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public List<File> findAllFiles(String dirPath, PersistenceManager pm) throws TechnicalException {
        return this.findFileStartWith(dirPath + "/", pm);
    }

    @Override
    public List<File> findFileStartWith(String prefix, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(File.class);
            query.setFilter("path.startsWith(pathParam) && disposable == false");
            query.declareParameters("String pathParam");

            List<File> files = (List<File>) query.execute(prefix);

            return files;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public List<Repository> findSnapshotsReposirories(PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(Repository.class);
            query.setFilter("snapshots == true && disposable == false");
            
            List<Repository> repositories = (List<Repository>) query.execute();

            return repositories;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public File findDisposableFileByPath(String path, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(File.class);
            query.setFilter("path == pathParam && disposable == true");
            query.declareParameters("String pathParam");

            List<File> files = (List<File>) query.execute(path);

            return files.isEmpty() ? null : files.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }
    
}
