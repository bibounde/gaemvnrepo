package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.model.User.Role;
import com.bibounde.gaemvnrepo.server.PMF;
import com.bibounde.gaemvnrepo.server.dao.UserDao;
import com.bibounde.gaemvnrepo.server.service.util.GravatarUtil;
import com.bibounde.gaemvnrepo.shared.domain.authentication.AuthenticatedUserInfo;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListItem;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListResponse;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;
    
    @Override
    public void createUser(User user) throws TechnicalException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            userDao.saveOrUpdate(user, pm);
            tx.commit();
        
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                    tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public User findUserActiveByEmail(String email) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        try {
            return this.userDao.findUserByEmail(email, true, pm);
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            pm.close();
        }
    }
    
    @Override
    public boolean userAdminExists() throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        try {
            return !this.userDao.findUserByRole(Role.ADMIN, true, pm).isEmpty();
        } finally {
            pm.close();
        }
    }

    @Override
    public void createUserAdmin() throws TechnicalException {
        User admin = new User();
        admin = new User();
        admin.setEmail("admin-gaemvnrepo@gmail.com");
        admin.setPassword("21232f297a57a5a743894a0e4a801fc3");
        admin.setLogin("admin");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        admin.setLocale("en_GB");
        this.createUser(admin);
    }

    @Override
    public AuthenticatedUserInfo getAuthenticatedUserInfo() throws TechnicalException {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        try {
            User user = this.userDao.findUserByLogin(login, true, pm);
            AuthenticatedUserInfo ret = new AuthenticatedUserInfo();
            ret.gravatarUrl = GravatarUtil.getGravatarUrl(user.getEmail());
            ret.login = user.getLogin();
            ret.role = com.bibounde.gaemvnrepo.shared.domain.Role.valueOf(user.getRole().name());
            String[] locale = user.getLocale().split("_");
            if (locale.length > 1) {
                ret.locale = new Locale(locale[0], locale[1]);
            } else {
                ret.locale = new Locale(locale[0]);
            }
            
            return ret;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            pm.close();
        }
    }

    @Override
    public UserListResponse getUsers(UserListQuery userQuery) throws TechnicalException, BusinessException {
        if (userQuery.page < 0 || userQuery.pageSize < 1) {
            throw new BusinessException("Page index and/or page size are invalid");
        }
        
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        Query query = null;
        try {
            query = pm.newQuery(User.class);
            
            int start = userQuery.page * userQuery.pageSize;
            int stop = start + userQuery.pageSize;
            
            String sortColumn = null;
            switch (userQuery.sort) {
            case ADMINISTRATOR:
                sortColumn = "role";
                break;
            case LOGIN:
                sortColumn = "login";
                break;
            case EMAIL:
                sortColumn = "email";
                break;
            case CREATED:
                sortColumn = "created";
                break;
            case LOCKED:
                sortColumn = "active";
                break;
            default:
                throw new BusinessException(userQuery.sort + " is not supported");
            }
            query.setOrdering(sortColumn + " " + (userQuery.ascending ? "asc" : "desc"));
            query.setRange(start, stop);
            List<User> users = (List<User>) query.execute();
            
            UserListResponse ret = new UserListResponse();
            ret.total = this.userDao.count(pm);
            ret.currentPage = userQuery.page;
            ret.pageSize = userQuery.pageSize;
            ret.ascending = userQuery.ascending;
            ret.sort = userQuery.sort;
            ret.users = new ArrayList<UserListItem>();
            for (User user : users) {
                UserListItem item = new UserListItem();
                item.id = user.getId();
                item.active = user.isActive();
                item.administrator = Role.ADMIN == user.getRole();
                item.created = new Date(user.getCreated());
                item.email = user.getEmail();
                item.gravatarURL = GravatarUtil.getGravatarUrl(user.getEmail(), 16);
                item.login = user.getLogin();
                
                ret.users.add(item);
            }
            
            return ret;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            if (query != null) {
                query.closeAll();
            }
            pm.close();
        }
    }

    @Override
    public UserEditResponse saveUser(UserEditQuery userToSave, boolean edition) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            
            User user = null;
            if (edition) {
                user = this.userDao.findUserByLogin(userToSave.login, false, pm);
                if (user == null) {
                    throw new BusinessException("User with login " + userToSave.login + " does not exist");
                }
            } else {
                user = new User();
                user.setLogin(userToSave.login);
            }
            
            user.setActive(true);
            user.setEmail(userToSave.email);
            user.setLocale(userToSave.locale);
            user.setRole(Role.valueOf(userToSave.role.name()));
            
            if (userToSave.password != null && !userToSave.password.isEmpty()) {
                user.setPassword(new Md5PasswordEncoder().encodePassword(userToSave.password, null));
            }
            
            User savedUser = userDao.saveOrUpdate(user, pm);
            
            UserEditResponse ret = this.createUserEditResponse(savedUser);
            tx.commit();
        
            return ret;
        } catch (BusinessException e) {
            throw e;
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                    tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public UserEditResponse findUserByLogin(String login) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        try {
            User user = this.userDao.findUserByLogin(login, false, pm);
            return user != null ? this.createUserEditResponse(user) : null;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            pm.close();
        }
    }
    
    @Override
    public UserEditResponse findUserById(long login) throws TechnicalException {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        
        try {
            User user = this.userDao.findUserById(login, false, pm);
            return user != null ? this.createUserEditResponse(user) : null;
        } catch (Exception e) {
            throw new TechnicalException("Unable to execute query", e);
        } finally {
            pm.close();
        }
    }
    
    private UserEditResponse createUserEditResponse(User user) {
        UserEditResponse ret = new UserEditResponse();
        ret.id = user.getId();
        ret.role = com.bibounde.gaemvnrepo.shared.domain.Role.valueOf(user.getRole().name());
        ret.email = user.getEmail();
        ret.locale = user.getLocale();
        ret.login = user.getLogin();
        ret.active = user.isActive();
        
        return ret;
    }

    @Override
    public void deleteUser(long id) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            User toDelete = this.userDao.findUserById(id, false, pm);
            if (toDelete != null) {
                if (toDelete.getLogin().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    throw new BusinessException("Unable to delete authenticated user");
                }
                this.userDao.delete(toDelete, pm);
            }
            
            tx.commit();
        
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                    tx.rollback();
            }
            pm.close();
        }
    }

    @Override
    public void setActiveUser(long id, boolean active) throws TechnicalException, BusinessException {
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }

        try {
            tx.begin();
            User toChange = this.userDao.findUserById(id, false, pm);
            if (toChange != null) {
                if (toChange.getLogin().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                    throw new BusinessException("Unable to lock authenticated user");
                }
                toChange.setActive(active);
            }
            
            tx.commit();
        
        } catch (TechnicalException e) {
            throw e;
        } catch (Exception e) {
            throw new TechnicalException("Unable to persist", e);
        } finally {
            if (tx != null && tx.isActive()) {
                    tx.rollback();
            }
            pm.close();
        }
    }
}
