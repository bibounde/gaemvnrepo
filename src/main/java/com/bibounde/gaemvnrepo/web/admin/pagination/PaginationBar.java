package com.bibounde.gaemvnrepo.web.admin.pagination;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.i18n.Messages;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

public class PaginationBar extends HorizontalLayout{

    private Label results;
    private List<PaginationListener> listeners = new ArrayList<PaginationListener>();
    
    public PaginationBar() {
        this.setSpacing(true);
        this.results = new Label();
    }
    
    public void addPaginationListener(PaginationListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    public void removePaginationListener(PaginationListener listener) {
        this.listeners.remove(listener);
    }
    
    public void setPagination(int total, int currentPage, int pageSize) {
        this.removeAllComponents();
        
        int nbPages = (total / pageSize) + (total % pageSize != 0 ? 1 : 0);
        if (currentPage > 0) {
            Button previous = new Button("\u00ab " + Messages.INSTANCE.getString("PaginationBar.previous", this.getLocale()));
            previous.setStyleName(BaseTheme.BUTTON_LINK);
            previous.addStyleName("gaemvnrepo-pagination-link");
            previous.addListener(this.createClickListener(currentPage -1));
            this.addComponent(previous);
            this.setComponentAlignment(previous, Alignment.MIDDLE_CENTER);
        }
        for (int i = 1; i <= nbPages; i++) {
            Component page = null;
            if (i == (currentPage + 1)) {
                page = new Label(String.valueOf(i));
            } else {
                page = new Button(String.valueOf(i));
                page.setStyleName(BaseTheme.BUTTON_LINK);
                page.addStyleName("gaemvnrepo-pagination-link");
                ((Button) page).addListener(this.createClickListener(i - 1));
            }
            this.addComponent(page);
            this.setComponentAlignment(page, Alignment.MIDDLE_CENTER);
        }
        if (currentPage < nbPages - 1) {
            Button next = new Button(Messages.INSTANCE.getString("PaginationBar.next", this.getLocale()) + " \u00bb");
            next.addStyleName(BaseTheme.BUTTON_LINK);
            next.addStyleName("gaemvnrepo-pagination-link");
            next.addListener(this.createClickListener(currentPage + 1));
            this.addComponent(next);
            this.setComponentAlignment(next, Alignment.MIDDLE_CENTER);
        }
        int start = currentPage * pageSize + 1;
        int stop = start + pageSize - 1;
        StringBuilder statusContent = new StringBuilder();
        statusContent.append("(").append(start).append("-").append(stop > total ? total : stop).append("/").append(total).append(")");
        this.results.setValue(statusContent);
        this.addComponent(this.results);
        this.setExpandRatio(this.results, 1.0f);
        this.setComponentAlignment(this.results, Alignment.MIDDLE_CENTER);
    }
    
    private ClickListener createClickListener(final int page) {
        ClickListener ret = new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                for (PaginationListener listener : listeners) {
                    listener.pageSelected(page);
                }
            }
        };
        return ret;
    }
}
