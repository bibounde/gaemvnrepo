package com.bibounde.gaemvnrepo.server.service.impl;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bibounde.gaemvnrepo.model.Repository;
import com.bibounde.gaemvnrepo.model.User;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.bibounde.gaemvnrepo.shared.service.GaeService;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

@Service("demoService")
public class GAEServiceImpl implements GaeService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(GAEServiceImpl.class);

    @Override
    public void deleteAll() throws TechnicalException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        //No transaction ("can't operate on multiple entity groups in a single transaction.found both Element")
        
        try {

            Query query = new Query(User.class.getSimpleName()).setKeysOnly();

            final ArrayList<Key> keys = new ArrayList<Key>();

            for (final Entity entity : datastore.prepare(query).asIterable()) {
                keys.add(entity.getKey());
            }
            
            query = new Query(Repository.class.getSimpleName()).setKeysOnly();
            for (final Entity entity : datastore.prepare(query).asIterable()) {
                keys.add(entity.getKey());
            }

            keys.trimToSize();
            logger.debug("Found {} entities to delete", keys.size());

            datastore.delete(keys);
            
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); 
            Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
            while (iterator.hasNext()) {
                BlobInfo blobInfo = (BlobInfo) iterator.next();
                blobstoreService.delete(blobInfo.getBlobKey());
            }
        } catch (Exception e) {
            throw new TechnicalException("Unable to delete entites or blobs", e);
        }
    }
}
