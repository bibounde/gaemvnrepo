package com.bibounde.gaemvnrepo.web.admin.history;

public interface HistoryChangeListener {

    /**
     * Method called when history chaged
     * @param history
     */
    void historyChanged(History history);
}
