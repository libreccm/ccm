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
package com.arsdigita.portation.conversion.core.workflow;


import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.workflow.Task;
import com.arsdigita.portation.modules.core.workflow.Workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/29/16
 */
public class TaskConversion {

    public static void convertAll() {
        // Todo:
        List<com.arsdigita.workflow.simple.Task> trunkTask = new ArrayList<>();

        trunkTask.forEach(Task::new);

        setWorkflow(trunkTask);
    }

    private static void setWorkflow(List<com.arsdigita.workflow.simple.Task>
                                            trunkTasks) {
        long id, workflowId;
        Task task;
        Workflow workflow;

        for (com.arsdigita.workflow.simple.Task trunkTask : trunkTasks) {
            id = trunkTask.getID().longValue();
            workflowId = trunkTask.getWorkflow().getID().longValue();

            task = NgCollection.tasks.get(id);
            workflow = NgCollection.workflows.get(workflowId);

            if (task != null && workflow != null) {
                task.setWorkflow(workflow);
                workflow.addTask(task);
            }
        }

    }
}
