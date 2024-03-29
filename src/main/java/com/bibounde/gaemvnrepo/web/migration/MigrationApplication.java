package com.bibounde.gaemvnrepo.web.migration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.bibounde.gaemvnrepo.server.service.util.ConfigurationUtil;
import com.bibounde.gaemvnrepo.shared.domain.migration.MigrationResponse;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.MigrationService;
import com.bibounde.gaemvnrepo.web.ApplicationData;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class MigrationApplication extends Application implements HttpServletRequestListener {

    @Autowired
    private MigrationService migrationService;
    
    private ProgressIndicator progressIndicator;
    private Embedded statusIcon;
    private Label statusLabel;
    private NativeButton performButton;
    private Link adminLink;
    private String pauseMessage, successMessage, resumeMessage;
    
    
    @Override
    public void init() {
        this.setTheme("gaemvnrepo");
        
        ApplicationData sessionData = new ApplicationData(this);
        this.getContext().addTransactionListener(sessionData);
        ApplicationData.initLocale(getLocale());
        
        final Window main = new Window(ConfigurationUtil.INSTANCE.getCaption() + " " + ApplicationData.getMessage("MigrationApplication.title")); 
        setMainWindow(main);
        
        VerticalLayout container = (VerticalLayout) main.getContent();
        container.setSizeFull();
        container.setMargin(true);
        container.setStyleName(Reindeer.LAYOUT_BLUE);
        
        Panel panel = new Panel();
        ((VerticalLayout) panel.getContent()).setSpacing(true);
        ((VerticalLayout) panel.getContent()).setSizeFull();
        panel.setWidth(60, Panel.UNITS_PERCENTAGE);
        panel.setHeight(80, Panel.UNITS_PERCENTAGE);
        
        container.addComponent(panel);
        container.setExpandRatio(panel, 1.0f);
        container.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
        
        Label caption = new Label(ApplicationData.getMessage("MigrationApplication.subtitle"));
        caption.setSizeUndefined();
        caption.setStyleName("gaemvnrepo-status-bar-caption");
        panel.addComponent(caption);
        ((VerticalLayout) panel.getContent()).setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
        
        HorizontalLayout statusLayout = new HorizontalLayout();
        statusLayout.setSpacing(true);
        panel.addComponent(statusLayout);
        ((VerticalLayout) panel.getContent()).setComponentAlignment(statusLayout, Alignment.MIDDLE_CENTER);
        
        this.statusIcon = new Embedded();
        this.statusIcon.setWidth(16, Embedded.UNITS_PIXELS);
        this.statusIcon.setVisible(false);
        statusLayout.addComponent(this.statusIcon);
        statusLayout.setComponentAlignment(this.statusIcon, Alignment.MIDDLE_RIGHT);
        
        this.progressIndicator = new ProgressIndicator(new Float(0));
        this.progressIndicator.setEnabled(false);
        statusLayout.addComponent(this.progressIndicator);
        statusLayout.setComponentAlignment(this.progressIndicator, Alignment.MIDDLE_RIGHT);
        
        this.performButton = new NativeButton(ApplicationData.getMessage("MigrationApplication.migrate"));
        statusLayout.addComponent(performButton);
        
        performButton.addListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    progressIndicator.setEnabled(true);
                    performButton.setEnabled(false);
                    migrationService.startMigration();
                } catch (Exception e) {
                    //TODO: manage exceptions
                    e.printStackTrace();
                }
            }
        });
        
        this.statusLabel = new Label();
        this.statusLabel.setSizeUndefined();
        panel.addComponent(this.statusLabel);
        ((VerticalLayout) panel.getContent()).setComponentAlignment(this.statusLabel, Alignment.TOP_CENTER);
        
        this.adminLink = new Link(ApplicationData.getMessage("MigrationApplication.page.administration") + " \u00bb", new ExternalResource("/admin"));
        this.adminLink.setVisible(false);
        panel.addComponent(this.adminLink);
        ((VerticalLayout) panel.getContent()).setComponentAlignment(this.adminLink, Alignment.BOTTOM_RIGHT);
        ((VerticalLayout) panel.getContent()).setExpandRatio(this.adminLink, 1.0f);
        
        //Sets message to avoid NPE in request start
        this.successMessage = ApplicationData.getMessage("MigrationApplication.done");
        this.pauseMessage = ApplicationData.getMessage("MigrationApplication.paused");
        this.resumeMessage = ApplicationData.getMessage("MigrationApplication.resume");
    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        if (this.progressIndicator != null && this.progressIndicator.isVisible()) {
            try {
                MigrationResponse migration = this.migrationService.getMigrationStatus();
                this.progressIndicator.setValue(migration.progression);
                
                switch (migration.status) {
                case DONE:
                    this.progressIndicator.setEnabled(false);
                    this.statusIcon.setSource(new ExternalResource("/static/icons/accept-16.png"));
                    this.statusIcon.setVisible(true);
                    
                    this.statusLabel.setValue(successMessage);
                    this.statusLabel.addStyleName("gaemvnrepo-notification-success");
                    this.statusLabel.removeStyleName("gaemvnrepo-notification-warning");
                    
                    this.performButton.setEnabled(false);
                    this.adminLink.setVisible(true);
                    break;
                case PAUSED:
                    this.progressIndicator.setEnabled(false);
                    
                    this.statusIcon.setSource(new ExternalResource("/static/icons/warning-16.png"));
                    this.statusIcon.setVisible(true);
                    
                    this.statusLabel.setValue(pauseMessage);
                    this.statusLabel.addStyleName("gaemvnrepo-notification-warning");
                    this.statusLabel.removeStyleName("gaemvnrepo-notification-success");
                    
                    this.performButton.setEnabled(true);
                    this.performButton.setCaption(resumeMessage);
                    this.adminLink.setVisible(false);
                    break;
                default:
                    break;
                }
                
            } catch (TechnicalException e) {
                // TODO: manage exception in future version
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        //Do nothings
    }

}
