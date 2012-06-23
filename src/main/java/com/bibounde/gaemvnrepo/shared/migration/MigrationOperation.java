package com.bibounde.gaemvnrepo.shared.migration;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface MigrationOperation extends Serializable {
    
    String getName();
    String getLocalizedName();
    void run() throws TechnicalException;
}
