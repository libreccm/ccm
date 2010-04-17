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
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.util.GlobalizationUtil;


/**
 *
 * @version $Id: AddProcess.java 287 2005-02-22 00:29:02Z sskracic $ 
 */
public class AddProcess extends Form
    implements FormProcessListener, FormInitListener,
               FormSubmissionListener {

    private SingleSelectionModel m_processes;
    private ToggleLink m_addLink;

    private Hidden m_id;
    private TextField m_title;
    private TextArea m_descr;

    private Submit m_save;
    private Submit m_cancel;

    /** Constructor: set up the form, consisting of a hidden id field,
     *  title, description, and buttons to save or cancel */
    public AddProcess(SingleSelectionModel m, ToggleLink addLink) {
        super("addProcess");

        m_processes = m;
        m_addLink = addLink;

        addInitListener(this);
        addProcessListener (this);
        addSubmissionListener(this);

        m_id = new Hidden("id");
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.process_name")));
        m_title = new TextField("title");
        m_title.addValidationListener(new NotNullValidationListener());
        add(m_title);

        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.short_description")));
        m_descr = new TextArea("desc") ;
        m_descr.setRows(3);
        m_descr.setCols(40);

        add(m_descr);

        m_save = new Submit("save", "Save");
        m_cancel = new Submit("cancel", "Cancel");

        add(m_save, ColumnPanel.LEFT);
        add(m_cancel, ColumnPanel.RIGHT);
    }

    /** The main action of the form.  Mandated by the {@link
     *  FormProcessListener} interface.  See {@link
     *  FormProcessListener#process method documentation}. */
    public void process(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();

        if ( m_save.isSelected(s) ) {
            String key = (String) m_id.getValue(s);
            String title = (String) m_title.getValue(s);

            Process p = new Process(key, title);
            p.setDescription((String) m_descr.getValue(s));

            SampleProcesses.getInstance().add(p);

            m_processes.setSelectedKey(s, key);
        }
    }


    /** Check if we want to examine the submission.
     *  Mandated by the {@link FormSubmissionListener} interface.
     *  See {@link FormSubmissionListener#submitted method documentation}. */
    public void submitted(FormSectionEvent e)
        throws FormProcessException {
        PageState s = e.getPageState();

        if ( m_cancel.isSelected(s) ) {
            m_addLink.setSelected(s, false);
            throw new FormProcessException("Cancel hit");
        }
    }

    /** Initialize form data.  Mandated by the {@link
     *  FormInitListener} interface.  See {@link FormInitListener#init
     *  method documentation}. */
    public void init(FormSectionEvent e) {
        m_id.setValue(e.getPageState(), Process.getNextKey());
    }

}
