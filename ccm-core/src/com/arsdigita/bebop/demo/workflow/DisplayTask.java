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
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Iterator;
import java.util.TooManyListenersException;

public class DisplayTask extends Form
    implements FormProcessListener, FormSubmissionListener {

    public static final String versionId = "$Id: DisplayTask.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private SingleSelectionModel m_tasks;

    private SingleSelect m_selectAssignee;
    private SingleSelect m_addDependency;

    private Submit m_save;
    private Submit m_cancel;

    public DisplayTask (SingleSelectionModel tasks) {
        super("taskEdit");

        m_tasks = tasks;

        Label l = new Label(GlobalizationUtil.globalize("bebop.demo.workflow.task_details"));
        l.setFontWeight(Label.BOLD);

        add(l, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);
        makeAssigneeSelect("group");

        // add table of deps

        makeAddDependency();

        m_save = new Submit("save", "Save");
        m_cancel = new Submit("cancel", "Cancel");

        add(m_save, ColumnPanel.LEFT);
        add(m_cancel, ColumnPanel.RIGHT);

        addProcessListener( this );
        addSubmissionListener( this );
    }

    private void makeAssigneeSelect(String name) {
        String[] groups = { "Authors", "Editors", "Approvers",
                            "Publishers", "Sharks" };

        m_selectAssignee = new SingleSelect(name);
        for (int i=0; i<groups.length; i++) {
            m_selectAssignee.addOption(new Option(groups[i]));
        }
        addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    PageState s = e.getPageState();
                    FormData f = e.getFormData();
                    f.put(m_selectAssignee.getName(),
                          getSelectedTask(s).getAssignee());
                }
            });

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.default_assignee_group")));
        add(m_selectAssignee);
    }

    private void makeAddDependency() {
        m_addDependency = new SingleSelect("dep");
        try {
            m_addDependency.addPrintListener(new DependencyPrintListener());
        } catch (TooManyListenersException e) {
            // can't happen
            throw new UncheckedWrapperException("Too many listeners: " + e.getMessage(), e);
        }

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.dependencies")), ColumnPanel.FULL_WIDTH);
        add(makeDependencyTable(), ColumnPanel.FULL_WIDTH);
        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.add_dependency")));
        add(m_addDependency);
    }

    private Table makeDependencyTable() {
        final String[] headers = { "Task", "Action" };

        TableModelBuilder b = new AbstractTableModelBuilder() {

                public TableModel makeModel(final Table t, final PageState s) {

                    return new TableModel() {
                            private Iterator iter = getSelectedTask(s).dependencies();
                            private Task current = null;

                            public int getColumnCount() {
                                return 2;
                            }

                            public boolean nextRow() {
                                if ( iter.hasNext() ) {
                                    current = (Task) iter.next();
                                    return true;
                                } else {
                                    return false;
                                }
                            }

                            public Object getElementAt(int columnIndex) {
                                if ( columnIndex == 0 ) {
                                    return current.getName();
                                } else if ( columnIndex == 1 ) {
                                    return "delete";
                                }
                                return null;
                            }

                            public Object getKeyAt(int columnIndex) {
                                return current.getKey();
                            }
                        };
                }
            };

        Table result = new Table(b, headers);
        result.getColumn(1).setCellRenderer(new
                                            DefaultTableCellRenderer(true));
        result.addTableActionListener(new DependencyTableActionListener());
        Label l = new Label(GlobalizationUtil.globalize("bebop.demo.workflow.none"));
        l.setFontWeight(Label.ITALIC);
        l.setStyleAttr("padding-left: 3em");
        result.setEmptyView(l);
        result.setWidth("100%");
        return result;
    }

    public boolean isCancelled(PageState s) {
        return m_cancel.isSelected(s);
    }

    public void submitted(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();

        if ( isCancelled(s) ) {
            m_tasks.clearSelection(s);
            throw new FormProcessException("Cancel hit");
        }
    }

    public void process(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();
        Task t = getSelectedTask(s);

        t.setAssignee((String) m_selectAssignee.getValue(s));
        String dep = (String) m_addDependency.getValue(s);
        Task d = t.getProcess().getTask(dep);
        t.addDependency(d);
        m_tasks.clearSelection(s);
    }

    private class DependencyPrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PageState s = e.getPageState();
            SingleSelect w = (SingleSelect) e.getTarget();
            Process p = getSelectedTask(s).getProcess();
            for (int i=0; i < p.taskCount(); i++) {
                Task t = p.getTask(i);
                w.addOption(new Option(t.getKey(), t.getName()));
            }
        }
    }

    private class DependencyTableActionListener
        extends TableActionAdapter {
        public void cellSelected(TableActionEvent e) {
            PageState s = e.getPageState();
            Task t = getSelectedTask(s);
            Task d = t.getDependency((String) e.getRowKey());
            if ( d != null ) {
                t.removeDependency(d);
            }
        }
    }

    private Task getSelectedTask(PageState s) {
        return
            SampleProcesses.getTask(m_tasks.getSelectedKey(s).toString());
    }

}
