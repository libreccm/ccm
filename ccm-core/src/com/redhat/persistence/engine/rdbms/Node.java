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

import com.redhat.persistence.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

class Node {

    public final static String versionId = "$Id: Node.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private HashSet m_dependencies = new HashSet();
    private ArrayList m_events = new ArrayList();

    public void addEvent(Event ev) {
        if (m_events.contains(ev)) { return; }
        m_events.add(ev);
    }

    public Collection getEvents() {
        return m_events;
    }

    public void addEvents(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            addEvent((Event) it.next());
        }
    }

    public void addDependency(Event ev) {
        if (ev == null) { return; }
        if (m_dependencies.contains(ev) || m_events.contains(ev)) {
            return;
        }
        m_dependencies.add(ev);
    }

    public Collection getDependencies() {
        return m_dependencies;
    }

    public void addDependencies(Collection c) {
        if (c == null) { return; }
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            addDependency((Event) it.next());
        }
    }

    private boolean containsAny(Collection c, Collection candidates) {
        for (Iterator it = candidates.iterator(); it.hasNext(); ) {
            if (c.contains(it.next())) { return true; }
        }
        return false;
    }

    public void merge(Node from) {
        if (from == this) {
            throw new IllegalArgumentException
                ("cannot merge a node with itself");
        }
        if (containsAny(m_events, from.getDependencies())) {
            addEvents(from.getEvents());
        } else {
            ArrayList evs = m_events;
            m_events = new ArrayList(from.m_events);
            addEvents(evs);
        }
        m_dependencies.addAll(from.getDependencies());
        m_dependencies.removeAll(m_events);
    }

    public String toString() {
        return "<node events: "  + m_events + "\n        deps: " +
            m_dependencies + ">";
    }

}
