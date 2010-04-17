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
import com.arsdigita.bebop.SingleSelectionModel;
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
import com.arsdigita.workflow.simple.Workflow;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * @author Justin Ross
 * @version $Id: TaskAddForm.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
class TaskAddForm extends BaseTaskForm {

    private static final Logger s_log = Logger.getLogger(TaskAddForm.class);

    protected final static String ERROR_MSG =
        "A workflow template with that name already exists in this content " +
        "section.";

    private final SingleSelectionModel m_model;

    public TaskAddForm(final WorkflowRequestLocal workflow,
                       final SingleSelectionModel model) {
        super("task", gz("cms.ui.workflow.task.add"), workflow);

        m_model = model;

        try {
            m_deps.addPrintListener(new DependencyPrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

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

                options.addOption(new Option(task.getID().toString(),
                                             task.getLabel()));
            }
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final Workflow workflow = m_workflow.getWorkflow(state);
            final CMSTask task = new CMSTask();

            task.setLabel((String) m_name.getValue(state));
            task.setDescription((String) m_description.getValue(state));
            task.setTaskType(CMSTaskType.retrieve((Integer) m_type.getValue(state)));
            task.setActive(true);

            task.save();

            // XXX Why are we doing this?
            workflow.save();

            workflow.addTask(task);

            processDependencies(task, (String[]) m_deps.getValue(state));

            task.save();

            m_model.setSelectedKey(state, task.getID().toString());
        }
    }
}
