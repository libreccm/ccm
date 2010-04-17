/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.demo.workflow;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Select;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;

/**
 *
 * @version $Id: AddTask.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class AddTask extends Form
    implements FormProcessListener, FormSubmissionListener {

    private final SingleSelectionModel m_processes;

    private OptionGroup m_deps;
    private Hidden m_id;
    private TextField m_name;

    private Submit m_add;
    private Submit m_cancel;

    public AddTask(SingleSelectionModel procs) {
        super("addTask");

        m_processes = procs;

        m_id = new Hidden("id");
        add(m_id);

        addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    m_id.setValue(e.getPageState(), Task.getNextKey());
                }
            });

        PrintListener p = new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t = (Label) e.getTarget();
                    PageState s = e.getPageState();
                    // FIXME: name of template comes from DB
                    Assert.isTrue(m_processes.isSelected(s));
                    Process inner_p =
                        SampleProcesses.getProcess(m_processes.getSelectedKey(s));
                    t.setLabel("Add a new Task to " + inner_p.getName());
                }
            };

        add(new Label(p), ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.name")));
        m_name = new TextField("name");
        m_name.addValidationListener(new NotNullValidationListener());
        add(m_name);

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.task")));
        Select taskType = new SingleSelect("task");
        try {
            taskType.addPrintListener(new TaskTypePrintListener());
        } catch (TooManyListenersException e) {
            // can't happen
            throw new UncheckedWrapperException("Too many listeners: " + e.getMessage(), e);
        }
        add(taskType);

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.description")));
        TextArea ta = new TextArea("desc");
        ta.setRows(3);
        ta.setCols(30);
        add(ta);
        addProcessListener ( this );
        addSubmissionListener( this );

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.depends_on")), ColumnPanel.TOP);

        m_deps = new CheckboxGroup("dep");
        try {
            m_deps.addPrintListener(new DependencyPrintListener());
        } catch (TooManyListenersException e) {
            // can't happen
            throw new UncheckedWrapperException("Too many listeners: " + e.getMessage(), e);
        }
        add(m_deps);

        m_add = new Submit("add", "Add Task");
        add(m_add, ColumnPanel.RIGHT);

        m_cancel = new Submit("cancel", "Cancel");
        add(m_cancel, ColumnPanel.RIGHT);
    }

    public String getNewKey(PageState s) {
        return (String) m_id.getValue(s);
    }

    public void process(FormSectionEvent event)
        throws FormProcessException {
        PageState s = event.getPageState();

        String key = (String) m_id.getValue(s);
        Process p =
            SampleProcesses.getProcess(m_processes.getSelectedKey(s));
        Task t = new Task(key, (String) m_name.getValue(s));

        String[] deps = (String[]) m_deps.getValue(s);
        if ( deps != null ) {
            for (int i=0; i<deps.length; i++) {
                Task d = p.getTask(deps[i]);
                if ( d != null ) {
                    t.addDependency(d);
                }
            }
        }
        p.addTask(t, false);

        m_id.setValue(s, Task.getNextKey());
    }

    public boolean isCancelled(PageState s) {
        return m_cancel.isSelected(s);
    }

    public void submitted(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();

        if ( isCancelled(s) ) {
            throw new FormProcessException("Cancel hit");
        }
    }

    private class TaskTypePrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PageState s = e.getPageState();
            Select w = (Select) e.getTarget();
            ListModel m = SampleProcesses.getAllTasks(s);
            while (m.next()) {
                Task t = (Task) m.getElement();
                w.addOption(new Option(m.getKey(), t.getName()));
            }
        }
    }

    private class DependencyPrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PageState s = e.getPageState();
            OptionGroup w = (OptionGroup) e.getTarget();
            Process p =
                SampleProcesses.getProcess(m_processes.getSelectedKey(s));
            for (int i=0; i < p.taskCount(); i++) {
                Task t = p.getTask(i);
                w.addOption(new Option(t.getKey(), t.getName()));
            }
        }
    }

}
