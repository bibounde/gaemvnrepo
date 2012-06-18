package com.bibounde.gaemvnrepo.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
//Not supported by GAE @Unique(name = "FILE_UNIQUE_IDX", members = { "path" })
public class File implements Disposable {
    
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
    private String mime;
    
    @Persistent
    private Blob content;
    
    private boolean disposable;

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
     * @return the content
     */
    public byte[] getContent() {
        if (content == null) {
            return null;
        }

        return content.getBytes();
    }

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = new Blob(content);
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
     * @return the mime
     */
    public String getMime() {
        return mime;
    }

    /**
     * @param mime the mime to set
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    @Override
    public boolean isDisposable() {
        return disposable;
    }

    @Override
    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }
}
