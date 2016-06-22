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
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class Task implements Identifiable {

    private long taskId;
    private LocalizedString label;
    private LocalizedString description;
    private boolean active;
    private String taskState;
    private Workflow workflow;
    private List<Task> dependentTasks;
    private List<Task> dependsOn;
    private List<String> comments;

    public Task(final com.arsdigita.workflow.simple.Task trunkTask) {

    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new TaskMarshaller();
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public List<Task> getDependentTasks() {
        return dependentTasks;
    }

    public void setDependentTasks(List<Task> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }

    public List<Task> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<Task> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
