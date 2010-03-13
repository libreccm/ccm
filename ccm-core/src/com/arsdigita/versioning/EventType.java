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
import com.arsdigita.util.Assert;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// new versioning

final class EventType {

    private final static Map s_types = new HashMap(5);
    private DataObject m_dobj;
    

    // the following need to be kept in sync with
    // sql/default/versioning/insert-vcx_events.sql
    public final static int CREATE_SWITCH = 1;
    public final static EventType CREATE = newEventType(CREATE_SWITCH, "create");
    public final static int DELETE_SWITCH = 2;
    public final static EventType DELETE = newEventType(DELETE_SWITCH, "delete");
    public final static int ADD_SWITCH    = 3;
    public final static EventType ADD    = newEventType(ADD_SWITCH, "add");
    public final static int REMOVE_SWITCH = 4;
    public final static EventType REMOVE = newEventType(REMOVE_SWITCH, "remove");
    public final static int SET_SWITCH = 5;
    public final static EventType SET    = newEventType(SET_SWITCH, "set");

    private final static String DATA_TYPE = Constants.PDL_MODEL + ".EventType";

    // Method initialize() should only be run once.
    // Originally it had been controlled by the package this.initializer,
    // which uses the old initializer system and has been commented out in
    // enterprise.ini for a long time.
    // As a quick replacement we introduce the variable here.
    // Fixme: It might be necessary to controle it by the core initializer, so
    // CCM can be restarted in a servlet container using management extension
    // (and without restarting the container itself).
    // (2010-01-14, as of version 6.6.0)
    private static boolean s_hasRun = false;

    private final BigInteger m_id;
    private final String m_name;

    private EventType(BigInteger id, String name) {
        m_id = id;
        m_name = name;
    }

    private static EventType newEventType(int id, String name) {
        BigInteger iid = new BigInteger(String.valueOf(id));
        EventType result = new EventType(iid, name);
        s_types.put(iid, result);
        return result;
    }

    static void initialize() {
        // XXX FixMe
        // refers to the old style initializer of the package versioning which
        // is commented out of the enterprise.ini file (since an unkown time,
        // currently version 1.0.5
        // Must be dealt with internally here!
    //  if (Initializer.hasRun()) {
        if ( s_hasRun ) {
           throw new IllegalStateException("can't be called more than once");
        }
        for (Iterator ii=s_types.values().iterator(); ii.hasNext(); ) {
            EventType type = (EventType) ii.next();
            type.getDataObject();
        }
        s_hasRun = true;
    }

    synchronized DataObject getDataObject() {
        if ( m_dobj == null ) {
            m_dobj = SessionManager.getSession().retrieve(new OID(DATA_TYPE, m_id));
            m_dobj.disconnect();
        }

        return m_dobj;
    }

    static EventType getEventType(DataObject ev) {
        BigInteger id = (BigInteger) ev.get("id");
        Assert.isTrue(s_types.containsKey(id), "s_types.contains(id)");
        return (EventType) s_types.get(id);
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    int intValue() {
        return m_id.intValue();
    }

    public int hashCode() {
        return intValue();
    }

    public String toString() {
        return m_name;
    }
}
