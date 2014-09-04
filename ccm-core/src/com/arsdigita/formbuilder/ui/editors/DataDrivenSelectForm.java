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



import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.DataDrivenSelect;


import java.math.BigDecimal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import java.util.TooManyListenersException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Select;
import java.util.Iterator;

import com.arsdigita.formbuilder.PersistentDataQuery;
import com.arsdigita.formbuilder.BebopObjectType;

public class DataDrivenSelectForm extends WidgetLabelForm {
    private SingleSelect m_query;
    private RadioGroup m_multiple;

    public DataDrivenSelectForm(String name,
                                SingleSelectionModel form,
                                SingleSelectionModel control) {
        super(name, form, control);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_query = new SingleSelect(new BigDecimalParameter("query"));
        m_query.addValidationListener(new NotNullValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.query")), ColumnPanel.RIGHT);
        section.add(m_query);

        try {
            m_query.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        ((Select) e.getTarget()).clearOptions();
                        loadComponents((Select)e.getTarget(), e.getPageState());
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("this should not happen", ex);
        }

        m_multiple = new RadioGroup(new BooleanParameter("multiple"));
        m_multiple.addValidationListener(new NotNullValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.multiselect")), ColumnPanel.RIGHT);
        section.add(m_multiple);

        m_multiple.addOption(new Option("false", "No"));
        m_multiple.addOption(new Option("true", "Yes"));
    }

    protected PersistentWidget getWidget() {
        return new DataDrivenSelect();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new DataDrivenSelect(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        DataDrivenSelect query = (DataDrivenSelect)w;

        // FIXME: should be removed? -- 2002-11-26
        if (query == null) {
            //m_query
            //m_multiple.setOptionSelected("false");
        } else {
            //m_query.setOptionSelected(query.getQueryID().toString());
            //m_multiple.setOptionSelected(query.isMultiple() ? "true" : "false");
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        DataDrivenSelect widget = (DataDrivenSelect)w;

        FormData data = e.getFormData();

        BigDecimal query = (BigDecimal)data.get("query");
        Boolean multiple = (Boolean)data.get("multiple");

        widget.setMultiple(multiple.booleanValue());
        widget.setQueryID(query);
    }

    protected void loadComponents(Select select, PageState state) {
        try {
            BebopObjectType type = BebopObjectType.findByClass(getApplication(), PersistentDataQuery.class);
            Iterator objects = PersistentDataQuery.getQueries(type).iterator();
            while (objects.hasNext()) {
                PersistentDataQuery control = (PersistentDataQuery)objects.next();

                select.addOption(new Option(control.getID().toString(),
                                            control.getDescription()));
            }
        } catch (DataObjectNotFoundException ex) {
            ex.printStackTrace();
            throw new UncheckedWrapperException(ex);
        }
    }
}
