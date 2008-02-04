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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.Assert;
import java.util.Iterator;

/**
 * Describe class <code>WorkflowDisplay</code> here.
 *
 * @author Uday Mathur
 * @author David Lutterkort
 * @version $Id: ProcessDisplay.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ProcessDisplay extends BoxPanel
    implements ActionListener, ChangeListener, TableActionListener {

    public static final String versionId = "$Id: ProcessDisplay.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final SingleSelectionModel m_processes;

    // The "Details for ..." label
    private Label m_title;
    // The list of tasks in the process
    private Table m_taskTable;
    // form to add one more task to the process
    private AddTask m_addTaskForm ;
    // form to edit a task
    private DisplayTask m_displayTask;

    private ToggleLink m_addTask;

    public ProcessDisplay (SingleSelectionModel m) {
        super(VERTICAL, false);

        m_processes = m;
        m_processes.addChangeListener(this);

        m_title = makeTitle();
        add(m_title);

        m_taskTable = makeTaskTable();
        add(m_taskTable);
        m_taskTable.addTableActionListener(this);
        m_taskTable.getRowSelectionModel().addChangeListener(this);

        m_addTask = new ToggleLink("add task");
        add(m_addTask);
        m_addTask.addActionListener(this);

        m_addTaskForm = new AddTask(m_processes);

        m_addTaskForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent e) {
                    PageState s = e.getPageState();
                    if ( m_addTaskForm.isCancelled(s) ) {
                        m_addTask.setSelected(s, false);
                        m_addTaskForm.setVisible(s, false);
                    }
                }
            });


        add(m_addTaskForm);

        m_displayTask = new DisplayTask(m_taskTable.getRowSelectionModel());
        add(m_displayTask);
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        addTaskMode(s);
    }

    public void cellSelected(TableActionEvent e) {
        PageState s = e.getPageState();

        if ( e.getSource() == m_taskTable ) {
            viewTaskMode(s);
        }
    }

    public void headSelected(TableActionEvent e) {
        return;
    }

    public void stateChanged(ChangeEvent e) {
        PageState s = e.getPageState();

        if ( e.getSource() == m_processes ) {
            m_addTask.setSelected(s, false);
            m_addTaskForm.setVisible(s, false);
            m_taskTable.getRowSelectionModel().clearSelection(s);
            m_displayTask.setVisible(s, false);
        } else if ( e.getSource() == m_taskTable.getRowSelectionModel() ) {
            viewTaskMode(s);
        }
    }

    public void addTaskMode(PageState s) {
        m_taskTable.getRowSelectionModel().clearSelection(s);
        m_displayTask.setVisible(s, false);
        if ( m_addTask.isSelected(s) ) {
            m_addTaskForm.setVisible(s, true);
        }
    }

    public void viewTaskMode(PageState s) {
        if ( m_taskTable.getRowSelectionModel().isSelected(s) ) {
            m_addTask.setSelected(s, false);
            m_addTaskForm.setVisible(s, false);
            m_displayTask.setVisible(s, true);
        } else {
            m_displayTask.setVisible(s, false);
        }
    }

    private Table makeTaskTable() {
        final String[] headers = { "Task", "Depends", "Assignee" };

        TableModelBuilder b = new TableModelBuilder () {
                private boolean m_locked;

                public TableModel makeModel(final Table t, final PageState s) {
                    Assert.assertTrue(m_processes.isSelected(s));

                    return new TableModel() {
                            private Process p =
                                SampleProcesses.getProcess(m_processes.getSelectedKey(s));

                            private int i = -1;

                            public int getColumnCount() {
                                return 3;
                            }

                            public boolean nextRow() {
                                return (++i < p.taskCount());
                            }

                            public Object getElementAt(int columnIndex) {
                                if ( columnIndex == 0 ) {
                                    return p.getTask(i).getName();
                                } else if ( columnIndex == 1 ) {
                                    Task temp_t = (Task) p.getTask(i);
                                    boolean first = true;
                                    StringBuffer temp_b = new StringBuffer(40);
                                    for (Iterator i = temp_t.dependencies(); i.hasNext();
                                         first=false) {
                                        Task dep = (Task) i.next();
                                        if (! first) {
                                            temp_b.append(", ");
                                        }
                                        temp_b.append(dep.getName());
                                    }
                                    return temp_b.toString();
                                } else if ( columnIndex == 2 ) {
                                    return p.getTask(i).getAssignee();
                                }
                                throw new IllegalArgumentException("columnIndex exceeds number of columns available");
                            }

                            public Object getKeyAt(int columnIndex) {
                                return p.getTask(i).getKey();
                            }
                        };
                }

                public void lock() {
                    m_locked = true;
                }

                public final boolean isLocked() {
                    return m_locked;
                }
            };

        Table result = new Table(b, headers);
        result.getColumn(0).setCellRenderer(new DefaultTableCellRenderer(true));
        return result;

    }

    private Label makeTitle() {
        PrintListener l = new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t = (Label) e.getTarget();
                    PageState s = e.getPageState();
                    Assert.assertTrue(m_processes.isSelected(s));
                    Process p =
                        SampleProcesses.getProcess(m_processes.getSelectedKey(s));
                    t.setLabel("<h4>Details for "
                               + p.getName() + " </h4>");
                    t.setOutputEscaping(false);
                }
            };
        return new Label(l);
    }

}
