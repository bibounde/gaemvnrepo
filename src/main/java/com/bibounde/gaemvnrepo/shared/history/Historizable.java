package com.bibounde.gaemvnrepo.shared.history;

import java.io.Serializable;
import java.util.Map;

public interface Historizable extends Serializable {

    /**
     * Loads values from history
     * @param historyParams history params
     */
    void load(Map<String, String> historyParams);
    
    /**
     * Encode hiztorizable object
     * @return encode params
     */
    Map<String, String> encode(); 
}
