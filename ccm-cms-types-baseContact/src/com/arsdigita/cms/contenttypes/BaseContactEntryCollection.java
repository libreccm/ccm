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
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

        
/**
 *
 * @author quasi
 */
public class BaseContactEntryCollection extends DomainCollection {

    private TreeSet m_sortedCollection = new TreeSet(new BaseContactEntryComparator());
    private Iterator m_iterator;
    private boolean m_firstElem;
    private BaseContactEntry m_currentBaseContactEntry;
    
    /**
     * Creates a new instance of BaseContactEntryCollection
     */
    public BaseContactEntryCollection(BaseContact baseContact) {
        this((DataCollection) baseContact.getContactEntries());
    }
    
    public BaseContactEntryCollection(DataCollection dataCollection) {
        super(dataCollection);
        
        // Now copy all objects from m_dataCollection to the sorting TreeSet
        this.sortCollection();
    }
    
    public boolean next() {
        boolean retVal = m_iterator.hasNext();
        if(retVal) {
            m_currentBaseContactEntry = (BaseContactEntry)m_iterator.next();
            m_firstElem = false;
        }
        return retVal;
    }
    
    public boolean isBeforeFirst() {
        return false;   
    }
    
    public boolean isAfterLast() {
        return false;   
    }
    
    public boolean isFirst() {
        return m_firstElem;
    }
    
    public boolean isLast() {
        return !m_iterator.hasNext();
    }
    
    public boolean isEmpty() {
        return m_sortedCollection.isEmpty();   
    }

    public long size() {
        return m_sortedCollection.size();
    }
    
    public void reset() {
        super.reset();
        m_iterator = null;
        m_currentBaseContactEntry = null;
        m_sortedCollection = new TreeSet(new BaseContactEntryComparator());
        this.sortCollection();
    }
    
    public void rewind() {
        m_iterator = m_sortedCollection.iterator();
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
//        return new BaseContactEntry(m_dataCollection.getDataObject());
        return m_currentBaseContactEntry;
    }

    
    private void sortCollection() {
        
        m_dataCollection.rewind();
        
        while(m_dataCollection.next()) {
            m_sortedCollection.add(new BaseContactEntry(m_dataCollection.getDataObject()));
        }
        
        m_iterator = m_sortedCollection.iterator();
        m_firstElem = true;
    }


    private class BaseContactEntryComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            int retVal = 0;
            if(o1 instanceof BaseContactEntry && o2 instanceof BaseContactEntry) {
                BaseContactEntry bc1 = (BaseContactEntry)o1;
                BaseContactEntry bc2 = (BaseContactEntry)o2;
                retVal = BaseContact.getConfig().getKeyIndex(bc1.getKey()) - 
                            BaseContact.getConfig().getKeyIndex(bc2.getKey());
                if(retVal == 0) {
                    retVal=-1;
                }
            }
            return retVal;
        }
    }
}
