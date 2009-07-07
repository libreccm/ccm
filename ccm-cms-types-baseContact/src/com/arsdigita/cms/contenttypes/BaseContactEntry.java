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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class BaseContactEntry extends ACSObject {
    
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
    public BaseContactEntry(String typeName) {
        super(typeName);
    }

    public BaseContactEntry(ObjectType type) {
        super(type);
    }

    public BaseContactEntry(OID oid) {
        super(oid);
    }
    
    public BaseContactEntry(DataObject dataObject) {
        super(dataObject);
    }
    
    public BaseContactEntry() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public BaseContactEntry(String key, String value, String description) {
        this();
        setKey(key);
        setValue(value);
        setDescription(description);
    }
    
    public BaseContactEntry(OID oid, String key, String value, String description) {
        this(oid);
        setKey(key);
        setValue(value);
        setDescription(description);
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
