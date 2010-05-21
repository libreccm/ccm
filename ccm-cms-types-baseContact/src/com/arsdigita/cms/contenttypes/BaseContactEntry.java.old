/*
 * BaseContactEntry.java
 *
 * Created on 13. Mai 2009, 12:31
 *
 * This class is part of BaseContact and stores the contact informations.
 * These informations is organized by a key, which possible values are set by a config param.
 * In addition there is a description field to provide additional information for this entry
 * which will be shown along with the entry value, if set. If there is no description set, the
 * key will be used as a fallback label.
 *
 * For example:
 * key = "phone"
 * description = "office phone"
 * value = "1234 / 123456"
 *
 * would be shown as
 * office phone: 1234 / 123456
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class BaseContactEntry extends ContentItem {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.BaseContactEntry";
    private static final String BASE_DATA_OBJECT_PACKAGE = "com.arsdigita.cms.contenttypes";
    
    private static final Logger s_log = Logger.getLogger(BaseContactEntry.class);
    
    /** PDL property names */
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DESCRIPTION = "description";
    
    
    /**
     * Creates a new instance of BaseContactEntry
     */
    public BaseContactEntry() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public BaseContactEntry(String typeName) {
        super(typeName);
    }
    
    public BaseContactEntry(OID oid) {
        super(oid);
    }
    
    public BaseContactEntry(DataObject object) {
        super(object);
    }
    
    /**
     * Constructor. Retrieves an object instance with the given id.
     * @param id the id of the object to retrieve
     */
    public BaseContactEntry(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public BaseContactEntry(BaseContact contact, String key, String value, String description) {
        this();
        setName(key + " for " + contact.getName() + "(" + contact.getID() + ")");
        setKey(key);
        setValue(value);
        setDescription(description);
        save();
    }
    
    /////////////////////////////////////////////////
    // accessors
    
    // Get key
    public String getKey() {
        return (String) get(KEY);
    }
    
    // Set key
    public void setKey(String key) {
        set(KEY, key);
    }
    
    // Get value
    public String getValue() {
        return (String) get(VALUE);
    }
    
    // Set value
    public void setValue(String value) {
        set(VALUE, value);
    }
    
    // Get description
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }
    
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
}
