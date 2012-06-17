package com.bibounde.gaemvnrepo.web.admin.pagination;

public interface PaginationListener {

    /**
     * Method called when page link is clicked
     * @param page
     */
    void pageSelected(int page);
}
