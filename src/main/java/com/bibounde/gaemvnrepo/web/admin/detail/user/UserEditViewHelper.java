package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserLogin;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.history.Historizable;
import com.bibounde.gaemvnrepo.shared.service.UserService;

public class UserEditViewHelper implements Serializable {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserEditViewHelper.class);

    private UserEditView view;    
    
    public void setView(UserEditView view) {
        this.view = view;
    }
    
    private UserEditModel getModel() {
        this.checkState();
        return (UserEditModel) this.view.getModel();
    }
    
    public void initView() {
        this.checkState();
        logger.debug("Initializes {} data", view.getId());
        this.getModel().initUser(null);
    }
    
    public void userSelected(UserLogin userLogin, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("User {} selected", userLogin);
        UserEditResponse user = userService.findUserByLogin(userLogin.login);
        
        if (user == null) {
            throw new BusinessException("User with login " + userLogin.login + " does not exist");
        }
        
        this.getModel().initUser(user);
    }
    
    public Historizable userSaved(UserEditQuery userEdit, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("User saved {}", userEdit.login);
        
        UserEditResponse saved = userService.save(userEdit, this.getModel().isEditionMode());
        this.getModel().saveUser(saved);
        
        UserLogin ret = new UserLogin();
        ret.login = saved.login;
        return ret; 
    }
    
    private void checkState() {
        if (this.view == null) {
            throw new IllegalStateException(UserEditView.class.getSimpleName()  + " must be initialized");
        }
    }
}
