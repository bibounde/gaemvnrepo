package com.bibounde.gaemvnrepo.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface UserDao extends Serializable {

    /**
     * Save or update a user
     * @param user user to create
     * @param pm persistence manager
     * @throws TechnicalException
     */
    void saveOrUpdate(User user, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves user by email. 
     * @param email user's email
     * @param filterActivation if true enable activation filter
     * @param pm persistence manager
     * @return user object or null if no user with specified email found
     * @throws TechnicalException
     */
    User findUserByEmail(String email, boolean filterActivation, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves user by login. 
     * @param login user's login
     * @param filterActivation if true enable activation filter
     * @param pm persistence manager
     * @return user object or null if no user with specified login found
     * @throws TechnicalException
     */
    User findUserByLogin(String login, boolean filterActivation, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves users by role
     * @param role role
     * @param filterActivation if true enable activation filter
     * @param pm persistence manager
     * @return user list
     * @throws TechnicalException
     */
    List<User> findUserByRole(Role role, boolean filterActivation, PersistenceManager pm) throws TechnicalException;
    
    /**
     * Retrieves user count
     * @return user count
     * @param pm persistence manager
     * @throws TechnicalException
     */
    int count(PersistenceManager pm) throws TechnicalException;
    
    /**
     * Delete user
     * @param user user to delete
     * @param pm persistence manager
     * @throws TechnicalException
     */
    void delete(User user, PersistenceManager pm) throws TechnicalException;
}
