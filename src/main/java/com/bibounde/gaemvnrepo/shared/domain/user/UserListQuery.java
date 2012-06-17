package com.bibounde.gaemvnrepo.shared.domain.user;

import java.util.HashMap;
import java.util.Map;

import com.bibounde.gaemvnrepo.shared.history.Historizable;

public class UserListQuery implements Historizable {

    private static final String PAGE_KEY = "page";
    private static final String PAGE_SIZE_KEY = "pagesize";
    private static final String SORT_KEY = "sort";
    private static final String ASCENDING_KEY = "ascending";
    
    public int page, pageSize;
    public UserSort sort;
    public boolean ascending;

    @Override
    public void load(Map<String, String> historyParams) {
        this.page = Integer.valueOf(historyParams.get(PAGE_KEY));
        this.pageSize = Integer.valueOf(historyParams.get(PAGE_SIZE_KEY));
        this.sort = UserSort.valueOf(historyParams.get(SORT_KEY).toUpperCase());
        this.ascending = Boolean.valueOf(historyParams.get(ASCENDING_KEY));
    }

    @Override
    public Map<String, String> encode() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(PAGE_KEY, String.valueOf(this.page));
        ret.put(PAGE_SIZE_KEY, String.valueOf(this.pageSize));
        ret.put(SORT_KEY, this.sort.name().toLowerCase());
        ret.put(ASCENDING_KEY, String.valueOf(this.ascending));
        
        
        return ret;
    }
}
