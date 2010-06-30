/*
 * GenericContactType.java
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class GenericContactType extends ACSObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.GenericContactType";
    
    private static final Logger s_log = Logger.getLogger(GenericContactType.class);
    
    /** PDL property names */
    public static final String KEY = "key";
    public static final String LANGUAGE = "language";
    public static final String NAME = "name";
    
    /**
     * Creates a new instance of GenericContactEntry
     */
    public GenericContactType() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public GenericContactType(String typeName) {
        super(typeName);
    }
    
    public GenericContactType(OID oid) {
        super(oid);
    }
    
    public GenericContactType(DataObject object) {
        super(object);
    }

    /**
     * Constructor. Retrieves an object instance with the given id.
     * @param id the id of the object to retrieve
     */
    public GenericContactType(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public GenericContactType(String key, String language, String name, int order) {
        this();
        setKey(key);
        setLanguage(language);
        setName(name);
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
    
    // Get language
    public String getLanguage() {
        return (String) get(LANGUAGE);
    }

    public void setLanguage(String language) {
        set(LANGUAGE, language);
    }

    // Get name
    public String getName() {
        return (String) get(NAME);
    }
    
    // Set name
    public void setName(String name) {
        set(NAME, name);
    }
    
}
