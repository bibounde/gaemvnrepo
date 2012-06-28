package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.Arrays;

import com.bibounde.gaemvnrepo.shared.domain.Role;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.web.ApplicationData;
import com.bibounde.gaemvnrepo.web.ResourceBundleUtil;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
import com.bibounde.gaemvnrepo.web.admin.tools.ConfirmationDialogUtil;
import com.bibounde.gaemvnrepo.web.admin.tools.ConfirmationDialogUtil.ConfirmationDialogListener;
import com.bibounde.gaemvnrepo.web.mvc.ActionEvent;
import com.bibounde.gaemvnrepo.web.mvc.Controller;
import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.View;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class UserEditView extends VerticalLayout implements View {

    public static final String ACTION_SAVED = UserEditView.class.getName() + "action.saved";
    public static final String ACTION_DELETED = UserEditView.class.getName() + "action.deleted";
    public static final String ACTION_ACTIVATION = UserEditView.class.getName() + "action.activation";
    
    public static final String ID = UserEditView.class.getName();
    
    private UserEditModel model;
    private Controller controller;
    private Form profileForm, authenticationForm;
    private UserEditProfileData userProfileFormData;
    private UsereditAuthData userAuthData;
    private NativeButton saveButton;
    private Button deleteButton, activationButton;
    private Label statusLabel, statusIcon;
    private HeaderComponent header;
    
    
    public UserEditView() {
        this.model = new UserEditModel();
        this.model.addModelEventListener(this);
        
        this.profileForm = new Form();
        this.profileForm.setCaption(ApplicationData.getMessage("UserEditView.profile"));
        this.profileForm.setFormFieldFactory(new ProfileFieldFactory());
        this.userProfileFormData = new UserEditProfileData();
        BeanItem<UserEditProfileData> personItem = new BeanItem<UserEditProfileData>(this.userProfileFormData);
        this.profileForm.setItemDataSource(personItem);
        this.profileForm.setInvalidCommitted(false);
        
        this.profileForm.setVisibleItemProperties(Arrays.asList(new String[] {
                UserEditProfileData.LOGIN_PROPERTY, UserEditProfileData.EMAIL_PROPERTY, UserEditProfileData.LOCALE_PROPERTY, UserEditProfileData.ROLE_PROPERTY,}));
        this.profileForm.getField(UserEditProfileData.LOGIN_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.login"));
        this.profileForm.getField(UserEditProfileData.EMAIL_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.email"));
        this.profileForm.getField(UserEditProfileData.LOCALE_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.lang"));
        this.profileForm.getField(UserEditProfileData.ROLE_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.role"));
        
        this.authenticationForm = new Form();
        this.authenticationForm.setCaption(ApplicationData.getMessage("UserEditView.authentication"));
        this.authenticationForm.setFormFieldFactory(new AuthFieldFactory());
        this.userAuthData = new UsereditAuthData();
        BeanItem<UsereditAuthData> userAuthItem = new BeanItem<UsereditAuthData>(this.userAuthData);
        this.authenticationForm.setItemDataSource(userAuthItem);
        this.authenticationForm.setInvalidCommitted(false);
        
        this.authenticationForm.setVisibleItemProperties(Arrays.asList(new String[] {
                UsereditAuthData.PASSWORD_PROPERTY, UsereditAuthData.REPASSWORD_PROPERTY}));
        this.authenticationForm.getField(UsereditAuthData.PASSWORD_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.password"));
        this.authenticationForm.getField(UsereditAuthData.REPASSWORD_PROPERTY).setCaption(ApplicationData.getMessage("UserEditView.confirmation"));
        
        this.saveButton = new NativeButton(ApplicationData.getMessage("UserEditView.save"));
        this.saveButton.addListener(new ClickListener() {
            
            @Override
            @edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception is managed by form")
            public void buttonClick(ClickEvent event) {
                try {
                    profileForm.commit();
                    if (!model.isEditionMode() || !((String) authenticationForm.getField(UsereditAuthData.PASSWORD_PROPERTY).getValue()).isEmpty()) {
                        authenticationForm.commit();
                    }
                    
                    UserEditQuery user = new UserEditQuery();
                    user.email = userProfileFormData.getEmail();
                    user.locale = userProfileFormData.getLocale();
                    user.role = userProfileFormData.getRole();
                    user.login = userProfileFormData.getLogin();
                    user.password = userAuthData.getPassword();
                    
                    
                    ActionEvent actionEvent = new ActionEvent(ID, ACTION_SAVED, user);
                    controller.actionPerformed(actionEvent);
                } catch (Exception e) {
                    // Ignored, we'll let the Form handle the errors
                }
            }
        });
        
        this.deleteButton = new Button(ApplicationData.getMessage("UserEditView.delete"));
        this.deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.deleteButton.setIcon(new ExternalResource("/static/icons/trash-16.png"));
        this.deleteButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                ConfirmationDialogListener confirmationListener = new ConfirmationDialogListener() {
                    @Override
                    public void onConfirmation() {
                        ActionEvent actionEvent = new ActionEvent(ID, ACTION_DELETED, model.getUser().id);
                        controller.actionPerformed(actionEvent);
                    }
                };
                ConfirmationDialogUtil.showConfirmationDialog(getWindow(), ApplicationData.getMessage("UserEditView.delete.action"), ApplicationData.getMessage("UserEditView.delete.message"), confirmationListener);
            }
        });
        
        this.activationButton = new Button();
        this.activationButton.setStyleName(BaseTheme.BUTTON_LINK);
        this.activationButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_ACTIVATION, model.getUser().id);
                controller.actionPerformed(actionEvent);
            }
        });
        
        this.statusIcon = new Label();
        this.statusIcon.setWidth(16, UNITS_PIXELS);
        this.statusLabel = new Label();
        
        this.header = new HeaderComponent("", "/static/icons/user-edit-32.png");
        
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(this.header);
        
        HorizontalLayout toolBarLayout = new HorizontalLayout();
        toolBarLayout.setWidth(100, UNITS_PERCENTAGE);
        toolBarLayout.setSpacing(true);
        this.addComponent(toolBarLayout);
        
        toolBarLayout.addComponent(this.statusIcon);
        toolBarLayout.addComponent(this.statusLabel);
        toolBarLayout.setExpandRatio(this.statusLabel, 1.0f);
        toolBarLayout.addComponent(this.activationButton);
        toolBarLayout.setComponentAlignment(this.activationButton, Alignment.MIDDLE_RIGHT);
        toolBarLayout.addComponent(this.deleteButton);
        toolBarLayout.setComponentAlignment(this.deleteButton, Alignment.MIDDLE_RIGHT);
        
        GridLayout detailTabLayout = new GridLayout(2, 2);
        //detailTabLayout.setMargin(new MarginInfo(false, true, true, false));
        detailTabLayout.setSpacing(true);
        detailTabLayout.setSizeFull();
        this.addComponent(detailTabLayout);
        this.setExpandRatio(detailTabLayout, 1.0f);
        
        this.profileForm.setWidth(100, UNITS_PERCENTAGE);
        this.profileForm.getLayout().addStyleName("gaemvnrepo-form");
        //this.profileForm.addStyleName("gaemvnrepo-form");
        this.profileForm.getLayout().setMargin(true);
        detailTabLayout.addComponent(this.profileForm, 0, 0);
        
        this.authenticationForm.setWidth(100, UNITS_PERCENTAGE);
        this.authenticationForm.getLayout().addStyleName("gaemvnrepo-form");
        //this.authenticationForm.addStyleName("gaemvnrepo-form");
        this.authenticationForm.getLayout().setMargin(true);
        detailTabLayout.addComponent(this.authenticationForm, 1, 0);
        //detailTabLayout.setRowExpandRatio(0, 0.7f);
        
        detailTabLayout.addComponent(this.saveButton, 1, 1);
        detailTabLayout.setComponentAlignment(this.saveButton, Alignment.TOP_RIGHT);
        detailTabLayout.setRowExpandRatio(1, 1.0f);
    }
    
    @Override
    public void modelChanged(ModelEvent event) {
        this.resetStatus();
        if ((UserEditModel.USER_CHANGED.equals(event.getType()) && this.model.isEditionMode()) || UserEditModel.USER_ACTIVATION_CHANGED.equals(event.getType()) || UserEditModel.USER_SAVED.equals(event.getType())) {
            UserEditResponse user = this.model.getUser();
            
            this.deleteButton.setVisible(true);
            this.activationButton.setVisible(true);
            
            if (this.model.getUser().active) {
                this.activationButton.setCaption(ApplicationData.getMessage("UserEditView.lock"));
                this.activationButton.setIcon(new ExternalResource("/static/icons/locked-16.png"));
            } else {
                this.activationButton.setCaption(ApplicationData.getMessage("UserEditView.unlock"));
                this.activationButton.setIcon(new ExternalResource("/static/icons/unlocked-16.png"));
            }
            
            this.header.setTitle(ApplicationData.getMessage("UserEditView.title") + " \u00bb " + user.login);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ROLE_PROPERTY).setValue(user.role);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.EMAIL_PROPERTY).setValue(user.email);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOCALE_PROPERTY).setValue(user.locale);
            
            Property loginProperty = this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOGIN_PROPERTY);
            loginProperty.setReadOnly(false);
            loginProperty.setValue(user.login);
            loginProperty.setReadOnly(true);
            
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.PASSWORD_PROPERTY).setValue("");
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.REPASSWORD_PROPERTY).setValue("");
            
            if (UserEditModel.USER_SAVED.equals(event.getType())) {
                this.setStatus(true, ApplicationData.getMessage("UserEditView.usersaved"));
            }
        } else {
            
            this.header.setTitle(ApplicationData.getMessage("UserEditView.title.new"));
            this.deleteButton.setVisible(false);
            this.activationButton.setVisible(false);
            
            this.resetStatus();
            
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ROLE_PROPERTY).setValue(null);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.EMAIL_PROPERTY).setValue("");
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOCALE_PROPERTY).setValue(null);
            
            Property loginProperty = this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOGIN_PROPERTY);
            loginProperty.setReadOnly(false);
            loginProperty.setValue("");
            
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.PASSWORD_PROPERTY).setValue("");
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.REPASSWORD_PROPERTY).setValue("");
        }
    }
    
    private void setStatus(boolean success, String message) {
        if (success) {
            this.statusIcon.setIcon(new ExternalResource("/static/icons/accept-16.png"));
            this.statusLabel.addStyleName("gaemvnrepo-notification-success");

        } else {
            this.statusIcon.setIcon(new ExternalResource("/static/icons/error-16.png"));
            this.statusLabel.addStyleName("gaemvnrepo-notification-error");
        }
        this.statusLabel.setValue(message);
        this.statusIcon.setVisible(true);
        this.statusLabel.setVisible(true);
    }
    
    private void resetStatus() {
        this.statusIcon.setVisible(false);
        this.statusLabel.setVisible(false);
        this.statusLabel.removeStyleName("gaemvnrepo-notification-error");
        this.statusLabel.removeStyleName("gaemvnrepo-notification-success");
    }
    
    /**
     * Show saving error in detail view
     */
    public void showSavingError() {
        this.setStatus(false, ApplicationData.getMessage("UserEditView.save.error"));
    }
    
    /**
     * Show deletion error in detail view
     */
    public void showDeleteError() {
        this.setStatus(false, ApplicationData.getMessage("UserEditView.delete.error"));
    }
    
    /**
     * Show lock error in detail view
     */
    public void showLockError() {
        this.setStatus(false, ApplicationData.getMessage("UserEditView.lock.error"));
    }
    
    /**
     * Show unlock error in detail view
     */
    public void showUnlockError() {
        this.setStatus(false, ApplicationData.getMessage("UserEditView.lock.error"));
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
    
    private class ProfileFieldFactory extends DefaultFieldFactory {
        
        private final NativeSelect localeSelect, roleSelect;
        
        public ProfileFieldFactory() {
            this.localeSelect = new NativeSelect();
            this.localeSelect.addStyleName("gaemvnrepo-form-input");
            this.localeSelect.setRequired(true);
            this.localeSelect.setRequiredError(ApplicationData.getMessage("UserEditView.required.lang"));
            for (String lang : ResourceBundleUtil.INSTANCE.getSupportedLanguages()) {
                this.localeSelect.addItem(lang);
            }
            this.roleSelect = new NativeSelect();
            this.roleSelect.addStyleName("gaemvnrepo-form-input");
            this.roleSelect.setRequired(true);
            this.roleSelect.setRequiredError(ApplicationData.getMessage("UserEditView.required.role"));
            
            this.roleSelect.addItem(Role.USER);
            this.roleSelect.setItemCaption(Role.USER, ApplicationData.getMessage("UserEditView.role.user"));
            this.roleSelect.addItem(Role.UPLOADER);
            this.roleSelect.setItemCaption(Role.UPLOADER, ApplicationData.getMessage("UserEditView.role.uploader"));
            this.roleSelect.addItem(Role.MANAGER);
            this.roleSelect.setItemCaption(Role.MANAGER, ApplicationData.getMessage("UserEditView.role.manager"));
            this.roleSelect.addItem(Role.ADMIN);
            this.roleSelect.setItemCaption(Role.ADMIN, ApplicationData.getMessage("UserEditView.role.administrator"));
        }
        
        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            Field f;
            if (UserEditProfileData.LOCALE_PROPERTY.equals(propertyId)) {
                return this.localeSelect;
            } else if (UserEditProfileData.ROLE_PROPERTY.equals(propertyId)) {
                return this.roleSelect;
            } else {
                // Use the super class to create a suitable field base on the
                // property type.
                f = super.createField(item, propertyId, uiContext);
                f.addStyleName("gaemvnrepo-form-input");
            }
            
            if (UserEditProfileData.LOGIN_PROPERTY.equals(propertyId)) {
                TextField tf = (TextField) f; 
                tf.setRequired(true);
                tf.setRequiredError(ApplicationData.getMessage("UserEditView.required.login"));
                tf.addValidator(new StringLengthValidator(
                        ApplicationData.getMessage("UserEditView.invalid.login"), 3, 25, false));
            } else if (UserEditProfileData.EMAIL_PROPERTY.equals(propertyId)) {
                TextField tf = (TextField) f; 
                tf.setRequired(true);
                tf.setRequiredError(ApplicationData.getMessage("UserEditView.required.email"));
                tf.addValidator(new EmailValidator(ApplicationData.getMessage("UserEditView.invalid.email")));
            }

            return f;
        }
    }
    
    private class AuthFieldFactory extends DefaultFieldFactory {
        
        private final PasswordField password, rePassword;
        
        public AuthFieldFactory() {
            this.password = new PasswordField();
            this.password.addStyleName("gaemvnrepo-form-input");
            this.password.setRequired(true);
            this.password.setRequiredError(ApplicationData.getMessage("UserEditView.required.password"));
            this.password.addValidator(new StringLengthValidator(
                    ApplicationData.getMessage("UserEditView.invalid.password"), 3, 25, false));
            
            this.rePassword = new PasswordField();
            this.rePassword.addStyleName("gaemvnrepo-form-input");
            this.rePassword.setRequired(true);
            this.rePassword.setRequiredError(ApplicationData.getMessage("UserEditView.required.confirmation"));
            this.rePassword.addValidator(new Validator(){

                @Override
                public void validate(Object value) throws InvalidValueException {
                    if (!this.isValid(value)) {
                        throw new InvalidValueException(ApplicationData.getMessage("UserEditView.invalid.confirmation"));
                    }
                }

                @Override
                public boolean isValid(Object value) {
                    return password.getValue().equals(value);
                }
                
            });
        }
        
        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            Field f;
            if (UsereditAuthData.PASSWORD_PROPERTY.equals(propertyId)) {
                return this.password;
            } else if (UsereditAuthData.REPASSWORD_PROPERTY.equals(propertyId)) {
                return this.rePassword;
            } else {
                // Use the super class to create a suitable field base on the
                // property type.
                f = super.createField(item, propertyId, uiContext);
            }

            return f;
        }
    }
}
