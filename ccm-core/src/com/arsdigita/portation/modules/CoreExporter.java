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
package com.arsdigita.portation.modules;

import com.arsdigita.portation.AbstractExporter;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.CategorizationMarshaller;
import com.arsdigita.portation.modules.core.categorization.CategoryMarshaller;
import com.arsdigita.portation.modules.core.security.*;
import com.arsdigita.portation.modules.core.workflow.AssignableTaskMarshaller;
import com.arsdigita.portation.modules.core.workflow.TaskAssignmentMarshaller;
import com.arsdigita.portation.modules.core.workflow.WorkflowMarshaller;
import com.arsdigita.portation.modules.core.workflow.WorkflowTemplateMarshaller;

import java.util.ArrayList;

/**
 * Helper to implement the specifics for the exportation. Makes source code
 * in the cli-tool shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 25.07.2016
 */
public class CoreExporter extends AbstractExporter {
    public static void startExport() {
        exportUsers();
        exportGroups();
        exportGroupMemberships();
        exportRoles();
        exportRoleMemberships();

        exportCategories();
        exportCategorizations();

        exportWorkflows();
        exportWorkflowTemplates();
        exportAssignableTasks();
        exportTaskAssignments();

        exportPermissions();
    }


    private static void exportUsers() {
        System.out.printf("\tExporting users...");
        UserMarshaller userMarshaller = new UserMarshaller();
        userMarshaller.prepare(
                Format.XML, pathName, "users", indentation);
        userMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.users.values()));
        System.out.printf("\t\tdone.\n");
    }

    private static void exportGroups() {
        System.out.printf("\tExporting groups...");
        GroupMarshaller groupMarshaller = new GroupMarshaller();
        groupMarshaller.prepare(
                Format.XML, pathName, "groups", indentation);
        groupMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.groups.values()));
        System.out.printf("\t\tdone.\n");
    }

    private static void exportGroupMemberships() {
        System.out.printf("\tExporting group memberships...");
        GroupMembershipMarshaller groupMembershipMarshaller = new
                GroupMembershipMarshaller();
        groupMembershipMarshaller.prepare(
                Format.XML, pathName, "groupMemberships", indentation);
        groupMembershipMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.groupMemberships.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportRoles() {
        System.out.printf("\tExporting roles...");
        RoleMarshaller roleMarshaller = new RoleMarshaller();
        roleMarshaller.prepare(
                Format.XML, pathName, "roles", indentation);
        roleMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.roles.values()));
        System.out.printf("\t\tdone.\n");
    }

    private static void exportRoleMemberships() {
        System.out.printf("\tExporting role memberships...");
        RoleMembershipMarshaller roleMembershipMarshaller = new
                RoleMembershipMarshaller();
        roleMembershipMarshaller.prepare(
                Format.XML, pathName, "roleMemberships", indentation);
        roleMembershipMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.roleMemberships.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportCategories() {
        System.out.printf("\tExporting categories...");
        CategoryMarshaller categoryMarshaller = new CategoryMarshaller();
        categoryMarshaller.prepare(
                Format.XML, pathName, "categories", indentation);
        categoryMarshaller.exportList(NgCoreCollection.sortedCategories);
        System.out.printf("\t\tdone.\n");
    }

    private static void exportCategorizations() {
        System.out.printf("\tExporting categorizations...");
        CategorizationMarshaller categorizationMarshaller = new
                CategorizationMarshaller();
        categorizationMarshaller.prepare(
                Format.XML, pathName, "categorizations", indentation);
        categorizationMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.categorizations.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportWorkflowTemplates() {
        System.out.printf("\tExporting workflow templates...");
        WorkflowTemplateMarshaller workflowTemplateMarshaller = new
                WorkflowTemplateMarshaller();
        workflowTemplateMarshaller.prepare(
                Format.XML, pathName, "workflowTemplates", indentation);
        workflowTemplateMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.workflowTemplates.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportWorkflows() {
        System.out.printf("\tExporting workflows...");
        WorkflowMarshaller workflowMarshaller = new WorkflowMarshaller();
        workflowMarshaller.prepare(
                Format.XML, pathName, "workflows", indentation);
        workflowMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.workflows.values()));
        System.out.printf("\t\tdone.\n");
    }

    private static void exportAssignableTasks() {
        System.out.printf("\tExporting assignable tasks...");
        AssignableTaskMarshaller assignableTaskMarshaller = new
                AssignableTaskMarshaller();
        assignableTaskMarshaller.prepare(
                Format.XML, pathName, "assignableTasks", indentation);
        assignableTaskMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.assignableTasks.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportTaskAssignments() {
        System.out.printf("\tExporting task assignments...");
        TaskAssignmentMarshaller taskAssignmentMarshaller = new
                TaskAssignmentMarshaller();
        taskAssignmentMarshaller.prepare(
                Format.XML, pathName, "taskAssignments", indentation);
        taskAssignmentMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.taskAssignments.values()));
        System.out.printf("\tdone.\n");
    }

    private static void exportPermissions() {
        System.out.printf("\tExporting permissions...");
        PermissionMarshaller permissionMarshaller = new
                PermissionMarshaller();
        permissionMarshaller.prepare(
                Format.XML, pathName, "permissions", indentation);
        permissionMarshaller.exportList(
                new ArrayList<>(NgCoreCollection.permissions.values()));
        System.out.printf("\tdone.\n");
    }
}
