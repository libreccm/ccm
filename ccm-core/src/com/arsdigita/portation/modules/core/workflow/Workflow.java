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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class Workflow implements Identifiable {

    private long workflowId;

    private LocalizedString name;
    private LocalizedString description;

    private List<Task> tasks;

    public Workflow(final com.arsdigita.workflow.simple.Workflow trunkWorkFlow) {
        this.workflowId = trunkWorkFlow.getID().longValue();

        this.name.addValue(Locale.ENGLISH, trunkWorkFlow.getDisplayName());
        this.description.addValue(Locale.ENGLISH, trunkWorkFlow.getDescription());

        this.tasks = new ArrayList<>();

        NgCollection.workflows.put(this.workflowId, this);
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new WorkflowMarshaller();
    }

    public long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(final long workflowId) {
        this.workflowId = workflowId;
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
