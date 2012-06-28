package com.bibounde.gaemvnrepo.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializabelResourceBundle extends ResourceBundle implements Serializable {

    private static final long serialVersionUID = -8046382795711130969L;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SerializabelResourceBundle.class);

    private Map<String, String> bundle;

    public SerializabelResourceBundle(String baseName, Locale locale) {
        bundle = new HashMap<String, String>();

        Properties properties = new Properties();

        try {
            String defaultFile = baseName + ".properties";
            logger.debug("Load default {}", defaultFile);
            properties.load(getClass().getClassLoader().getResourceAsStream(defaultFile));
            this.putAll(properties);

            properties.clear();
            String languageFile = baseName + "_" + locale.getLanguage() + ".properties";
            logger.debug("Tries to load language {}", languageFile);
            InputStream languageStream = getClass().getClassLoader().getResourceAsStream(languageFile);
            if (languageStream != null) {
                properties.load(languageStream);
                this.putAll(properties);
                logger.debug("Done");
            } else {
                logger.debug("No file found");
            }

            properties.clear();
            String countyrFile = baseName + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties";
            logger.debug("Tries to load country {}", countyrFile);
            InputStream countryStream = getClass().getClassLoader().getResourceAsStream(countyrFile);
            if (countryStream != null) {
                properties.load(countryStream);
                this.putAll(properties);
                logger.debug("Done");
            } else {
                logger.debug("No file found");
            }
        } catch (IOException e) {
            logger.error("Unable to initialize ResourceBundle", e);
        }

    }

    private void putAll(Properties properties) {
        Iterator<Object> keyIterator = properties.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            this.bundle.put(key, properties.getProperty(key));
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(this.bundle.keySet());
    }

    @Override
    protected Object handleGetObject(String key) {
        return this.bundle.get(key);
    }

}
