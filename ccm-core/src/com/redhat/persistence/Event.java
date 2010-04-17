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

import com.redhat.persistence.metadata.ObjectMap;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Event.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public abstract class Event {

    private static final Logger LOG = Logger.getLogger(Event.class);

    public static abstract class Switch {

        public abstract void onCreate(CreateEvent e);

        public abstract void onDelete(DeleteEvent e);

        public abstract void onSet(SetEvent e);

        public abstract void onAdd(AddEvent e);

        public abstract void onRemove(RemoveEvent e);

    }

    private Session m_ssn;
    private Object m_obj;

    private List m_dependentEvents = new ArrayList();

    // used by Session for flushing
    boolean m_flushable = false;

    Event(Session ssn, Object obj) {
        m_ssn = ssn;
        m_obj = obj;
        if (m_obj == null) {
            throw new NullException(ProtoException.OBJECT);
        }
    }

    public final Session getSession() {
        return m_ssn;
    }

    public final Object getObject() {
        ObjectData od = getObjectData();
        if (od == null) {
            return m_obj;
        } else {
            return getObjectData().getObject();
        }
    }

    public final ObjectMap getObjectMap() {
        return m_ssn.getObjectMap(m_obj);
    }

    public abstract void dispatch(Switch sw);

    final void addDependent(Event dependent) {
        m_dependentEvents.add(dependent);
    }

    final Iterator getDependentEvents() {
        return m_dependentEvents.iterator();
    }

    abstract ObjectData getObjectData();

    abstract void prepare();

    abstract void activate();

    abstract void sync();

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    abstract void dump(PrintWriter out);

    abstract String getName();

    final void log() {
        if (LOG.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();

            int level = m_ssn.getLevel();
            for (int i = 0; i < level + 1; i++) {
                msg.append("  ");
            }

            msg.append(this);
            LOG.debug(msg.toString());
        }
    }

}
