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
import com.arsdigita.portation.modules.core.workflow.Task;
import com.arsdigita.portation.modules.core.workflow.TaskAssignment;
import com.arsdigita.portation.modules.core.workflow.TaskComment;
import com.arsdigita.portation.modules.core.workflow.TaskDependency;
import com.arsdigita.portation.modules.core.workflow.Workflow;

import java.util.ArrayList;
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

        System.err.printf("\tConverting assignable tasks, task dependencies " +
                "and task assignments...\n");
        createAssignableTasksAndSetAssociations(trunkUserTasks);
        System.err.printf("\tSorting task assignments...\n");
        sortAssignableTaskMap();

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
        int pTasks = 0, pAssignments = 0, pDependencies = 0;

        for (com.arsdigita.workflow.simple.UserTask trunkUserTask :
                trunkUserTasks) {

            // TASK STUFF

            // create assignableTask
            AssignableTask assignableTask = new AssignableTask(trunkUserTask);

            // set workflow and opposed associations
            com.arsdigita.workflow.simple.Workflow userTaskWorkflow;
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
            } catch (Exception ignored) {}

            // taskDependencies
            pDependencies += createTaskDependencies(assignableTask,
                    trunkUserTask.getDependencies());

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


            // ASSIGNABLETASK STUFF

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


            /*System.err.printf("\t\tTasks: %d, " +
                                  "Dependencies: %d, " +
                                  "Assignments: %d\n",
                                  pTasks, pDependencies, pAssignments);*/
        }

        System.err.printf("\t\tCreated %d assignable tasks and\n" +
                          "\t\tCreated %d task dependencies and\n" +
                          "\t\tcreated %d task assignments.\n",
                          pTasks, pDependencies, pAssignments);
    }

    /**
     * Method for recreating the
     * ng-{@link com.arsdigita.portation.modules.core.workflow.Task}s ring-like
     * dependencies between dependentTask and dependsOn. Because all
     * ng-{@link com.arsdigita.portation.modules.core.workflow.Task}s have
     * already been created, it is possible e.g. to find the dependsOn-{@code
     * Tasks} and bind them for association.
     *
     * @param assignableTask The {@link AssignableTask}
     * @param dependencyIt An iterator representing all dependencies of the
     *                     given assignableTask
     */
    private static long createTaskDependencies(AssignableTask assignableTask,
                                               Iterator dependencyIt) {

        int processed = 0;

        while (dependencyIt.hasNext()) {
            AssignableTask dependency = NgCoreCollection
                        .assignableTasks
                        .get(((com.arsdigita.workflow.simple
                                .Task) dependencyIt.next())
                                .getID()
                                .longValue());

            if (assignableTask != null && dependency != null) {
                TaskDependency taskDependency =
                        new TaskDependency(assignableTask, dependency);

                // set opposed associations
                assignableTask.addBlockingTask(taskDependency);
                dependency.addBlockedTask(taskDependency);

                processed++;
            }
        }
        return processed;
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
     * Sorts values of assignable-task-map to ensure that the dependencies will
     * be listed before their dependant tasks in the export file.
     *
     * Runs once over the unsorted map and iterates over each their dependencies
     * to add them to the sorted list.
     */
    private static void sortAssignableTaskMap() {
        ArrayList<AssignableTask> sortedList = new ArrayList<>();

        int runs = 0;
        for (AssignableTask assignableTask :
                NgCoreCollection.assignableTasks.values()) {

            addDependencies(sortedList, assignableTask);

            if (!sortedList.contains(assignableTask)) {
                sortedList.add(assignableTask);
            }

            runs++;
        }
        NgCoreCollection.sortedAssignableTasks = sortedList;

        System.err.printf("\t\tSorted assignable tasks in %d runs.\n", runs);
    }

    /**
     * Recursively adds the dependencies of the given assignable task to the
     * sorted list to guaranty that the dependencies will be imported before
     * their dependant task.
     *
     * @param sortedList List of already sorted tasks
     * @param assignableTask Current assignable task
     */
    private static void addDependencies(ArrayList<AssignableTask> sortedList,
                                        AssignableTask assignableTask) {
        List<TaskDependency> dependencies = assignableTask.getBlockingTasks();

        if (!dependencies.isEmpty()) {
            for (TaskDependency dependency : dependencies) {
                AssignableTask blockingTask = (AssignableTask) dependency
                        .getBlockingTask();
                if (blockingTask != null) {
                    addDependencies(sortedList, blockingTask);

                    if (!sortedList.contains(blockingTask))
                        sortedList.add(blockingTask);
                }
            }
        }
    }
}
