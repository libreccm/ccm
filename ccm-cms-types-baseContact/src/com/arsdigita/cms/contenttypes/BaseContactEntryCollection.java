/*
 * BaseContactEntryCollection.java
 *
 * Created on 13. Mai 2009, 12:32
 *
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;

        
/**
 *
 * @author quasi
 */
public class BaseContactEntryCollection extends ACSObjectCollection {
    
    /**
     * Creates a new instance of BaseContactEntryCollection
     */
    public BaseContactEntryCollection(BaseContact baseContact) {
        super((DataCollection) baseContact.getContactEntries());
    }
    
    public BaseContactEntryCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    public final String getKey() {
        return (String) getBaseContactEntry().getKey();
    }
    
    public final String getDescription() {
        return (String) getBaseContactEntry().getDescription();
    }
    
    public final String getValue() {
        return (String) getBaseContactEntry().getValue();
    }
    
    public BaseContactEntry getBaseContactEntry() {
        return (BaseContactEntry) getDomainObject();
    }
    
}
