package com.bibounde.gaemvnrepo.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MigrationStatus implements Serializable {

    public static enum Status {
        IN_PROGESS, DONE, PAUSED;
    }
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;
    
    @Persistent
    private Status status;
    
    @Persistent
    private int currentMigrationCount;
    
    @Persistent
    private List<String> migrationNamesToPerform;
    
    @Persistent
    private List<AppliedMigration> appliedMigrations;

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
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the currentMigrationCount
     */
    public int getCurrentMigrationCount() {
        return currentMigrationCount;
    }

    /**
     * @param currentMigrationCount the currentMigrationCount to set
     */
    public void setCurrentMigrationCount(int currentMigrationCount) {
        this.currentMigrationCount = currentMigrationCount;
    }

    /**
     * @return the migrationNamesToPerform
     */
    public List<String> getMigrationNamesToPerform() {
        return migrationNamesToPerform;
    }

    /**
     * @param migrationNamesToPerform the migrationNamesToPerform to set
     */
    public void setMigrationNamesToPerform(List<String> migrationNames) {
        this.migrationNamesToPerform = migrationNames;
    }

    /**
     * @return the appliedMigrations
     */
    public List<AppliedMigration> getAppliedMigrations() {
        return appliedMigrations;
    }

    /**
     * @param appliedMigrations the appliedMigrations to set
     */
    public void setAppliedMigrations(List<AppliedMigration> appliedMigrations) {
        this.appliedMigrations = appliedMigrations;
    }
}
