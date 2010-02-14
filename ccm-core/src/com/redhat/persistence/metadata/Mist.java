/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.redhat.persistence.metadata;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mist
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/
class Mist extends AbstractList {

    public final static String versionId = "$Id: Mist.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private Object m_parent = null;
    private ArrayList m_children = new ArrayList();
    private HashMap m_childrenMap = new HashMap();

    public Mist(Object parent) {
        m_parent = parent;
    }

    private Object check(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("null child");
        }

        if (!(o instanceof Element)) {
            throw new IllegalArgumentException("not an element");
        }

        Element child = (Element) o;
        Object key = child.getElementKey();

        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        return key;
    }

    public void add(int index, Object o) {
        Object key = check(o);
        Element child = (Element) o;

        if (child.getParent() != null) {
            throw new IllegalArgumentException("child is already contained");
        }

        if (m_childrenMap.containsKey(key)) {
            throw new IllegalArgumentException("duplicate key: " + key);
        }

        m_children.add(index, child);
        m_childrenMap.put(key, child);
        child.setParent(m_parent);
    }

    public Object get(int index) {
        return m_children.get(index);
    }

    public int size() {
        return m_children.size();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        Object key = check(o);
        Element child = (Element) o;

        // Quasimodo: BEGIN
        // Diabled because it prevents to delete item from the list.
        // The result is always false because child.getParent() gets
        // the parent of the Class. It doesn't say anything about the
        // membership to this List.
        /*
        if (!this.equals(child.getParent())) {
        throw new IllegalArgumentException
        ("child does not belong to this parent");
        }
         */
        // Qusimodo: END

        if (!this.containsKey(key)) {
            throw new IllegalArgumentException("child does not belong to this parent");
        }
        
        m_children.remove(o);
        m_childrenMap.remove(key);
        child.setParent(null);
        return true;
    }

    public Object[] toArray() {
        return m_children.toArray();
    }

    public Object[] toArray(Object[] a) {
        return m_children.toArray(a);
    }

    public Object get(Object key) {
        return m_childrenMap.get(key);
    }

    public boolean containsKey(Object key) {
        return m_childrenMap.containsKey(key);
    }

    public String toString() {
        return m_children.toString();
    }
}
