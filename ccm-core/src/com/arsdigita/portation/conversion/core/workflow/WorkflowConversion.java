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
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.portation.modules.core.workflow.WorkflowTemplate;

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
public class WorkflowConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.workflow.simple.Workflow}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Workflow}s.
     */
    public static void convertAll() {
        System.err.printf("\tFetching workflows from database...");
        List<com.arsdigita.workflow.simple.Workflow> trunkWorkflows =
                com.arsdigita.workflow.simple.Workflow.getAllObjectWorkflows();
        System.err.println("done.");

        System.err.printf("\tConverting workflows...\n");
        createWorkflowAndSetAssociations(trunkWorkflows);
        System.err.printf("\tdone.\n");
    }

    private static void createWorkflowAndSetAssociations(
            List<com.arsdigita.workflow.simple.Workflow> trunkWorkflows) {
        long processed = 0;

        for (com.arsdigita.workflow.simple.Workflow
                trunkWorkflow : trunkWorkflows) {
            // create workflows
            Workflow workflow = new Workflow(trunkWorkflow, false);

            // set template association
            com.arsdigita.workflow.simple.WorkflowTemplate
                    trunkWorkflowTemplate = trunkWorkflow.getWorkflowTemplate();
            if (trunkWorkflowTemplate != null) {
                WorkflowTemplate workflowTemplate = NgCoreCollection
                        .workflowTemplates.get(trunkWorkflowTemplate.getID()
                                .longValue());
                workflow.setTemplate(workflowTemplate);
            }

            // set object association
            ACSObject trunkObject = trunkWorkflow.getObject();
            if (trunkObject != null) {
                CcmObject object = NgCoreCollection.ccmObjects.get(trunkObject
                        .getID().longValue());
                workflow.setObject(object);
            }

            processed++;
        }

        System.err.printf("\t\tCreated %d workflows.\n", processed);
    }
}
