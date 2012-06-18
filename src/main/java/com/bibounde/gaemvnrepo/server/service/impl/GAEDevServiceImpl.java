package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.DevService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

@Service("demoService")
public class GAEDevServiceImpl implements DevService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(GAEDevServiceImpl.class);

    @Override
    public void deleteAll() throws TechnicalException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        //No transaction (can't operate on multiple entity groups in a single transaction.found both Element)
        
        try {

            Query query = new Query(User.class.getSimpleName()).setKeysOnly();

            final ArrayList<Key> keys = new ArrayList<Key>();

            for (final Entity entity : datastore.prepare(query).asIterable()) {
                keys.add(entity.getKey());
            }

            keys.trimToSize();
            logger.debug("Found {} users to delete", keys.size());

            datastore.delete(keys);
        } catch (Exception e) {
            throw new TechnicalException("Unable to delete users", e);
        }
    }

}
