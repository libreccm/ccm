package org.libreccm.pagemodel;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemListComponent extends ComponentModel {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.ItemListComponent";
    
    public static final String DESCENDING = "descending";
    
    public static final String LIMIT_TO_TYPE = "limitToType";
    
    public static final String PAGE_SIZE = "pageSize";
    
    public static final String PROPERTIES = "properties";
    
    public ItemListComponent(final DataObject dataObject) {
        super(dataObject);
    }
    
    public ItemListComponent(final OID oid) {
        super(oid);
    }
    
    public ItemListComponent(final BigDecimal componentModelId) {
        
        this(new OID(BASE_DATA_OBJECT_TYPE, componentModelId));
    }
    
    public Boolean isDescending() {
        
        return (Boolean) get(DESCENDING);
    }
    
    public void setDescending(final Boolean descending) {
        
        set(DESCENDING, descending);
    }
    
    public String getLimitToType() {
        
        return (String) get(LIMIT_TO_TYPE);
    }
    
    public void setLimitToType(final String limitToType) {
        
        set(LIMIT_TO_TYPE, limitToType);
    }
    
    public Integer getPageSize() {
        
        return (Integer)get(PAGE_SIZE);
    }
    
    public void setPageSize(final Integer pageSize) {
        
        set(PAGE_SIZE, pageSize);
    }
    
    public String getProperties() {
        
        return (String) get(PROPERTIES);
    }
    
    public void setProperties(final String properties) {
        
        set(PROPERTIES, properties);
    }
}
