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

import com.arsdigita.portation.modules.core.workflow.WorkflowTemplate;

import java.util.List;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.workflow.simple.WorkflowTemplate}s into
 * ng-{@link WorkflowTemplate}s as preparation for a successful export of all
 * trunk classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 11/21/16
 */
public class WorkflowTemplateConversion {

    /**
     * Retrieves all
     * trunk-{@link com.arsdigita.workflow.simple.WorkflowTemplate}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link WorkflowTemplate}s.
     */
    public static void convertAll() {
        System.err.printf("\tFetching workflow templates from database...");
        List<com.arsdigita.workflow.simple.WorkflowTemplate>
                trunkWorkflowTemplates = com.arsdigita.workflow.simple
                .WorkflowTemplate.getAllObjectWorkflowTemplates();
        System.err.println("done.");

        System.err.printf("\tConverting workflow templates...\n");
        int processed = 0;
        for (com.arsdigita.workflow.simple.WorkflowTemplate
                trunkWorkflowTemplate : trunkWorkflowTemplates) {
            new WorkflowTemplate(trunkWorkflowTemplate);
            processed++;
        }
        System.out.printf("\t\tCreated %d workflow templates.\n", processed);
        System.err.println("\tdone.\n");
    }
}
