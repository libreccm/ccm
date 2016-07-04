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
package com.arsdigita.portation.modules.core.workflow;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.security.Role;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class TaskAssignment implements Identifiable {

    private long taskAssignmentId;
    private UserTask task;
    private Role role;

    public TaskAssignment() {

    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new TaskAssignmentMarshaller();
    }

    public long getTaskAssignmentId() {
        return taskAssignmentId;
    }

    public void setTaskAssignmentId(final long taskAssignmentId) {
        this.taskAssignmentId = taskAssignmentId;
    }

    public UserTask getTask() {
        return task;
    }

    public void setTask(final UserTask task) {
        this.task = task;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
}
