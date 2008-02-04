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
package com.arsdigita.workflow.simple;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 * This is the Junit testcase for WorkflowProcess class. Assumed Tinman
 * environment.
 *
 * The Tests are the following:
 *                - Construction of a WorkflowProcess from a
 *                  WorkflowProcessDefiniton
 *                - Loading of a stored WorkflowProcess
 *                - Deletion
 *                - assign, remove a ProcessObject
 *                - add 5 tasks, remove 2, check rest tasks
 *                - start, getEnabled Tasks, stop
 *                - finish 1st task, getEnabled Tasks, stop
 *
 * @author Richard Li
 * @author Stefan Deusch
 * @author Khy Huang
 *
 */
public class WorkflowProcessTest extends WorkflowProcessTestHarness {

    public static final String versionId = "$Id: WorkflowProcessTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static int taskDefId;
    // Arrays for WorkflowProcess Pattern below

    // initialize reference material
    static final String label = "My WorkflowProcess",
        description = "My WorkflowProcess Description";

    public WorkflowProcessTest(String name) {
        super(name,8);
    }

    /**
     * Method creates workflow process . The dependencies
     * are layed out from top to bottom. (i.e. 4 dependends on
     * 0, 1, 2, 3 finishing
     *
     *          0
     *         /|\
     *        | 1 \
     *        | |\ \
     *        | | \ \
     *        | |  |/\
     *        | |  2  \
     *        |\|/  \  |
     *        | 3    | |
     *        | |    | |
     *         \|   / /
     *          4 - -
     **/
    private Workflow _createProcessDefinition() {
        final int n = 5;
        Task[] task = new Task[n];

        Workflow workflow =
            new Workflow("process def title", "process def description");
        workflow.save();
        assertNotNull("Tasks 1: workflow is not null", workflow);

        for(int i=0; i<n; i++) {
            task[i] =
                new Task("Task "+i, "Task Description "+i);

            task[i].setActive(true);
            task[i].save();

            workflow.addTask(task[i]);

            try {
                for (int j = 0; j < i; j++) {
                    task[i].addDependency(task[j]);
                }
            } catch (Exception e) {
                fail("_createProcessDefinition: failed creating task defs");

            }
            task[i].save();
        }
        workflow.save();
        return workflow;
    }

    public void testCreate() throws ProcessException, TaskException {

        Workflow workflow = new Workflow("Test", "Test Description");
        workflow.save();

        User acs_obj = makeNewUser();
        acs_obj.save();

        assertNotNull("Create 1: process is null ", workflow);

        assertEquals("Create 4: WF-Process not in INIT state initialized",
                     workflow.getProcessState(), Workflow.INIT);

        // just to check if we have some crap in here
        assertEquals("Create 5: something wrong with the getTasks method",
                     workflow.getTaskCount(), 0);

    }

    /**
     * Test reload
     **/
    public void testReload() throws ProcessException, TaskException {

        Workflow workflow = new Workflow("test2","test2 description");
        assertEquals("Reload 0: workflow not in state init",
                     workflow.getProcessState(), Workflow.INIT);
        workflow.stop(null);
        assertEquals("Reload 0: workflow not in state init",
                     workflow.getProcessState(), Workflow.STOPPED);

        User acs_obj = makeNewUser();
        acs_obj.save();

        workflow.setObject(acs_obj);

        workflow.save();
        OID workflowOID = workflow.getOID();



        Workflow workflowReload = null;

        try {
            workflowReload =
                new Workflow(workflowOID);
        } catch (DataObjectNotFoundException e) {
            fail("Reload 1.1: could not reload process");
        }

        assertNotNull(workflowReload);

        assertEquals("Reload 2: Workflow ID of reloaded workflow is different"+
                     " from original", workflow, workflowReload);

        assertEquals("Reload 3: Workflow state of reloaded workflow is "+
                     "different from original workflow",
                     workflow.getProcessState(), workflowReload.getProcessState());

        assertEquals("Reload 4: Number of tasks in workflow is different",
                     workflow.getTaskCount(), workflowReload.getTaskCount());

        assertEquals("Reload 5: Object OID is different then User ",
                     acs_obj.getOID(),
                     workflow.getObjectOID());
    }

    /**
     * Test deletion
     *
     **/
    public void testDeletion() throws ProcessException, TaskException {

        Workflow workflow = new Workflow("test3","test3");
        workflow.save();

        User acs_obj = makeNewUser();
        acs_obj.save();

        assertEquals("Deletion 1: WF-Workflow not in INIT state",
                     workflow.getProcessState() ,
                     Workflow.INIT);

        OID processOID = workflow.getOID();

        // delete
        workflow.delete();

        try {
            workflow = new Workflow(processOID);
            fail("Delete 2: Delete failed");
        } catch (DataObjectNotFoundException e) {

        }

    }

    public void testTasks() throws  ProcessException, TaskException {
        final int n = 5;
        Task[] task = new Task[n];

        Workflow workflow = new Workflow("process def title",
                                         "process def description");
        workflow.save();

        assertNotNull("Tasks 1: Process definition is not null", workflow);

        // add 5 definitions
        for(int i=0; i<n; i++) {
            task[i] =
                new Task("Task "+i, "Task Description "+i);
            task[i].setActive(true);
            task[i].save();

            workflow.addTask(task[i]);

            for (int j = 0; j < i; j++) {
                task[i].addDependency(task[j]);
            }
            task[i].save();
            assertEquals("Tasks 2: The number of dependecies is not correct:",
                         i, task[i].getDependencyCount());
        }

        workflow.save();

        // Create new workflow process
        User user = makeNewUser();
        user.save();

        workflow.setObject(user);
        workflow.start(user);
        workflow.save();

        Workflow reloadWorkflow = null;
        try {
            reloadWorkflow =
                new Workflow(workflow.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Tasks 5: failed to restore object from db");
        }

        // The number of task in wfProcess should be the same for the load
        // from db
        assertEquals("Tasks 6: Process Reload failed: the task counts are "+
                     "different",
                     workflow.getTaskCount(),
                     reloadWorkflow.getTaskCount());

        assertEquals("Tasks 7: Process Reload failed: the states are different",
                     workflow.getProcessState(),
                     reloadWorkflow.getProcessState());

        Iterator enabledTasksItr = workflow.getEnabledTasks();
        int count = 0;

        Task enabledTask = null;

        // Check for enabled tasks
        while(enabledTasksItr.hasNext()) {
            enabledTask = (Task)enabledTasksItr.next();
            count++;
        }

        // Test if not null
        assertNotNull("Tasks 11: There were no enabled tasks", enabledTask);
        assertEquals("Tasks 12: The number of task not correct ", 1, count);

    }

    public void testAddTaskDefinition() {

        User user = makeNewUser();
        user.save();

        Workflow wfProcess =
            _createProcessDefinition();

        Task newOne = new Task("test","test2");
        newOne.setActive(true);
        newOne.save();

        wfProcess.setObject(user);

        wfProcess.save();

        //Get the before count
        int beforeTaskCount = wfProcess.getTaskCount();

        wfProcess.addTask(newOne);

        int afterTaskCount = wfProcess.getTaskCount();

        assertEquals("Add task 1: invalid number of task def after add",
                     afterTaskCount,
                     beforeTaskCount+1);

        wfProcess.save();

        Workflow wfReload = null;
        try {
            wfReload =
                new Workflow(wfProcess.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("workflow process exception error "+e);
        }

        assertEquals("Add Task 2: reload task count invalid" ,
                     afterTaskCount,
                     wfReload.getTaskCount());


    }

    /**
     * creates a somewhat complicated testpattern of a Workflow
     * where concurrency, state, etc. can be tested

     1
     |
     2
     / \
     /   \
     3     4 --> L1
     / \    |
     /   \   |
     5     6  |
     \     |  |
     \    |  |
     \   |  |
     \  |  /
     \ | /
     7 --> L2

     1,...,7 are numbered Tasks
     L1, L2 are Task listeners
    */
    protected Workflow createTestProcessDefinition() {
        // build 7 TaskDefinitions

        m_arrayToIdMap.clear();
        m_idToArrayMap.clear();

        for(int i=1;i<=7;i++) {
            task[i] = createTask(i+1);
            m_arrayToIdMap.put(task[i].getID(), new Integer(i));
            m_idToArrayMap.put(new Integer(i), task[i].getID());
        }

        // make dependencies (arcs)
        task[2].addDependency(task[1]);
        task[3].addDependency(task[2]);
        task[4].addDependency(task[2]);
        task[5].addDependency(task[3]);
        task[6].addDependency(task[3]);
        task[7].addDependency(task[5]);
        task[7].addDependency(task[6]);
        task[7].addDependency(task[4]);

        Workflow wfpdef =
            new Workflow("Test Workflow Process Definition",
                         "Test Workflow Process Description");

        //HERE WE CHANGE AN ASSUMPTION. A workflow no longer saves its children
        wfpdef.save();
        for(int i=1;i<8;i++) {
            task[i].save();
            wfpdef.addTask(task[i]);
        }
        wfpdef.save();
        return wfpdef;
    }

    // ============== end utility functions ============================

    /**
     * main WorkflowProcess Test function that is supposed to
     * simulate the run-time behavior of a 7-task  Process
     *
     */
    public void testWorkflowProcessExecution() throws ProcessException {

        User user = makeNewUser();
        user.save();

        Workflow wp = createTestProcessDefinition();

        assertNotNull("1: WorkflowProcess wp is null ", wp);

        // create two listener Tasks
        Task listener1 = createTask("Listening Task 1",
                                    "Listening Task 1 Description");
        Task listener2 = createTask("Listening Task 2",
                                    "Listening Task 2 Description");

        listener1.save();
        listener2.save();

        // add listeners
        task[4].addFinishedListener(listener1);
        task[7].addFinishedListener(listener2);

        // Pre-run condition: no finished tasks
        if(wp.getFinishedTasks().hasNext())
            fail("4: there should not be any finished tasks before starting the process");

        assertEquals("5: wf is not in INIT state", Workflow.INIT, wp.getProcessState());


        // starting the process
        wp.start(user);
        assertEquals("6: wf is not in STARTED state", Workflow.STARTED, wp.getProcessState());

        Iterator enabledTasks = wp.getEnabledTasks();
        int itrCount = 0;
        Task tempTask = null;

        while (enabledTasks.hasNext()) {
            itrCount++;
            tempTask = (Task)enabledTasks.next();
        }
        assertEquals("6.10: number of enabled task is incorrect",
                     1, itrCount);

        //        assertSame("6.11: different task reference",task[1], tempTask);

        recreateTask(1);
        assertEquals("6.12: task1 is still not enabled",
                     Task.ENABLED, task[1].getState());


        // Action: get all enabled tasks and fire them repeatedly
        int fired = moveProcess(wp);
        recreateTask(2);
        assertEquals("7: task 1 is not marked as finished", Task.FINISHED, task[1].getState());
        assertEquals("7.1: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("7.2: task 2 is not enabled", Task.ENABLED, task[2].getState());

        // fire next task
        fired = moveProcess(wp);
        assertEquals("9: task 2 is not marked as finished", Task.FINISHED, task[2].getState());
        assertEquals("10: not exactly 1 task (task 2) was finished", 1, fired);


        // and next
        fired = moveProcess(wp);
        assertEquals("11: not exactly 2 tasks (task 3 and 4) were finished", 2, fired);
        assertEquals("12: task 3 is not marked as finished", Task.FINISHED, task[3].getState());
        assertEquals("13: task 4 is not marked as finished", Task.FINISHED, task[4].getState());

        // task[7] should still be disabled
        assertEquals("14: task 7 is not disabled, but it should be", Task.DISABLED, task[7].getState());
        wp.save();

        // move on process
        Workflow reloadWp = null;
        Task task4 = null;
        try {
            reloadWp = new Workflow(wp.getOID());
            task4 = new Task(task[4].getID());
        } catch (DataObjectNotFoundException e) {
            fail("14.5: could not reload workflow ("+e+")");
        }

        fired = moveProcess(reloadWp);

        assertEquals("13.5: loaded task 4 is not the same in memory ",
                     task[4].getState(), task4.getState());

        Task task5 = null, task6 = null, task7 = null;

        //Reload from persistencel layer
        try {
            task5 = new Task(task[5].getID());
            task6 = new Task(task[6].getID());
            task7 = new Task(task[7].getID());
        } catch (DataObjectNotFoundException e) {
            fail("14.7: failed reloading task from persistence layer ("+e+")");
        }

        assertEquals("15: not exactly 2 tasks (task 5 and 6) were finished", 2, fired);
        assertEquals("16: task 5 is not marked as finished", Task.FINISHED, task5.getState());
        assertEquals("17: task 6 is not marked as finished", Task.FINISHED, task6.getState());

        // task[7] should now be enabled
        assertEquals("18: task 7 should be enabled ("+task7.getDependencyCount()+")",
                     Task.ENABLED, task7.getState());

        //Reload from persistence layer again since task 7 is not enabled
        try {
            reloadWp = new Workflow(wp.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("18.5: could not reload workflow ("+e+")");
        }

        // move on process and finish last task
        fired = moveProcess(reloadWp);
        try {
            task[7] = new Task(task[7].getID());
        } catch(DataObjectNotFoundException e) {
            fail("18.7: could not reload task ("+e+")");
        }

        assertEquals("19: there should only be 1 task (task 7) that finished", 1, fired);
        assertEquals("20: task 7 is not marked as finished", Task.FINISHED, task[7].getState());

        // test if any enabled task remains
        assertEquals("21: still find enabled task(s) after completion", false, reloadWp.getEnabledTasks().hasNext());

        // do we have correct number of tasks (7)
        assertEquals("22: could not find 7 tasks in process", 7, wp.getTaskCount());
        reloadWp.save();

        assertEquals("23: reloaded task does not have correct number of "+
                     "enabled tasks.", false, reloadWp.getEnabledTasks().hasNext());
    }

    /**
     * Test on interrupting a Process
     * simulate the run-time behavior of a 7-task Process
     * same process flow as above
     */
    public void testWorkflowProcessInterrupt() throws ProcessException {
        User user = makeNewUser();
        user.save();

        Workflow wp = createTestProcessDefinition();

        assertNotNull("1: WorkflowProcess wp is null ", wp);

        // Pre-run condition: no finished tasks
        if(wp.getFinishedTasks().hasNext())
            fail("1: there should not be any finished tasks before starting the process");

        assertEquals("2: wf is not in INIT state", Workflow.INIT,
                     wp.getProcessState());

        // starting the process
        wp.start(user);
        assertEquals("3: wf is not in STARTED state", Workflow.STARTED,
                     wp.getProcessState());

        int n_enabled = getEnabledTaskCount(wp);
        assertEquals("4: number of enabled task is incorrect", 1, n_enabled);

        // Action: fire first task
        int fired = moveProcess(wp);
        recreateTask(1);
        assertEquals("5: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("6: task 1 is not marked as finished", Task.FINISHED, task[1].getState());

        // fire next second task
        fired = moveProcess(wp);
        assertEquals("7: not exactly 1 task (task 2) was finished", 1, fired);
        assertEquals("8: task 2 is not marked as finished", Task.FINISHED, task[2].getState());

        // by now, task[3] and task[4] should be enabled
        recreateTask(3);
        recreateTask(4);
        assertEquals("9: task 3 is not marked as enabled", Task.ENABLED, task[3].getState());
        assertEquals("10: task 4 is not marked as enabled", Task.ENABLED, task[4].getState());

        // stop process
        wp.stop(user);

        // try to fire, nothing should change now
        fired = moveProcess(wp);
        // probably wp.getEnabledTasks() should return 0 after wp.stop()
        // to make wp in a non-executable state
        /*
          assertEquals("11: still find enabled task(s) after completion", false,
          wp.getEnabledTasks().hasNext());
        */
        assertEquals("12: no task should have been executed", 0, fired);

        // test: task[5] and task[6] should still be DISABLED
        assertEquals("13: task 5 is not marked as disabled", Task.DISABLED, task[5].getState());
        assertEquals("14: task 6 is not marked as disabled", Task.DISABLED, task[6].getState());

        // do we have correct number of tasks (7)
        assertEquals("15: could not find 7 tasks in process", 7, wp.getTaskCount());

        /*
          if we have a resume() method (and I think we should), this would reverse the stop
          and we can test if the Process will continue to execute until the end
        */

    }




    /**
     * Test editing a running process
     *
     */
    public void testWorkflowProcessAlteration() throws ProcessException {
        User user = makeNewUser();
        user.save();

        Workflow wp = createTestProcessDefinition();

        // starting the process
        wp.start(user);
        assertEquals("1: wf is not in STARTED state", Workflow.STARTED,
                     wp.getProcessState());

        int n_enabled = getEnabledTaskCount(wp);
        assertEquals("2: number of enabled task is incorrect", 1, n_enabled);

        // Action: fire first task
        int fired = moveProcess(wp);
        recreateTask(1);
        assertEquals("3: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("4: task 1 is not marked as finished", Task.FINISHED, task[1].getState());

        // fire next second task
        fired = moveProcess(wp);
        assertEquals("5: not exactly 1 task (task 2) was finished", 1, fired);
        assertEquals("6: task 2 is not marked as finished", Task.FINISHED, task[2].getState());

        // by now, task[3] and task[4] should be enabled
        recreateTask(3);
        recreateTask(4);
        assertEquals("7: task 3 is not marked as enabled", Task.ENABLED, task[3].getState());
        assertEquals("8: task 4 is not marked as enabled", Task.ENABLED, task[4].getState());

        // do we have correct number of tasks (7)
        assertEquals("9: could not find 7 tasks in process", 7, wp.getTaskCount());

        // insert between task[3] and task[6] a new task, task36in
        Task task36in = createTask(36, "intermediate Task btw. task[3] and task[6]");
        task36in.save();
        m_idToArrayMap.put(new Integer(36), task36in.getID());

        if(!insertTask(wp, task[3], task[6], task36in)) {
            fail("10: Could not insert new Task between task[3] and task[6]");
        }

        wp.save();

        // do we have correct number of tasks (8)
        assertEquals("11: could not find 8 tasks in process", 8, wp.getTaskCount());

        fired = moveProcess(wp);
        recreateTask(5);

        try {
            task36in = new Task((BigDecimal) m_idToArrayMap.get(new Integer(36)));
        } catch ( DataObjectNotFoundException e ) {
            throw new UncheckedWrapperException(e);
        }

        assertEquals("12: not exactly 2 tasks (task 5 and task36in) was finished", 2, fired);
        assertEquals("13: task 5 is not marked as enabled", Task.ENABLED, task[5].getState());
        assertEquals("14: task36in is not marked as enabled", Task.ENABLED, task36in.getState());

        // work off task36in
        fired = moveProcess(wp);

        try {
            task36in = new Task((BigDecimal) m_idToArrayMap.get(new Integer(36)));
        } catch ( DataObjectNotFoundException e ) {
            throw new UncheckedWrapperException(e);
        }


        assertEquals("15: not exactly 2 tasks (task36in, task 1) were finished", 2, fired);
        assertEquals("16: task36in is not marked as finished", Task.FINISHED, task36in.getState());

        fired = moveProcess(wp);
        fired = moveProcess(wp);

        // done

        assertEquals("17: still find enabled task(s) after completion", false, wp.getEnabledTasks().hasNext());

    }

}
