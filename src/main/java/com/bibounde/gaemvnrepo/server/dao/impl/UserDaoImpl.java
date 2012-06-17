package com.bibounde.gaemvnrepo.server.dao.impl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.server.dao.UserDao;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil.Mode;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Component
public class UserDaoImpl implements UserDao {

    @Override
    public void createUser(User user, PersistenceManager pm) throws TechnicalException {
        try {
            user.setCreated(System.currentTimeMillis());
            pm.makePersistent(user);
        } catch (Exception e) {
            throw new TechnicalException("Unable to create user db", e);
        }
    }

    @Override
    public User findUserByEmail(String email, boolean filterActivation, PersistenceManager pm) throws TechnicalException {
        
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            
            StringBuilder filter = new StringBuilder("email == emailParam");
            if (filterActivation) {
                filter.append(" && active == true");
            }
            
            query.setFilter(filter.toString());
            query.declareParameters("String emailParam");
            
            List<User> users = (List<User>) query.execute(email);
            
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public List<User> findUserByRole(Role role, boolean filterActivation, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            
            StringBuilder filter = new StringBuilder("role == roleParam");
            if (filterActivation) {
                filter.append(" && active == true");
            }
            query.setFilter(filter.toString());
            query.declareParameters("String roleParam");
            
            List<User> users = (List<User>) query.execute(role.name());
            
            return users;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                    query.closeAll();
            }
        }
    }

    @Override
    public User findUserByLogin(String login, boolean filterActivation, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            
            StringBuilder filter = new StringBuilder("login == loginParam");
            if (filterActivation) {
                filter.append(" && active == true");
            }
            
            query.setFilter(filter.toString());
            query.declareParameters("String loginParam");
            
            List<User> users = (List<User>) query.execute(login);
            
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public int count(PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            query.setResult("count(this)");
            
            return (Integer) query.execute();
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }
    
    @Override
    public List<User> findAll(PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            List<User> ret = (List<User>) query.execute();
            
            return ret;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @Override
    public void delete(String login, PersistenceManager pm) throws TechnicalException {
        if (ConfigurationUtil.INSTANCE.getMode() != Mode.DEV) {
            throw new TechnicalException("Delete all is only available in DEV mode", null);
        }
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            query.setFilter("login == loginParam");
            query.declareParameters("String loginParam");
            
            List<User> users = (List<User>) query.execute(login);
            
            if (!users.isEmpty()) {
                pm.deletePersistent(users.get(0));
            }
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }
}
