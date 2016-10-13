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
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class Task implements Identifiable {

    private long taskId;
    private LocalizedString label;
    private LocalizedString description;

    private boolean active;
    private String taskState;

    @JsonBackReference
    private Workflow workflow;

    @JsonBackReference
    private List<Task> dependentTasks;
    @JsonManagedReference
    private List<Task> dependsOn;
    private List<String> comments;

    public Task(final com.arsdigita.workflow.simple.Task trunkTask) {
        this.taskId = trunkTask.getID().longValue();

        this.label = new LocalizedString();
        this.label.addValue(Locale.ENGLISH, trunkTask.getLabel());
        this.description = new LocalizedString();
        this.description.addValue(Locale.ENGLISH, trunkTask.getDescription());

        this.active = trunkTask.isActive();
        this.taskState = trunkTask.getStateString();

        //this.workflow

        this.dependentTasks = new ArrayList<>();
        this.dependsOn = new ArrayList<>();
        this.comments = new ArrayList<>();
        Iterator commentsIt = trunkTask.getComments();
        while (commentsIt.hasNext()) {
            addComment(commentsIt.next().toString());
        }

        NgCollection.tasks.put(this.getTaskId(), this);
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new TaskMarshaller();
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(final long taskId) {
        this.taskId = taskId;
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

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(final String taskState) {
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


    public List<String> getComments() {
        return comments;
    }

    public void setComments(final List<String> comments) {
        this.comments = comments;
    }

    public void addComment(final String comment) {
        comments.add(comment);
    }

    public void removeComment(final String comment) {
        comments.remove(comment);
    }
}
