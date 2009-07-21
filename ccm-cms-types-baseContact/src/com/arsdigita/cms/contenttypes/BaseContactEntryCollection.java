/*
 * BaseContactEntryCollection.java
 *
 * Created on 13. Mai 2009, 12:32
 *
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;

        
/**
 *
 * @author quasi
 */
public class BaseContactEntryCollection extends DomainCollection {

    /**
     * Creates a new instance of BaseContactEntryCollection
     */
    public BaseContactEntryCollection(BaseContact baseContact) {
        this((DataCollection) baseContact.getContactEntries());
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
        return new BaseContactEntry(m_dataCollection.getDataObject());
    }
    
}
