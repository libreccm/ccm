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
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.workflow.AssignableTask;
import com.arsdigita.portation.modules.core.workflow.TaskAssignment;
import com.arsdigita.portation.modules.core.workflow.TaskComment;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.workflow.simple.Task;

import java.util.Iterator;
import java.util.List;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.workflow.simple.UserTask}s into
 * ng-{@link AssignableTask}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 29.6.16
 */
public class AssignableTaskConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.workflow.simple.UserTask}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link AssignableTask}s focusing on keeping all the
     * associations in tact. The ring dependencies of class {@code Task} have
     * to be recreated once all ng-{@link AssignableTask}s have been created.
     */
    public static void convertAll() {
        System.err.printf("\tFetching assignable tasks from database...");
        List<com.arsdigita.workflow.simple.UserTask> trunkUserTasks = com
                .arsdigita.workflow.simple.UserTask.getAllObjectUserTasks();
        System.err.println("done.");

        System.err.printf("\tConverting assignable tasks and task " +
                "assignments...\n");
        createAssignableTasksAndSetAssociations(trunkUserTasks);
        setTaskRingDependencies(trunkUserTasks);
        System.err.println("\tdone.\n");

    }

    /**
     * Creates the equivalent ng-class of the {@code AssignableTask} and restores
     * the associations to other classes.
     *
     * @param trunkUserTasks List of all
     *                       {@link com.arsdigita.workflow.simple.UserTask}s
     *                       from this old trunk-system.
     */
    private static void createAssignableTasksAndSetAssociations(List<com.arsdigita
            .workflow.simple.UserTask> trunkUserTasks) {
        int pTasks = 0, pAssignments = 0;

        for (com.arsdigita.workflow.simple.UserTask trunkUserTask :
                trunkUserTasks) {

            // create assignableTask
            AssignableTask assignableTask = new AssignableTask(trunkUserTask);

            // set workflow and opposed associations
            com.arsdigita.workflow.simple.Workflow userTaskWorkflow = null;
            try {
                userTaskWorkflow = trunkUserTask.getWorkflow();
                if (userTaskWorkflow != null) {
                    Workflow workflow = NgCoreCollection.workflows.get(
                            userTaskWorkflow.getID().longValue());
                    if (workflow != null) {
                        assignableTask.setWorkflow(workflow);
                        workflow.addTask(assignableTask);
                    }
                }
            } catch (Exception e) {}

            // set taskComments
            Iterator commentsIt = trunkUserTask.getComments();
            while (commentsIt.hasNext()) {
                com.arsdigita.workflow.simple.TaskComment trunkTaskComment = (com
                        .arsdigita.workflow.simple.TaskComment) commentsIt.next();

                TaskComment taskComment = new TaskComment(trunkTaskComment);
                User author = NgCoreCollection.users.get(
                        trunkTaskComment.getUser().getID().longValue());
                taskComment.setAuthor(author);

                assignableTask.addComment(taskComment);
            }


            // set lockingUser and notificationSender
            if (trunkUserTask.getLockedUser() != null) {
                User lockingUser = NgCoreCollection.users.get(trunkUserTask
                        .getLockedUser()
                        .getID().longValue());
                if (lockingUser != null)
                    assignableTask.setLockingUser(lockingUser);
            }
            if (trunkUserTask.getNotificationSender() != null) {
                User notificationSender = NgCoreCollection.users.get(trunkUserTask
                        .getNotificationSender().getID().longValue());
                if (notificationSender != null)
                    assignableTask.setNotificationSender(notificationSender);
            }

            // taskAssignments
            GroupCollection groupCollection = trunkUserTask
                    .getAssignedGroupCollection();
            pAssignments += createTaskAssignments(assignableTask,
                    groupCollection);

            pTasks++;
        }

        System.err.printf("\t\tCreated %d assignable tasks and\n" +
                          "\t\tcreated %d task assignments.\n",
                          pTasks, pAssignments);
    }

    /**
     * Method for creating {@link TaskAssignment}s between {@link AssignableTask}s
     * and {@link Role}s which is an association-class and has not been
     * existent in this old system. The {@link Role}s are represented by the
     * given groups.
     *
     * @param assignableTask The {@link AssignableTask}
     * @param groupCollection A collection of the
     *                        {@link com.arsdigita.kernel.Group}s representing
     *                        {@link com.arsdigita.kernel.Role}s belonging to
     *                        the assignableTask
     */
    private static long  createTaskAssignments(AssignableTask assignableTask,
                                              GroupCollection groupCollection) {
        int processed = 0;

        while (groupCollection.next()) {
            RoleCollection roleCollection = groupCollection.getGroup().getRoles();
            while (roleCollection.next()) {
                Role role = NgCoreCollection.roles.get(roleCollection.getRole()
                        .getID().longValue());

                if (assignableTask != null && role != null) {
                    // create taskAssignments
                    TaskAssignment taskAssignment = new TaskAssignment
                            (assignableTask, role);

                    // set opposed associations
                    assignableTask.addAssignment(taskAssignment);
                    role.addAssignedTask(taskAssignment);

                    processed++;
                }
            }
        }

        return processed;
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
            AssignableTask assignableTask = NgCoreCollection.assignableTasks.get(trunkUserTask.getID()
                    .longValue());

            Iterator it = trunkUserTask.getDependencies();
            while (it.hasNext()) {
                AssignableTask dependency = NgCoreCollection.assignableTasks.get(((Task) it
                        .next())
                        .getID().longValue());

                if (assignableTask != null && dependency != null) {
                    // set dependencies and opposed
                    assignableTask.addDependsOn(dependency);
                    dependency.addDependentTask(assignableTask);
                }
            }
        }
    }
}
