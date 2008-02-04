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
import com.arsdigita.persistence.OID;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * This is the Junit testcase for TaskDefintion class. Assumed Tinman environment.
 *
 * The Tests are the following:
 *                             - Construction of TaskDefinition
 *                             - Loading of a stored Task
 *                             - Updating (change s.th., save, retrieve and check)
 *                             - Cloning of a Task
 *                             - Setting Active
 *                             - Deletion
 *                             - AddDependencies
 *                             - RemoveDependencies
 *                             - setActive
 *                             - IsDependency
 *                             - hasLoop
 * @author Khy Huang
 * @author Stefan Deusch
 */
public class TaskDefinitionTest extends WorkflowProcessTestHarness {

    public static final String versionId = "$Id: TaskDefinitionTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // initialize reference material

    public TaskDefinitionTest(String name) {
        super(name, 10);
    }

    // ------------- TESTS ----------------------------

    public void testCreate() throws ProcessException, TaskException {
        Task taskDef = new Task("Task Def 1",
                                "Task Def 1 description");
        assertNotNull(taskDef);
        taskDef.save();
    }

    public void testReload() throws ProcessException, TaskException {

        Task taskDef = new Task("Task Def 2",
                                "Task Def 2 description");
        assertNotNull(taskDef);
        taskDef.save();

        OID taskOID = taskDef.getOID();
        Task taskDefFromDb = null;

        try {
            taskDefFromDb = new Task(taskOID);
        } catch (Exception e) {
            fail("testReload 1: could not load task definition using OID ("+
                 e+")");
        }
        assertNotNull(taskDefFromDb);

        assertTrue("Task definition from db and current task should be equals()",
                   taskDefFromDb.equals(taskDef));


        // Comparing the task definition label
        assertEquals("testReload 2: Label Failed to reload from the db",
                     taskDefFromDb.getLabel(),
                     taskDef.getLabel());

        // Comparing the task definition description
        assertEquals("testReload 3: Description Failed to reload from the db",
                     taskDefFromDb.getDescription(),
                     taskDef.getDescription());

        assertEquals("testReload 4: Different Label then the one used",
                     taskDefFromDb.getLabel(),
                     "Task Def 2");

        assertEquals("testReload 5: Different description then the one used",
                     taskDefFromDb.getDescription(),
                     "Task Def 2 description");
    }

    public void testUpdate() throws ProcessException, TaskException {
        Task td;
        td = new Task("TaskDef label Test","TaskDef Description Test");

        String label = "New Test Label",
            description = "New Test Description....";

        td.setLabel(label);
        td.setDescription(description);
        td.save(); // should update in the DB

        try {
            td = new Task(td.getOID());
        } catch (Exception e) {
            fail("testUpdate: failed restoring task definition with OID");
        }

        assertEquals("Update 1: The label is different", td.getLabel(),label);

        assertEquals("Update 2: The description is different",
                     td.getDescription(), description);
    }


    /**
     * Testing the cloning process
     *
     **/
    public void testClone() throws  ProcessException, TaskException {

        String label = "testing clone label";
        String description = "testing clone description";

        Task td = new Task(label, description);
        Task td_clone = null;

        try {
            td_clone = (Task)td.clone();
        } catch (Exception e) {
            fail("testClone: failed while cloning ("+e+")");

        }

        assertNotNull("testClone 1: the td_clone is null", td_clone);

        // is the clone filled with values?
        assertNotNull("testClone 2:  description exists",
                      td_clone.getDescription());

        assertNotNull("testClone 3: label exists", td_clone.getLabel());

        assertEquals("testClone 4: Original and clone do not "+
                     "have same description",
                     td.getDescription() ,
                     td_clone.getDescription());

        assertEquals("testClone 5: Original and clone do not "+
                     "have the same label",
                     td.getLabel() ,
                     td_clone.getLabel());

        assertTrue("testClone 6: Origin and clone have the same Id",
                   td.getOID() != td_clone.getOID());

    }


    /**
     * test activating  a TaskDefinition
     **/
    public void testActivate() throws  ProcessException, TaskException {
        String label = "test activation label";
        String description = "test activation description ";

        Task td = new Task(label, description);
        td.setActive(true);
        td.save();

        OID id = td.getOID();
        td = null;
        System.gc();

        // reload
        Task td2 = null;
        try {
            td2 = new Task(id);
            assertNotNull(td2);
        } catch (Exception e) {
            fail("testActivate: failed recreating task def from OID");
        }
        assertTrue("Reloaded Task Definition is not valid (active) anymore",
                   td2.isActive());
    }

    /**
     * test deletion:
     */
    public void testDeletion() throws  ProcessException, TaskException {
        String label = "delete label test";
        String description = "delete description test";

        Task td = new Task(label, description);
        OID taskDefinitionOID = td.getOID();

        td.setActive(true); // make it aktive
        td.save();

        td.delete();

        try {
            td = new Task(taskDefinitionOID);
            fail("testDeletion: failed because we recreated a deleted task");
        } catch (Exception e) {

        }

    }

    public void testDependencies()
        throws  ProcessException, TaskException, DataObjectNotFoundException {

        createTestProcessDefinition();

        String label = "test dependency";
        String description = "test dependency description";
        Task taskDefinition = new Task(label, description);
        Task taskDefinitionDuplicate = null;
        taskDefinition.setActive(true);
        taskDefinition.save();

        OID id = taskDefinition.getOID();
        int dependencySize = 10;

        // Create new task definitions and create dependencies
        // save
        taskDefinition.save();

        for ( int i = 0; i < 10; i++ ) {
            taskDefinition.addDependency(task[i]);
        }

        assertEquals("The number of dependencies does not match (1)", dependencySize,
                     taskDefinition.getDependencyCount());
        assertNotNull("task definition is null", taskDefinition);

        // Check if isDependency is functional
        assertTrue("Dependency 1: isDependency failed",
                   taskDefinition.isDependency(task[8]));


        // Create a loop and see if it is caught
        boolean loopDoesNotExist = true;
        if (task[8].addDependency(taskDefinition)) {
            loopDoesNotExist = false;
        }

        task[8].save();

        assertTrue("Dependency 2: Loop back was not caught", loopDoesNotExist);


        // Check that the dependency was removed
        int dependencyCount = taskDefinition.getDependencyCount();

        // Removing dependencies
        taskDefinition.removeDependency(task[8]);
        taskDefinition.save();

        assertEquals("Dependency 3: Removed Dependency failed", dependencyCount - 1,
                   taskDefinition.getDependencyCount());

        dependencyCount = taskDefinition.getDependencyCount();

        // Removing dependencies
        taskDefinition.removeDependency(task[7]);
        taskDefinition.save();

        assertTrue("Dependency 4: Removed Dependency failed using clone",
                   (dependencyCount-1) == taskDefinition.getDependencyCount());

        // Check to see if save dependency and reload works
        taskDefinition.save();

        try {
            taskDefinitionDuplicate =
                new Task(taskDefinition.getOID());
        } catch (Exception e) {
            fail("Dependency 5:  failed recreating task def using OID");
        }

        assertEquals("Dependency 6: Removed depedency and reload from db failed",
                     taskDefinition.getDependencyCount() ,
                     taskDefinitionDuplicate.getDependencyCount());

        // Check to see if removeAllDependencies is functional
        taskDefinition.removeAllDependencies();
        taskDefinition.save();

        assertTrue("Dependency 7: Remove all dependencies failed",
                   taskDefinition.getDependencyCount() == 0);

        // Check if reload works
        try {
            taskDefinition =
                new Task(taskDefinition.getOID());
        } catch (Exception e) {
            fail("Dependency 8: failed recreating task def using OID");
        }
        assertTrue("Dependency 9: Reload after removing all dependencies failed",
                   taskDefinition.getDependencyCount() == 0);
    }

    /**
     * Test adding / removing Dependencies, can we find dependencies,
     * is the order preserved
     * test isDependency function, make a loop dependency and test hasLoop!
     **/

    // ----------------------------- END TESTS ------------------------------------

    public String toString() {
        return "TaskDefinitionTest";
    }


    public static Test suite() {
        TestSuite suite = new TestSuite(TaskDefinitionTest.class);
        // suite.addTest(new TaskDefinitionTest("Task Definition Procedures"));
        return suite;
    }


    protected Workflow createTestProcessDefinition() {
        m_arrayToIdMap.clear();
        m_idToArrayMap.clear();

        for (int i = 0; i<10; i++) {
            task[i] = createTask(i);
            m_arrayToIdMap.put(task[i].getID(), new Integer(i));
            m_idToArrayMap.put(new Integer(i), task[i].getID());
        }

        // I know this is a horrible abuse, and the
        // test harness could use some design, but I was
        // lazy and I wanted some data structures already
        // set up for me.
        return new Workflow();

    }


}
