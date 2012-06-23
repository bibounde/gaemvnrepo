package com.bibounde.gaemvnrepo.shared.domain.migration;

import java.io.Serializable;

public class MigrationResponse implements Serializable {

    public static enum Status {
        IN_PROGESS, DONE, PAUSED;
    }
    
    public float progression;
    public Status status;
    
}
