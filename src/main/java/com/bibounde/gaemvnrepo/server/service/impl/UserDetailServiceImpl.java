package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.PersistenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.server.PMF;
import com.bibounde.gaemvnrepo.server.dao.UserDao;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    
    @Autowired
    private UserDao userDao;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        logger.debug("Tries to login with {}", username);
        
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Persistence initialization failed", e);
        }
        
        try {
            
            com.bibounde.gaemvnrepo.model.User user = this.userDao.findUserByLogin(username, true, pm);
            
            if (user == null) {
                throw new UsernameNotFoundException(username + "does not exist or is not active");
            }
            return this.createUserDetails(user);
        } catch (TechnicalException e) {
            throw new UsernameNotFoundException("Technical exception occurs", e);
        } finally {
            pm.close();
        }
    }
    
    private UserDetails createUserDetails(com.bibounde.gaemvnrepo.model.User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl(user.getRole().name().toLowerCase()));
        return new User(user.getLogin(), user.getPassword(), true, true, true, true, authorities);
    }

}
