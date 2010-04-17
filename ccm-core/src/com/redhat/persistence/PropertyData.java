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

import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: PropertyData.java 287 2005-02-22 00:29:02Z sskracic $
 **/

class PropertyData {

    final private ObjectData m_odata;
    final private Property m_prop;
    private Object m_value;
    final private List m_dependentEvents = new LinkedList();

    public PropertyData(ObjectData odata, Property prop, Object value) {
        m_odata = odata;
        m_prop = prop;
        m_value = value;

        m_odata.addPropertyData(m_prop, this);
    }

    ObjectData getObjectData() {
        return m_odata;
    }

    public Session getSession() {
        return m_odata.getSession();
    }

    private Object getObject() { return m_odata.getObject(); }

    public Property getProperty() {
        return m_prop;
    }

    public void setValue(Object value) {
        if (getProperty().isCollection()) {
            throw new IllegalStateException
                ("setting value of collections is not allowed. "
                 + "property: " + m_prop + " value: " + value);
        }
        m_value = value;
    }

    public Object getValue() {
        return m_value;
    }

    public Object get() {
        if (!m_prop.isCollection()) {
            PropertyEvent ev = getSession().getEventStream().getLastEvent
                (getObject(), getProperty());
            if (ev == null) { return m_value; }

            return ev.getArgument();
        } else {
            return m_value;
        }
    }

    public boolean isFlushed() {
        if (m_prop.isCollection()) {
            Collection evs = getSession().getEventStream().getCurrentEvents
                (getObject(), getProperty());
            return evs.size() == 0;
        } else {
            return getSession().getEventStream().getLastEvent
                (getObject(), getProperty()) == null;
        }
    }

    void addNotNullDependent(Event ev) {
        getSession().addViolation(this);
        m_dependentEvents.add(ev);
    }

    Iterator getDependentEvents() { return m_dependentEvents.iterator(); }

    void transferNotNullDependentEvents(Event ev) {
        for (Iterator it = m_dependentEvents.iterator(); it.hasNext(); ) {
            ev.addDependent((Event) it.next());
        }

        m_dependentEvents.clear();
        getSession().removeViolation(this);
    }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    void dump(PrintWriter out) {
        out.print("    ");
        out.print(m_prop.getName());
        out.print(" = ");
        out.println(m_value);
        if (m_prop.isCollection()) {
	    boolean first = true;
            for (Iterator it = getSession().getEventStream().
                     getCurrentEvents(getObject(), getProperty()).iterator();
                 it.hasNext(); ) {
		if (first) {
		    first = false;
		    out.println("    Current Property Events:");
		}
                ((Event) it.next()).dump(out);
            }
        } else {
            Event ev = getSession().getEventStream().getLastEvent
                (getObject(), getProperty());
            if (ev != null) {
		out.println("    Current Property Events:");
		ev.dump(out);
	    }
        }
    }

    public String toString() {
	return "<pdata " + getObject() + "." + getProperty() + ">";
    }

}
