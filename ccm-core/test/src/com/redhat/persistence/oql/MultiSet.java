/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MultiSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: MultiSet.java 740 2005-09-02 10:13:54Z sskracic $
 **/

class MultiSet {

    private Map m_entries;

    MultiSet() {
        m_entries = new HashMap();
    }

    MultiSet(MultiSet ms) {
        m_entries = new HashMap();
        m_entries.putAll(ms.m_entries);
    }

    public void add(Object obj) {
        Integer count = (Integer) m_entries.get(obj);
        if (count == null) {
            count = new Integer(1);
        } else {
            count = new Integer(count.intValue() + 1);
        }
        m_entries.put(obj, count);
    }

    public void addAll(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            add(it.next());
        }
    }

    public void remove(Object obj) {
        Integer count = (Integer) m_entries.get(obj);
        if (count != null) {
            count = new Integer(count.intValue() - 1);
            if (count.intValue() == 0) {
                m_entries.remove(obj);
            } else {
                m_entries.put(obj, count);
            }
        }
    }

    public void removeAll(MultiSet ms) {
        Collection entries = ms.m_entries.entrySet();
        for (Iterator it = entries.iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Object obj = me.getKey();
            Integer count = (Integer) me.getValue();
            for (int i = 0; i < count.intValue(); i++) {
                remove(obj);
            }
        }
    }

    public int hashCode() {
        return m_entries.hashCode();
    }

    public boolean equals(Object o) {
        MultiSet ms = (MultiSet) o;
        return m_entries.equals(ms.m_entries);
    }

    public String toString() {
        return m_entries.toString();
    }

}
