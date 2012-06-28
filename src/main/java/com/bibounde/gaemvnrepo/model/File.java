package com.bibounde.gaemvnrepo.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang.ArrayUtils;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
//Not supported by GAE @Unique(name = "FILE_UNIQUE_IDX", members = { "path" })
public class File implements Serializable {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;
    
    @Persistent(nullValue=NullValue.EXCEPTION)
    private String path;
    
    @Persistent(nullValue=NullValue.EXCEPTION)
    private String name;

    @Persistent
    private int depth;
    
    @Persistent
    private boolean file;

    @Persistent
    private long creationDate;
    
    @Persistent
    private String creator;
    
    @Persistent
    private BlobKey contentKey;

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return the creationDate
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(long uploadDate) {
        this.creationDate = uploadDate;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(String uploaderEmail) {
        this.creator = uploaderEmail;
    }

    /**
     * @return the file
     */
    public boolean isFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(boolean file) {
        this.file = file;
    }

    /**
     * @return the id
     */
    public Key getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Key id) {
        this.id = id;
    }
    
    /**
     * @param contentKey the contentKey to set
     */
    public void setContentKey(BlobKey contentKey) {
        this.contentKey = contentKey;
    }

    /**
     * Retrieves file content
     * @return file content
     */
    public byte[] getContent() {
        BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(this.contentKey);
        
        if (blobInfo == null) {
            return null;
        }
        
        byte[] content = new byte[0];
        
        long amountLeftToRead = blobInfo.getSize();
        long startIndex = 0;
        while (amountLeftToRead > 0) {
            long amountToReadNow = Math.min(
                    BlobstoreService.MAX_BLOB_FETCH_SIZE - 1, amountLeftToRead);

            byte[] chunkOfBytes = blobStoreService.fetchData(this.contentKey, startIndex, startIndex + amountToReadNow);

            content = ArrayUtils.addAll(content, chunkOfBytes);

            amountLeftToRead -= amountToReadNow;
            startIndex += amountToReadNow;
        }
        
        return content;
    }

    /**
     * @return the contentKey
     */
    public BlobKey getContentKey() {
        return contentKey;
    }
}
