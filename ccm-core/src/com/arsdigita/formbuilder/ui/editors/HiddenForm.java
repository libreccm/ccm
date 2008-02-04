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

import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentHidden;


import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.ColumnPanel;


public class HiddenForm extends WidgetForm {
    private TextField m_value;

    public HiddenForm(String name,
                      SingleSelectionModel form,
                      SingleSelectionModel control) {
        super(name, form, control);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_value = new TextField(new StringParameter("value"));
        m_value.setSize(50);
        m_value.addValidationListener(new NotNullValidationListener());
        m_value.addValidationListener(new StringInRangeValidationListener(1, 200));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.value")), ColumnPanel.RIGHT);
        section.add(m_value);
    }

    protected PersistentWidget getWidget() {
        return new PersistentHidden();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new PersistentHidden(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PersistentHidden widget = (PersistentHidden)w;

        PageState state = e.getPageState();

        if (widget == null) {
            m_value.setValue(state, "");
        } else {
            m_value.setValue(state, widget.getDefaultValue());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        PersistentHidden widget = (PersistentHidden)w;

        FormData data = e.getFormData();

        String value = (String)data.get("value");

        widget.setDefaultValue(value);
    }

    /**
     *  This determines whether or not the "required value" radio group
     *  is part of the form.  This returns true and should be overridden
     *  by fields where it does not make sense to ask.  For instance,
     *  when the widget is a hidden field then asking if it is required
     *  or not does not make any logical sense so those widgets should
     *  return false.
     *
     *  This will always return the same value for a given widget no matter
     *  what state the widget is in.
     */
    protected boolean includeRequiredRadioGroup() {
        return false;
    }
}
