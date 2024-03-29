package com.bibounde.gaemvnrepo.shared.service;

import java.io.Serializable;

import org.springframework.security.access.prepost.PreAuthorize;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.shared.domain.authentication.AuthenticatedUserInfo;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListResponse;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface UserService extends Serializable {
    
    /**
     * Creates a user
     * @param user user to create
     * @throws TechnicalException
     */
    void createUser(User user) throws TechnicalException;
    
    /**
     * Retrieves user by email. 
     * @param email user's email
     * @return user object or null if no user with specified email found
     * @throws TechnicalException
     */
    User findUserActiveByEmail(String email) throws TechnicalException;
    
    /**
     * Retrieves user by login
     * @param login user's login
     * @return user object or null if no user with specified login found
     * @throws TechnicalException
     */
    UserEditResponse findUserByLogin(String login) throws TechnicalException;
    
    /**
     * Retrieves user by id
     * @param id user's id
     * @return user object or null if no user with specified id found
     * @throws TechnicalException
     */
    UserEditResponse findUserById(long login) throws TechnicalException;
    
    /**
     * Create user admin
     * @throws TechnicalException
     */
    void createUserAdmin() throws TechnicalException;
    
    /**
     * Returns true if admin user exists
     * @return true if admin user exists
     * @throws TechnicalException
     */
    boolean userAdminExists() throws TechnicalException;
    
    /**
     * Retrieves authenticated user info
     * @return authenticated user info
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("isFullyAuthenticated()")
    AuthenticatedUserInfo getAuthenticatedUserInfo() throws TechnicalException;
    
    /**
     * Retrieves users
     * @param query user query
     * @return paginated user list
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    UserListResponse getUsers(UserListQuery query) throws TechnicalException, BusinessException;
    
    /**
     * Saves user
     * @param userToSave user to save
     * @param edition true means that user is already in db
     * @return user
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    UserEditResponse saveUser(UserEditQuery userToSave, boolean edition) throws TechnicalException, BusinessException;
    
    /**
     * Deletes user 
     * @param id user's id
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    void deleteUser(long id) throws TechnicalException, BusinessException;
    
    /**
     * Set activation status of specified user 
     * @param id user's id
     * @param active true means active
     * @throws TechnicalException
     * @throws BusinessException
     */
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    void setActiveUser(long id, boolean active) throws TechnicalException, BusinessException;
}
