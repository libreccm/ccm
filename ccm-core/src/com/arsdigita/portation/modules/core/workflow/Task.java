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

import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.arsdigita.portation.modules.core.workflow.util.StateMapper;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = TaskIdResolver.class,
                  property = "uuid")
public class Task {

    private long taskId;
    private String uuid;
    private LocalizedString label;
    private LocalizedString description;
    private boolean active;
    private TaskState taskState;
    @JsonIdentityReference(alwaysAsId = true)
    private Workflow workflow;
    @JsonIgnore
    private List<Task> dependentTasks;
    @JsonIdentityReference(alwaysAsId = true)
    private List<Task> dependsOn;
    private List<TaskComment> comments;


    public Task(final com.arsdigita.workflow.simple.Task trunkTask) {
        this.taskId = trunkTask.getID().longValue();
        this.uuid = UUID.randomUUID().toString();

        this.label = new LocalizedString();
        this.label.addValue(Locale.getDefault(), trunkTask.getLabel());
        this.description = new LocalizedString();
        this.description.addValue(Locale.getDefault(), trunkTask.getDescription());

        this.active = trunkTask.isActive();
        this.taskState = StateMapper.mapTaskState(trunkTask.getState());

        //this.workflow

        this.dependentTasks = new ArrayList<>();
        this.dependsOn = new ArrayList<>();
        this.comments = new ArrayList<>();

        NgCoreCollection.tasks.put(this.getTaskId(), this);
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(final long taskId) {
        this.taskId = taskId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(final TaskState taskState) {
        this.taskState = taskState;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final Workflow workflow) {
        this.workflow = workflow;
    }

    public List<Task> getDependentTasks() {
        return dependentTasks;
    }

    public void setDependentTasks(final List<Task> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }

    public void addDependentTask(final Task task) {
        dependentTasks.add(task);
    }

    public void removeDependentTask(final Task task) {
        dependentTasks.remove(task);
    }

    public List<Task> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(final List<Task> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public void addDependsOn(final Task task) {
        dependsOn.add(task);
    }


    public void removeDependsOn(final Task task) {
        dependsOn.remove(task);
    }

    public List<TaskComment> getComments() {
        return comments;
    }

    public void setComments(final List<TaskComment> comments) {
        this.comments = comments;
    }

    public void addComment(final TaskComment comment) {
        comments.add(comment);
    }

    public void removeComment(final TaskComment comment) {
        comments.remove(comment);
    }
}
