package com.bibounde.gaemvnrepo.web.queue;

import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibounde.gaemvnrepo.server.PMF;
import com.bibounde.gaemvnrepo.shared.exception.BusinessException;
import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobDeletionServlet extends AbstractQueueServlet {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(BlobDeletionServlet.class);
    
    @Override
    protected void execute(Map parameterMap) throws TechnicalException, BusinessException {
        String[] blobKey = (String[]) parameterMap.get("blobkey");
        
        if (blobKey == null || blobKey.length == 0) {
            throw new BusinessException("Queue parameter is invalid. Unbale to find \"blobkey\"");
        }
        
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();
        } catch (Exception e) {
            throw new TechnicalException("Persistence initialization failed", e);
        }
        try {
            tx.begin();
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobStoreService.delete(new BlobKey(blobKey[0]));
            tx.commit();
        } catch (Exception e) {
            throw new TechnicalException("Unable to delete blob", e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

}
