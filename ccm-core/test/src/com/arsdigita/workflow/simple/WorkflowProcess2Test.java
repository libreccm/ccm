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
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Runtime test for a workflow with 7 tasks
 *
 * @author Multiple
 */
public class WorkflowProcess2Test extends WorkflowProcessTestHarness {

    public static final String versionId = "$Id: WorkflowProcess2Test.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(WorkflowProcess2Test.class);

    static int taskDefId;

    static final String label = "My WorkflowProcess",
        description = "My WorkflowProcess Description";


    public WorkflowProcess2Test(String name) {
        super(name,8);
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
        s_log.warn("Creating test process definition");

        m_arrayToIdMap.clear();
        m_idToArrayMap.clear();

        // build 7 TaskDefinitions
        for(int i=1;i<=7;i++) {
            task[i] = createTask(i);
            task[i].save();
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
        wfpdef.save();
        for(int i=1;i<8;i++) {
            task[i].save();
            wfpdef.addTask(task[i]);
        }
        return wfpdef;
    }

    /**
     * main WorkflowProcess Test function that is supposed to
     * simulate the run-time behavior of a 7-task  Process
     */
    public void testWorkflowProcessExecution() throws ProcessException {

        User user = makeNewUser();

        // create Process with flowchart above
        Workflow wp = createTestProcessDefinition();
        wp.save();

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
            tempTask = (UserTask) enabledTasks.next();
        }
        assertEquals("6.10: number of enabled task is incorrect",
                     1, itrCount);

        // assertSame("6.11: different task reference",task[1], tempTask);

        recreateTask(1);
        assertEquals("6.12: task1 is still not enabled, (" +
                     tempTask.getID() +", "+task[1].getID()+")",
                     Task.ENABLED, task[1].getState());


        // Action: get all enabled tasks and fire them repeatedly
        int fired = moveProcess(wp);
        recreateTask(1);
        assertEquals("7: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("8: task 1 is not marked as finished", Task.FINISHED, task[1].getState());

        // fire next task
        fired = moveProcess(wp);
        recreateTask(2);
        assertEquals("9: not exactly 1 task (task 2) was finished", 1, fired);
        assertEquals("10: task 2 is not marked as finished", Task.FINISHED, task[2].getState());

        // and next
        fired = moveProcess(wp);
        assertEquals("11: not exactly 2 tasks (task 3 and 4) were finished", 2, fired);
        assertEquals("12: task 3 is not marked as finished", Task.FINISHED, task[3].getState());
        assertEquals("13: task 4 is not marked as finished", Task.FINISHED, task[4].getState());


        // task[7] should still be disabled
        assertEquals("14: task 7 is not disabled, but it should be still", Task.DISABLED, task[7].getState());

        // move on process
        fired = moveProcess(wp);
        assertEquals("15: not exactly 2 tasks (task 5 and 6) were finished", 2, fired);
        assertEquals("16: task 5 is not marked as finished", Task.FINISHED, task[5].getState());
        assertEquals("17: task 6 is not marked as finished", Task.FINISHED, task[6].getState());

        // task[7] should now be enabled
        recreateTask(7);
        assertEquals("18: task 7 is not enabled, but it should be by now", Task.ENABLED, task[7].getState());


        // move on process and finish last task
        fired = moveProcess(wp);
        assertEquals("19: not exactly 1 task (task 7) was finished", 1, fired);
        assertEquals("20: task 7 is not marked as finished", Task.FINISHED, task[7].getState());

        // test if any enabled task remains
        assertEquals("21: still find enabled task(s) after completion", false, wp.getEnabledTasks().hasNext());

        // do we have correct number of tasks (7)
        assertEquals("22: could not find 7 tasks in process", 7, wp.getTaskCount());

        recreateTask(3);
        s_log.warn("Enabling task 3 again...");
        task[3].enable();

        s_log.warn("Done re-enabling task 3 again...");
        recreateTask(5);
        recreateTask(6);
        recreateTask(7);
        assertEquals("30: Rolling back work, 5 should be disabled", Task.DISABLED, task[5].getState());
        assertEquals("35: Rolling back work, 6 should be disabled" , Task.DISABLED, task[6].getState());
        assertEquals("40: Rolling back work, 7 should be disabled" , Task.DISABLED, task[7].getState());

        try {
            Task reloadTask6 = new UserTask(task[6].getID());
            Task reloadTask7 = new UserTask(task[7].getID());

            assertEquals("45: Rollback back work, reload of 6 should be disabled",
                         Task.DISABLED, reloadTask6.getState());

            assertEquals("45: Rollback back work, reload of 7 should be disabled",
                         Task.DISABLED, reloadTask7.getState());
        } catch (Exception e) {
            fail("55: reload task 6 and 7 failed");
        }

    }

    /**
     * Test on interrupting a Process
     * simulate the run-time behavior of a 7-task Process
     * same process flow as above
     */
    public void testWorkflowProcessInterrupt() throws ProcessException {


        User user = makeNewUser();
        user.save();

        // create Process with flowchart above
        Workflow wp = createTestProcessDefinition();
        wp.save();

        assertNotNull("1: WorkflowProcess wp is null ", wp);
        // use static method to initialize task[]

        for(int i=1;i<8;i++) {

        }

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
        assertEquals("5: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("6: task 1 is not marked as finished", Task.FINISHED, task[1].getState());

        // fire next second task
        fired = moveProcess(wp);
        assertEquals("7: not exactly 1 task (task 2) was finished", 1, fired);
        assertEquals("8: task 2 is not marked as finished", Task.FINISHED, task[2].getState());

        recreateTask(3);
        recreateTask(4);
        // by now, task[3] and task[4] should be enabled
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
        recreateTask(5);
        recreateTask(6);
        assertEquals("13: task 5 is not marked as disabled", Task.DISABLED, task[5].getState());
        assertEquals("14: task 6 is not marked as disabled", Task.DISABLED, task[6].getState());

        // do we have correct number of tasks (7)
        assertEquals("15: could not find 7 tasks in process", 7, wp.getTaskCount());

        /*
          if we have a resume() method (and I think we should), this
          would reverse the stop and we can test if the Process will
          continue to execute until the end
        */

    }

    /**
     * Test editing a running process
     *
     */
    public void testWorkflowProcessAlteration() throws ProcessException {
        User user = makeNewUser();
        user.save();

        // create Process with flowchart above
        Workflow wp = createTestProcessDefinition();
        wp.save();

        // starting the process
        wp.start(user);
        assertEquals("1: wf is not in STARTED state", Workflow.STARTED,
                     wp.getProcessState());

        int n_enabled = getEnabledTaskCount(wp);
        assertEquals("2: number of enabled task is incorrect", 1, n_enabled);

        // Finish all enabled tasks. This should mark task 1 as finished,
        // enabling task 2.
        s_log.warn("-->move process start");
        int fired = moveProcess(wp);
        s_log.warn("-->move process done");
        assertEquals("3: not exactly 1 task (task 1) was finished", 1, fired);
        assertEquals("4: task 1 is not marked as finished", Task.FINISHED, task[1].getState());
        recreateTask(2);
        assertEquals("5: Task 2 not marked as enabled", Task.ENABLED, task[2].getState());

        // Fire next second task, which should have been enabled by
        // finishing the previous task. This should enable tasks 3 and
        // 4 and finish task 2.
        s_log.warn("-->move process start2");
        fired = moveProcess(wp);
        s_log.warn("-->move process done2");
        assertEquals("6: not exactly 1 task (task 2) was finished", 1, fired);
        assertEquals("7: task 2 is not marked as finished", Task.FINISHED, task[2].getState());

        // by now, task[3] and task[4] should be enabled
        recreateTask(3);
        recreateTask(4);
        recreateTask(6);
        assertEquals("8: task 3 is not marked as enabled", Task.ENABLED, task[3].getState());
        assertEquals("9: task 4 is not marked as enabled", Task.ENABLED, task[4].getState());
        assertEquals("9.5: task 6 is marked as enabled", Task.DISABLED, task[6].getState());

        // do we have correct number of tasks (7)
        assertEquals("10: could not find 7 tasks in process", 7, wp.getTaskCount());

        // insert between task[3] and task[6] a new task, task36in
        Task task36in = createTask(36, "intermediate Task btw. task[3] and task[6]");
        task36in.setActive(true);
        task36in.save();
        m_idToArrayMap.put(new Integer(36), task36in.getID());

        // 1. Add task 36 to the workflow.
        // 2. task[6].removeDependency(task[3])
        // 3. task36.addDependency(task[3]
        // 4. task[6].addDepedency(task36)
        if(!insertTask(wp, task[3], task[6], task36in))
            fail("11: Could not insert new Task between task[3] and task[6]");
        wp.save();

        // Do we have correct number of tasks (8)?
        assertEquals("12: could not find 8 tasks in process", 8, wp.getTaskCount());

        // Now, we should finish 3, 4 and enable 5, 36.
        s_log.warn("------>moveprocess3");
        fired = moveProcess(wp);
        s_log.warn("------>donemoveprocess3");
        recreateTask(5);
        try {
            task36in = new Task((BigDecimal) m_idToArrayMap.get(new Integer(36)));
        } catch ( DataObjectNotFoundException e ) {
            throw new UncheckedWrapperException(e);
        }

        assertEquals("12: 2 tasks (task 5 and task36in) weren't finished", 2, fired);
        assertEquals("13: task 5 is not marked as enabled", Task.ENABLED, task[5].getState());
        assertEquals("14: task36in is not marked as enabled", Task.ENABLED, task36in.getState());

        // work off task36in and 5
        fired = moveProcess(wp);
        try {
            task36in = new Task((BigDecimal) m_idToArrayMap.get(new Integer(36)));
        } catch ( DataObjectNotFoundException e ) {
            throw new UncheckedWrapperException(e);
        }
        recreateTask(5);

        assertEquals("15: not exactly 2 tasks (task36in) was finished", 2, fired);
        assertEquals("16: task36in is not marked as finished", Task.FINISHED, task36in.getState());
        assertEquals("17: task5 is not marked as finished", Task.FINISHED, task[5].getState());

        // Complete task 6
        fired = moveProcess(wp);
        recreateTask(6);
        recreateTask(7);
        assertEquals("18: there should only be one task completed", 1, fired);
        assertEquals("19: task[6] is not marked as finished", Task.FINISHED, task[6].getState());
        assertEquals("20: task[7] is not marked as enabled ", Task.ENABLED, task[7].getState());

        //Complete task 7
        fired = moveProcess(wp);
        recreateTask(7);
        assertEquals("21: there should only be one task completed",1, fired);
        assertEquals("22: task[7] is not marked as finished", Task.FINISHED, task[7].getState());

        // done
        assertEquals("17: still find enabled task(s) after completion", false, wp.getEnabledTasks().hasNext());
    }

    /**
     * Test active and enabled. Note that enabled tasks are a subset
     * of active tasks. Active means it's applied to an item. Enabled
     * means it's available to be completed.
     */
    public void testWorkflowProcessActive() throws ProcessException {
        User user = makeNewUser();

        Workflow wp = createTestProcessDefinition();
        wp.save();

        assertNotNull("1: WorkflowProcess wp is null:", wp);

        wp.start(user);
        assertEquals("2: Workflow is not in STARTED state:", Workflow.STARTED,
                     wp.getProcessState());

        int n_enabled = getEnabledTaskCount(wp);
        assertEquals("3: Number of enabled tasks is incorrect:", 1, n_enabled);

        Iterator itr = wp.getEnabledTasks();
        Task tempTask1 = (Task) itr.next();
        tempTask1.setActive(false);
        tempTask1.save();
        assertEquals("4: Task state should be inactive", false, tempTask1.isActive());

//         n_enabled = getEnabledTaskCount(wp);
//         assertEquals("4: There should be no enabled tasks: ", 0, n_enabled);

//         tempTask1.setActive(true);
//         assertTrue("5: Task 1 should be enabled", tempTask1.isEnabled());
    }

    /**
     * Test that cloning works for user task
     *
     **
     public void testWorkflowCloneAndDelete() {
     Iterator itr;
     Task task = null;

     Workflow wf = createTestProcessDefinition();
     wf.save();

     Workflow wf2 = (Workflow)wf.clone();
     wf2.save();

     itr  = wf.getTasks();
     while (itr.hasNext()) {
     task = (Task)itr.next();
     assertTrue("testWorkflowCloning 1: task is not an instance of user task ", (task instanceof UserTask));
     }


     // Reload Tasks from DB:
     try {
     wf = new Workflow(wf2.getID());
     } catch (DataObjectNotFoundException e) {
     fail("testWorkflowCloning 5: could not reload workflow");
     }

     itr = wf.getTasks();

     while (itr.hasNext()) {
     task = (Task)itr.next();
     assertTrue("testWorkflowCloning 10: task is not an instance of user task ", (task instanceof UserTask));
     }

     //Get task count before deleting
     int beforeDeleteCount = wf.getTaskCount();

     wf.start();
     itr = wf.getEnabledTasks();

     //Enabled Task
     task  =  (Task)itr.next();
     task.delete();

     //Get next enable task
     itr = wf.getEnabledTasks();
     Task nextTask = (Task)itr.next();

     assertNotEquals("deleted task still exists",nextTask, task);

     assertTrue("The workflow should have one less number of tasks ",
     beforeDeleteCount == wf.getTaskCount()+1);

     }


     /**
     * notification test
     *
     **/
    public void testNotification() {
        User user = makeNewUser();

        Workflow wp = createTestProcessDefinition();
        wp.save();
        user.save();

        // starting the process
        _assignAllTasks(wp,user);
        _setSenderAllTasks(wp,user);

        wp.start(user);
        Iterator itr = wp.getEnabledTasks();

        UserTask tempTask = (UserTask)itr.next();
        assertNotNull("10: task is not assigned "+tempTask.getNotificationSender());

        moveProcess(wp);
    }


    /**
     * workflow process within another workflow process
     *
     **/
    public void testSubProcess() {
        /* commented due to changes in how WF is supposed to work
           Task[] topTask = new Task[3];
           Task[] subTask = new Task[4];
           Task tempTask = null;
           Iterator itr;
           int loopCount = 0;

           Workflow wfSub = new Workflow("sub", "sub");
           Workflow wfTop = new Workflow("top", "top");
           wfSub.save();

           // Add four subtasks and one dependency
           for (int i = 0; i < 4; i++) {
           subTask[i] = new Task("sub "+i, "sub "+i);
           subTask[i].save();
           wfSub.addTask(subTask[i]);
           }

           subTask[0].addDependency(subTask[3]);
           subTask[0].save();

           topTask[0] = wfSub;

           wfTop.save();
           wfTop.addTask(topTask[0]);

           for (int i = 1; i < 3; i++) {
           topTask[i] = new Task("top 1", "top 1");
           topTask[i].save();
           wfTop.addTask(topTask[i]);
           }

           topTask[1].addDependency(topTask[0]);
           topTask[1].save();

           topTask[2].addDependency(topTask[1]);
           topTask[2].save();

         topTask[1].save();
           wfTop.save();

           wfTop.start(makeNewUser());

           _runProcessTest(wfTop, "normal");

           //-------- Testing rolling back to a previous task ------------

           //Enable the completed sub workflow and run again
           wfSub = _newInstanceWF(wfSub.getID());
           wfSub.enable();
           wfTop = _newInstanceWF(wfTop.getID());
           _runProcessTest(wfTop, "rollback");


           Task tk = new Task("test1", "test2");
           tk.save();
           wfSub = _newInstanceWF(wfSub.getID());
           wfSub.addFinishedListener(tk);
           wfSub.save();

           loopCount = _wfItrCount(wfSub.getFinishedListeners());

           wfSub = null;

           // Clone the workflow and run
           wfTop = (Workflow)wfTop.clone();
           itr = wfTop.getTasks();
           while (itr.hasNext()) {
           tempTask = (Task)itr.next();
           if (tempTask instanceof Workflow) {
           wfSub = (Workflow)tempTask;
           }
           }

           assertNotNull("Sub process is null "+wfSub);

           assertEquals("Clone did not copy the listeners",
           loopCount, _wfItrCount(wfSub.getFinishedListeners()));

           wfTop.start(makeNewUser());
           _runProcessTest(wfTop, "clone");
           loopCount =  _wfItrCount(wfTop.getFinishedListeners());

        */
    }

    // ----------------------------- END TESTS -------------------------
    private void  _runProcessTest(Workflow wfTop, String prefix) {

        Iterator itr = null;
        Task tempTask;
        Workflow wfSub = null;
        int loopCount = 0;
        int taskFinished = 0;

        itr = wfTop.getTasks();
        while (itr.hasNext()) {
            tempTask = (Task)itr.next();
            if (tempTask instanceof Workflow) {
                wfSub = (Workflow)tempTask;
            }
            loopCount++;
        }

        assertEquals("testSubProcess: 5 ("+prefix+") there should be 3 task in top process", 3, loopCount);
        assertNotNull("testSubProcess: 10 ("+prefix+") the sub process should not be null", wfSub);

        loopCount = _wfItrCount(wfTop.getEnabledTasks());
        assertEquals("testSubProcess: 11 ("+prefix+") there should be 1 enabled task in top process", 1, loopCount);

        loopCount = wfSub.getTaskCount();
        assertEquals("testSubProcess: 15 ("+prefix+") there should be 4 task in clone sub process", 4, loopCount);

        wfSub = _newInstanceWF(wfSub.getID());

        loopCount = _wfItrCount(wfSub.getEnabledTasks());
        assertEquals("testSubprocess : 20 ("+prefix+") should be on enabled task", 3, loopCount);

        taskFinished = moveProcess(wfSub);
        assertEquals("testSubProcess : 25 ("+prefix+") should be one task finished", 3, taskFinished);

        wfSub = _newInstanceWF(wfSub.getID());
        taskFinished = moveProcess(wfSub);
        assertEquals("testSubProcess : 30 ("+prefix+") should be one task finished", 1, taskFinished);

        wfSub = _newInstanceWF(wfSub.getID());
        assertTrue("testSubProcess : 35 ("+prefix+") subtask should be finished", wfSub.isFinished());

        wfTop = _newInstanceWF(wfTop.getID());

        assertTrue("testSubProcess : 40 ("+prefix+") top workflow is not enabled", wfTop.isEnabled());
        taskFinished = moveProcess(wfTop);
        assertEquals("testSubProcess : 45 ("+prefix+") should be one task finished", 1, taskFinished);

        wfTop = _newInstanceWF(wfTop.getID());
        taskFinished = moveProcess(wfTop);
        assertEquals("testSubProcess : 50 ("+ prefix+") should be one task finished", 1, taskFinished);

        wfTop = _newInstanceWF(wfTop.getID());
        assertEquals("testSubProcess : 55 ("+ prefix+") top workflow should be completed", wfTop.FINISHED, wfTop.getState());

    }

    private Workflow _newInstanceWF(BigDecimal ID) {
        Workflow wf = null;
        try {
            wf = new Workflow(ID);
        } catch (Exception e) {
            fail("Reloading workflow failed");
        }
        return wf;
    }

    private UserTask _newInstanceTask(BigDecimal ID) {
        UserTask tk = null;
        try {
            tk = new UserTask(ID);
        } catch (Exception e) {
            fail("failed loading user task");
        }
        return tk;

    }

    private int _wfItrCount(Iterator itr) {

        int index = 0;

        while (itr.hasNext()) {
            itr.next();
            index++;
        }
        return index;
    }

    /**
     * assign all tasks the user
     *
     **/
    private void _assignAllTasks(Workflow wf, User user) {

        Iterator itr = wf.getTasks();
        UserTask userTask = null;

        while (itr.hasNext()) {
            userTask = (UserTask)itr.next();
            userTask.assignUser(user);
            userTask.save();
        }
    }

    /**
     * Set all tasks to have the same notification sender
     *
     **/
    private void _setSenderAllTasks(Workflow wf, User user) {
        Iterator itr = wf.getTasks();
        UserTask userTask = null;

        while (itr.hasNext()) {
            userTask = (UserTask)itr.next();
            userTask.setNotificationSender(user);
            userTask.save();
        }
    }


    public String toString() {
        return "WorkflowProcess2Test";
    }

    public static void main(String args[]) {
        String[] testCaseName = {WorkflowProcess2Test.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
}

// ----------------------------- END TESTS -------------------------
