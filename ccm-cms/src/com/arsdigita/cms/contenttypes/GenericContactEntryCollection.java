/*
 * GenericContactEntryCollection.java
 *
 * Created on 13. Mai 2009, 12:32
 *
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author quasi
 */
public class GenericContactEntryCollection extends DomainCollection {

    private TreeSet m_sortedCollection = new TreeSet(new ContactEntryComparator());
    private Iterator m_iterator;
    private boolean m_firstElem;
    private GenericContactEntry m_currentContactEntry;

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactEntryCollection(GenericContact contact) {
        this((DataCollection) contact.getContactEntries());
    }

    public GenericContactEntryCollection(DataCollection dataCollection) {
        super(dataCollection);

        // Now copy all objects from m_dataCollection to the sorting TreeSet
        this.sortCollection();
    }

    @Override
    public boolean next() {
        boolean retVal = m_iterator.hasNext();
        if (retVal) {
            m_currentContactEntry = (GenericContactEntry) m_iterator.next();
            m_firstElem = false;
        }
        return retVal;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return m_firstElem;
    }

    @Override
    public boolean isLast() {
        return !m_iterator.hasNext();
    }

    @Override
    public boolean isEmpty() {
        return m_sortedCollection.isEmpty();
    }

    @Override
    public long size() {
        return m_sortedCollection.size();
    }

    @Override
    public void reset() {
        super.reset();
        m_iterator = null;
        m_currentContactEntry = null;
        m_sortedCollection = new TreeSet(new ContactEntryComparator());
        this.sortCollection();
    }

    @Override
    public void rewind() {
        m_iterator = m_sortedCollection.iterator();
    }

    public final String getKey() {
        return (String) getContactEntry().getKey();
    }

    public final String getDescription() {
        return (String) getContactEntry().getDescription();
    }

    public final String getValue() {
        return (String) getContactEntry().getValue();
    }

    public GenericContactEntry getContactEntry() {
//        return new GenericContactEntry(m_dataCollection.getDataObject());
        return m_currentContactEntry;
    }

    private void sortCollection() {

        m_dataCollection.rewind();

        while (m_dataCollection.next()) {
            m_sortedCollection.add(new GenericContactEntry(m_dataCollection.getDataObject()));
        }

        m_iterator = m_sortedCollection.iterator();
        m_firstElem = true;
    }

    private class ContactEntryComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int retVal = 0;
            if (o1 instanceof GenericContactEntry && o2 instanceof GenericContactEntry) {
                GenericContactEntry bc1 = (GenericContactEntry) o1;
                GenericContactEntry bc2 = (GenericContactEntry) o2;
                retVal = GenericContact.getConfig().getKeyIndex(bc1.getKey())
                        - GenericContact.getConfig().getKeyIndex(bc2.getKey());
                if (retVal == 0) {
                    retVal = -1;
                }
            }
            return retVal;
        }
    }
}
