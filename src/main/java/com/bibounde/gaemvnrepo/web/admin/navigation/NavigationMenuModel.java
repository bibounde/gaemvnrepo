package com.bibounde.gaemvnrepo.web.admin.navigation;

import java.util.ArrayList;
import java.util.List;

import com.bibounde.gaemvnrepo.web.mvc.Model;
import com.bibounde.gaemvnrepo.web.mvc.ModelEvent;
import com.bibounde.gaemvnrepo.web.mvc.ModelEventListener;

public class NavigationMenuModel implements Model {

    
    public enum MenuItem {
        HOME_ITEM, BROWSE_REPOSITORY_ITEM, REPOSITORY_EDIT_ITEM, SYSTEM_CONFIGURATION_ITEM, BROWSE_USER_ITEM, USER_EDIT_ITEM, ROLE_ITEM, PROFILE_ITEM;
    }
    
    public static final String MENU_ITEM_CHANGED = NavigationMenuModel.class.getName() + ".event.menuitemchanged";
    public static final String SELECTION_CHANGED = NavigationMenuModel.class.getName() + ".event.selectionchanged";
    
    private List<ModelEventListener> listeners = new ArrayList<ModelEventListener>();
    
    private List<MenuItem> menuItems = new ArrayList<MenuItem>();
    private int selectedIndex = -1;
    
    /**
     * @return the menuItems
     */
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    /**
     * @param menuItems the menuItems to set
     */
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
        for (ModelEventListener listener : this.listeners) {
            ModelEvent event = new ModelEvent(MENU_ITEM_CHANGED);
            listener.modelChanged(event);
        }
    }

    /**
     * @return the selectedIndex
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        for (ModelEventListener listener : this.listeners) {
            ModelEvent event = new ModelEvent(SELECTION_CHANGED);
            listener.modelChanged(event);
        }
    }

    @Override
    public void addModelEventListener(ModelEventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeModelEventListener(ModelEventListener listener) {
        this.listeners.remove(listener);
    }    
}
