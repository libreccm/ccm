/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class RelationAttribute extends ContentItem {

    public static final String ATTRIBUTE = "attribute";
    public static final String KEY = "KEY";
    public static final String LANGUAGE = "lang";
    //public static final String NAME = "name";
    public static final String DESCRIPTION = "DESCRIPTION";
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

    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /* accessors *****************************************************/
    public String getAttribute() {
        return (String) get(ATTRIBUTE);
    }

    public void setAttribute(String attribute) {
        set(ATTRIBUTE, attribute);
    }

    public String getKey() {
        return (String) get(KEY);
    }

    public void setKey(String key) {
        set(KEY, key);
    }

    @Override
    public String getLanguage() {
        return (String) get(LANGUAGE);
    }

    @Override
    public void setLanguage(String language) {
        set(LANGUAGE, language);
    }

//    @Override
//    public String getName() {
//        return (String) get(NAME);
//    }
//
//    @Override
//    public void setName(String name) {
//        set(NAME, name);
//    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }




}
