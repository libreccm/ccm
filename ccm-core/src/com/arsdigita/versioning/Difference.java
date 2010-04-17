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
package com.arsdigita.versioning;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Difference
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Difference.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class Difference {

    private static final String[] STATES = {"Created", "Modified", "Deleted"};

    private static final int CREATED = 0;
    private static final int MODIFIED = 1;
    private static final int DELETED = 2;

    private static final boolean compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == o2;
        } else {
            return o1.equals(o2);
        }
    }

    public class Change {

        private OID m_oid;
        private int m_state;
        private Map m_from = new HashMap();
        private Map m_to = new HashMap();
        private Map m_added = new HashMap();
        private Map m_removed = new HashMap();

        private Change(OID oid, int state) {
            m_oid = oid;
            m_state = state;
        }

        public OID getOID() {
            return m_oid;
        }

        public int getState() {
            return m_state;
        }

        boolean isEmpty() {
            if (isCreated() || isDeleted()) {
                return false;
            }

            if (m_from.size() > 0) {
                return false;
            }

            for (Iterator it = m_added.values().iterator(); it.hasNext(); ) {
                Collection c = (Collection) it.next();
                if (c.size() > 0) {
                    return false;
                }
            }

            for (Iterator it = m_removed.values().iterator(); it.hasNext(); ) {
                Collection c = (Collection) it.next();
                if (c.size() > 0) {
                    return false;
                }
            }

            return true;
        }

        Property toProp(String propertyName) {
            Property prop = m_oid.getObjectType().getProperty(propertyName);
            Assert.exists(prop, Property.class);
            return prop;
        }

        void setFrom(String prop, Object value) {
            setFrom(toProp(prop), value);
        }

        void setTo(String prop, Object value) {
            setTo(toProp(prop), value);
        }

        void clearFrom(String prop) {
            clearFrom(toProp(prop));
        }

        void clearFrom(Property prop) {
            m_from.remove(prop);
        }

        void setFrom(Property prop, Object value) {
            m_from.put(prop, value);
        }

        void setTo(Property prop, Object value) {
            if (compare(getFrom(prop), value)) {
                clearFrom(prop);
            } else {
                m_to.put(prop, value);
            }
        }

        public boolean isCreated() {
            return m_state == CREATED;
        }

        public boolean isDeleted() {
            return m_state == DELETED;
        }

        public boolean isModified(Property prop) {
            if (prop.isCollection()) {
                return getAdded(prop).size() > 0
                    || getRemoved(prop).size() > 0;
            } else {
                return m_from.containsKey(prop);
            }
        }

        private Object get(Map map, Property prop) {
            if (map.containsKey(prop)) {
                return map.get(prop);
            } else if (isDeleted()) {
                return null;
            } else {
                DataObject dobj = SessionManager.getSession().retrieve(m_oid);
                if (dobj == null) {
                    return null;
                } else {
                    Object result = dobj.get(prop.getName());
                    if (result instanceof DataObject) {
                        result = ((DataObject) result).getOID();
                    }
                    return result;
                }
            }
        }

        public Object getFrom(Property prop) {
            return get(m_from, prop);
        }

        public Object getTo(Property prop) {
            return get(m_to, prop);
        }

        private Collection getCollection(Map map, Property prop) {
            ArrayList result = (ArrayList) map.get(prop);
            if (result == null) {
                result = new ArrayList();
                map.put(prop, result);
            }

            return result;
        }

        Collection getAdded(String prop) {
            return getAdded(toProp(prop));
        }

        Collection getRemoved(String prop) {
            return getRemoved(toProp(prop));
        }

        public Collection getAdded(Property prop) {
            return getCollection(m_added, prop);
        }

        public Collection getRemoved(Property prop) {
            return getCollection(m_removed, prop);
        }

        public String toString() {
            return "<change state=" + STATES[m_state] + " oid=" + m_oid +
                "from=" + m_from + " to=" + m_to + " added=" + m_added +
                " removed=" + m_removed + ">";
        }

    }

    private ArrayList m_changes = new ArrayList();

    Change create(OID oid) {
        return new Change(oid, CREATED);
    }

    Change delete(OID oid) {
        return new Change(oid, DELETED);
    }

    Change modify(OID oid) {
        return new Change(oid, MODIFIED);
    }

    void addChange(Change change) {
        if (!change.isEmpty()) {
            m_changes.add(change);
        }
    }

    public Collection getChanges() {
        return m_changes;
    }

}
