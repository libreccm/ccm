/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.workflow.simple.Workflow;
import org.apache.log4j.Logger;

/**
 * @author Uday Mathur
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WorkflowEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class WorkflowEditForm extends BaseWorkflowForm {
    public static final String versionId =
        "$Id: WorkflowEditForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(WorkflowEditForm.class);

    private final WorkflowRequestLocal m_workflow;

    WorkflowEditForm(final WorkflowRequestLocal workflow) {
        super("workflow", gz("cms.ui.workflow.edit"));

        m_workflow = workflow;

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final Workflow workflow = m_workflow.getWorkflow(state);

            workflow.setLabel((String) m_title.getValue(state));
            workflow.setDescription((String) m_description.getValue(state));

            workflow.save();
        }
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();
            final Workflow workflow = m_workflow.getWorkflow(state);

            m_title.setValue(state, workflow.getLabel());
            m_description.setValue(state, workflow.getDescription());
        }
    }
}
