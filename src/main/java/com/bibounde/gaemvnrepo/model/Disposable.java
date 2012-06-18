package com.bibounde.gaemvnrepo.model;

import java.io.Serializable;

public interface Disposable extends Serializable {

    boolean isDisposable();
    void setDisposable(boolean disposable);
}
