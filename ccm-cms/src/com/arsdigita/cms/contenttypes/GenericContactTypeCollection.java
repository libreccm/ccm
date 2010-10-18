/*
 * GenericContactEntryCollection.java
 *
 * Created on 13. Mai 2009, 12:32
 *
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author quasi
 */
public class GenericContactTypeCollection extends RelationAttributeCollection {

    public static final String CONTACTS_KEY = GenericPerson.CONTACTS_KEY;
    public static final String CONTACTS_ORDER = GenericPerson.CONTACTS_ORDER;
    public static final String ATTRIBUTE_NAME = "person";

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactTypeCollection() {
        super(ATTRIBUTE_NAME);
    }

    public GenericContactTypeCollection(String key) {
        super(ATTRIBUTE_NAME, CONTACTS_KEY);
    }

    public GenericContactTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

//    public void setContactOrder(String contact_order) {
//        set(CONTACT_ORDER, contact_order);
//    }

    public String getContactOrder() {
        return (String) get(CONTACTS_ORDER);
    }
}
