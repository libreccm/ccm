/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.conversion.core.workflow;


import com.arsdigita.kernel.UserCollection;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.workflow.UserTask;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.workflow.simple.Task;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/29/16
 */
public class UserTaskConversion {

    public static void convertAll() {
        List<com.arsdigita.workflow.simple.UserTask> trunkUserTasks = com
                .arsdigita.workflow.simple.UserTask.getAllObjectUserTasks();

        createUserTasksAndSetAssociations(trunkUserTasks);

        setTaskRingDependencies(trunkUserTasks);
    }

    private static void createUserTasksAndSetAssociations(List<com.arsdigita
            .workflow.simple.UserTask> trunkUserTasks) {
        for (com.arsdigita.workflow.simple.UserTask trunkUserTask :
                trunkUserTasks) {

            // create userTask
            UserTask userTask = new UserTask(trunkUserTask);

            // set workflow and opposed associations
            Workflow workflow = NgCollection.workflows.get(
                    trunkUserTask.getWorkflow().getID().longValue());
            if (workflow != null) {
                userTask.setWorkflow(workflow);
                workflow.addTask(userTask);
            }

            // set lockingUser and notificationSender
            User lockingUser = NgCollection.users.get(trunkUserTask
                    .getLockedUser()
                    .getID().longValue());
            User notificationSender = NgCollection.users.get(trunkUserTask
                    .getNotificationSender().getID().longValue());
            if (lockingUser != null)
                userTask.setLockingUser(lockingUser);
            if (notificationSender != null)
                userTask.setNotificationSender(notificationSender);

            // create taskAssignments
            UserCollection userCollection = trunkUserTask
                    .getAssignedUserCollection();
            createTaskAssignments(userTask, userCollection);
        }
    }

    private static void createTaskAssignments(UserTask userTask,
                                              UserCollection userCollection) {
        while (userCollection.next()) {
//            Role role = NgCollection.users.get(userCollection.getUser().getID
//                    ().longValue()).getRoleMemberships().;
        }
    }

    private static void setTaskRingDependencies(List<com.arsdigita.workflow
            .simple.UserTask> trunkUserTasks) {
        UserTask userTask, dependency;

        for (com.arsdigita.workflow.simple.UserTask trunkUserTask :
                trunkUserTasks) {
            userTask = NgCollection.userTasks.get(trunkUserTask.getID()
                    .longValue());

            Iterator it = trunkUserTask.getDependencies();
            while (it.hasNext()) {
                dependency = NgCollection.userTasks.get(((Task) it.next())
                        .getID().longValue());

                if (userTask != null && dependency != null) {
                    // set dependencies and opposed
                    userTask.addDependsOn(dependency);
                    dependency.addDependentTask(userTask);
                }
            }

        }
    }
}
