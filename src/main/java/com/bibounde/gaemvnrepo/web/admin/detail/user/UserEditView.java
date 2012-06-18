package com.bibounde.gaemvnrepo.web.admin.detail.user;

import java.util.Arrays;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditQuery;
import com.bibounde.gaemvnrepo.shared.domain.user.UserEditResponse;
import com.bibounde.gaemvnrepo.web.admin.detail.HeaderComponent;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UserEditView extends VerticalLayout implements View {

    public static final String ACTION_SAVED = UserEditView.class.getName() + "action.saved";
    
    public static final String ID = UserEditView.class.getName();
    
    private UserEditModel model;
    private Controller controller;
    private Form profileForm, authenticationForm;
    private UserEditProfileData userProfileFormData;
    private UsereditAuthData userAuthData;
    private NativeButton saveButton;
    
    
    public UserEditView() {
        this.model = new UserEditModel();
        this.model.addModelEventListener(this);
        
        this.profileForm = new Form();
        this.profileForm.setCaption(Messages.INSTANCE.getString("UserEditView.profile", this.getLocale()));
        this.profileForm.setFormFieldFactory(new ProfileFieldFactory());
        this.userProfileFormData = new UserEditProfileData();
        BeanItem<UserEditProfileData> personItem = new BeanItem<UserEditProfileData>(this.userProfileFormData);
        this.profileForm.setItemDataSource(personItem);
        this.profileForm.setInvalidCommitted(false);
        
        this.profileForm.setVisibleItemProperties(Arrays.asList(new String[] {
                UserEditProfileData.LOGIN_PROPERTY, UserEditProfileData.EMAIL_PROPERTY, UserEditProfileData.LOCALE_PROPERTY, UserEditProfileData.ACTIVE_PROPERTY, UserEditProfileData.ADMINISTRATOR_PROPERTY}));
        this.profileForm.getField(UserEditProfileData.LOGIN_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.login", this.getLocale()));
        this.profileForm.getField(UserEditProfileData.EMAIL_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.email", this.getLocale()));
        this.profileForm.getField(UserEditProfileData.LOCALE_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.lang", this.getLocale()));
        this.profileForm.getField(UserEditProfileData.ACTIVE_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.active", this.getLocale()));
        this.profileForm.getField(UserEditProfileData.ADMINISTRATOR_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.administrator", this.getLocale()));
        
        this.authenticationForm = new Form();
        this.authenticationForm.setCaption(Messages.INSTANCE.getString("UserEditView.authentication", this.getLocale()));
        this.authenticationForm.setFormFieldFactory(new AuthFieldFactory());
        this.userAuthData = new UsereditAuthData();
        BeanItem<UsereditAuthData> userAuthItem = new BeanItem<UsereditAuthData>(this.userAuthData);
        this.authenticationForm.setItemDataSource(userAuthItem);
        this.authenticationForm.setInvalidCommitted(false);
        
        this.authenticationForm.setVisibleItemProperties(Arrays.asList(new String[] {
                UsereditAuthData.PASSWORD_PROPERTY, UsereditAuthData.REPASSWORD_PROPERTY}));
        this.authenticationForm.getField(UsereditAuthData.PASSWORD_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.password", this.getLocale()));
        this.authenticationForm.getField(UsereditAuthData.REPASSWORD_PROPERTY).setCaption(Messages.INSTANCE.getString("UserEditView.confirmation", getLocale()));
        
        this.saveButton = new NativeButton(Messages.INSTANCE.getString("UserEditView.save", getLocale()));
        this.saveButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    profileForm.commit();
                    if (!((String) authenticationForm.getField(UsereditAuthData.PASSWORD_PROPERTY).getValue()).isEmpty()) {
                        authenticationForm.commit();
                    }
                } catch (Exception e) {
                    // Ignored, we'll let the Form handle the errors
                }
                
                UserEditQuery user = new UserEditQuery();
                user.active = userProfileFormData.isActive();
                user.administrator = userProfileFormData.isAdministrator();
                user.email = userProfileFormData.getEmail();
                user.locale = userProfileFormData.getLocale();
                user.login = userProfileFormData.getLogin();
                user.password = userAuthData.getPassword();
                
                ActionEvent actionEvent = new ActionEvent(ID, ACTION_SAVED, user);
                controller.actionPerformed(actionEvent);
            }
        });
        
        
        this.initLayout();
    }
    
    private void initLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.addComponent(new HeaderComponent(Messages.INSTANCE.getString("UserEditView.title", this.getLocale()), "/static/icons/user-edit-32.png"));
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        this.addComponent(tabSheet);
        this.setExpandRatio(tabSheet, 1.0f);
        
        GridLayout detailTabLayout = new GridLayout(2, 2);
        detailTabLayout.setMargin(true);
        detailTabLayout.setSpacing(true);
        detailTabLayout.setSizeFull();
        tabSheet.addTab(detailTabLayout, Messages.INSTANCE.getString("UserEditView.details", this.getLocale()));
        
        this.profileForm.setSizeFull();
        this.profileForm.addStyleName("gaemvnrepo-form");
        this.profileForm.getLayout().setMargin(true);
        detailTabLayout.addComponent(this.profileForm, 0, 0);
        
        this.authenticationForm.setSizeFull();
        this.authenticationForm.addStyleName("gaemvnrepo-form");
        this.authenticationForm.getLayout().setMargin(true);
        detailTabLayout.addComponent(this.authenticationForm, 1, 0);
        detailTabLayout.setRowExpandRatio(0, 0.7f);
        
        detailTabLayout.addComponent(this.saveButton, 1, 1);
        detailTabLayout.setComponentAlignment(this.saveButton, Alignment.TOP_RIGHT);
        detailTabLayout.setRowExpandRatio(1, 0.3f);
    }
    
    @Override
    public void modelChanged(ModelEvent event) {
        if ((UserEditModel.USER_CHANGED.equals(event.getType()) && this.model.isEditionMode()) || UserEditModel.USER_SAVED.equals(event.getType())) {
            UserEditResponse user = this.model.getUser();
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ACTIVE_PROPERTY).setValue(user.active);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.EMAIL_PROPERTY).setValue(user.email);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOCALE_PROPERTY).setValue(user.locale);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ADMINISTRATOR_PROPERTY).setValue(user.administrator);
            
            Property loginProperty = this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOGIN_PROPERTY);
            loginProperty.setValue(user.login);
            loginProperty.setReadOnly(true);
            
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.PASSWORD_PROPERTY).setValue("");
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.REPASSWORD_PROPERTY).setValue("");
            
            if (UserEditModel.USER_SAVED.equals(event.getType())) {
                this.getWindow().showNotification(Messages.INSTANCE.getString("UserEditView.usersaved", getLocale()), null);
            }
        } else {
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ACTIVE_PROPERTY).setValue(false);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.EMAIL_PROPERTY).setValue("");
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOCALE_PROPERTY).setValue(null);
            this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.ADMINISTRATOR_PROPERTY).setValue(false);
            
            Property loginProperty = this.profileForm.getItemDataSource().getItemProperty(UserEditProfileData.LOGIN_PROPERTY);
            loginProperty.setValue("");
            loginProperty.setReadOnly(false);
            

            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.PASSWORD_PROPERTY).setValue("");
            this.authenticationForm.getItemDataSource().getItemProperty(UsereditAuthData.REPASSWORD_PROPERTY).setValue("");
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
    
    private class ProfileFieldFactory extends DefaultFieldFactory {
        
        private final NativeSelect localeSelect;
        
        public ProfileFieldFactory() {
            this.localeSelect = new NativeSelect();
            this.localeSelect.addStyleName("gaemvnrepo-form-input");
            this.localeSelect.setRequired(true);
            this.localeSelect.setRequiredError(Messages.INSTANCE.getString("UserEditView.required.lang", getLocale()));
            for (String lang : Messages.INSTANCE.getSupportedLanguages()) {
                this.localeSelect.addItem(lang);
            }
        }
        
        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            Field f;
            if (UserEditProfileData.LOCALE_PROPERTY.equals(propertyId)) {
                return this.localeSelect;
            } else {
                // Use the super class to create a suitable field base on the
                // property type.
                f = super.createField(item, propertyId, uiContext);
                f.addStyleName("gaemvnrepo-form-input");
            }
            
            if (UserEditProfileData.LOGIN_PROPERTY.equals(propertyId)) {
                TextField tf = (TextField) f; 
                tf.setRequired(true);
                tf.setRequiredError(Messages.INSTANCE.getString("UserEditView.required.login", getLocale()));
                tf.addValidator(new StringLengthValidator(
                        Messages.INSTANCE.getString("UserEditView.invalid.login", getLocale()), 3, 25, false));
            } else if (UserEditProfileData.EMAIL_PROPERTY.equals(propertyId)) {
                TextField tf = (TextField) f; 
                tf.setRequired(true);
                tf.setRequiredError(Messages.INSTANCE.getString("UserEditView.required.email", getLocale()));
                tf.addValidator(new EmailValidator(Messages.INSTANCE.getString("UserEditView.invalid.email", getLocale())));
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
            this.password.setRequiredError(Messages.INSTANCE.getString("UserEditView.required.password", getLocale()));
            this.password.addValidator(new StringLengthValidator(
                    Messages.INSTANCE.getString("UserEditView.invalid.password", getLocale()), 3, 25, false));
            
            this.rePassword = new PasswordField();
            this.rePassword.addStyleName("gaemvnrepo-form-input");
            this.rePassword.setRequired(true);
            this.rePassword.setRequiredError(Messages.INSTANCE.getString("UserEditView.required.confirmation", getLocale()));
            this.rePassword.addValidator(new Validator(){

                @Override
                public void validate(Object value) throws InvalidValueException {
                    if (!this.isValid(value)) {
                        throw new InvalidValueException(Messages.INSTANCE.getString("UserEditView.invalid.confirmation", getLocale()));
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
