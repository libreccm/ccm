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
package com.arsdigita.portation.conversion;

import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.*;
import com.arsdigita.portation.modules.core.workflow.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Storage class for all ng-objects after conversion. This also helps for an
 * easier access for the restoration of the dependencies.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 27.6.16
 */
public class NgCoreCollection {
    public static Map<Long, Party> parties = new HashMap<>();
    public static Map<Long, User> users = new HashMap<>();
    public static Map<Long, Group> groups = new HashMap<>();
    public static Map<Long, GroupMembership> groupMemberships = new HashMap<>();

    public static Map<Long, Role> roles = new HashMap<>();
    public static Map<Long, RoleMembership> roleMemberships = new HashMap<>();

    public static Map<Long, CcmObject> ccmObjects = new HashMap<>();
    public static Map<Long, Category> categories = new TreeMap<>();
    public static Map<Long, Categorization> categorizations = new HashMap<>();

    public static Map<Long, Workflow> workflows = new HashMap<>();
    public static Map<Long, WorkflowTemplate> workflowTemplates = new HashMap<>();
    public static Map<Long, TaskComment> taskComments = new HashMap<>();
    public static Map<Long, Task> tasks = new HashMap<>();
    public static Map<Long, AssignableTask> assignableTasks = new HashMap<>();
    public static Map<Long, TaskAssignment> taskAssignments = new HashMap<>();

    public static Map<Long, Permission> permissions = new HashMap<>();


    // if lists need to be sorted in specific way to work with import
    public static ArrayList<Category> sortedCategories;


    /**
     * Private constructor to prevent the instantiation of this class.
     */
    private NgCoreCollection() {}

    /**
     * Sorts values of category-map to ensure that the parent-categories will
     * be listed before their childs in the export file.
     *
     * Runs once over the unsorted list and iterates over each their parents
     * to add them to the sorted list. After being added to the sorted list the
     * category will be removed from the unsorted list and therefore ignored
     * in this foreach run.
     */
    public static void sortCategories() {
        ArrayList<Category> unsortedCategories =
                new ArrayList<>(categories.values());
        sortedCategories = new ArrayList<>(unsortedCategories.size());

        int runs = 0;
        for (Category anUnsorted : unsortedCategories) {
            add(anUnsorted);
            runs++;
        }
        System.err.printf("\t\tSorted categories in %d runs.\n", runs);
    }

    /**
     * Helper method to recursively add all parent categories before their
     * childs.
     *
     * @param category the current category in the unsorted list
     */
    private static void add(Category category) {
        Category parent = category.getParentCategory();

        if (parent != null && !sortedCategories.contains(parent)) {
            add(parent);
        }

        if (!sortedCategories.contains(category)) {
            sortedCategories.add(category);
        }
    }

    /**
     * Removes the workflow templates from the list of all workflows for
     * easier importation in ng as workflow is the parent class of all
     * templates. Now you don't have to consider the already imported
     * templates while importing all other workflows and being in danger of
     * duplications.
     */
    public static void removeTemplatesFromWorkflows() {
        int removed = 0;
        for (long templateId : workflowTemplates.keySet()) {
            workflows.remove(templateId);
            removed++;
        }
        System.err.printf("\t\tRemoved %d templates from over all workflows." +
                "\n", removed);
    }
}
