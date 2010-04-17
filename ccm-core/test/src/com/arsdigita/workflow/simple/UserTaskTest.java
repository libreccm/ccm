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
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.TestHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * This is the Junit testcase for UserTask class. Assumed Tinman environment.
 *
 * The Tests are the following:
 *                             - construction of a UserTask from a UserTaskDefiniton
 *                             - Loading of a stored UserTask
 *                             - update task, force to enabled
 *                             - comments
 *                             - assign a User, Group
 *                             - finish, rollback
 *                             - lock, getLockedUsers, unlock
 *                             - set DueDate, check if overdue
 *                             - deletion
 *
 *
 * @author Stefan Deusch
 * @version $Id: UserTaskTest.java 741 2005-09-02 10:21:19Z sskracic $
 */
public class UserTaskTest extends WorkflowTestCase {

    private static Logger s_cat =
        Logger.getLogger(UserTaskTest.class.getName());

    private static User s_testUser;
    private static Party s_systemParty;

    // initialize reference material
    static final String label = "My UserTask",
        description = "My UserTask Description";


    public UserTaskTest(String name) {
        super(name);
    }

    // ------------- TESTS ----------------------------

    public void testCreate() throws ProcessException, TaskException {

        UserTask task = new UserTask(label,description);
        assertNotNull("Create 1: task is null" , task);
        task.save();

        OID taskOID = task.getOID();
        task.save();

        assertEquals("Create 2: Test state failed" , Task.DISABLED, task.getState());
        assertEquals("Create 3: Test isEnabled failed" , false, task.isEnabled());
        assertEquals("Create 4: Test isFinished failed", false, task.isFinished());
    }


    public void testReload() throws ProcessException, TaskException {

        UserTask task = new UserTask(label,description);
        assertNotNull(task);
        task.save();

        UserTask taskReload = null;

        try {
            taskReload = new UserTask(task.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Could not load user task");
        }

        assertNotNull(taskReload);
        assertEquals("Reload 4: Task reloaded is different from original task",
                     task , taskReload);
        assertEquals("Reload 5: Task state of reloaded task is different from original task",
                     task.getState(), taskReload.getState());

        assertEquals("Reload 6: Task isEnabled of reloaded task is different from original task",
                     task.isEnabled(), taskReload.isEnabled());

        assertEquals("Reload 7: Task isFinished of reloaded task is different from original task",
                     task.isFinished(), taskReload.isFinished());

    }


    public void testUpdate() throws ProcessException, TaskException {

        UserTask task = new UserTask(label, description);
        UserTask taskTwo;
        //TODO: should we have to do a save here?
        int taskState = Task.ENABLED;
        task.setState(taskState);
        task.save();

        // check for persistance - after save
        taskTwo = null;
        try {
            taskTwo = new UserTask(task.getOID());
        } catch (Exception e) {
            fail("could not restore a user task" + e.getMessage());
        }

        assertNotNull(taskTwo);
        assertEquals("Update 5: Task Id not the same anymore",
                     taskTwo, task);
        assertEquals("Update 6: Taskdef Label different from origianl",
                     task.getLabel(), taskTwo.getLabel());

        assertEquals("Update 8: Task State not correctly updated",
                     taskTwo.getState(), taskState);

    }


    /**
     * test finish and rollback state changes
     */
    public void testFinish() throws  ProcessException, TaskException {
        UserTask task = new UserTask(label, description);
        User u1 = makeNewUser();


        task.enable();
        assertEquals("Finish 1: Task did not move into FINISHED state",
                     Task.ENABLED, task.getState());
        TestHelper.setCurrentSystemParty(u1);
        task.finish(u1);
        assertEquals("Finish 1: Task did not move into FINISHED state",
                     Task.FINISHED, task.getState());
        task.save();
    }


    /**
     * Tes lock mechanism
     */
    public void testLock() throws  ProcessException, TaskException {
        UserTask task = new UserTask(label, description);
        User u1 = makeNewUser();
        task.lock(u1);

        assertEquals("Lock 1: Task is not locked", true, task.isLocked());
        assertEquals("Lock 2: locking user is different",
                     task.getLockedUser(),
                     u1);

        s_cat.debug("About to call save on task " + task);
        task.save();
        OID taskOID = task.getOID();
        task = null;
        // test persistance of lock
        try {
            task = new UserTask(taskOID);
        } catch (Exception e) {
            fail("Lock 2.5: could not reload user task" + e.getMessage());
        }
        assertNotNull(task);
        assertEquals("Lock 3: Task is not locked", true, task.isLocked());
        assertEquals("Lock 4: locking user is different",
                     task.getLockedUser().getOID(), u1.getOID());

        task.unlock(u1);
        task.save();
        task = null;

        try {
            task = new UserTask(taskOID);
        } catch (Exception e) {
            fail("Lock 5.5: could not reload user task" + e.getMessage());
        }
        assertNotNull(task);
        assertEquals("Lock 5: Task is locked", false, task.isLocked());
        assertEquals("Lock 4: locking user is not null",
                     task.getLockedUser(), null);

        task.delete();
    }



    /**
     * test deletion: this sets the cw_tasks.task_state = 'deleted' and the definition.is_active = 'f'
     */
    public void testDeletion() throws  ProcessException, TaskException {
        UserTask task = new UserTask(label, description);
        OID taskOID;
        task.save();
        taskOID = task.getOID();
        task = null;

        //make sure a persistent copy is saved before deletion
        try {
            task = new UserTask(taskOID);
        } catch (DataObjectNotFoundException d) {
            fail("Delete 1: shouldbe able to load the UserTask before deletion.");
        }
        task.delete();
        task = null;
        // now that task is deleted, there is currently no way to
        // reload it from the DB
        try {
            task = new UserTask(task.getOID());
            fail("Delete 1: should not be able to load the UserTask that is deleted");
        } catch( Exception e) {
        }

    }



    public void testAssignments() throws ProcessException, TaskException {

        String label = "Assignment test";
        String description =  "Assignment test description";
        UserTask userTask = new UserTask(label, description);
        OID taskOID;
        // Create 5 users
        int userCreated = 5;
        User[] users = new User[userCreated];

        int groupCreated = 5;
        Group[] groups = new Group[groupCreated];

        for (int x = 0; x < userCreated; x++) {
            users[x] = makeNewUser();
            userTask.assignUser(users[x]);
        }

        for (int x = 0; x < groupCreated; x++) {
            groups[x] = new Group();
            groups[x].setName("Workflow Test " + x);
            groups[x].save();
            userTask.assignGroup(groups[x]);
        }

        userTask.save();
        taskOID = userTask.getOID();
        // Check that user and group assignments were successful
        assertTrue("Assignment 1: failed is assigned()",
                   userTask.isAssigned());

        try {
            userTask =
                new UserTask(userTask.getOID());
        } catch (Exception e) {
            fail("fail to create usr task ");
        }

        assertTrue("Assignment 2: failed is assigned(user)",
                   userTask.isAssigned(users[0]));

        try {
            userTask = new UserTask(taskOID);
        } catch (DataObjectNotFoundException e) {
            fail("fail to create user task ");
        }

        assertTrue("Assignment 3: failed is assigned(group)",
                   userTask.isAssigned(groups[0]));

        int assignedGroupCount = userTask.getAssignedGroupCount();
        int assignedUserCount = userTask.getAssignedUserCount();

        assertEquals("Assignment 4: The number of user assignments failed",
                     assignedUserCount, userCreated);

        assertEquals("Assignment 5: The number of group assignments failed",
                     assignedGroupCount, groupCreated);

        // Verify the remove assignment was successful
        userTask.removeUser(users[0]);
        userTask.removeGroup(groups[0]);

        userTask.removeUser(users[4]);
        userTask.removeGroup(groups[4]);
        userTask.save();

        try {
            userTask = new UserTask(taskOID);
        } catch (DataObjectNotFoundException e) {
            fail("could not create user task ");
        }

        assertEquals("Assignment 6: the number of users is not correct!",
                     userTask.getAssignedUserCount(),
                     userCreated-2);

        assertEquals("Assignment 7: the number of groups is not correct!",
                     userTask.getAssignedGroupCount(),
                     groupCreated-2);
    }

    public void testDuration() {

        //Test with no specified duration
        try {

            UserTask uTask = new UserTask("no duration",
                                          "no duration description",
                                          true,
                                          0);
            uTask.save();


            UserTask uTaskReload = new UserTask(uTask.getOID());

            Date startDate = uTask.getStartDate();
            Date startDateCopy = uTaskReload.getStartDate();
            Date dueDate = uTask.getDueDate();
            Date dueDateCopy = uTask.getDueDate();
            Date currDate = new Date();

            assertEquals("Duration 1: the start and date is not equal ",
                         startDate.getTime(), dueDate.getTime());

            assertTrue("Duration 2: the start date is later than current date",
                       startDate.getTime() < currDate.getTime());

            // Verify the basic duration works
            // Divide by 1000 because the date pulled back from Oracle
            // resolves to seconds.
            assertTrue("Duration 3: The start date is different from reload",
                       startDate.getTime()/1000 + 2 > startDateCopy.getTime()/1000);

            assertTrue("Duration 3b: The start date is different from reload",
                       startDate.getTime()/1000 - 2 < startDateCopy.getTime()/1000);


            assertTrue("Duration 4: The due date is different from reload",
                       dueDate.getTime()/1000 + 2 > dueDateCopy.getTime()/1000);

            assertTrue("Duration 4b: The due date is different from reload",
                       dueDate.getTime()/1000 - 2 < dueDateCopy.getTime()/1000);



            //--------- Verify task definition with duration  --------------
            uTask = new UserTask("with duration",
                                 "with duration description", true, 10);
            uTask.save();
            uTaskReload = new UserTask(uTask.getOID());

            startDate = uTask.getStartDate();
            startDateCopy = uTaskReload.getStartDate();
            dueDate = uTask.getDueDate();
            dueDateCopy = uTask.getDueDate();

            // Verify the basic duration works
            assertTrue("Duration 5: The start date is different from reload",
                       startDate.getTime()/1000 + 2 > startDateCopy.getTime()/1000);

            assertTrue("Duration 5b: The start date is different from reload",
                       startDate.getTime()/1000 - 2 < startDateCopy.getTime()/1000);


            assertTrue("Duration 6: The due date is different from reload",
                       dueDate.getTime()/1000 + 2 > dueDateCopy.getTime()/1000);

            assertTrue("Duration 6b: The due date is different from reload",
                       dueDate.getTime()/1000 - 2 < dueDateCopy.getTime()/1000);



            // Check only the valid ranges within on minute apart.
            assertTrue("Duration 7: Due date is after start date ",
                       dueDate.getTime() > (startDate.getTime()+593000));

            assertTrue("Duration 8: Due date is after start date ("+dueDate.getTime()+","+startDate.getTime(),
                       dueDate.getTime() < (startDate.getTime()+670000));

        } catch (Exception e) {
            fail("fail miserably "+e);

        }
    }


    // ----------------------------- END TESTS ---------------------------------------------



    public String toString() {
        return "UserTaskTest";
    }

    public static void main (String args[]) {

        String[] testCaseName = {UserTaskTest.class.getName()};
        junit.textui.TestRunner.main(testCaseName);

    }


}
