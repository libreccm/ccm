/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;

/**
 * Operation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 **/

public class Operation {

    public final static String versionId = "$Id: Operation.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static interface Switch {
        void onCreate();

        void onDelete();

        void onSet();

        void onAdd();

        void onRemove();
    }

    public static abstract class Type {

        private String m_name;
        private int m_sortKey;

        private Type(String name, int sortKey) {
            m_name = name;
            m_sortKey = sortKey;
        }

        public abstract void dispatch(Switch sw);

        int sortKey() {
            return m_sortKey;
        }

        public String toString() {
            return m_name;
        }

    }

    public static final Type CREATE = new Type("create", 0) {
        public void dispatch(Switch sw) {
            sw.onCreate();
        }
    };
    public static final Type SET = new Type("set", 1) {
        public void dispatch(Switch sw) {
            sw.onSet();
        }
    };
    public static final Type ADD = new Type("add", 2) {
        public void dispatch(Switch sw) {
            sw.onAdd();
        }
    };
    public static final Type REMOVE = new Type("remove", 3) {
        public void dispatch(Switch sw) {
            sw.onRemove();
        }
    };
    public static final Type DELETE = new Type("delete", 4) {
        public void dispatch(Switch sw) {
            sw.onDelete();
        }
    };

    private Type m_type;
    private OID m_object;
    private Property m_property;
    private Object m_argument;

    Operation(Type type, OID object) {
        m_type = type;
        m_object = object;
        m_property = null;
        m_argument = null;
    }

    Operation(Type type, OID object, String property, Object argument) {
        m_type = type;
        m_object = object;
        m_property = object.getObjectType().getProperty(property);
        m_argument = argument;
    }

    public Type getType() {
        return m_type;
    }

    public OID getObject() {
        return m_object;
    }

    public Property getProperty() {
        return m_property;
    }

    public Object getArgument() {
        return m_argument;
    }

}
