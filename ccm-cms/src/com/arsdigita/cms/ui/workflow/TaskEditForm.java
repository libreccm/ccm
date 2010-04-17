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
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 * @author Justin Ross
 * @version $Id: TaskEditForm.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
class TaskEditForm extends BaseTaskForm {

    private static final Logger s_log = Logger.getLogger(TaskEditForm.class);

    private TaskRequestLocal m_task;

    public TaskEditForm(final WorkflowRequestLocal workflow,
                        final TaskRequestLocal task) {
        super("task", gz("cms.ui.workflow.task.edit"), workflow);

        m_task = task;

        try {
            m_deps.addPrintListener(new DependencyPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class DependencyPrinter implements PrintListener {
        public final void prepare(final PrintEvent e) {
            final PageState state = e.getPageState();
            final TaskCollection tasks = m_workflow.getWorkflow
                (state).getTaskCollection();

            final OptionGroup options = (OptionGroup) e.getTarget();

            while (tasks.next()) {
                final Task task = tasks.getTask();

                if (!m_task.getTask(state).getID().equals(task.getID())) {
                    options.addOption(new Option(task.getID().toString(),
                                                 task.getLabel()));
                }
            }
        }
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);

            m_name.setValue(state, task.getLabel());
            m_description.setValue(state, task.getDescription());
            m_type.setValue(state, task.getTaskType().getID().toString());

            final TaskCollection deps = task.getRequiredTasks();
            final ArrayList list = new ArrayList();

            while (deps.next()) {
                list.add(deps.getTask().getID().toString());
            }

            m_deps.setValue(state, list.toArray());
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);

            task.setLabel((String) m_name.getValue(state));
            task.setDescription((String) m_description.getValue(state));
            task.setTaskType(CMSTaskType.retrieve((Integer) m_type.getValue(state)));

            // XXX Is this necessary?
            task.save();

            processDependencies(task, (String[]) m_deps.getValue(state));

            task.save();
        }
    }
}
