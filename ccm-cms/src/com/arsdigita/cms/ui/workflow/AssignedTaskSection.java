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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Workflow;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * @author unknown
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: AssignedTaskSection.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
public final class AssignedTaskSection extends Section {

    private static final Logger s_log = Logger.getLogger(AssignedTaskSection.class);
    private final WorkflowRequestLocal m_workflow;
    private final WorkflowFacade m_facade;

    public AssignedTaskSection(final WorkflowRequestLocal workflow,
                               final Component subject) {
        super(gz("cms.ui.workflow.task.assigned"));

        m_workflow = workflow;
        m_facade = new WorkflowFacade(m_workflow);

        final ActionGroup group = new ActionGroup();
        setBody(group);

        group.setSubject(subject);
        group.addAction(new RestartLink());
        //jensp 2014-06-06 Removed this two links because the funcationality they provide should
        //be accessible from this place. 
        //group.addAction(new LockLink());
        //group.addAction(new UnlockLink());
    }

    @Override
    public final boolean isVisible(final PageState state) {
        return m_workflow.getWorkflow(state) != null;
    }

    private class RestartLink extends ActionLink {

        RestartLink() {
            super(new Label(gz("cms.ui.workflow.restart_stopped_workflow")));

            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, Workflow.INIT) || m_facade.workflowState(state,
                                                                                          Workflow.STOPPED);
        }

        private class Listener implements ActionListener {

            public final void actionPerformed(final ActionEvent e) {
                m_facade.restartWorkflow(e.getPageState());
            }

        }

    }

    private class LockLink extends ActionLink {

        LockLink() {
            super(new Label(gz("cms.ui.workflow.task.assigned.lock_all")));

            addActionListener(new Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, Workflow.STARTED) && m_facade.tasksExist(state)
                   && !m_facade.tasksLocked(state);
        }

        private class Listener implements ActionListener {

            public final void actionPerformed(final ActionEvent e) {
                m_facade.lockTasks(e.getPageState());
            }

        }

    }

    private class UnlockLink extends ActionLink {

        UnlockLink() {
            super(new Label(gz("cms.ui.workflow.task.assigned.unlock_all")));

            addActionListener(new UnlockLink.Listener());
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_facade.workflowState(state, Workflow.STARTED) && m_facade.tasksExist(state)
                   && m_facade.tasksLocked(state);
        }

        private class Listener implements ActionListener {

            public final void actionPerformed(final ActionEvent e) {
                m_facade.unlockTasks(e.getPageState());
            }

        }

    }

    private class WorkflowFacade {

        private final WorkflowRequestLocal m_flow;
        private final TaskListRequestLocal m_tasks;

        WorkflowFacade(final WorkflowRequestLocal flow) {
            m_flow = flow;
            m_tasks = new TaskListRequestLocal();
        }

        private class TaskListRequestLocal extends RequestLocal {

            @Override
            protected final Object initialValue(final PageState state) {
                final Workflow workflow = m_flow.getWorkflow(state);
                final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
                return engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID());
            }

            final ArrayList getTasks(final PageState state) {
                return (ArrayList) get(state);
            }

        }

        final void restartWorkflow(final PageState state) {
            final Workflow workflow = m_flow.getWorkflow(state);
            workflow.start(Web.getWebContext().getUser());
            workflow.save();

            // Lock tasks if not locked
            if (!tasksLocked(state)) {
                lockTasks(state);
            }
        }

        final void lockTasks(final PageState state) {
            final Iterator iter = m_tasks.getTasks(state).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();

                if (relevant(task) && !task.isLocked()) {
                    task.lock(Web.getWebContext().getUser());
                    task.save();
                }
            }
        }

        final void unlockTasks(final PageState state) {
            final Iterator iter = m_tasks.getTasks(state).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();

                if (relevant(task) && task.isLocked()) {
                    task.unlock(Web.getWebContext().getUser());
                    task.save();
                }
            }
        }

        final boolean tasksLocked(final PageState state) {
            final Iterator iter = m_tasks.getTasks(state).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();

                if (relevant(task) && !task.isLocked()) {
                    return false;
                }
            }

            return true;
        }

        final boolean workflowState(final PageState state, int processState) {
            return m_flow.getWorkflow(state).getProcessState() == processState;
        }

        final boolean tasksExist(final PageState state) {
            return !m_tasks.getTasks(state).isEmpty();
        }

        private boolean relevant(final CMSTask task) {
            return task.getTaskType().getID().equals(CMSTaskType.AUTHOR)
                       || task.getTaskType().getID().equals(CMSTaskType.EDIT);
        }

    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
