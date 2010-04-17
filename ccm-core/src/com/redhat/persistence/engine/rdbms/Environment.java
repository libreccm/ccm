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
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.JoinFrom;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Static;
import com.redhat.persistence.metadata.Value;
import com.redhat.persistence.metadata.Qualias;
import java.util.HashMap;

/**
 * Environment
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Environment.java 287 2005-02-22 00:29:02Z sskracic $
 **/

class Environment {

    private RDBMSEngine m_engine;
    private ObjectMap m_om;
    private HashMap m_values = new HashMap();
    private HashMap m_types = new HashMap();

    public Environment(RDBMSEngine engine, ObjectMap om) {
        m_engine = engine;
        m_om = om;
    }

    RDBMSEngine getEngine() {
        return m_engine;
    }

    public boolean contains(Path path) {
        return m_values.containsKey(path);
    }

    public void set(Path parameter, Object value) {
        final int type[] = {
            RDBMSEngine.getType(m_engine.getSession().getRoot(), value)
        };

        if (m_om != null) {
            Path path = Path.get(parameter.getPath().substring(1));

            ObjectType ot = m_om.getObjectType().getType(path);
            if (ot != null) {
                type[0] = RDBMSEngine.getType
                    (m_om.getRoot(), ot.getJavaClass());
            }

            Mapping m = m_om.getMapping(path);
            if (m != null) {
                m.dispatch(new Mapping.Switch() {
                    public void onValue(Value v) {
                        type[0] = v.getColumn().getType();
                    }
                    public void onJoinTo(JoinTo j) {}
                    public void onJoinFrom(JoinFrom j) {}
                    public void onJoinThrough(JoinThrough j) {}
                    public void onStatic(Static s) {}
                    public void onQualias(Qualias q) {
                        // XXX do real read only properties from session
                    }
                });
            }
        }


        set(parameter, value, type[0]);
    }

    public void set(Path parameter, Object value, int type) {
        m_values.put(parameter, value);
        m_types.put(parameter, new Integer(type));
    }

    public Object get(Path parameter) {
        return m_values.get(parameter);
    }

    public int getType(Path parameter) {
        return ((Integer) m_types.get(parameter)).intValue();
    }

    public String toString() {
        return m_values.toString();
    }
}
