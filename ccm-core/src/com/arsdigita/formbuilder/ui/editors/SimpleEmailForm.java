/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui.editors;


import com.arsdigita.formbuilder.util.FormBuilderUtil; 
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 


import com.arsdigita.formbuilder.actions.SimpleEmailListener;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.TextField;

import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;



import java.math.BigDecimal;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.ColumnPanel;


public class SimpleEmailForm extends ProcessListenerForm {
    private TextField m_to;
    private TextField m_subject;

    public SimpleEmailForm(String name,
                           SingleSelectionModel form,
                           SingleSelectionModel action) {
        super(name, form, action);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        if (FormBuilderUtil.getConfig().getInterpolateEmailActionsToAddress()) {
            m_to = new TextField(new StringParameter("to"));
        } else {
        	m_to = new TextField(new EmailParameter("to"));
        }
        m_to.setSize(50);
        m_to.addValidationListener(new NotNullValidationListener());
        //m_to.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.to")), ColumnPanel.RIGHT);
        section.add(m_to);

        m_subject = new TextField(new StringParameter("subject"));
        m_subject.setSize(50);
        m_subject.addValidationListener(new NotNullValidationListener());
        m_subject.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.subject")), ColumnPanel.RIGHT);
        section.add(m_subject);
    }

    protected PersistentProcessListener getProcessListener() {
        return new SimpleEmailListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id)
        throws DataObjectNotFoundException {

        return new SimpleEmailListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);

        SimpleEmailListener l = (SimpleEmailListener)listener;

        PageState state = e.getPageState();

        if (l == null) {
            m_to.setValue(state, "");
            m_subject.setValue(state, "");
        } else {
            m_to.setValue(state, l.getTo());
            m_subject.setValue(state, l.getSubject());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);

        SimpleEmailListener l = (SimpleEmailListener)listener;

        FormData data = e.getFormData();

        String to = data.get("to").toString();
        String subject = (String)data.get("subject");

        l.setTo(to);
        l.setSubject(subject);
    }
}
