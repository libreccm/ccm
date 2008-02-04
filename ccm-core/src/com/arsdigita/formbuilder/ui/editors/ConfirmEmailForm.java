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

import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.actions.ConfirmEmailListener;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;

import java.math.BigDecimal;

public class ConfirmEmailForm extends ProcessListenerForm {
    private TextField m_from;
    private TextField m_subject;
    private TextArea m_body;

    public ConfirmEmailForm(String name,
                            SingleSelectionModel form,
                            SingleSelectionModel action) {
        super(name, form, action);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_from = new TextField(new EmailParameter("from"));
        m_from.setSize(50);
        m_from.addValidationListener(new NotNullValidationListener());
        //m_from.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.from")), ColumnPanel.RIGHT);
        section.add(m_from);

        m_subject = new TextField(new StringParameter("subject"));
        m_subject.setSize(50);
        m_subject.addValidationListener(new NotNullValidationListener());
        m_subject.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.subject")), ColumnPanel.RIGHT);
        section.add(m_subject);

        m_body = new TextArea(new StringParameter("body"));
        m_body.setRows(20);
        m_body.setCols(50);
        m_body.addValidationListener(new NotNullValidationListener());
        m_body.addValidationListener(new StringInRangeValidationListener(1, 4000));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.body")), ColumnPanel.RIGHT);
        section.add(m_body);
    }

    protected PersistentProcessListener getProcessListener() {
        return new ConfirmEmailListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id) {
        return new ConfirmEmailListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);

        ConfirmEmailListener l = (ConfirmEmailListener)listener;

        PageState state = e.getPageState();

        if (l == null) {
            m_from.setValue(state, "");
            m_subject.setValue(state, "");
            m_body.setValue(state, "");
        } else {
            m_from.setValue(state, l.getFrom());
            m_subject.setValue(state, l.getSubject());
            m_body.setValue(state, l.getBody());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);

        ConfirmEmailListener l = (ConfirmEmailListener)listener;

        FormData data = e.getFormData();

        String from = data.get("from").toString();
        String subject = (String)data.get("subject");
        String body = (String)data.get("body");

        l.setFrom(from);
        l.setSubject(subject);
        l.setBody(body);
    }
}
