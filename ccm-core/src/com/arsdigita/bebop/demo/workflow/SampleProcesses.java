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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A class that provides a sample set of workflows and tasks. The class
 * simulates the part of the system that would usually be implemented with
 * a database or some other form of persistant storage.
 *
 * <p> <b>Warning:</b> This class is only meant for demo purposes. It's use
 * of synchronization will make sure that it becomes a bottleneck under
 * load.
 *
 * @author David Lutterkort
 * @version $Id: SampleProcesses.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SampleProcesses {

    public static final String versionId = "$Id: SampleProcesses.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static SampleProcesses m_instance;

    /**
     * A list of processes known to the system.
     */
    private ArrayList m_processes;

    /**
     * The list of known task types (represented by Task objects).
     */
    private static ArrayList m_allTasks;

    // Set up some default task types
    static {
        Task write = new Task("Write");
        write.setAssignee("Authors");
        Task edit = new Task("Edit");
        edit.setAssignee("Editors");
        Task approve = new Task("Approve");
        approve.setAssignee("Approvers");
        Task publish = new Task("Publish");
        publish.setAssignee("Publishers");
        Task review = new Task("Review");
        review.setAssignee("Sharks");

        m_allTasks = new ArrayList();
        m_allTasks.add(write);
        m_allTasks.add(edit);
        m_allTasks.add(approve);
        m_allTasks.add(publish);
        m_allTasks.add(review);
    }

    public synchronized static SampleProcesses getInstance() {
        if ( m_instance == null ) {
            m_instance = new SampleProcesses();
        }
        return m_instance;
    }

    private SampleProcesses() {
        m_processes = new ArrayList();
        Process simple = new Process("1", "Simple Article");
        Process front = new Process("2", "Front Page");
        Process legal = new Process("3", "Legal Article");
        Process press = new Process("4", "Press Release");
        m_processes.add(simple);
        m_processes.add(front);
        m_processes.add(legal);
        m_processes.add(press);

        Task write, edit, approve, publish, review;

        // Setup simple
        write = new Task("Write");
        write.setAssignee("Authors");
        publish = new Task("Publish");
        publish.setAssignee("Publishers");

        simple.addTask(write);
        simple.addTask(publish);

        // Setup front
        write = new Task("Write");
        write.setAssignee("Authors");
        edit = new Task("Edit");
        edit.setAssignee("Editors");
        approve = new Task("Approve");
        approve.setAssignee("Approvers");
        publish = new Task("Publish");
        publish.setAssignee("Publishers");

        front.addTask(write);
        front.addTask(edit);
        front.addTask(approve);
        front.addTask(publish);

        // Setup legal
        write = new Task("Write");
        write.setAssignee("Authors");
        edit = new Task("Edit");
        edit.setAssignee("Editors");
        approve = new Task("Approve");
        approve.setAssignee("Approvers");
        publish = new Task("Publish");
        publish.setAssignee("Publishers");
        review = new Task("Review");
        review.setAssignee("Sharks");

        legal.addTask(write);
        legal.addTask(edit);
        legal.addTask(approve);
        legal.addTask(review);
        legal.addTask(publish);

        // Setup press
        approve = new Task("Approve");
        approve.setAssignee("Approvers");
        publish = new Task("Publish");
        publish.setAssignee("Publishers");
        review = new Task("Review");
        review.setAssignee("Sharks");

        press.addTask(approve, false);
        press.addTask(review, false);
        press.addTask(publish);

    }

    public synchronized int size() {
        return m_processes.size();
    }

    public synchronized Process get(int i) {
        return (Process) m_processes.get(i);
    }

    public synchronized void add(Process p) {
        m_processes.add(p);
    }

    public synchronized static Process getProcess(Object key) {
        int i = Integer.parseInt(key.toString());
        return getInstance().get(i-1);
    }

    public synchronized static Task getTask(String key) {
        SampleProcesses l = getInstance();

        if ( key == null ) {
            return null;
        }

        for (int i=0; i < l.size(); i++) {
            Process p = l.get(i);
            for (int j=0; j < p.taskCount(); j++) {
                if ( key.equals(p.getTask(j).getKey()) ) {
                    return p.getTask(j);
                }
            }
        }

        for (int i=0; i < m_allTasks.size(); i++) {
            Task t = (Task) m_allTasks.get(i);
            if ( key.equals(t.getKey()) ) {
                return t;
            }
        }

        return null;
    }

    public synchronized static ListModel getAllTasks(PageState s) {
        return new ListModel() {
                private int i = -1;

                public boolean next() throws NoSuchElementException {
                    i += 1;
                    return ( i < m_allTasks.size() );
                }

                public Object getElement() { return m_allTasks.get(i); }

                public String getKey() {
                    return ((Task) m_allTasks.get(i)).getKey();
                }

            };
    }

}
