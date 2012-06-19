package com.bibounde.gaemvnrepo.shared.domain.user;

import java.util.HashMap;
import java.util.Map;

import com.bibounde.gaemvnrepo.shared.history.Historizable;

public class UserId implements Historizable {

    private static final String KEY_INDEX = "index";
    
    public long id;

    @Override
    public void load(Map<String, String> historyParams) {
        this.id = Long.valueOf(historyParams.get(KEY_INDEX));
    }

    @Override
    public Map<String, String> encode() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(KEY_INDEX, String.valueOf(this.id));
        return ret;
    }
}
