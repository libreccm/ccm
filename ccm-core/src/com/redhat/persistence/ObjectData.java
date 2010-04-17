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
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: ObjectData.java 287 2005-02-22 00:29:02Z sskracic $
 **/

class ObjectData {

    private static final Logger LOG = Logger.getLogger(ObjectData.class);

    private final Session m_ssn;
    private Object m_object;

    static class State {
        private String m_name;

        private State(String name) { m_name = name; }

        public String toString() { return m_name; }
    };

    public static final State INFANTILE = new State("infantile");
    public static final State NUBILE = new State("nubile");
    public static final State AGILE = new State("agile");
    public static final State SENILE = new State("senile");
    public static final State DEAD = new State("dead");

    private boolean m_startedNew = false;

    private State m_state;

    private HashMap m_pdata = new HashMap();

    public ObjectData(Session ssn, Object object, State state) {
        m_ssn = ssn;
        m_object = object;
        setState(state);

        m_ssn.addObjectData(this);
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object getObject() {
        return m_object;
    }

    void setObject(Object object) {
        m_object = object;
    }

    public void addPropertyData(Property p, PropertyData pd) {
        m_pdata.put(p, pd);
    }

    public PropertyData getPropertyData(Property prop) {
        return (PropertyData) m_pdata.get(prop);
    }

    public boolean hasPropertyData(Property prop) {
        return m_pdata.containsKey(prop);
    }

    public boolean isNew() { return m_startedNew; }

    public boolean isDeleted() { return isDead() || isSenile(); }

    public boolean isModified() { return !isNubile(); }

    public boolean isInfantile() { return m_state.equals(INFANTILE); }

    public boolean isNubile() { return m_state.equals(NUBILE); }

    public boolean isAgile() { return m_state.equals(AGILE); }

    public boolean isSenile() { return m_state.equals(SENILE); }

    public boolean isDead() { return m_state.equals(DEAD); }

    public boolean isFlushed() {
        if (getSession().getEventStream().getLastEvent(getObject()) != null) {
            return false;
        }

        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            if (!pdata.isFlushed()) { return false; }
        }

        return true;
    }

    void setState(State state) {
        if (state.equals(INFANTILE)) {
            m_startedNew = true;
        }

        m_state = state;
    }

    public State getState() { return m_state; }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    void dump(PrintWriter out) {
        out.println("    object = " + getObject());
        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            pdata.dump(out);
        }

        Event ev = getSession().getEventStream().getLastEvent(getObject());
        if (ev != null) {
            out.println("    Current Object Event:");
            ev.dump(out);
        }
    }

}
