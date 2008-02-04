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
package com.redhat.persistence;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.ObjectType;

/**
 * A generic class to persist and an appropriate adapter.
 *
 * @author <a href="mailto:ashah@redhat.com">Archit Shah</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class Generic {

    private ObjectType m_type;
    private Object m_id;

    public static class Adapter
        extends com.redhat.persistence.metadata.Adapter {

        public Object getObject(ObjectType type, PropertyMap properties) {
            return new Generic(type, properties.get(type.getProperty("id")));
        }

        public PropertyMap getProperties(Object obj) {
            Generic g = (Generic) obj;
            PropertyMap result = new PropertyMap(g.getType());
            result.put(g.getType().getProperty("id"), g.getID());
            return result;
        }

        public ObjectType getObjectType(Object obj) {
            return ((Generic) obj).getType();
        }
    }

    public Generic(ObjectType type, Object id) {
        m_type = type;
        m_id = id;
    }

    public ObjectType getType() {
        return m_type;
    }

    public Object getID() {
        return m_id;
    }

    public String toString() {
        return m_type + ": " + m_id;
    }

    public int hashCode() {
        return m_id.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Generic) {
            Generic g = (Generic) o;
            return g.getID().equals(getID()) && g.getType().equals(getType());
        }

        return false;
    }
}
