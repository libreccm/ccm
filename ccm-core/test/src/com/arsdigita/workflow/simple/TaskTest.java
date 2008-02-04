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
import java.util.Date;
import java.util.Iterator;

/**
 * This is the Junit testcase for Task class. Assumed Tinman environment.
 *
 * The Tests are the following:
 *                   - Construction of a Task from a TaskDefiniton
 *                   - Loading of a stored Task
 *                   - Updating (change s.th., save, retrieve and check)
 *                   - Adding Comments
 *                   - Deletion
 *                   - Listeners
 *
 * @author Stefan Deusch
 *
 */
public class TaskTest extends WorkflowTestCase {
    public static final String versionId = "$Id: TaskTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public TaskTest(String name) {
        super(name);
    }


    // ------------- TESTS ----------------------------
    /**
     * Test the create process
     *
     **/
    public void testCreate() throws ProcessException, TaskException {
        String definitionLabel = "Task Label Definition";
        String definitionDesc  = "Task Label definition description";

        Task task = new Task(definitionLabel, definitionDesc);

        assertNotNull("Create 1: task is null", task);

        // The default state should be disabled
        if (task.getState() != Task.DISABLED) {
            fail("Create 3: Task state not DISABLED after creation");
        }
        task.save();
    }

    /**
     * Test the reload process
     *
     **/
    public void testReload() throws ProcessException, TaskException {


        Task task;
        Task taskReload = null;

        // create a new Task
        task = new Task("Reload Task", "Reload Task Description");
        assertNotNull("Reload 1 task is null", task);
        task.save();

        OID taskOID = task.getOID();

        // check if task and taskReload are the same
        try {
            taskReload = new Task(taskOID);
        } catch (DataObjectNotFoundException e) {
            fail("Reload 2 task reload failed");

        }

        assertNotNull(taskReload);

        assertEquals("Reload 3: The tasks are different ",
                     task, taskReload);

        assertEquals("Reload 4: Task state of reloaded task is different from "+
                     "original task",
                     task.getState(), taskReload.getState());

        assertEquals("Reload 5: Task ENABLED of reloaded task is different "+
                     "from original task",
                     task.isEnabled(), taskReload.isEnabled());

        assertEquals("Reload 6: Labels of Tasks are unequal",
                     task.getLabel(),
                     taskReload.getLabel());

        assertEquals("Reload 7: Descriptions of Tasks are unequal",
                     task.getDescription(),
                     taskReload.getDescription());
    }

    /**
     * Test the task update attributes
     *
     **/
    public void testUpdate() throws ProcessException, TaskException {
        String taskLabel = "task test definition label";
        String taskDesc  = "task test definition desc";
        int enabledState = Task.ENABLED;
        int disabledState = Task.DISABLED;

        Task task = new Task(taskLabel, taskDesc);
        assertEquals("Update 1: Task State not disabled by default",
                     task.getState()
                     ,disabledState);
        task.save();

        Task taskReload = null;
        //Reload from db to verify
        try {
            taskReload = new Task(task.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Update 1.5: Could not reload task");
        }
        // check before save()
        assertEquals("Update 2: Task State not correctly saved",
                     taskReload.getState()
                     , disabledState);

        taskReload.setState(enabledState);
        taskReload.save();

        taskReload=null;
        try {
            taskReload = new Task(task.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Update 2.5: Could not reload task");
        }
        assertEquals("Update 3: Task State not correctly updated",
                     taskReload.getState()
                     ,enabledState);

    }


    /**
     * Test handling of comments
     *
     **/
    public void testComments() throws ProcessException, TaskException {
        String taskLabel = "task test definition label";
        String taskDesc  = "task test definition desc";

        Task task = new Task(taskLabel, taskDesc);
        task.save();

        User commentingUser = makeNewUser();

        Iterator commentList = null;

        // This also tests class TaskComment
        String firstComment = "first comment";
        String secondComment = "second comment, no date";
        String lastComment = "third comment, just comment";
        //TODO : setting dates
        task.addComment(commentingUser, firstComment);
        task.addComment(commentingUser,secondComment);
        task.addComment(lastComment);
        task.save();

        try {
            commentList = task.getComments();
        } catch (Exception e) { //TODO: FIXME
            fail("Comments .25: failed retrieving comment list");
        }

        assertEquals("Comments .30: invalid number of comment list",
                     task.getCommentsSize(),
                     3);

        Task taskReload = null;
        try {
            taskReload = new Task(task.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Comments .5: failed reloading task");
        }

        try {
            commentList = taskReload.getComments();
        } catch (Exception e) {//TODO: FIXME
            fail("failed retrieving comment list");
        }

        int count = 0;
        User reloadUser = null;

        while (commentList.hasNext()) {

            count++;

            TaskComment tc = (TaskComment)commentList.next();

            // Handle first comment
            if (firstComment.equals(tc.getComment())) {

                assertNotNull("Comments(1) "+count+".0: User OID is null ",
                              tc.getUserOID());
                try {
                    reloadUser = new User(tc.getUserOID());
                } catch (DataObjectNotFoundException e) {
                    fail("Comments(1) "+count+".05: failed reloading user ");
                }
                assertTrue("Comments(1) "+count+".1: User is different ",
                           same(reloadUser,commentingUser));


                assertNotNull("Comments(1) "+count+".2: date is null ",
                              tc.getDate());
            } else if (secondComment.equals(tc.getComment())) {
                assertNotNull("Comments(2) "+count+".0: User OID is null ",
                              tc.getUserOID());

                try {
                    reloadUser = new User(tc.getUserOID());
                } catch (DataObjectNotFoundException e) {
                    fail("Comments(2) "+count+".05: failed reloading user ");
                }

                assertTrue("Comments(2) "+count+".1: User is different ",
                           same(reloadUser, commentingUser));


                assertNotNull("Comments(2) "+count+".2: date is null ",
                              tc.getDate());
            } else if (lastComment.equals(tc.getComment())) {

                if (tc.getUserOID() != null) {
                    fail("User OID should be null");
                }

                assertNotNull("Comments(3) "+count+".2: date is null ",
                              tc.getDate());
            } else {
                fail("Comments(4) invalid comment string");
            }
        }

        assertEquals("Comments 1: incorrect number of comments",
                     count, 3);

        // when a new TaskComment is created and saved, the creation
        // of the task loads the new TaskComment.  But, if it is then
        // added to the task then it is added twice.  So, we make
        // sure that it is not possible to add the same comment twice
        try {
            taskReload = new Task(task.getOID());
            taskReload = new Task(task.getOID());
            TaskComment comment = new TaskComment(taskReload.getID(),
                                                  commentingUser,
                                                  "test comment");
            comment.save();
            taskReload.addComment(comment);
            // there should now only be 4 comments
            commentList = taskReload.getComments();
            count = 0;
            while (commentList.hasNext()) {
                commentList.next();
                count++;
            }
            assertEquals("After saving a comment and adding it, the comment " +
                         "was added twice.", count, 4);
        } catch (DataObjectNotFoundException e) {
            fail("Comments .5: failed reloading task");
        }

        // the save should be successfull
        task.delete();
    }

    private boolean same(User user1, User user2) {
        return user1.getID().equals(user2.getID());

    }

    public void testGetLastCommentInWorkflow() {
        Workflow workflow =
            new Workflow("process def title", "process def description");
        workflow.save();

        final int n = 3;

        Task[] task = new Task[n];
        for(int i = 0; i < n; i++) {
            task[i] = new Task("Task "+i, "Task Description "+i);
            workflow.addTask(task[i]);
        }
        workflow.save();

        User commentingUser = makeNewUser();
        TaskComment comment[] = new TaskComment[n];
        for (int i = 0; i < n; i++) {
            // start with newest and go backwards
            Date date = new Date(System.currentTimeMillis() - (i * 3600000));
            comment[i] = new TaskComment
                (task[i].getID(), commentingUser, String.valueOf(i), date);
            comment[i].save();
        }

        for (int i = 0; i < n; i++) {
            assertEquals(comment[i], task[i].getLastComment());
            assertEquals(comment[0], task[i].getLastCommentInWorkflow());
        }
    }


    /**
     * test deletion process
     */
    public void testDeletion() throws  ProcessException, TaskException {
        String taskLabel = "task test definition label";
        String taskDesc  = "task test definition desc";

        Task task = new Task(taskLabel,
                             taskDesc);
        //FIXME: this shouldn't be needed
        task.save();

        OID taskOID = task.getOID();

        task.delete();

        // Now that task is deleted, should not be able to reload
        try {
            task = new Task(taskOID);
            fail("Delete 1: should not be able to load the Task that "+
                 "is deleted");
        } catch(DataObjectNotFoundException e) {

        }
    }


    /**
     * test adding/removing Listeners
     *
     */
    public void testListeners() throws  ProcessException, TaskException {
        String taskLabel = "task test definition label";
        String taskDesc  = "task test definition desc";

        Task task = new Task(taskLabel, taskDesc);

        // add Listeners
        Task k1, k2;
        Task taskReload = null;
        k1 = new Task("Listener 1",
                      "This task listens for completion");
        k2 = new Task("Listener 2",
                      "This task listens for completion too");
        k1.save();
        k2.save();

        task.addFinishedListener(k1);
        task.addFinishedListener(k2);
        task.save(); // save listeners in cw_task_listeners

        try {
            taskReload = new Task(task.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Listener 2.5: could reload task");
        }

        assertEquals("The number of listeners is not correct after addition",
                     taskReload.getFinishedListenersCount(), 2);


        // remove listeners, and check if null
        task.removeFinishedListener(k1);
        task.removeFinishedListener(k2);
        task.save();
        OID taskOID = task.getOID();
        task = null;

        // load task
        try {
            task = new Task(taskOID);
        } catch (Exception e) {
            fail("Failed to create task.");
        }
        assertEquals("The number of listeners is not correct after removal",
                     task.getFinishedListenersCount(), 0);

    }


    // ----------------------------- END TESTS --------------------
    public String toString() {
        return "TaskTest";
    }

    public static void main(String args[]) {

        String[] testCaseName = {TaskTest.class.getName()};
        junit.textui.TestRunner.main(testCaseName);

    }


}
