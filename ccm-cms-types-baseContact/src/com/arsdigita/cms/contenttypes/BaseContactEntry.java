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

/**
 *
 * @author quasi
 */
public class BaseContactEntry extends DomainObject {
    
    private static final Logger s_log = Logger.getLogger(BaseContactEntry.class);
    
    /** PDL property names */
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DESCRIPTION = "description";

    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.BaseContactEntry";
    
    
    /**
     * Creates a new instance of BaseContactEntry
     */
    public BaseContactEntry() {
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
    
    public void setDescription(String Description) {
        set(DESCRIPTION, description);
    }
}
