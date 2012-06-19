package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListItem;
import com.bibounde.gaemvnrepo.shared.domain.user.UserListResponse;
import com.bibounde.gaemvnrepo.shared.domain.user.UserSort;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.admin.pagination.PaginatedTable;
import com.bibounde.gaemvnrepo.web.admin.pagination.PaginationBar;
import com.bibounde.gaemvnrepo.web.admin.pagination.PaginationListener;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class BrowserUserView extends VerticalLayout implements View, PaginationListener {

    public static final String ID = BrowserUserView.class.getName();
    
    public static  final String ACTION_PAGE_CHANGED = BrowserUserView.class.getName() + ".action.page.changed";
    public static  final String ACTION_SORT_CHANGED = BrowserUserView.class.getName() + ".action.sort.changed";
    public static  final String ACTION_USER_SELECTED = BrowserUserView.class.getName() + ".action.user.selected";
    
    private static final String TABLE_LOGIN_PROPERTY = UserSort.LOGIN.name();
    private static final String TABLE_EMAIL_PROPERTY = UserSort.EMAIL.name();
    private static final String TABLE_ADMINISTRATOR_PROPERTY = UserSort.ADMINISTRATOR.name();
    private static final String TABLE_LOCKED_PROPERTY = UserSort.LOCKED.name();
    private static final String TABLE_CREATED_PROPERTY = UserSort.CREATED.name();

    private BrowserUserModel model;
    private Controller controller;
    private PaginatedTable table;
    private PaginationBar paginationBar;

    public BrowserUserView(int pageSize) {
        this.model = new BrowserUserModel();
        this.model.addModelEventListener(this);
        
        this.table = new PaginatedTable(pageSize) {
            
            @Override
            public void doSort() {
                ActionEvent event = new ActionEvent(ID, ACTION_SORT_CHANGED, table.getSortContainerPropertyId(), table.isSortAscending());
                controller.actionPerformed(event);
            }
            
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (colId.equals(TABLE_CREATED_PROPERTY) && property.getValue() != null) {
                    return new SimpleDateFormat("d MMM yyyy").format(property.getValue());
                }
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        
        this.table.setSelectable(true);
        
        this.table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);

        this.table.addContainerProperty(TABLE_LOGIN_PROPERTY, String.class, null);
        this.table.addContainerProperty(TABLE_EMAIL_PROPERTY, String.class, null);
        this.table.addContainerProperty(TABLE_ADMINISTRATOR_PROPERTY, Boolean.class, null);
        this.table.addContainerProperty(TABLE_LOCKED_PROPERTY, Boolean.class, null);
        this.table.addContainerProperty(TABLE_CREATED_PROPERTY, Date.class, null);

        this.table.setColumnExpandRatio(TABLE_LOGIN_PROPERTY, 0.2f);
        this.table.setColumnExpandRatio(TABLE_EMAIL_PROPERTY, 0.35f);
        this.table.setColumnExpandRatio(TABLE_ADMINISTRATOR_PROPERTY, 0.15f);
        this.table.setColumnExpandRatio(TABLE_LOCKED_PROPERTY, 0.15f);
        this.table.setColumnExpandRatio(TABLE_CREATED_PROPERTY, 0.15f);

        this.table.setColumnAlignment(TABLE_ADMINISTRATOR_PROPERTY, Table.ALIGN_CENTER);
        this.table.setColumnAlignment(TABLE_LOCKED_PROPERTY, Table.ALIGN_CENTER);

        this.table.addGeneratedColumn(TABLE_ADMINISTRATOR_PROPERTY, this.createAdministratorColumnGenerator());
        this.table.addGeneratedColumn(TABLE_LOCKED_PROPERTY, this.createActiveColumnGenerator());

        this.table.setColumnHeaders(new String[] {Messages.INSTANCE.getString("BrowserUserView.login", this.getLocale()),
                Messages.INSTANCE.getString("BrowserUserView.email", this.getLocale()),
                Messages.INSTANCE.getString("BrowserUserView.administrator", this.getLocale()),
                Messages.INSTANCE.getString("BrowserUserView.locked", this.getLocale()),
                Messages.INSTANCE.getString("BrowserUserView.created", this.getLocale())});
        
        this.table.setImmediate(true);
        this.table.addListener(new Property.ValueChangeListener() {
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_USER_SELECTED, event.getProperty().getValue());
                controller.actionPerformed(actionEvent);
            }
        });

        this.paginationBar = new PaginationBar();
        this.paginationBar.addPaginationListener(this);
        
        
        this.initLayout();
    }

    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("BrowserUserView.title", this.getLocale()), "/static/icons/browse-user-32.png"));

        this.addComponent(this.table);
        this.setExpandRatio(this.table, 1.0f);
        this.table.setSizeFull();
        
        this.addComponent(this.paginationBar);
        this.setComponentAlignment(this.paginationBar, Alignment.MIDDLE_LEFT);
    }

    @Override
    public void modelChanged(ModelEvent event) {
        if (BrowserUserModel.USER_LIST_INITIALIZED.equals(event.getType())) {
            this.table.showFirstPage();
        } else if (BrowserUserModel.USER_LIST_CHANGED.equals(event.getType())) {
            this.table.removeAllItems();

            UserListResponse userList = this.model.getUserList();
            
            for (UserListItem user : userList.users) {
                Item item = this.table.addItem(user.id);

                this.table.setItemIcon(user.id, new ExternalResource(user.gravatarURL));
                item.getItemProperty(TABLE_LOGIN_PROPERTY).setValue(user.login);
                item.getItemProperty(TABLE_EMAIL_PROPERTY).setValue(user.email);
                item.getItemProperty(TABLE_ADMINISTRATOR_PROPERTY).setValue(user.administrator);
                item.getItemProperty(TABLE_LOCKED_PROPERTY).setValue(user.active);
                item.getItemProperty(TABLE_CREATED_PROPERTY).setValue(user.created);
            }
            
            this.table.refreshSortInfo(userList.sort.name(), userList.ascending);
            
            this.paginationBar.setPagination(userList.total, userList.currentPage, userList.pageSize);
            
        }
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    private ColumnGenerator createAdministratorColumnGenerator() {
        ColumnGenerator ret = new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Property property = source.getItem(itemId).getItemProperty(columnId);

                if (property.getType().equals(Boolean.class) && property.getValue() != null) {
                    boolean value = (Boolean) property.getValue();
                    if (value) {
                        Embedded ret = new Embedded();
                        ret.setSource(new ExternalResource("/static/icons/accept-16.png"));
                        return ret;
                    }
                }
                return new Label();
            }
        };
        return ret;
    }

    private ColumnGenerator createActiveColumnGenerator() {
        ColumnGenerator ret = new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Property property = source.getItem(itemId).getItemProperty(columnId);

                if (property.getType().equals(Boolean.class) && property.getValue() != null) {
                    boolean value = (Boolean) property.getValue();
                    if (!value) {
                        Embedded ret = new Embedded();
                        ret.setSource(new ExternalResource("/static/icons/locked-16.png"));
                        return ret;
                    }

                }
                return new Label();
            }
        };
        return ret;
    }

    @Override
    public void pageSelected(int page) {
        ActionEvent event = new ActionEvent(ID, ACTION_PAGE_CHANGED, page);
        this.controller.actionPerformed(event);
    }
    
    public int getPageSize() {
        return this.table.getPageSize();
    }
}
