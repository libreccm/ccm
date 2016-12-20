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
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.arsdigita.portation.modules.core.workflow.util.StateMapper;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class Workflow implements Portable {

    private long workflowId;
    private String uuid;

    private WorkflowTemplate template;

    private LocalizedString name;
    private LocalizedString description;

    private WorkflowState workflowState;
    private boolean active;
    private TaskState tasksState;

    private CcmObject object;

    @JsonManagedReference
    private List<Task> tasks;


    public Workflow(final com.arsdigita.workflow.simple.Workflow
                            trunkWorkFlow, boolean template) {
        this.workflowId = trunkWorkFlow.getID().longValue();
        this.uuid = UUID.randomUUID().toString();

        //template

        this.name = new LocalizedString();
        this.name.addValue(Locale.getDefault(), trunkWorkFlow.getDisplayName());
        this.description = new LocalizedString();
        this.description.addValue(Locale.getDefault(),
                trunkWorkFlow.getDescription());

        this.workflowState = StateMapper.mapWorkflowState(trunkWorkFlow
                .getProcessState());
        this.active = trunkWorkFlow.isActive();
        this.tasksState = StateMapper.mapTaskState(trunkWorkFlow.getState());

        //object

        this.tasks = new ArrayList<>();

        if (!template)
            NgCollection.workflows.put(this.workflowId, this);
    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new WorkflowMarshaller();
    }

    public long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public WorkflowTemplate getTemplate() {
        return template;
    }

    public void setTemplate(final WorkflowTemplate template) {
        this.template = template;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(final WorkflowState workflowState) {
        this.workflowState = workflowState;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public TaskState getTasksState() {
        return tasksState;
    }

    public void setTasksState(final TaskState tasksState) {
        this.tasksState = tasksState;
    }

    public CcmObject getObject() {
        return object;
    }

    public void setObject(final CcmObject object) {
        this.object = object;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(final List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(final Task task) {
        tasks.add(task);
    }

    public void removeTask(final Task task) {
        tasks.remove(task);
    }
}
