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
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public final class AssignedTaskSection extends Section {
    public static final String versionId =
        "$Id: AssignedTaskSection.java 1280 2006-07-27 09:12:09Z cgyg9330 $" +
        "$Author: cgyg9330 $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (AssignedTaskSection.class);

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
        group.addAction(new LockLink());
        group.addAction(new UnlockLink());
    }

    public final boolean isVisible(final PageState state) {
        return m_workflow.getWorkflow(state) != null;
    }

    private class LockLink extends ActionLink {
        LockLink() {
            super(new Label(gz("cms.ui.workflow.task.assigned.lock_all")));

            addActionListener(new Listener());
        }

        public final boolean isVisible(final PageState state) {
            return m_facade.tasksExist(state) && !m_facade.tasksLocked(state);
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

            addActionListener(new Listener());
        }

        public final boolean isVisible(final PageState state) {
            return m_facade.tasksExist(state) && m_facade.tasksLocked(state);
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
            protected final Object initialValue(final PageState state) {
                final Workflow workflow = m_flow.getWorkflow(state);
                final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
                return engine.getEnabledTasks
                    (Web.getContext().getUser(), workflow.getID());
            }

            final ArrayList getTasks(final PageState state) {
                return (ArrayList) get(state);
            }
        }

        final void lockTasks(final PageState state) {
            final Iterator iter = m_tasks.getTasks(state).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();

                if (relevant(task) && !task.isLocked()) {
                    task.lock(Web.getContext().getUser());
                    task.save();
                }
            }
        }

        final void unlockTasks(final PageState state) {
            final Iterator iter = m_tasks.getTasks(state).iterator();

            while (iter.hasNext()) {
                final CMSTask task = (CMSTask) iter.next();

                if (relevant(task) && task.isLocked()) {
                    task.unlock(Web.getContext().getUser());
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
