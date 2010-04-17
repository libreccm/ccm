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
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.TestHelper;
import com.arsdigita.kernel.User;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *  The Junit test for Engine class
 *
 *  Testing each of the methods, which are essentially reports
 * @version $Id: EngineTest.java 741 2005-09-02 10:21:19Z sskracic $
 */
public class EngineTest extends WorkflowTestCase {

    private static final Logger s_log =
        Logger.getLogger(EngineTest.class);

    public EngineTest(String name) {
        super(name);
    }

    /**
     * Test if we are able to get enabled tasks
     *
     **/
    public void testTaskMethods() {
        int currentEnabledTasks;
        int currentOverdueTasks;

        final User user = makeNewUser();

        Engine eng = Engine.getInstance();

        Collection col = eng.getEnabledTasks(user);
        currentEnabledTasks = col.size();
        currentOverdueTasks = eng.getOverdueTasks(user).size();


        final Workflow wf = _createProcess();
        wf.save();
        wf.start(user);

        _assignAllTasks(wf, user);
        _setFutureDueDate(wf);

        col = eng.getEnabledTasks(user);

        assertEquals("testTaskMethods 1: Incorrect number of enabled tasks ",
                     currentEnabledTasks+1, col.size());
        Party old = TestHelper.setCurrentSystemParty(user);
        try {
            moveProcess(wf);
        } finally {
            TestHelper.setCurrentSystemParty(old);
        }

        col = eng.getEnabledTasks(user);
        assertEquals("testTaskMethods 2: Incorrect number of enabled tasks after fire",
                     currentEnabledTasks+1, col.size());

        UserTask task = (UserTask)(col.iterator().next());

        col = eng.getFinishedTasks(user,null,null);
        assertEquals("testTaskMethods 3: Incorrect number of finished tasks",
                     currentEnabledTasks+1, col.size());

        col = eng.getOverdueTasks(user);
        assertEquals("testTaskMethods 4: There should be no overdue tasks",
                     currentEnabledTasks, col.size());

        _setTasksOverdue(wf);

        col = eng.getEnabledTasks(user);
        assertEquals("testTaskMethods 4.5: Incorrect number of enabled tasks after fire",
                     currentEnabledTasks+1, col.size());


        col = eng.getOverdueTasks(user);
        assertEquals("testTaskMethods 5: Incorrect number of enabled overdue user tasks",
                     currentOverdueTasks+1, col.size());

    }


    /**
     * Check for active and finished processes
     *
     */
    public void testProcessMethods() {

        int currentOverdueProcesses;
        int currentActiveProcesses;

        User user = makeNewUser();
        Engine eng = Engine.getInstance();
        Collection col = eng.getOverdueProcesses();

        currentOverdueProcesses = col.size();
        currentActiveProcesses = eng.getActiveProcesses().size();

        Workflow wf = _createProcess();
        wf.save();
        wf.start(user);

        _assignAllTasks(wf, user);
        _setFutureDueDate(wf);


        col = eng.getActiveProcesses();
        assertEquals("testProcessMethods 5: Incorrect number of active processes",
                     currentActiveProcesses+1, col.size());

        col = eng.getEnabledTasks(user);


        Iterator itr = wf.getTasks();

        UserTask ut;
        while (itr.hasNext()) {
            ut = (UserTask) itr.next();
        }


        col = eng.getOverdueProcesses();
        assertEquals("testProcessMethods 10: Incorrect number of overdue processes",
                     currentOverdueProcesses, col.size());

        _setTasksOverdue(wf);
        col = eng.getOverdueProcesses();
        assertEquals("testProcessMethods 15: Incorrect number of overdue processes",
                     currentOverdueProcesses+1, col.size());

    }



    // ----------- HELPER METHODS  ----------------




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
    private Workflow _createProcess() {
        final int n = 5;

        final Workflow workflow =
            new Workflow("process def title", "process def description");
        new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                workflow.save();
                assertNotNull("Tasks 1: workflow is not null", workflow);
                UserTask[] task = new UserTask[n];

                for (int i=0; i<n; i++) {
                    task[i] =
                        new UserTask("Task "+i, "Task Description "+i);

                    task[i].save();

                    workflow.addTask(task[i]);

                    try {
                        for (int j = 0; j < i; j++) {
                            task[i].addDependency(task[j]);
                        }
                    } catch (Exception e) {
                        fail("_createProcessDefinition: failed creating task defs");

                    }
                    task[i].setActive(true);
                    task[i].save();
                }
                workflow.save();
            }
        }.run();
        return workflow;
    }

    /**
     * moves a WorkflowProcess one round of tasks further
     * @return how many enabled tasks were finished off
     */
    private int moveProcess(Workflow wp) {
        Iterator itr;
        Task task;

        itr = wp.getEnabledTasks();

        int n=0;
        while (itr.hasNext()) {
            task = (Task)itr.next();
            try {
                task.finish();
            } catch (TaskException t) {
                fail("Could not finish Task " + t);
            }
            n++;
        }

        return n;
    }

    /**
     * assign all tasks the user
     *
     **/
    private void _assignAllTasks(Workflow wf, User user) {

        Iterator itr = wf.getTasks();
        while (itr.hasNext()) {
            UserTask userTask = (UserTask)itr.next();
            userTask.assignUser(user);
            userTask.save();
        }
    }

    /**
     * set enabled tasks to past due
     *
     **/
    private void _setTasksOverdue(Workflow wf) {
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year-5);

        Date overdueDate = cal.getTime();

        Iterator itr = wf.getEnabledTasks();
        UserTask tmpTask = null;
        UserTask reloadTask = null;

        while (itr.hasNext()) {
            tmpTask = (UserTask)itr.next();
            tmpTask.setDueDate(overdueDate);
            tmpTask.save();

            try {
                reloadTask = new UserTask(tmpTask.getID());
            } catch (DataObjectNotFoundException e) {
                throw new RuntimeException("failed loading user task "+e);
            }

        }
    }

    /**
     * set enabled tasks to past due
     *
     **/
    private void _setFutureDueDate(Workflow wf) {
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        cal.set(Calendar.YEAR, year+5);

        Date futureDueDate = cal.getTime();

        Iterator itr = wf.getTasks();
        UserTask tmpTask = null;
        UserTask reloadTask = null;

        while (itr.hasNext()) {
            tmpTask = (UserTask)itr.next();
            tmpTask.setDueDate(futureDueDate);
            tmpTask.save();

            try {
                reloadTask = new UserTask(tmpTask.getID());
            } catch (DataObjectNotFoundException e) {
                throw new RuntimeException("failed loading user task "+e);
            }
        }
    }

}
