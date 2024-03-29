package com.bibounde.gaemvnrepo.shared.service;

import java.io.Serializable;

import com.bibounde.gaemvnrepo.shared.exception.TechnicalException;

public interface GaeService extends Serializable {

    /**
     * Delete all elements stored in db
     */
    void deleteAll() throws TechnicalException;
}
