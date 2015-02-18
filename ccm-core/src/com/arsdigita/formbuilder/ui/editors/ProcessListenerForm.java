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


import com.arsdigita.formbuilder.ui.PropertiesForm;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.TextField;

import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;



import java.math.BigDecimal;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.bebop.ColumnPanel;


public abstract class ProcessListenerForm extends PropertiesForm {
    private SingleSelectionModel m_form;
    private SingleSelectionModel m_action;

    private TextField m_name;
    private TextField m_description;

    public ProcessListenerForm(String name,
                               SingleSelectionModel form,
                               SingleSelectionModel action) {
        super(name);

        m_form = form;
        m_action = action;

        addInitListener(new ProcessListenerFormInitListener());
        addProcessListener(new ProcessListenerFormProcessListener());
    }

    protected abstract PersistentProcessListener getProcessListener();
    protected abstract PersistentProcessListener getProcessListener(BigDecimal id)
        throws DataObjectNotFoundException;

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_name = new TextField(new StringParameter("name"));
        m_name.setSize(50);
        m_name.addValidationListener(new NotNullValidationListener());
        m_name.addValidationListener(new StringInRangeValidationListener(1, 30));
        m_name.addValidationListener(new URLTokenValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.name")), ColumnPanel.RIGHT);
        section.add(m_name);

        m_description = new TextField(new StringParameter("description"));
        m_description.setSize(50);
        m_description.addValidationListener(new NotNullValidationListener());
        m_description.addValidationListener(new StringInRangeValidationListener(1, 70));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.description")), ColumnPanel.RIGHT);
        section.add(m_description);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener l)
        throws FormProcessException {

        PageState state = e.getPageState();

        if (l == null) {
            m_name.setValue(state, "");
            m_description.setValue(state, "");
        } else {
            m_name.setValue(state, l.getName());
            m_description.setValue(state, l.getDescription());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener l)
        throws FormProcessException {

        FormData data = e.getFormData();

        String name = (String)data.get("name");
        String description = (String)data.get("description");

        l.setName(name);
        l.setDescription(description);
    }

    protected void addToForm(FormSectionEvent e,
                             PersistentProcessListener l)
        throws FormProcessException {

        BigDecimal form_id = (BigDecimal)m_form.getSelectedKey(e.getPageState());

        PersistentFormSection form = null;
        try {
            form = new PersistentFormSection(form_id);
        } catch (DataObjectNotFoundException ex) {
            ex.printStackTrace();
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "formbuilder.ui.editors.cannot_find_form"));
        }

        form.addProcessListener(l);
        form.save();
    }


    private class ProcessListenerFormInitListener implements FormInitListener {
        public void init(FormSectionEvent e)
            throws FormProcessException {

            PageState state = e.getPageState();

            BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);

            if (action == null) {
                initWidgets(e, null);
            } else {
                PersistentProcessListener widget = null;
                try {
                    widget = getProcessListener(action);
                } catch (DataObjectNotFoundException ex) {
                    throw new FormProcessException(GlobalizationUtil.globalize(
                            "formbuilder.ui.editors.cannot_find_persistent_process_listener", new Object[]{action}));
                }
                initWidgets(e, widget);
            }
        }
    }

    private class ProcessListenerFormProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e)
            throws FormProcessException {

            PageState state = e.getPageState();

            if (isCancelled(state))
                return;

            BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);

            PersistentProcessListener widget = null;
            if (action == null) {
                widget = getProcessListener();
                m_action.setSelectedKey(state, widget.setID());
            } else {
                try {
                    widget = getProcessListener(action);
                } catch (DataObjectNotFoundException ex) {
                    throw new FormProcessException(GlobalizationUtil.globalize(
                            "formbuilder.ui.editors.cannot_find_persistent_process_listener", new Object[]{action}));
                }
            }

            processWidgets(e, widget);
            widget.save();

            if (action == null)
                addToForm(e, widget);
        }
    }
}
