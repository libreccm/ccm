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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.workflow.simple.Workflow}s into
 * ng-{@link Workflow}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 27.6.16
 */
public class WorkflowConversion extends AbstractConversion {
    private static WorkflowConversion instance;

    static {
        instance = new WorkflowConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.workflow.simple.Workflow}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Workflow}s.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("workflows");
        List<com.arsdigita.workflow.simple.Workflow> trunkWorkflows =
                com.arsdigita.workflow.simple.Workflow.getAllObjectWorkflows();

        ExportLogger.converting("workflows");
        createWorkflowAndSetAssociations(trunkWorkflows);
        setTemplateAssociations(trunkWorkflows);

        ExportLogger.sorting("workflows");
        sortWorkflowMap();

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code Workflow} and restores
     * the associations to other classes.
     *
     * @param trunkWorkflows List of all
     *                       {@link com.arsdigita.workflow.simple.Workflow}s
     *                       from this old trunk-system.
     */
    private void createWorkflowAndSetAssociations(
            List<com.arsdigita.workflow.simple.Workflow> trunkWorkflows) {
        int processed = 0;

        for (com.arsdigita.workflow.simple.Workflow
                trunkWorkflow : trunkWorkflows) {
            // create workflows
            Workflow workflow = new Workflow(trunkWorkflow);

            // set object association
            ACSObject trunkObject = trunkWorkflow.getObject();
            if (trunkObject != null) {
                CcmObject object = NgCoreCollection
                        .ccmObjects
                        .get(trunkObject.getID().longValue());
                workflow.setObject(object);
            }

            processed++;
        }
        ExportLogger.created("workflows", processed);
    }

    /**
     * Set the template associations, because an equivalent class for
     * templates does not exists in the ng-system.
     *
     * @param trunkWorkflows List of all
     *                       {@link com.arsdigita.workflow.simple.Workflow}s
     *                       from this old trunk-system.
     */
    private void setTemplateAssociations(
            List<com.arsdigita.workflow.simple.Workflow> trunkWorkflows) {
        int processed = 0;

        for (com.arsdigita.workflow.simple.Workflow trunkWorkflow :
                trunkWorkflows) {

            // set template associations
            WorkflowTemplate trunkWorkflowTemplate = trunkWorkflow
                    .getWorkflowTemplate();
            if (trunkWorkflowTemplate != null) {
                Workflow workflow = NgCoreCollection
                        .workflows
                        .get(trunkWorkflow.getID().longValue());

                Workflow template = NgCoreCollection
                        .workflows
                        .get(trunkWorkflowTemplate.getID().longValue());
                workflow.setTemplate(template);
                template.setAbstractWorkflow(true);
            } else
                processed++;
        }
        ExportLogger.found("templates", processed);
    }

    /**
     * Sorts values of workflow-map to ensure that the template-workflows will
     * be listed before the workflows constructed with this templates in the
     * export file.
     *
     * Runs once over the unsorted list and iterates over each their templates
     * to add them to the sorted list.
     */
    private void sortWorkflowMap() {
        ArrayList<Workflow> sortedList = new ArrayList<>();

        int runs = 0;
        for (Workflow workflow : NgCoreCollection.workflows.values()) {

            addTemplate(sortedList, workflow);

            if (!sortedList.contains(workflow)) {
                sortedList.add(workflow);
            }

            runs++;
        }
        NgCoreCollection.sortedWorkflows = sortedList;

        ExportLogger.ranSort("workflows", runs);
    }

    /**
     * Recursively adds the template of the given workflow to the sorted list
     * to guaranty that the templates will be imported before their workflows.
     *
     * @param sortedList List of already sorted workflows
     * @param workflow Current workflow
     */
    private void addTemplate(ArrayList<Workflow> sortedList, Workflow
            workflow) {
        Workflow template = workflow.getTemplate();

        if (template != null) {
            addTemplate(sortedList, template);

            if (!sortedList.contains(template))
                sortedList.add(template);
        }
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static WorkflowConversion getInstance() {
        return instance;
    }
}
