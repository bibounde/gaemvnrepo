package com.bibounde.gaemvnrepo.server.dao.impl;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Component;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.server.dao.UserDao;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Component
public class UserDaoImpl implements UserDao {

    @Override
    public User saveOrUpdate(User user, PersistenceManager pm) throws TechnicalException {
        try {
            if (user.getLogin() == null || user.getLogin().length() < 4 || user.getLogin().length() > 25) {
                throw new IllegalArgumentException("User login is invalid. Login length must be between 3 and 25");
            }
            
            this.checkUniqueContstaint("login", user.getLogin(), pm);
            this.checkUniqueContstaint("email", user.getEmail(), pm);

            user.setCreated(System.currentTimeMillis());
            return pm.makePersistent(user);
        } catch (Exception e) {
            throw new TechnicalException("Unable to create user db", e);
        }
    }

    @Override
    public User findUserByEmail(String email, boolean filterActivation, PersistenceManager pm) throws TechnicalException {

        Query query = null;
        try {
            query = pm.newQuery(User.class);

            StringBuilder filter = new StringBuilder("email == emailParam && disposable == false");
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

            StringBuilder filter = new StringBuilder("role == roleParam && disposable == false");
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

            StringBuilder filter = new StringBuilder("login == loginParam && disposable == false");
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
    public User findUserById(long id, boolean filterActivation, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);

            StringBuilder filter = new StringBuilder("id == idParam && disposable == false");
            if (filterActivation) {
                filter.append(" && active == true");
            }

            query.setFilter(filter.toString());
            query.declareParameters("Long idParam");

            List<User> users = (List<User>) query.execute(id);

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
            query.setFilter("disposable == false");
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
    public void delete(User user, PersistenceManager pm) throws TechnicalException {
        pm.deletePersistent(user);
    }

    /**
     * Checks unique constraint (due to GAE limitation one method per property - OR is not supported for
     * multiple properties)
     * 
     * @param propterty property to check
     * @param value property value
     * @param pm
     * @throws TechnicalException
     */
    private void checkUniqueContstaint(String property, String value, PersistenceManager pm) throws TechnicalException {
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            query.setFilter("(" + property + " == param) && disposable == false");
            query.declareParameters("String param");

            List<User> users = (List<User>) query.execute(value);
            if (users.size() > 0) {
                throw new IllegalStateException("Unique constraint violation on " + property + " property");
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
