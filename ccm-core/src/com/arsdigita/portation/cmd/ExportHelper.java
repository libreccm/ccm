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
package com.arsdigita.portation.cmd;

import com.arsdigita.portation.Format;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.categorization.CategorizationMarshaller;
import com.arsdigita.portation.modules.core.categorization.CategoryMarshaller;
import com.arsdigita.portation.modules.core.security.GroupMarshaller;
import com.arsdigita.portation.modules.core.security.GroupMembershipMarshaller;
import com.arsdigita.portation.modules.core.security.PermissionMarshaller;
import com.arsdigita.portation.modules.core.security.RoleMarshaller;
import com.arsdigita.portation.modules.core.security.RoleMembershipMarshaller;
import com.arsdigita.portation.modules.core.security.UserMarshaller;
import com.arsdigita.portation.modules.core.workflow.TaskAssignmentMarshaller;
import com.arsdigita.portation.modules.core.workflow.AssignableTaskMarshaller;
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
class ExportHelper {

    private static String pathName =
            "/home/tosmers/Svn/libreccm/ccm_ng/ccm-core/src/test/resources/" +
                    "portation/trunk-iaw-exports";
    private static boolean indentation = false;

    static void exportCategories() {
        CategoryMarshaller categoryMarshaller = new
                CategoryMarshaller();
        categoryMarshaller.prepare(Format.XML, pathName,
                "categories", indentation);
        categoryMarshaller.exportList(new ArrayList<>(
                NgCollection.categories.values()));
    }

    static void exportCategorizations() {
        CategorizationMarshaller categorizationMarshaller = new
                CategorizationMarshaller();
        categorizationMarshaller.prepare(Format.XML, pathName,
                "categorizations", indentation);
        categorizationMarshaller.exportList(new ArrayList<>(
                NgCollection.categorizations.values()));
    }

    static void exportUsers() {
        UserMarshaller userMarshaller = new UserMarshaller();
        userMarshaller.prepare(Format.XML, pathName,
                "users", indentation);
        userMarshaller.exportList(new ArrayList<>(
                NgCollection.users.values()));
    }

    static void exportGroups() {
        GroupMarshaller groupMarshaller = new GroupMarshaller();
        groupMarshaller.prepare(Format.XML, pathName,
                "groups", indentation);
        groupMarshaller.exportList(new ArrayList<>(
                NgCollection.groups.values()));
    }

    static void exportGroupMemberships() {
        GroupMembershipMarshaller groupMembershipMarshaller = new
                GroupMembershipMarshaller();
        groupMembershipMarshaller.prepare(Format.XML, pathName,
                "groupMemberships", indentation);
        groupMembershipMarshaller.exportList(new ArrayList<>(
                NgCollection.groupMemberships.values()));
    }

    static void exportRoles() {
        RoleMarshaller roleMarshaller = new RoleMarshaller();
        roleMarshaller.prepare(Format.XML, pathName,
                "roles", indentation);
        roleMarshaller.exportList(new ArrayList<>(NgCollection
                .roles.values()));
    }

    static void exportRoleMemberships() {
        RoleMembershipMarshaller roleMembershipMarshaller = new
                RoleMembershipMarshaller();
        roleMembershipMarshaller.prepare(Format.XML, pathName,
                "roleMemberships", indentation);
        roleMembershipMarshaller.exportList(new ArrayList<>
                (NgCollection.roleMemberships.values()));
    }

    static void exportWorkflowTemplates() {
        WorkflowTemplateMarshaller workflowTemplateMarshaller = new
                WorkflowTemplateMarshaller();
        workflowTemplateMarshaller.prepare(Format.XML, pathName,
                "workflowTemplates", indentation);
        workflowTemplateMarshaller.exportList(new ArrayList<>(NgCollection
                .workflowTemplates.values()));
    }

    static void exportWorkflows() {
        WorkflowMarshaller workflowMarshaller = new
                WorkflowMarshaller();
        workflowMarshaller.prepare(Format.XML, pathName,
                "workflows", indentation);
        workflowMarshaller.exportList(new ArrayList<>
                (NgCollection.workflows.values()));
    }

    static void exportAssignableTasks() {
        AssignableTaskMarshaller assignableTaskMarshaller = new
                AssignableTaskMarshaller();
        assignableTaskMarshaller.prepare(Format.XML, pathName,
                "assignableTasks", indentation);
        assignableTaskMarshaller.exportList(new ArrayList<>
                (NgCollection.assignableTasks.values()));
    }

    static void exportTaskAssignments() {
        TaskAssignmentMarshaller taskAssignmentMarshaller = new
                TaskAssignmentMarshaller();
        taskAssignmentMarshaller.prepare(Format.XML, pathName,
                "taskAssignments", indentation);
        taskAssignmentMarshaller.exportList(new ArrayList<>
                (NgCollection.taskAssignments.values()));
    }

    static void exportPermissions() {
        PermissionMarshaller permissionMarshaller = new
                PermissionMarshaller();
        permissionMarshaller.prepare(Format.XML, pathName,
                "permissions", indentation);
        permissionMarshaller.exportList(new ArrayList<>
                (NgCollection.permissions.values()));
    }
}
