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


import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.util.GlobalizationUtil;

/**
 *
 * @version $Id: AdminPane.java 2089 2010-04-17 07:55:43Z pboy $
 */
public class AdminPane extends SimpleContainer
    implements ActionListener {

    private SingleSelectionModel m_processes;
    private ToggleLink m_addLink;

    private Label m_noSelection;
    private ProcessDisplay m_processDisplay;
    private AddProcess m_addProcess;

    public AdminPane (SingleSelectionModel m, ToggleLink l) {
        super();
        m_processes = m;
        m_addLink = l;

        m_noSelection = new Label(GlobalizationUtil.globalize("bebop.demo.workflow.h4emselect_process_to_view_detailsemh4"),  false);
        add(m_noSelection);

        m_processDisplay = new ProcessDisplay(m);
        add(m_processDisplay);

        m_addProcess = new AddProcess(m, l);
        add(m_addProcess);
    }

    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_noSelection, true);
        p.setVisibleDefault(m_processDisplay, false);
        p.setVisibleDefault(m_addProcess, false);
        p.addActionListener( this );
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        boolean proc = m_processes.isSelected(s);
        boolean add = m_addLink.isSelected(s);

        m_noSelection.setVisible(s, ! ( proc || add ));
        m_processDisplay.setVisible(s, proc);
        m_addProcess.setVisible(s, add);
    }

}
