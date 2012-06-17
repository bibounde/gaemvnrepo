package com.bibounde.gaemvnrepo.web.mvc;

import java.io.Serializable;

public interface Controller extends Serializable {

    void add(View view);
    void removeView(View view);
    
    /**
     * Method called by views
     * @param event
     */
    void actionPerformed(ActionEvent event);
}
