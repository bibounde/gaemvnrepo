package com.bibounde.gaemvnrepo.shared.domain.user;

import java.util.HashMap;
import java.util.Map;

import com.bibounde.gaemvnrepo.shared.history.Historizable;

public class UserLogin implements Historizable {

    private static final String KEY_INDEX = "index";
    
    public String login;

    @Override
    public void load(Map<String, String> historyParams) {
        this.login = historyParams.get(KEY_INDEX);
    }

    @Override
    public Map<String, String> encode() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(KEY_INDEX, this.login);
        return ret;
    }
}
