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
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;
import java.util.HashMap;

/**
 * EventNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class EventNd extends Node {

    

    private static final HashMap TYPES = new HashMap();

    public static final class Type {

        private String m_name;

        private Type(String name) {
            m_name = name;
            TYPES.put(m_name, this);
        }

        public String toString() {
            return m_name;
        }

    }

    public static final Type getType(String name) {
        return (Type) TYPES.get(name);
    }

    public static final Type INSERT = new Type("insert");
    public static final Type UPDATE = new Type("update");
    public static final Type DELETE = new Type("delete");
    public static final Type ADD = new Type("add");
    public static final Type REMOVE = new Type("remove");
    public static final Type CLEAR = new Type("clear");
    public static final Type RETRIEVE = new Type("retrieve");
    public static final Type RETRIEVE_ALL = new Type("all");
    public static final Type RETRIEVE_ATTRIBUTES = new Type("attributes");

    public static final Field SQL =
        new Field(EventNd.class, "sql", SQLBlockNd.class);
    public static final Field NAME =
        new Field(EventNd.class, "name", IdentifierNd.class, 0, 1);
    public static final Field SUPERS =
        new Field(EventNd.class, "supers", SuperNd.class);

    private Type m_type;

    public void setType(Type type) {
        m_type = type;
    }

    public Type getType() {
        return m_type;
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public Collection getSQL() {
        return (Collection) get(SQL);
    }

    public boolean isSingle() {
        return m_type.equals(RETRIEVE_ALL) ||
            (m_type.equals(RETRIEVE) && getName() != null);
    }

    public Collection getSupers() {
        return (Collection) get(SUPERS);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onEvent(this);
    }

}
