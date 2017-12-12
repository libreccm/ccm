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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 12/12/17
 */
@JsonIdentityInfo(generator = TaskDependencyIdGenerator.class,
                  property = "customDepId")
public class TaskDependency implements Portable {

    private long taskDependencyId;
    @JsonIdentityReference(alwaysAsId = true)
    private Task blockedTask;
    @JsonIdentityReference(alwaysAsId = true)
    private Task blockingTask;

    public TaskDependency(final Task blockedTask, final Task blockingTask) {
        this.taskDependencyId = ACSObject.generateID().longValue();

        this.blockedTask = blockedTask;
        this.blockingTask = blockingTask;

        NgCoreCollection.taskDependencies.put(this.taskDependencyId, this);
    }

    public long getTaskDependencyId() {
        return taskDependencyId;
    }

    public void setTaskDependencyId(long taskDependencyId) {
        this.taskDependencyId = taskDependencyId;
    }

    public Task getBlockedTask() {
        return blockedTask;
    }

    public void setBlockedTask(Task blockedTask) {
        this.blockedTask = blockedTask;
    }

    public Task getBlockingTask() {
        return blockingTask;
    }

    public void setBlockingTask(Task blockingTask) {
        this.blockingTask = blockingTask;
    }
}
