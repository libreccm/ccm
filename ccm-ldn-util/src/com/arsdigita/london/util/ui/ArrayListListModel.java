package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.kernel.ACSObject;

import java.util.ArrayList;

/**
 * A ListModel that allows you to add elements to it
 * (presumably from a ListModelBuilder).  Only ACSObjects
 * may be added to it.  getKey() returns the ID of the 
 * current element, and getElement() returns the ACSObject
 * itself.
 *
 * @author Mike Bonnet <mikeb@arsdigita.com>
 **/
public class ArrayListListModel implements ListModel {
    ArrayList m_list;
    int index;
    boolean accessed;

    public ArrayListListModel() {
        m_list = new ArrayList();
        index = -1;
        accessed = false;
    }

    public boolean next() {
        index++;
        accessed = true;
        if (m_list.size() > index) {
            return true;
        } else {
            return false;
        }
    }

    public String getKey() {
        return ((ACSObject) m_list.get(index)).getID().toString();
    }

    public Object getElement() {
        return m_list.get(index);
    }

    public void add(Object obj) {
        if (accessed) {
            throw new IllegalStateException("Elements cannot be added to the ListModel " +
                                            "once next() has been called.");
        }
        m_list.add(obj);
    }

    public int size() {
        return m_list.size();
    }

    public boolean contains(ACSObject obj) {
        return m_list.contains(obj);
    }
}
