package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.shared.domain.user.UserListQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserSort;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.history.Historizable;
import com.bibounde.gaemvnrepo.shared.service.UserService;

public class BrowserViewHelper implements Serializable {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(BrowserViewHelper.class);

    private BrowserUserView view;    
    
    public void setView(BrowserUserView view) {
        this.view = view;
    }
    
    private BrowserUserModel getModel() {
        this.checkState();
        return (BrowserUserModel) this.view.getModel();
    }
    
    public void initView() {
        this.checkState();
        logger.debug("Initializes {} data", view.getId());
        this.getModel().setUserList(null);
    }
    
    public Historizable pageChanged(int page, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("Page changed {}", page);
        
        UserListResponse currentUserList = this.getModel().getUserList();
        UserListQuery query = new UserListQuery();
        query.page = page;
        query.pageSize = currentUserList.pageSize;
        query.ascending = currentUserList.ascending;
        query.sort = currentUserList.sort;
        
        this.getModel().setUserList(userService.getUsers(query));
        
        return query;
    }
    
    public Historizable sortChanged(String column, boolean ascending, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        logger.debug("Sort data changed (column: {}, ascending : {}", column, ascending);
        
        UserListResponse currentValues = this.getModel().getUserList();
        UserListQuery query = null;
        if (currentValues == null) {
            query = this.getInitialUserListQuery();
        } else {
            query = new UserListQuery();
            query.page = currentValues.currentPage;
            query.pageSize = currentValues.pageSize;
            query.ascending = ascending;
            query.sort = UserSort.valueOf(column);
        }
        
        this.getModel().setUserList(userService.getUsers(query));
        
        return query;
    }
    
    public void refreshInfo(UserListQuery query, UserService userService) throws TechnicalException, BusinessException {
        this.checkState();
        this.getModel().setUserList(userService.getUsers(query));
    }
    
    private void checkState() {
        if (this.view == null) {
            throw new IllegalStateException(BrowserUserView.class.getSimpleName()  + " must be initialized");
        }
    }
    
    private UserListQuery getInitialUserListQuery() {
        UserListQuery query = new UserListQuery();
        query.page = 0;
        query.pageSize = this.view.getPageSize();
        query.sort = UserSort.CREATED;
        query.ascending = false;
        return query;
    }
}
