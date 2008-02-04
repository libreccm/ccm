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

import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextField;

import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.bebop.parameters.NotEmptyValidationListener;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentDate;


import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.bebop.ColumnPanel;


public class DateForm extends WidgetLabelForm {
    private Date m_value;
    private TextField m_startYear;
    private TextField m_endYear;

    public DateForm(String name,
                    SingleSelectionModel form,
                    SingleSelectionModel control) {
        super(name, form, control);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_value = new Date(new DateParameter("value"));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.default_value")), ColumnPanel.RIGHT);
        section.add(m_value);

        m_startYear = new TextField(new IntegerParameter("startYear"));
        m_startYear.setDefaultValue(new Integer(1995));
        m_startYear.addValidationListener(new NotEmptyValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.start_year")));
        section.add(m_startYear);

        m_endYear = new TextField(new IntegerParameter("endYear"));
        m_endYear.setDefaultValue(new Integer(2004));
        m_endYear.addValidationListener(new NotEmptyValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.end_year")));
        section.add(m_endYear);
    }

    protected PersistentWidget getWidget() {
        return new PersistentDate();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new PersistentDate(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PersistentDate widget = (PersistentDate)w;

        PageState state = e.getPageState();

        if (widget == null) {
            m_value.setValue(state, new java.util.Date());
        } else {
            m_value.setValue(state, widget.getDefaultValue());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        PersistentDate date = (PersistentDate)w;

        FormData data = e.getFormData();

        java.util.Date value = (java.util.Date)data.get("value");

        date.setDefaultValue(value);

        Integer startYear = (Integer)data.get("startYear");
        Integer endYear = (Integer)data.get("endYear");

        date.setYearRange(startYear.intValue(), endYear.intValue());
    }
}
