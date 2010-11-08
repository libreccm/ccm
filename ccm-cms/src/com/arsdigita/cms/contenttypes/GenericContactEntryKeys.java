/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class GenericContactEntryKeys extends RelationAttributeCollection {

    public static final String ATTRIBUTE_NAME = "GenericContactEntryKeys";

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactEntryKeys() {
        super(ATTRIBUTE_NAME);
    }

    public GenericContactEntryKeys(String key) {
        super(ATTRIBUTE_NAME, key);
    }

    public GenericContactEntryKeys(DataCollection dataCollection) {
        super(dataCollection);
    }

}
