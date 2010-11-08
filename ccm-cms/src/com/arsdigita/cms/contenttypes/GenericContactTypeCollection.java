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

    public static final String ATTRIBUTE_NAME = "GenericContactTypes";

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactTypeCollection() {
        super(ATTRIBUTE_NAME);
    }

    public GenericContactTypeCollection(String key) {
        super(ATTRIBUTE_NAME, key);
    }

    public GenericContactTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
}
