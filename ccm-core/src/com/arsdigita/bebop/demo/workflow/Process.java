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


/**
 * This class is a standin for the proper process template domain
 * object. Process objects have a <code>key</code> which identifies them
 * uniquely, a <code>name</code> for display to the user, a
 * <code>description</code> and contain a set of tasks. Tasks are ordered
 * simply by the sequence in which they were added to the process.
 *
 * <p> <b>Warning:</b> This class is only meant for demo purposes. It's use
 * of synchronization will make sure that it becomes a bottleneck under
 * load.
 *
 * @author David Lutterkort
 * @version $Id: Process.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Process {

    private String m_key;
    private String m_name;
    private String m_description;
    private ArrayList m_tasks;

    public Process(String key, String name) {
        m_key = key;
        m_name = name;
        m_tasks = new ArrayList();
    }

    public final synchronized String getKey() {
        return m_key;
    }

    public final synchronized void setKey(String  v) {
        m_key = v;
    }

    // Side effect: make t depend on all previous tasks
    public synchronized void addTask(Task t) {
        addTask(t, true);
    }

    public synchronized void addTask(Task t, boolean chain) {
        if (chain && m_tasks.size()  > 0) {
            t.addDependency((Task) m_tasks.get(m_tasks.size()-1));
        }
        t.setProcess(this);
        m_tasks.add(t);
    }

    public synchronized int taskCount() {
        return m_tasks.size();
    }

    public synchronized Task getTask(int i) {
        return (Task) m_tasks.get(i);
    }

    public synchronized Task getTask(String key) {
        for (int i=0; i < taskCount(); i++) {
            Task t = getTask(i);
            if ( t.getKey().equals(key) ) {
                return t;
            }
        }
        return null;
    }

    public final synchronized String getDescription() {
        return m_description;
    }

    public final synchronized void setDescription(String s) {
        m_description = s;
    }

    public final synchronized String getName() {
        return m_name;
    }

    public final synchronized void setName(String  v) {
        m_name = v;
    }

    public synchronized static String getNextKey() {
        return String.valueOf(SampleProcesses.getInstance().size()+1);
    }

}
