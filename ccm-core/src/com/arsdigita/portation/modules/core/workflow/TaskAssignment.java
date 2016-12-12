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
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.security.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class TaskAssignment implements Portable {

    private long taskAssignmentId;

    @JsonManagedReference
    private AssignableTask task;
    @JsonManagedReference
    private Role role;

    public TaskAssignment(final AssignableTask task, final Role role) {
        this.taskAssignmentId = NgCollection.taskAssignments.size() + 1;

        this.task = task;
        this.role = role;

        NgCollection.taskAssignments.put(this.taskAssignmentId, this);
    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new TaskAssignmentMarshaller();
    }

    public long getTaskAssignmentId() {
        return taskAssignmentId;
    }

    public void setTaskAssignmentId(final long taskAssignmentId) {
        this.taskAssignmentId = taskAssignmentId;
    }

    public AssignableTask getTask() {
        return task;
    }

    public void setTask(final AssignableTask task) {
        this.task = task;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
}
