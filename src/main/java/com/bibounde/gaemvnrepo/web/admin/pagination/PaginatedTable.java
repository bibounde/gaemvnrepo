package com.bibounde.gaemvnrepo.web.admin.pagination;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

public abstract class PaginatedTable extends Table {

    private PaginatedContainer container;

    public PaginatedTable(int pageSize) {
        super();

        //Disable default container lazy behavior
        this.setPageLength(pageSize);
        this.setCacheRate(0);
        
        this.container = new PaginatedContainer();
        this.setContainerDataSource(this.container);
    }

    public abstract void doSort();
    
    public void showFirstPage() {
        this.container.performSort = true;
        this.container.doSort();
    }
    
    public void refreshSortInfo(Object propertyId, boolean ascending) {
        //Do not perform sort. Only refresh header values
        this.container.performSort = false;
        this.setSortAscending(ascending);
        this.setSortContainerPropertyId(propertyId);
        this.container.performSort = true;
    }
    
    public int getPageSize() {
        return this.getPageLength();
    }

    private class PaginatedContainer extends IndexedContainer {
        
        private boolean performSort = false;
        
        @Override
        protected void doSort() {
            if (performSort) {
                PaginatedTable.this.doSort();
            }
            
        }
    }
}
