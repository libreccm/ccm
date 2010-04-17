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

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;



//-----------------------------------------------------------------------------------------------------
/**
 * This is the Junit testcase for UserTaskDefintion class. Assumed Tinman
 * environment.
 *
 * The Tests are the following:
 *                         - Construction of UserTaskDefinition (3 constructors)
 *                         - Assigning Users, test Assignment
 *                         - Loading of a stored UserTaskDefinition
 *                         - Updating (change s.th., save, retrieve and check)
 *                         - Cloning of a UserTaskDefinition
 *                         - Deletion
 *
 * @author Stefan Deusch
 * @author Khy Huang
 * @version $Id: UserTaskDefinitionTest.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class UserTaskDefinitionTest extends WorkflowTestCase {

    // initialize reference material
    static final String label = "My UserTaskDefinition Label";
    static final String description = "My UserTaskDefinition Description";

    public UserTaskDefinitionTest(String name) {
        super(name);
    }


    // ------------- TESTS ----------------------------

    /**
     * create user task definition test
     *
     *
     **/
    public void testCreate() throws ProcessException, TaskException {

        UserTask userTaskDefinition =
            new UserTask(label,description);
        assertNotNull("Create 1: user task definition is null",userTaskDefinition);
        userTaskDefinition.save();

        // create new user task with duration
        userTaskDefinition = new UserTask(label,description, true, 1000);
        assertNotNull("Create 2: user task definition with duration is null",
                      userTaskDefinition);
        userTaskDefinition.save();

        // Verify the retrieved label is the same as in constructor
        assertEquals("Create 3: Label is not same as expected",
                     userTaskDefinition.getLabel() ,label);

        // Verify the retrieved description is the same as in constructor
        assertEquals("Create 4: Description is not same as expected",
                     userTaskDefinition.getDescription(), description);


        final Duration duration = userTaskDefinition.getDuration();
        // Verify the duration period is the same as in constructor
        assertEquals("Create 5: Initial creation and duration time is" +
                     "different.",duration.getDuration(), 1000);

        // Verify the isActive flag is set properly
        assertTrue("Create 6: The task definition was set to valid",
                   !userTaskDefinition.isActive());

        userTaskDefinition.save();
    }

    /**
     * Test reloading user task definition
     *
     **/
    public void testReload() throws ProcessException, TaskException {


        final UserTask td1 = new UserTask(label, description, false, 20000);
        assertNotNull("Reload 1: td1 is null", td1);
        assertNotNull("Reload 2: td1.getDuration() is null", td1.getDuration());
        td1.save();

        final UserTask td2 = new UserTask(td1.getOID());
        assertNotNull("Reload 3: td2 is null ", td2);
        assertNotNull("Reload 4: td2.getDuration() is null", td2.getDuration());

        // Test if Duration is the same
        final Duration d1 = td1.getDuration();
        final Duration d2 = td2.getDuration();

        assertEquals("Reload 5: Unequal Duration from reloaded UserTask",
                     td1.getDuration().getDuration(), td2.getDuration().getDuration());

        // Comparing the task definition label
        assertEquals("Reload 5: Label failed to reload",
                     td1.getLabel(), td2.getLabel());

        // Comparing the task definition description
        assertEquals("Reload 6: Description failed to reload",
                     td1.getDescription(), td2.getDescription());

        // Checking the isActive flags are the same
        assertEquals("Reload 7: isActive failed to reload",
                     td1.isActive() , td2.isActive());

    }

    /**
     * Test the update process
     *
     **/
    public void testUpdate() throws ProcessException, TaskException {

        final UserTask td1 = new UserTask(label,description);
        final String label = "New Label", description = "New description";
        final Duration d = new Duration(10,10,1000);


        td1.setDuration(d);

        td1.setLabel(label);
        td1.setDescription(description);
        td1.save();

        final UserTask td2 = new UserTask(td1.getOID());

        assertNotNull("Update 1.5: Retrieved Duration is null",
                      td2.getDuration());
        assertEquals("Update 2: Retrieved duration not same as original",
                     td2.getDuration().getDuration(), d.getDuration());
        assertEquals("Update 3: Retrieved label not same as original",
                     td2.getLabel(), label);
        assertEquals("Update 4: Retrieved description not same as original",
                     td2.getDescription(), description);
    }

    /**
     * Test the user and group gassignments
     * -------------------------------------------------------------------------------------------
     **/
    public void testAssignments() throws ProcessException, TaskException {

        final String label = "Assignment test";
        final String description =  "Assignment test description";

        UserTask userTaskDefinition =
            new UserTask(label, description);

        // Create 5 users
        final int userCreated = 5;
        final User[] users = new User[userCreated];

        final int groupCreated = 5;
        final Group[] groups = new Group[groupCreated];

        for (int x = 0; x < userCreated; x++) {
            users[x] = makeNewUser();
            userTaskDefinition.assignUser(users[x]);
        }

        for (int x = 0; x < groupCreated; x++) {
            groups[x] =makeNewGroup();
            userTaskDefinition.assignGroup(groups[x]);
        }

        userTaskDefinition.save();

        assertEquals("Assignment 1.2: invalid number of user assignments",
                     userCreated,
                     userTaskDefinition.getAssignedUserCount());

        assertEquals("Assignment 1.3: Invalid number of group assignments",
                     groupCreated,
                     userTaskDefinition.getAssignedGroupCount());



        // Check that user and group assignments were successful
        assertTrue("Assignment 1: failed is assigned()",
                   userTaskDefinition.isAssigned());

        assertTrue("Assignment 2: failed is assigned(user)",
                   userTaskDefinition.isAssigned(users[0]));

        assertTrue("Assignment 3: failed is assigned(group)",
                   userTaskDefinition.isAssigned(groups[0]));


        try {
            userTaskDefinition =
                new UserTask(userTaskDefinition.getOID());
        } catch (Exception e) {
            fail("fail to create user task definition");
        }

        assertTrue("Assignment 4: after reload failed is assigned(user)",
                   userTaskDefinition.isAssigned(users[0]));


        assertTrue("Assignment 5: after reload failed is assigned(group)",
                   userTaskDefinition.isAssigned(groups[0]));


        final int assignedGroupCount = userTaskDefinition.getAssignedGroupCount();
        final int assignedUserCount = userTaskDefinition.getAssignedUserCount();

        assertEquals("Assignment 6: The number of user assignments failed",
                     assignedUserCount, userCreated);

        assertEquals("Assignment 7: The number of group assignments failed",
                     assignedGroupCount, groupCreated);

        // Verify the remove assignment was successful
        userTaskDefinition.removeUser(users[0]);
        userTaskDefinition.removeGroup(groups[0]);

        userTaskDefinition.removeUser(users[4]);
        userTaskDefinition.removeGroup(groups[4]);
        userTaskDefinition.save();

        try {
            userTaskDefinition =
                new UserTask(userTaskDefinition.getOID());
        } catch (Exception e) {
            fail("could not create user task definition");
        }

        assertEquals("Assignment 8: the number of users is not correct!",
                     userTaskDefinition.getAssignedUserCount(),
                     userCreated-2);

        assertEquals("Assignment 9: the number of groups is not correct!",
                     userTaskDefinition.getAssignedGroupCount(),
                     groupCreated-2);
        userTaskDefinition.delete();
    }


    /**
     * test deletion
     */
    public void testDeletion() throws  ProcessException, TaskException {
        UserTask td1;
        OID taskDefinitionOID = null;

        td1 = new UserTask(label, description, false, 20000);
        td1.setActive(true); // make it aktive
        td1.save();

        // delete persistently
        td1.delete();

        try {
            td1 = new UserTask(taskDefinitionOID);
            if (td1 != null) {
                fail("Deletion 1: failed deleting the task definition");
            }
        } catch (Exception e) {

        }
    }


    public void testClone() throws  ProcessException, TaskException {
        // make UserTaskDefinition td1 with Duration, and default Assignees
        //
        final UserTask td1 = new UserTask(label, description, false, 20000);

        final User user1 = makeNewUser();
        final User user2 = makeNewUser();
        final Group group1 = makeNewGroup();
        final Group group2 = makeNewGroup();

        assertNotNull("Clone 1: user1 is null",user1);
        assertNotNull("Clone 2: user2 is null",user2);
        assertNotNull("Clone 3: group1 is null",group1);
        assertNotNull("Clone 4: group2 is null",group2);

        td1.assignUser(user1);
        td1.assignUser(user2);
        td1.assignGroup(group1);
        td1.assignGroup(group2);

        td1.setDuration(new Duration(0,0,2));
        td1.save();

        // make td2 a clone of td1
        final UserTask td2 = (UserTask)td1.clone();
        assertNotNull("Clone 5: clone user task definition is null",td2);

        td2.save();

        // TESTS
        // check that properties are the same
        assertTrue("Clone 6: TaskDefinitions are not different",
                   !td2.equals(td1));

        assertEquals("Clone 7: Original and clone have different label string",
                     td1.getLabel(),
                     td2.getLabel());

        assertEquals("Clone 8: Original and clone have different description"+
                     "string",
                     td1.getDescription() ,
                     td2.getDescription() );

        assertEquals("Clone 9: Original and clone have different valid status",
                     td1.isActive() ,
                     td2.isActive());

        td2.save();

        // Check that users were copied over
        assertTrue("Clone 10: user 1 was not copied over ", td2.isAssigned(user1));
        assertTrue("Clone 11: user 2 was not copied over ", td2.isAssigned(user2));
        assertTrue("Clone 12: group 1 was not copied over ", td2.isAssigned(group1));
        assertTrue("Clone 13: group 2 was not copied over ", td2.isAssigned(group2));

        // Verify the number of users and groups
        assertEquals("Clone 14: The number of assigned users failed",
                     td2.getAssignedUserCount(),2);
        assertEquals("Clone 15: The number of assigned groups failed",
                     td2.getAssignedGroupCount(),2);

        // complex delete
        td2.delete();
    }

}
