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
package com.arsdigita.workflow.simple;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.TestHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Basic test harness for testing various workflows.
 *
 * @author Richard Li
 */
public abstract class WorkflowProcessTestHarness extends WorkflowTestCase {

    private static final Logger s_log =
        Logger.getLogger(WorkflowProcessTestHarness.class);

    // This is used to store mappings from array indices
    // to IDs.
    HashMap m_arrayToIdMap = new HashMap();
    HashMap m_idToArrayMap = new HashMap();
    Task task[];

    static final String label = "My WorkflowProcess";
    static final String description = "My WorkflowProcess Description";

    public WorkflowProcessTestHarness(String name, int size) {
        super(name);
        task = new Task[size];
    }

    static Task createTask(int n) {
        Task taskObj =  createTask(label + " " + n, description + " " + n);
        taskObj.save();
        s_log.warn("Created task " + n + " id is " + taskObj.getID());
        return taskObj;
    }

    static Task createTask(int n, String comment)  {
        Task taskObj = createTask(n);
        taskObj.addComment(comment);
        taskObj.save();
        return taskObj;
    }

    static Task createTask(String label, String description){
        Task taskObj = new UserTask(label,description);
        taskObj.setActive(true);
        return taskObj;
    }

    // avoid data aliasing: force task recreation.
    void recreateTask(int i) {
        try {
            task[i] = new Task((BigDecimal) m_idToArrayMap.get(new Integer(i)));
        } catch ( DataObjectNotFoundException e ) {
            throw new UncheckedWrapperException(e);
        }
    }


    /**
     * return number of enabled Task currently in the process
     *
     */
    int getEnabledTaskCount(Workflow wp) {
        Iterator enabledTasks = wp.getEnabledTasks();
        int itrCount = 0;
        while (enabledTasks.hasNext()) {
            itrCount++;
            enabledTasks.next();
        }
        return itrCount;
    }

    /**
     * insert a Task in between 2 other Tasks which are already connected.
     *
     * @return true on success
     */
   synchronized boolean insertTask(Workflow wp,
                                   Task fromTask, Task toTask,
                                   Task insTask) {

       // get corresponding TaskDefinitions
       try {
           // cut arc and create-insert new Task
           wp.addTask(insTask);
           toTask.removeDependency(fromTask);
           insTask.addDependency(fromTask);
           toTask.addDependency(insTask);
           insTask.save();
           toTask.save();
           fromTask.save();
           // make persistent
           wp.save();

       } catch(Exception e){ // todo: be more specific on error
           // manual rollback of try-block in order not to destroy the process
           fromTask.removeDependency(insTask);
           wp.removeTask(insTask);
           if(!toTask.isDependency(fromTask))
               toTask.addDependency(fromTask);

           return false;
       }

       return true;
   }

    /**
     * moves a WorkflowProcess one round of tasks further
     * @return how many enabled tasks were finished off
     */
    int moveProcess(Workflow wp) {
        BigDecimal id;
        Iterator it;
        Task task;
        int index;

        it = wp.getEnabledTasks();

        int n = 0;
        while(it.hasNext()){
            task = (Task)it.next();
            try {
                task.finish();

                // The below is necessary to address data aliasing.
                id = task.getID();
                s_log.warn("--> Retrieving index for " + id +
                           " with value " + m_arrayToIdMap.get(id));
                if ( m_arrayToIdMap.get(id) != null ) {
                    index = ((Integer) m_arrayToIdMap.get(id)).intValue();
                    try {
                        this.task[index] = new Task(id);
                    } catch ( DataObjectNotFoundException e ) {
                        throw new UncheckedWrapperException(e);
                    }
                    s_log.warn("Task is " + this.task[index].getState() +
                               " " + this.task[index].getID() + " index is " + index);
                }
                s_log.warn("Finishing task " + id +
                           "; state is now " + task.getState());
            } catch (TaskException t) {
                fail("Could not finish Task " + t);
            }
            n++;
        }

        return n;
    }

    /**
     * Create test workflow.
     */
    protected abstract Workflow createTestProcessDefinition();

    protected User makeNewUser() {
        User user = super.makeNewUser();
        TestHelper.setCurrentSystemParty(user);
        return user;
    }

    
}
