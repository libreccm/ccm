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

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: PropertyEvent.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    final private Property m_prop;
    final private Object m_arg;
    private PropertyData m_pdata;
    private PropertyEvent m_origin;

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg,
                  PropertyEvent origin) {
        super(ssn, obj);
        m_prop = prop;
        m_arg = arg;
        m_origin = origin;

        if (arg != null) {
            ObjectType expected = prop.getType();
            ObjectType actual = getSession().getObjectType(arg);
            if (!actual.isSubtypeOf(expected)) {
                throw new TypeException
                    (ProtoException.VALUE, expected, actual, arg);
            }
        }

        if (origin != null) {
            origin.addDependent(this);
            this.addDependent(origin);
        }
    }

    public Property getProperty() {
        return m_prop;
    }

    public Object getArgument() {
        return m_arg;
    }

    void setPropertyData(PropertyData pdata) {
        m_pdata = pdata;
    }

    PropertyData getPropertyData() {
        return m_pdata;
    }

    ObjectData getObjectData() {
        if (m_pdata == null) { return null; }
        return m_pdata.getObjectData();
    }

    ObjectData getArgumentObjectData() {
        if (getArgument() == null) { return null; }
        return getSession().getObjectData(getArgument());
    }

    void prepare() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
    }

    void activate() {
        // WAW
        PropertyEvent prev = getSession().getEventStream().
            getLastEvent(this);
        if (prev != null) { prev.addDependent(this); }

        // connect event to session data
        getSession().getEventStream().add(this);

        // update object data state
        if (getObjectData().isNubile()) {
            getObjectData().setState(ObjectData.AGILE);
        }

        // object existence
        ObjectData od = getObjectData();
        if (od.isInfantile()) {
            CreateEvent ce = (CreateEvent)
                getSession().getEventStream().getLastEvent(getObject());
            ce.addDependent(this);
        }

        // arg existence
        ObjectData arg = getArgumentObjectData();
        if (arg != null) {
            if (arg.isInfantile()) {
                CreateEvent ce = (CreateEvent) getSession().getEventStream()
                    .getLastEvent(getArgument());
                ce.addDependent(this);
            }
        }
    }

    void sync() {
        getSession().getEventStream().remove(this);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.print(getName());
        out.print("(");
        out.print(m_arg);
        out.println(")");
    }

    public String toString() {
        return getName() + " " + getObject() + "." + getProperty().getName() +
            " " + getArgument();
    }
}
