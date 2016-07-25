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


import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.workflow.TaskAssignment;
import com.arsdigita.portation.modules.core.workflow.UserTask;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.workflow.simple.Task;

import java.util.Iterator;
import java.util.List;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.workflow.simple.UserTask}s into
 * ng-{@link UserTask}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 29.6.16
 */
public class UserTaskConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.workflow.simple.UserTask}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link UserTask}s focusing on keeping all the
     * associations in tact. The ring dependencies of class {@code Task} have
     * to be recreated once all ng-{@link UserTask}s have been created.
     */
    public static void convertAll() {
        List<com.arsdigita.workflow.simple.UserTask> trunkUserTasks = com
                .arsdigita.workflow.simple.UserTask.getAllObjectUserTasks();

        createUserTasksAndSetAssociations(trunkUserTasks);

        setTaskRingDependencies(trunkUserTasks);
    }

    /**
     * Creates the equivalent ng-class of the {@code UserTask} and restores
     * the associations to other classes.
     *
     * @param trunkUserTasks List of all
     *                       {@link com.arsdigita.workflow.simple.UserTask}s
     *                       from this old trunk-system.
     */
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

            // taskAssignments
            GroupCollection groupCollection = trunkUserTask
                    .getAssignedGroupCollection();
            createTaskAssignments(userTask, groupCollection);
        }
    }

    /**
     * Method for creating {@link TaskAssignment}s between {@link UserTask}s
     * and {@link Role}s which is an association-class and has not been
     * existent in this old system. The {@link Role}s are represented by the
     * given groups.
     *
     * @param userTask The {@link UserTask}
     * @param groupCollection A collection of the
     *                        {@link com.arsdigita.kernel.Group}s representing
     *                        {@link com.arsdigita.kernel.Role}s belonging to
     *                        the userTask
     */
    private static void createTaskAssignments(UserTask userTask,
                                              GroupCollection groupCollection) {
        while (groupCollection.next()) {
            RoleCollection roleCollection = groupCollection.getGroup().getRoles();
            while (roleCollection.next()) {
                Role role = NgCollection.roles.get(roleCollection.getRole()
                        .getID().longValue());

                if (userTask != null && role != null) {
                    // create taskAssignments
                    TaskAssignment taskAssignment = new TaskAssignment
                            (userTask, role);

                    // set opposed associations
                    userTask.addAssignment(taskAssignment);
                    role.addAssignedTask(taskAssignment);
                }
            }
        }
    }

    /**
     * Method for recreating the
     * ng-{@link com.arsdigita.portation.modules.core.workflow.Task}s ring-like
     * dependencies between dependentTask and dependsOn. Because all
     * ng-{@link com.arsdigita.portation.modules.core.workflow.Task}s have
     * already been created, it is possible e.g. to find the dependsOn-{@code
     * Tasks} and bind them for association.
     *
     * @param trunkUserTasks List of all
     *                       {@link com.arsdigita.workflow.simple.UserTask}s
     *                       from this old trunk-system.
     */
    private static void setTaskRingDependencies(List<com.arsdigita.workflow
            .simple.UserTask> trunkUserTasks) {

        for (com.arsdigita.workflow.simple.UserTask trunkUserTask :
                trunkUserTasks) {
            UserTask userTask = NgCollection.userTasks.get(trunkUserTask.getID()
                    .longValue());

            Iterator it = trunkUserTask.getDependencies();
            while (it.hasNext()) {
                UserTask dependency = NgCollection.userTasks.get(((Task) it
                        .next())
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
