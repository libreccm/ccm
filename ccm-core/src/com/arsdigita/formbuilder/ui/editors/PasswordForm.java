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


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 



import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.TextField;

import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentPassword;


import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.ColumnPanel;


public class PasswordForm extends WidgetLabelForm {
    private TextField m_width;
    private TextField m_length;
    private TextField m_value;

    public PasswordForm(String name,
                        SingleSelectionModel form,
                        SingleSelectionModel control) {
        super(name, form, control);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_length = new TextField(new IntegerParameter("length"));
        m_length.addValidationListener(new NotNullValidationListener());
        m_length.setSize(5);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.max_length")), ColumnPanel.RIGHT);
        section.add(m_length);

        m_width = new TextField(new IntegerParameter("width"));
        m_width.addValidationListener(new NotNullValidationListener());
        m_width.addValidationListener(new NumberInRangeValidationListener(1, 50));
        m_width.setSize(5);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.width")), ColumnPanel.RIGHT);
        section.add(m_width);

        m_value = new TextField(new StringParameter("value"));
        m_value.setSize(50);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.value")), ColumnPanel.RIGHT);
        section.add(m_value);
    }

    protected PersistentWidget getWidget() {
        return new PersistentPassword();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new PersistentPassword(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PersistentPassword widget = (PersistentPassword)w;

        PageState state = e.getPageState();

        if (widget == null) {
            m_width.setValue(state, "");
            m_length.setValue(state, "");
            m_value.setValue(state, "");
        } else {
            m_width.setValue(state, new Integer(widget.getSize()));
            m_length.setValue(state, new Integer(widget.getMaxLength()));
            m_value.setValue(state, widget.getDefaultValue());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        PersistentPassword widget = (PersistentPassword)w;

        FormData data = e.getFormData();

        Integer length = (Integer)data.get("length");
        Integer width = (Integer)data.get("width");
        String value = (String)data.get("value");

        widget.setMaxLength(length.intValue());
        widget.setSize(width.intValue());
        widget.setDefaultValue(value);
    }
}
