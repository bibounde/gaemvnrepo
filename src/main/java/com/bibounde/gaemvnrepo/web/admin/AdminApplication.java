package com.bibounde.gaemvnrepo.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.shared.domain.Role;
import com.bibounde.gaemvnrepo.shared.domain.authentication.AuthenticatedUserInfo;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserId;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListQuery;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.history.Historizable;
import com.bibounde.gaemvnrepo.shared.service.UserService;
import com.bibounde.gaemvnrepo.web.admin.detail.home.HomeView;
import com.bibounde.gaemvnrepo.web.admin.detail.profile.ProfileView;
import com.bibounde.gaemvnrepo.web.admin.detail.repository.BrowseRepositoryView;
import com.bibounde.gaemvnrepo.web.admin.detail.system.SystemConfigurationView;
import com.bibounde.gaemvnrepo.web.admin.detail.user.BrowserUserView;
import com.bibounde.gaemvnrepo.web.admin.detail.user.BrowserUserViewHelper;
import com.bibounde.gaemvnrepo.web.admin.detail.user.UserEditView;
import com.bibounde.gaemvnrepo.web.admin.detail.user.UserEditViewHelper;
import com.bibounde.gaemvnrepo.web.admin.history.History;
import com.bibounde.gaemvnrepo.web.admin.history.HistoryChangeListener;
import com.bibounde.gaemvnrepo.web.admin.navigation.NavigationMenu;
import com.bibounde.gaemvnrepo.web.admin.navigation.NavigationMenuModel;
import com.bibounde.gaemvnrepo.web.admin.navigation.NavigationMenuModel.MenuItem;
import com.bibounde.gaemvnrepo.web.admin.statusbar.StatusBar;
import com.bibounde.gaemvnrepo.web.admin.statusbar.StatusBarModel;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminApplication extends Application implements Controller, HistoryChangeListener {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AdminApplication.class);

    private static final int DEFAULT_DETAIL_VIEW_SELECTED_INDEX = 4;
    private Map<String, View> viewMap = new HashMap<String, View>();
    private List<String> detailViewIds = new ArrayList<String>();
    private int currentDetailViewIndex = -1;

    @Autowired
    private UserService userService;

    public WebApplicationContext appContext;
    private MainPage mainPage;
    private Window window;

    private UriFragmentUtility uriFragmentUtility;
    private BrowserUserViewHelper browserViewHelper = new BrowserUserViewHelper();
    private UserEditViewHelper userEditViewHelper = new UserEditViewHelper();

    @Override
    public void init() {
        this.setTheme("gaemvnrepo");
        
        try {
            AuthenticatedUserInfo userInfo = this.userService.getAuthenticatedUserInfo();

            this.setLocale(userInfo.locale);

            this.mainPage = new MainPage(this);
            mainPage.setSizeFull();

            this.window = new Window(Messages.INSTANCE.getString("AdminApplication.title", userInfo.locale));
            ((VerticalLayout) window.getContent()).setMargin(false);
            ((VerticalLayout) window.getContent()).setSizeFull();

            window.addComponent(mainPage);
            ((VerticalLayout) window.getContent()).setExpandRatio(mainPage, 1.0f);

            logger.debug("Main layout initialized. Starts fragment utility");

            this.uriFragmentUtility = new UriFragmentUtility();
            this.uriFragmentUtility.addListener(new FragmentChangedListener() {

                @Override
                public void fragmentChanged(FragmentChangedEvent source) {
                    String fragment = source.getUriFragmentUtility().getFragment();
                    if (fragment != null) {
                        AdminApplication.this.historyChanged(History.decode(fragment));
                    }

                }
            });
            window.addComponent(this.uriFragmentUtility);
            logger.debug("Fragment utility added to window");

            setMainWindow(window);
            setLogoutURL("/j_spring_security_logout");

            logger.debug("Starts authenticated user info initialization");
            this.initAuthenticatedUserInfo(userInfo);
            logger.debug("Authenticated user info initialization done");
        } catch (TechnicalException e) {
            throw new RuntimeException("Unable to initialize application", e);
        }

    }

    @Override
    public void add(View view) {
        logger.debug("Adds view {}", view.getId());
        this.viewMap.put(view.getId(), view);
        view.setController(this);
    }

    @Override
    public void removeView(View view) {
        logger.debug("Removes view {}", view.getId());
        this.viewMap.remove(view.getId());
    }

    private void initAuthenticatedUserInfo(AuthenticatedUserInfo info) {
        StatusBarModel statusBarModel = (StatusBarModel) this.viewMap.get(StatusBar.ID).getModel();
        statusBarModel.setGravaterUrl(info.gravatarUrl);
        statusBarModel.setLogin(info.login);

        // Init menu items
        NavigationMenuModel navigationMenuModel = (NavigationMenuModel) this.viewMap.get(NavigationMenu.ID).getModel();
        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(MenuItem.HOME_ITEM);
        this.detailViewIds.add(HomeView.ID);

        menuItems.add(MenuItem.BROWSE_REPOSITORY_ITEM);
        this.detailViewIds.add(BrowseRepositoryView.ID);

        if (info.role == Role.ADMIN) {
            menuItems.add(MenuItem.SYSTEM_CONFIGURATION_ITEM);
            this.detailViewIds.add(SystemConfigurationView.ID);

            menuItems.add(MenuItem.BROWSE_USER_ITEM);
            this.detailViewIds.add(BrowserUserView.ID);

            menuItems.add(MenuItem.USER_EDIT_ITEM);
            this.detailViewIds.add(UserEditView.ID);

        } else if (info.role == Role.MANAGER) {

            menuItems.add(MenuItem.BROWSE_USER_ITEM);
            this.detailViewIds.add(BrowserUserView.ID);

            menuItems.add(MenuItem.USER_EDIT_ITEM);
            this.detailViewIds.add(UserEditView.ID);
        }
        menuItems.add(MenuItem.PROFILE_ITEM);
        this.detailViewIds.add(ProfileView.ID);
        navigationMenuModel.setMenuItems(menuItems);

        logger.debug("Status bar and nvigation menu initialized. Display home");
        this.displayHome();
    }

    public void setWebApplicationContext(WebApplicationContext appContext) {
        this.appContext = appContext;
    }

    private void displayHome() {
        this.performAction(NavigationMenu.ID, NavigationMenu.ACTION_SELECTION, new Object[] { DEFAULT_DETAIL_VIEW_SELECTED_INDEX }, false);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.performAction(event.getSourceId(), event.getAction(), event.getParams(), true);
    }

    private void performAction(String viewId, String action, Object[] actionParams, boolean addHistory) {
        try {
            History history = null;

            if (StatusBar.ID.equals(viewId)) {
                if (StatusBar.ACTION_LOGOUT.equals(action)) {
                    getMainWindow().getApplication().close();
                }
            } else if (NavigationMenu.ID.equals(viewId)) {
                if (NavigationMenu.ACTION_SELECTION.equals(action)) {
                    // Change view
                    int index = (Integer) actionParams[0];
                    View detailView = this.changeDetailView(index);

                    history = new History();
                    history.setAction(HistoryAction.ACTION_SHOW_DETAIL_VIEW);
                    history.setViewId(detailView.getId());
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(HistoryAction.KEY_INDEX, String.valueOf(index));
                    history.setParams(params);

                    if (BrowserUserView.ID.equals(detailView.getId())) {
                        this.browserViewHelper.initView();
                    } else if (UserEditView.ID.equals(detailView.getId())) {
                        this.userEditViewHelper.initView();
                    } 
                }
            } else if (BrowserUserView.ID.equals(viewId)) {
                if (BrowserUserView.ACTION_PAGE_CHANGED.equals(action)) {
                    int page = (Integer) actionParams[0];
                    logger.debug("Page {} of {} selected", page, BrowserUserView.ID);
                    
                    Historizable historyItem = this.browserViewHelper.pageChanged(page, this.userService);
                    history = new History();
                    history.setAction(HistoryAction.ACTION_CHANGE_USER_BROWSER_INFO);
                    history.setViewId(BrowserUserView.ID);
                    history.setParams(historyItem.encode());
                    
                } else if (BrowserUserView.ACTION_SORT_CHANGED.equals(action)) {
                    String column = (String) actionParams[0];
                    boolean ascending = (Boolean) actionParams[1];
                    logger.debug("Sort action selected on column {} (ascending : {}) for {}", new Object[]{column, ascending, BrowserUserView.ID});
                    
                    Historizable historyItem = this.browserViewHelper.sortChanged(column, ascending, this.userService);
                    history = new History();
                    history.setAction(HistoryAction.ACTION_CHANGE_USER_BROWSER_INFO);
                    history.setViewId(BrowserUserView.ID);
                    history.setParams(historyItem.encode());
                } else if (BrowserUserView.ACTION_USER_SELECTED.equals(action)) {
                    //Changes view
                    this.changeDetailView(this.getDetailViewIndex(UserEditView.ID));
                    long id = (Long) actionParams[0];
                    logger.debug("User {} selected for {}", id, BrowserUserView.ID);
                    Historizable historyItem = this.userEditViewHelper.userSelected(id, this.userService);
                    history = new History();
                    history.setAction(HistoryAction.ACTION_SELECT_USER);
                    history.setViewId(UserEditView.ID);
                    history.setParams(historyItem.encode());
                } else {
                    logger.warn("{} action is not supported for {}", action, BrowserUserView.ID);
                }
            } else if (UserEditView.ID.equals(viewId)) {
                Historizable historyItem = null;
                if (UserEditView.ACTION_SAVED.equals(action)) {
                    UserEditQuery user = (UserEditQuery) actionParams[0];
                    logger.debug("User {} saved", user.login);
                    historyItem = this.userEditViewHelper.userSaved(user, this.userService);
                } else if (UserEditView.ACTION_DELETED.equals(action)) {
                    long id = (Long) actionParams[0];
                    logger.debug("User {} deleted", id);
                    if (this.userEditViewHelper.userDeleted(id, this.userService)) {
                        //Go to Browser view
                        this.performAction(NavigationMenu.ID, NavigationMenu.ACTION_SELECTION, new Object[]{this.getDetailViewIndex(BrowserUserView.ID)}, true);
                    }
                } else if (UserEditView.ACTION_ACTIVATION.equals(action)) {
                    long id = (Long) actionParams[0];
                    logger.debug("User {} activation need to change", id);
                    //No history
                    this.userEditViewHelper.userActivationChanged(id, this.userService);
                    
                } else {
                    logger.warn("{} action is not supported for {}", action, UserEditView.ID);
                }
                if (historyItem != null) {
                    history = new History();
                    history.setAction(HistoryAction.ACTION_SELECT_USER);
                    history.setViewId(UserEditView.ID);
                    history.setParams(historyItem.encode());
                }
            }

            if (history != null && addHistory) {
                logger.debug("Adds history action {} in history pool", history.getAction());
                this.uriFragmentUtility.setFragment(History.encode(history), false);
            }
        } catch (Exception e) {
            this.notifyError(e);
            logger.error("Unable to perform action " + action + " on " + viewId, e);
        }
    }

    /**
     * Performs action from history
     * 
     * @param history
     */
    private void performHistoryAction(History history) {
        logger.debug("Performs history action {}", history.getAction());
        if (history.getAction().equals(HistoryAction.NO_ACTION)) {
            //
        }
        try {
            // Actions are linked with view. Need to change view if this one is
            // not displayed
            String currentViewId = this.detailViewIds.get(this.currentDetailViewIndex);

            if (currentViewId != history.getViewId()) {
                logger.debug("History detail view and current detail view are differents. Need to change detail view");
                this.changeDetailView(this.getDetailViewIndex(history.getViewId()));
            }

            if (HistoryAction.ACTION_SHOW_DETAIL_VIEW.equals(history.getAction())) {
                // Not necessary to change view with index. Already done.
                if (BrowserUserView.ID.equals(history.getViewId())){
                    this.browserViewHelper.initView();
                } else if (UserEditView.ID.equals(history.getViewId())){
                    this.userEditViewHelper.initView();
                } else {
                    logger.debug("No specific history action for {}", history.getViewId());
                }
            } else if (HistoryAction.ACTION_CHANGE_USER_BROWSER_INFO.equals(history.getAction())) {
               // Not necessary to change view with index. Already done.
               UserListQuery query = new UserListQuery();
               query.load(history.getParams());
               this.browserViewHelper.refreshInfo(query, this.userService);
            } else if (HistoryAction.ACTION_SELECT_USER.equals(history.getAction())) {
                // Not necessary to change view with index. Already done.
                UserId userId = new UserId();
                userId.load(history.getParams());
                this.userEditViewHelper.userSelected(userId.id, this.userService);
             } else {
                logger.warn("Action {} is not supported in history", history.getAction());
            }
        } catch (Exception e) {
            this.notifyError(e);
            logger.error("Unable to perform history action " + history.getAction() + " on " + history.getViewId(), e);
        }
    }

    /**
     * Changes detail view if needed
     * 
     * @param indexView
     * @return displayed view of detail panel
     */
    private View changeDetailView(int viewIndex) {
        logger.debug("Changes view from {} to {}", this.currentDetailViewIndex, viewIndex);
        if (this.currentDetailViewIndex < 0 || this.currentDetailViewIndex != viewIndex) {
            ((NavigationMenuModel) this.viewMap.get(NavigationMenu.ID).getModel()).setSelectedIndex(viewIndex);
            if (this.currentDetailViewIndex >= 0) {
                String oldViewId = this.detailViewIds.get(this.currentDetailViewIndex);
                logger.debug("Removes {} from detail panel", oldViewId);
                this.viewMap.remove(oldViewId);
            }
            this.currentDetailViewIndex = viewIndex;

            View view = null;
            String newViewId = this.detailViewIds.get(viewIndex);

            if (HomeView.ID.equals(newViewId)) {
                view = new HomeView();
            } else if (BrowseRepositoryView.ID.equals(newViewId)) {
                view = new BrowseRepositoryView();
            } else if (SystemConfigurationView.ID.equals(newViewId)) {
                view = new SystemConfigurationView();
            } else if (BrowserUserView.ID.equals(newViewId)) {
                view = new BrowserUserView(ConfigurationUtil.INSTANCE.getUserTablePageSize());
                this.browserViewHelper.setView((BrowserUserView) view);
            } else if (UserEditView.ID.equals(newViewId)) {
                view = new UserEditView();
                this.userEditViewHelper.setView((UserEditView) view);
            } else if (ProfileView.ID.equals(newViewId)) {
                view = new ProfileView();
            } else {
                throw new UnsupportedOperationException(newViewId + " is not supported yet");
            }

            logger.debug("Adds {} in detail panel", newViewId);
            this.viewMap.put(newViewId, view);
            view.setController(this);
            this.mainPage.showView(view);

            return view;
        } else {
            return this.viewMap.get(this.detailViewIds.get(this.currentDetailViewIndex));
        }
    }

    private void notifyError(Exception e) {
        String title = null;
        if (e instanceof TechnicalException) {
            title = Messages.INSTANCE.getString("AdminApplication.technicalerror", this.getLocale());
        } else if (e instanceof BusinessException) {
            title = Messages.INSTANCE.getString("AdminApplication.businesserror", this.getLocale());;
        } else {
            title = Messages.INSTANCE.getString("AdminApplication.unexpectederror", this.getLocale());;
        }
        this.window.showNotification(title, e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
    }

    @Override
    public void historyChanged(History history) {
        logger.debug("History changed ({})", history == null ? "N/A" : history.getAction());
        if (history != null) {
            this.performHistoryAction(history);
        } else {
            this.displayHome();
        }
    }

    private int getDetailViewIndex(String viewId) {
        for (int i = 0; i < this.detailViewIds.size(); i++) {
            if (this.detailViewIds.get(i).equals(viewId)) {
                return i;
            }
        }
        return 0;
    }
}
