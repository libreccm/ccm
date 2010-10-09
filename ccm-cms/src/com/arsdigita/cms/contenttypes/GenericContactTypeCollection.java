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

    public static final String CONTACT_ORDER = "contact_order";

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactTypeCollection() {
        super("person");
    }

    public GenericContactTypeCollection(String key) {
        super("person", key);
    }

    public GenericContactTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

//    public void setContactOrder(String contact_order) {
//        set(CONTACT_ORDER, contact_order);
//    }

    public String getContactOrder() {
        return (String) get(CONTACT_ORDER);
    }
}
