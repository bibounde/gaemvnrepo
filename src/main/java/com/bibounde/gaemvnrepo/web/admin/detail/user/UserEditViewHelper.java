package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserId;
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
    
    public Historizable userSelected(long userId, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("User {} selected", userId);
        UserEditResponse user = userService.findUserById(userId);
        
        if (user == null) {
            logger.debug("User {} does not exist");
            this.getModel().initUser(null);
            return null;
        } else {
            this.getModel().initUser(user);
            
            UserId ret = new UserId();
            ret.id = userId;
            
            return ret;
        }
    }
    
    public Historizable userSaved(UserEditQuery userEdit, UserService userService) {
        this.checkState();
        logger.debug("User saved {}", userEdit.login);
        
        UserEditResponse saved;
        try {
            saved = userService.saveUser(userEdit, this.getModel().isEditionMode());
            this.getModel().saveUser(saved);
            
            UserId ret = new UserId();
            ret.id = saved.id;
            return ret;
        } catch (Exception e) {
            logger.error("Unable to save user", e);
            this.view.showSavingError();
        }
        return null;
    }
    
    /**
     * Performs delete
     * @param id user id
     * @param userService user service
     * @return true for successful delete
     */
    public boolean userDeleted(long id, UserService userService)  {
        this.checkState();
        logger.debug("User deleted {}", id);
        try {
            userService.deleteUser(id);
            return true;
        } catch (Exception e) {
            logger.error("Unable to delete user", e);
            this.view.showDeleteError();
            return false;
        }
    }
    
    public void userActivationChanged(long id, UserService userService) {
        this.checkState();
        boolean newActiveStatus = !this.getModel().getUser().active;
        logger.debug("User {} must be {}", id, newActiveStatus ? "unlocked" : "locked");
        try {
            userService.setActiveUser(id, newActiveStatus);
            this.getModel().activeUser(newActiveStatus);
        } catch (Exception e) {
            logger.error("Unable to delete user", e);
            if (newActiveStatus) {
                this.view.showUnlockError();
            } else {
                this.view.showLockError();
            }
        }
    }
    
    private void checkState() {
        if (this.view == null) {
            throw new IllegalStateException(UserEditView.class.getSimpleName()  + " must be initialized");
        }
    }
}
