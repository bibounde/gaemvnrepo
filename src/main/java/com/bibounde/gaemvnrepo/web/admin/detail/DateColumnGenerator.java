package com.bibounde.gaemvnrepo.web.admin.detail;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class DateColumnGenerator implements ColumnGenerator {

    private String pattern;

    public DateColumnGenerator(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        //Property property = source.getItem(itemId).getItemProperty(columnId);
        
        /*if (property.getType().equals(Date.class) && property.getValue() != null) {
            //SimpleDateFormat format = new SimpleDateFormat(pattern, Messages.INSTANCE.getLocale());
            
            Label ret = new Label("titi");//format.format(property.getValue()));
            return ret;
        }*/
        return null;
    }
    
}
