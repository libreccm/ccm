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

import com.arsdigita.formbuilder.PersistentTextField;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.parameters.PersistentParameterListener;
import com.arsdigita.formbuilder.parameters.TextValidationListener;
import com.arsdigita.formbuilder.util.GlobalizationUtil ;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringParameter;

import java.math.BigDecimal;
import java.util.Iterator;

public class TextFieldForm extends WidgetLabelForm {
    private TextField m_width;
    private TextField m_length;
    private TextField m_value;
    private SingleSelect m_datatype;

    private SingleSelectionModel m_control;

    public TextFieldForm(String name,
                         SingleSelectionModel form,
                         SingleSelectionModel control) {
        super(name, form, control);
        m_control = control;
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_length = new TextField(new IntegerParameter("length"));
        m_length.setSize(5);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.textfieldmsg")), ColumnPanel.RIGHT);
        section.add(new Label(""));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.max_length")), ColumnPanel.RIGHT);
        section.add(m_length);

        m_width = new TextField(new IntegerParameter("width"));
        m_width.setSize(5);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.width")), ColumnPanel.RIGHT);
        section.add(m_width);

        m_value = new TextField(new StringParameter("value"));
        m_value.setSize(50);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.default_value")), ColumnPanel.RIGHT);
        section.add(m_value);

        m_datatype = new SingleSelect("datatype");
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.data_type")), ColumnPanel.RIGHT);
        section.add(m_datatype);

        Integer types[] = TextValidationListener.getValidationTypes();
        for (int i = 0 ; i < types.length ; i++) {
            m_datatype.addOption(new Option(types[i].toString(),
                                            TextValidationListener.getValidationTypeName(types[i])));
        }
    }

    protected PersistentWidget getWidget() {
        return new PersistentTextField();
    }

    protected PersistentWidget getWidget(BigDecimal id) {

        return new PersistentTextField(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PersistentTextField widget = (PersistentTextField)w;

        PageState state = e.getPageState();

        if (widget == null) {
            m_width.setValue(state, "");
            m_length.setValue(state, "");
            m_value.setValue(state, "");
        }
        else {
            if(widget.getSize()==0) {
                m_width.setValue(state, "");
            }
            else{
                m_width.setValue(state, new Integer(widget.getSize()));
            }
            if(widget.getMaxLength()==0) {
                m_length.setValue(state, "");
            }
            else{
                m_length.setValue(state, new Integer(widget.getMaxLength()));
            }
            m_value.setValue(state, widget.getDefaultValue());
        }

        TextValidationListener tfl = null;
        if (widget != null) {
            Iterator iter = widget.getValidationListeners().iterator();
            while (iter.hasNext()) {
                PersistentParameterListener l = (PersistentParameterListener)iter.next();
                if (l instanceof TextValidationListener) {
                    tfl = (TextValidationListener)l;
                    break;
                }
            }
        }

        if (tfl != null) {
            FormData d = e.getFormData();
            d.getParameter("datatype").setValue(tfl.getValidationType().toString());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        //set the default width and height to 0
        PersistentTextField widget = (PersistentTextField)w;

        FormData data = e.getFormData();
        PageState state = e.getPageState();

        Integer width = new Integer(0);
        Integer length = new Integer(0);
        String value = (String)data.get("value");

        if(data.get("width")!=null) {
            width = (Integer)data.get("width");
        }
        if(data.get("length")!=null) {
            length = (Integer)data.get("length");
        }

        widget.setDefaultValue(value);
        widget.setMaxLength(length.intValue());
        widget.setSize(width.intValue());
        String validate = (String)m_datatype.getValue(state);

        TextValidationListener tfl = new TextValidationListener(new Integer(validate));
        widget.addValidationListener(tfl);
    }
}
