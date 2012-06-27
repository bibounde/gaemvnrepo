package com.bibounde.gaemvnrepo.web.login;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Configurable;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class LoginApplication extends Application {

    private Label loginFailedLabel;
    
    @Override
    public void init() {
        this.setTheme("gaemvnrepo");
        
        final Window main = new Window(ConfigurationUtil.INSTANCE.getCaption() + " " + Messages.INSTANCE.getString("LoginApplication.title", getLocale())); 
        setMainWindow(main);
        
        VerticalLayout container = (VerticalLayout) main.getContent();
        container.setSizeFull();
        container.setMargin(true);
        container.setStyleName(Reindeer.LAYOUT_BLUE);
        
        Panel panel = new Panel();
        ((VerticalLayout) panel.getContent()).setSpacing(true);
        //((VerticalLayout) panel.getContent()).setSizeFull();
        panel.setWidth(300, Panel.UNITS_PIXELS);
        //panel.setHeight(30, Panel.UNITS_PERCENTAGE);
        //panel.setSizeUndefined();
        
        container.addComponent(panel);
        container.setExpandRatio(panel, 1.0f);
        container.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
        
        HorizontalLayout captionLayout = new HorizontalLayout();
        captionLayout.setSpacing(true);
        captionLayout.setWidth(100, Label.UNITS_PERCENTAGE);
        panel.addComponent(captionLayout);
        
        Label icon = new Label();
        icon.setIcon(new ExternalResource("/static/icons/login-32.png"));
        icon.setWidth(32, Label.UNITS_PIXELS);
        captionLayout.addComponent(icon);
        
        Label caption = new Label(ConfigurationUtil.INSTANCE.getCaption());
        caption.setSizeUndefined();
        caption.setStyleName("gaemvnrepo-status-bar-caption");
        captionLayout.addComponent(caption);
        captionLayout.setExpandRatio(caption, 1.0f);
        captionLayout.setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
        
        this.loginFailedLabel = new Label(Messages.INSTANCE.getString("LoginApplication.login.failed", getLocale()));
        this.loginFailedLabel.setVisible(false);
        this.loginFailedLabel.addStyleName("gaemvnrepo-notification-error");
        panel.addComponent(this.loginFailedLabel);
        
        LoginForm form = new LoginForm() {

            @Override
            protected byte[] getLoginHTML() {
                try {
                    Configuration freemarkerConfiguration = new Configuration();
                    freemarkerConfiguration.setServletContextForTemplateLoading(((WebApplicationContext) getContext()).getHttpSession().getServletContext(), "WEB-INF/templates");
                    freemarkerConfiguration.setObjectWrapper(new BeansWrapper());
                    
                    Template template = freemarkerConfiguration.getTemplate("login.ftl");
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("labellogin", Messages.INSTANCE.getString("LoginApplication.login", getLocale()) + ":");
                    model.put("labelpassword", Messages.INSTANCE.getString("LoginApplication.password", getLocale()) + ":");
                    model.put("labelsubmit", Messages.INSTANCE.getString("LoginApplication.submit", getLocale()));
                    StringWriter writer = new StringWriter();
                    template.process(model, writer);
                    writer.flush();
                    String content = writer.toString();

                    return content.getBytes();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return e.getMessage().getBytes();
                }
            }
        };
        
        panel.addComponent(form);
        
        main.addParameterHandler(new ParameterHandler() {
            
            @Override
            public void handleParameters(Map<String, String[]> parameters) {
                if (parameters.containsKey("login") && parameters.get("login")[0].equals("failed")) {
                    loginFailedLabel.setVisible(true);
                } else {
                    loginFailedLabel.setVisible(false);
                }
            }
        });
    }

}
