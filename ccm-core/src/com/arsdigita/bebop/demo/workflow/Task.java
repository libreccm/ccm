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
package com.arsdigita.bebop.demo.workflow;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class is a standin for the proper workflow task domain
 * object. Tasks have exactly one <code>process</code> they belong to, a
 * unique <code>key</code> for internal identification, a <code>name</code>
 * to display to the user, one <code>assignee</code> to fake assignments,
 * and a set of tasks they depend on.
 *
 * <p> <b>Warning:</b> This class is only meant for demo purposes. It's use
 * of synchronization will make sure that it becomes a bottleneck under
 * load.
 *
 * @author David Lutterkort
 * @version $Id: Task.java 2089 2010-04-17 07:55:43Z pboy $
 */
public class Task {

    private static int s_seq = 1;

    private String m_key;
    private String m_name;
    private String m_assignee;
    private ArrayList m_deps;
    private Process m_process;

    public Task(String name) {
        this(getNextKey(), name);
    }

    public Task(String key, String name) {
        m_key = key;
        m_name = name;
        m_deps = new ArrayList();
    }

    public final synchronized Process getProcess() {
        return m_process;
    }

    public final synchronized void setProcess(Process  v) {
        m_process = v;
    }

    public final synchronized String getKey() {
        return m_key;
    }

    public final synchronized String getName() {
        return m_name;
    }

    public final synchronized void setName(String  v) {
        m_name = v;
    }

    public final synchronized void setAssignee(String a) {
        m_assignee = a;
    }

    public final synchronized String getAssignee() {
        return m_assignee;
    }

    public synchronized void addDependency(Task t) {
        for (Iterator i = dependencies(); i.hasNext(); ) {
            Task d = (Task) i.next();
            if (t.getKey().equals(d.getKey())) {
                return;
            }
        }
        m_deps.add(t);
    }

    public synchronized void removeDependency(Task t) {
        m_deps.remove(m_deps.indexOf(t));
    }

    public synchronized Task getDependency(String key) {
        for (Iterator i = dependencies(); i.hasNext(); ) {
            Task d = (Task) i.next();
            if (key.equals(d.getKey())) {
                return d;
            }
        }
        return null;
    }

    public synchronized Iterator dependencies() {
        return m_deps.iterator();
    }

    public synchronized int dependencyCount() {
        return m_deps.size();
    }

    public synchronized static String getNextKey() {
        return String.valueOf(s_seq++);
    }
}
