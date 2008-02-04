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
import com.arsdigita.persistence.OID;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.Iterator;

/**
 * This is the Junit testcase for Task class. Assumed Tinman environment.
 *
 *
 * @author Uday Mathur
 *
 */
public class PersistenceTest extends BaseTestCase {
    public static final String versionId = "$Id: PersistenceTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public PersistenceTest(String name) {
        super(name);
    }

    public void testPersistence() {
        String label = "Persistence test";
        String description =  "Persistence test description";

        //Create a new task
        UserTask userTask = new UserTask(label, description);
        OID taskOID;

        //assign 5 groups to this task
        int groupCreated = 5;
        Group[] groups = new Group[groupCreated];

        for (int x = 0; x < groupCreated; x++) {
            groups[x] = new Group();
            groups[x].setName("Workflow Test " + x);
            groups[x].save();
            userTask.assignGroup(groups[x]);
        }
        //store persistently
        userTask.save();

        //let's make sure that assignGroup() actually worked by
        //that we have 5 assigned groups

        assertEquals(5,userTask.getAssignedGroupCount());

        taskOID = userTask.getOID();

        // now let's reload the object and make sure those 5 tasks
        // were stored persistently
        userTask = null;
        try {
            userTask = new UserTask(taskOID);
        } catch (DataObjectNotFoundException d) {
            fail(d.getMessage());
        }
        assertEquals(5,userTask.getAssignedGroupCount());


        //now we will create another userTask and copy the task
        //assignees from the original task (this is so you don't have
        //to sift through the ugle clone() method.
        UserTask different = new UserTask();
        Group g;
        //use this counter to make sure 5 tasks were actually copied.
        int counter = 0;
        for (Iterator i = userTask.getAssignedGroups(); i.hasNext(); counter++) {
            g = (Group) i.next();
            different.assignGroup(g);
        }
        //make sure the domain object before saving has 5 tasks
        assertEquals(5,counter);
        assertEquals(5,different.getAssignedGroupCount());
        different.save();
        taskOID = different.getOID();

        //now dump it and reload it to see if the tasks were saved to
        //persistent storage (When sifting through the logs I did not
        //find the correct insert statement)
        different = null;
        try {
            different = new UserTask(taskOID);
        } catch (DataObjectNotFoundException d) {
            fail(d.getMessage());
        }
        assertEquals(5,different.getAssignedGroupCount());
    }
}
