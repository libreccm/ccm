package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.kernel.ACSObject;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A ListModel that allows you to add elements to it
 * (presumably from a ListModelBuilder).  Only ACSObjects
 * may be added to it.  getKey() returns the ID of the 
 * current element, and getElement() returns the ACSObject
 * itself.  This ListModel will not contain any duplicate 
 * elements (since the backing HashSet cannot contain duplicates). 
 *
 * @author Mike Bonnet <mikeb@redhat.com>
 **/
public class HashSetListModel implements ListModel {
    Set m_set;
    Iterator m_iter = null;
    ACSObject m_currentObj = null;

    public HashSetListModel() {
        m_set = new HashSet();
    }

    public boolean next() {
        if (m_iter == null) {
            m_iter = m_set.iterator();
        }
        boolean hasNext = m_iter.hasNext();
        if (hasNext) {
            m_currentObj = (ACSObject) m_iter.next();
        } else {
            m_currentObj = null;
        }
        return hasNext;
    }

    public String getKey() {
        return m_currentObj.getID().toString();
    }

    public Object getElement() {
        return m_currentObj;
    }

    public boolean add(ACSObject obj) {
        if (m_iter != null) {
            throw new IllegalStateException("Elements cannot be added to the ListModel " +
                                            "once next() has been called.");
        }
        return m_set.add(obj);
    }

    public boolean remove(ACSObject obj) {
        if (m_iter != null) {
            throw new IllegalStateException("Elements cannot be removed from the ListModel " +
                                            "once next() has been called.");
        }
        return m_set.remove(obj);
    }

    public int size() {
        return m_set.size();
    }

    public boolean contains(ACSObject obj) {
        return m_set.contains(obj);
    }
}
