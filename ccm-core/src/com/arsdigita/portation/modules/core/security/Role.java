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
package com.arsdigita.portation.modules.core.security;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.arsdigita.portation.modules.core.workflow.TaskAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class Role implements Portable {

    private long roleId;
    private String name;
    private LocalizedString description;
    @JsonIgnore
    private Set<RoleMembership> memberships;
    @JsonIgnore
    private List<Permission> permissions;
    @JsonIgnore
    private List<TaskAssignment> assignedTasks;

    public Role(final com.arsdigita.kernel.Role trunkRole) {
        this.roleId = trunkRole.getID().longValue();
        this.name = trunkRole.getName();

        this.memberships  = new HashSet<>();

        this.permissions = new ArrayList<>();
        this.assignedTasks = new ArrayList<>();

        Locale local = Locale.getDefault();
        this.description = new LocalizedString();
        this.description.addValue(local, trunkRole.getDescription());

        NgCollection.roles.put(this.roleId, this);
    }

    public Role(final String name) {
        this.roleId = NgCollection.roles.size() + 1;
        this.name = name;

        this.memberships  = new HashSet<>();

        this.permissions = new ArrayList<>();
        this.assignedTasks = new ArrayList<>();

        this.description = new LocalizedString();

        NgCollection.roles.put(this.roleId, this);
    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new RoleMarshaller();
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(final long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<RoleMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(final Set<RoleMembership> memberships) {
        this.memberships = memberships;
    }

    public void addMembership(final RoleMembership membership) {
        memberships.add(membership);
    }

    public void removeMembership(final RoleMembership membership) {
        memberships.remove(membership);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(final Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(final Permission permission) {
        permissions.remove(permission);
    }

    public List<TaskAssignment> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(final List<TaskAssignment> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public void addAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.add(taskAssignment);
    }

    public void removeAssignedTask(final TaskAssignment taskAssignment) {
        assignedTasks.remove(taskAssignment);
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }
}
