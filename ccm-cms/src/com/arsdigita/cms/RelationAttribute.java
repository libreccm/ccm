/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class RelationAttribute extends DomainObject {

    public static final String ID = "id";
    public static final String ATTRIBUTE = "attribute";
    public static final String KEY = "attr_key";
    public static final String LANGUAGE = "lang";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.RelationAttribute";

    /**
     * Default constructor. This creates a new (empty) RelationAttribute.
     **/
    public RelationAttribute() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public RelationAttribute(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public RelationAttribute(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public RelationAttribute(DataObject obj) {
        super(obj);
    }

    public RelationAttribute(String type) {
        super(type);
    }

    public RelationAttribute(String key, String language, String name) {
        this();
        setKey(key);
        setLanguage(language);
        setName(name);
        save();
    }

    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /* accessors *****************************************************/
    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

    public BigDecimal setID(BigDecimal id) {
        return getID();
    }

    public final String getAttribute() {
        return (String) get(ATTRIBUTE);
    }

    public final void setAttribute(String attribute) {
        set(ATTRIBUTE, attribute);
    }

    public final String getKey() {
        return (String) get(KEY);
    }

    public final void setKey(String key) {
        set(KEY, key);
    }

    public final String getLanguage() {
        return (String) get(LANGUAGE);
    }

    public final void setLanguage(String language) {
        set(LANGUAGE, language);
    }

    public final String getName() {
        return (String) get(NAME);
    }

    public final void setName(String name) {
        set(NAME, name);
    }

    public final String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public final void setDescription(String description) {
        set(DESCRIPTION, description);
    }

}
