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
import com.arsdigita.bebop.form.Widget;

import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.WidgetLabel;

import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.globalization.GlobalizedMessage;

import java.math.BigDecimal;


/**
 * 
 * 
 */
public abstract class WidgetLabelForm extends WidgetForm {
    private Widget m_label;


    public WidgetLabelForm(String name,
                           SingleSelectionModel form,
                           SingleSelectionModel control) {
        super(name, form, control);
    }

    @Override
    protected void addWidgets(FormSection section) {

        if (wantLabelMultiline()) {
            TextArea text = new TextArea(new StringParameter("label"));
            text.setCols(30);
            text.setRows(10);
            text.addValidationListener(new NotNullValidationListener());
            text.addValidationListener(new StringInRangeValidationListener(1, 4000));
            m_label = text;
        } else {
            TextField text = new TextField(new StringParameter("label"));
            text.setSize(30);
            text.addValidationListener(new NotNullValidationListener());
            text.addValidationListener(new StringInRangeValidationListener(1, 4000));
            m_label = text;
        }

        m_label.setOnFocus("if (this.form." + NAME + ".value == '') { " +
                           " defaulting = true; this.form." + NAME +
                           ".value = urlize(this.value); }");
        m_label.setOnKeyUp(
            "if (defaulting) { this.form." + NAME +
            ".value = urlize(this.value) }"
            );

        section.add(new Label(getLabelText()), ColumnPanel.RIGHT);
        section.add(m_label);

        super.addWidgets(section);

        automaticallySetName(m_label.getParameterModel());
    }

    /**
     * @return 
     * @deprecated used getGlobalizedLabelText()
     */
    protected String getLabelText() {
        return (String)getGlobalizedLabelText().localize();
    }

    protected GlobalizedMessage getGlobalizedLabelText() 
    {
            // Revision by CS Gupta
          
            if(!isEmailFormField())
                 return GlobalizationUtil
                        .globalize("formbuilder.ui.editors.UserEmailLabel");
            else
                 return GlobalizationUtil
                        .globalize("formbuilder.ui.editors.label");
                        
    }

    protected boolean wantLabelMultiline() {
        return false;
    }

    @Override
    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget widget)
        throws FormProcessException {

        super.initWidgets(e, widget);

        PageState state = e.getPageState();

        if (widget != null) {
            WidgetLabel l = WidgetLabel.findByWidget(widget);
            if( null == l )
                throw new FormProcessException("cannot find WidgetLabel for " 
                                               + widget.getOID());

            m_label.setValue(state, l.getLabel());
        } else {
            m_label.setValue(state, "");
        }
    }

    @Override
    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget widget)
        throws FormProcessException {

        super.processWidgets(e, widget);

        FormData data = e.getFormData();

        String label = (String)data.get("label");

        if (widget.isNew())
            return;

        PersistentLabel l = null;
        try {
            l = WidgetLabel.findByWidget(widget);
        } catch (DataObjectNotFoundException ex) {
            throw new FormProcessException("cannot find WidgetLabel for " 
                                           + widget.getOID());
        }

        l.setLabel(label);
        l.save();
    }

    /**
     * 
     * @param e
     * @param widget
     * @throws FormProcessException 
     */
    @Override
    protected void addToForm(FormSectionEvent e,
                             PersistentWidget widget)
        throws FormProcessException {

        FormData data = e.getFormData();

        String label = (String)data.get("label");

        BigDecimal form_id = (BigDecimal)getSelection()
                                         .getSelectedKey(e.getPageState());

        PersistentFormSection form = null;
        try {
            form = new PersistentFormSection(form_id);
        } catch (DataObjectNotFoundException ex) {
            throw new FormProcessException("cannot find form",ex);
        }

        addWidgetLabel(widget, label, form);

        // We could call super.addToForm at this point,
        // however since we already have a Form object
        // constructed its more efficient for us to do
        // it ourselves
        form.addComponent(widget);

        form.save();
    }

    /**
     *  this provides subclasses with the ability to add the widget label
     *  in any manner they see fit.  Specifically, it allows them to
     *  use a sublcass of WidgetLabel or to make this is no-op so that the
     *  this step is ignored if needed.
     *
     *  This will return null if not label is actually added.
     * 
     * @param widget
     * @param label
     * @param form
     * @return 
     */
    protected WidgetLabel addWidgetLabel(PersistentWidget widget,
                                         String label,
                                         PersistentFormSection form) {
        WidgetLabel l = WidgetLabel.create(widget,
                                          label);
        l.save();
        form.addComponent(l);
        return l;
    }
    /**
     * Added by CS Gupta to make Name Field invisible In EmailFormField.
     * Should Name Html Control be on the form? Can be overridden by sub classes.
     * @return 
     */
    protected boolean isEmailFormField() 
    {
        return true;
    }

}
