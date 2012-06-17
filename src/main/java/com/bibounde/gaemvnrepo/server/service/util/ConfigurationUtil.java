package com.bibounde.gaemvnrepo.server.service.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigurationUtil {

    INSTANCE;
    
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);
    
    private Properties properties;
    
    private ConfigurationUtil() {
        this.properties = new Properties();
        try {
            this.properties.load(getClass().getClassLoader().getResourceAsStream("configuration.properties"));
        } catch (IOException e) {
            logger.error("Unable to load properties", e);
        }
    }
    
    /**
     * Retrieves application version
     * @return application version
     */
    public String getApplicationVersion() {
        return this.properties.getProperty("version");
    }
    
    /**
     * Retrieves vaadin version
     * @return vaadin version
     */
    public String getApplicationVaadinVersion() {
        return this.properties.getProperty("version.vaadin");
    }
    
    public int getUserTablePageSize() {
        return Integer.valueOf(this.properties.getProperty("table.user.pagesize"));
    }
    
    public Mode getMode() {
        return Mode.valueOf(this.properties.getProperty("mode").toUpperCase());
    }
    
    public enum Mode {
        DEV, PRODUCTION;
    }
}
