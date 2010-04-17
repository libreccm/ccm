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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.CMS;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.web.Web;

import java.math.BigDecimal;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ItemWorkflowItemPane.java 754 2005-09-02 13:26:17Z sskracic $
 */
final class ItemWorkflowItemPane extends BaseWorkflowItemPane {

    private final AssignedTaskTable m_assigned;

    public ItemWorkflowItemPane(final WorkflowRequestLocal workflow,
                                final ActionLink editLink,
                                final ActionLink deleteLink) {
        super(workflow, editLink, deleteLink);

        m_actionGroup.addAction(new AdminVisible(new StartLink()));
        m_actionGroup.addAction(new AdminVisible(new StopLink()));

        m_assigned = new AssignedTaskTable(workflow);
        m_detailPane.add(new AssignedTaskSection(workflow, m_assigned));

        final TaskFinishForm taskFinishForm = new TaskFinishForm
            (new TaskSelectionRequestLocal());
        add(taskFinishForm);

        connect(m_assigned, 2, taskFinishForm);
        connect(taskFinishForm);
    }

    private final class TaskSelectionRequestLocal extends TaskRequestLocal {
        protected final Object initialValue(final PageState state) {
            final String id = m_assigned.getRowSelectionModel().getSelectedKey
                (state).toString();

            return new CMSTask(new BigDecimal(id));
        }
    }

    private boolean hasAdmin(final PageState state) {
        final SecurityManager sm = CMS.getContext().getSecurityManager();

        return sm.canAccess(state.getRequest(), WORKFLOW_ADMIN);
    }

    private class StopLink extends ActionLink {
        StopLink() {
            super(new Label(gz("cms.ui.item.workflow.stop")));

            addActionListener(new Listener());
        }

        public final boolean isVisible(final PageState state) {
            final Workflow workflow = m_workflow.getWorkflow(state);
            return workflow.getProcessState() == Workflow.STARTED;
        }

        private class Listener implements ActionListener {
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();

                if (hasAdmin(state)) {
                    final Workflow workflow = m_workflow.getWorkflow
                        (state);

                    workflow.stop(Web.getContext().getUser());
                }
            }
        }
    }

    private class StartLink extends ActionLink {
        StartLink() {
            super(new Label(gz("cms.ui.item.workflow.start")));

            addActionListener(new Listener());
        }

        public final boolean isVisible(final PageState state) {
            final Workflow workflow = m_workflow.getWorkflow(state);

            return workflow.getProcessState() == Workflow.STOPPED;
        }

        private class Listener implements ActionListener {
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();

                if (hasAdmin(state)) {
                    final Workflow workflow = m_workflow.getWorkflow
                        (state);

                    workflow.start(Web.getContext().getUser());
                }
            }
        }
    }

}
