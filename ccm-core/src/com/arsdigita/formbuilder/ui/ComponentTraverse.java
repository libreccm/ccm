/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.xml.Element;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Component;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import java.util.Iterator;


/**
 *  This class is used to walk through the form and print out
 *  information that is necessary to print out the information required
 *  to allow Bebop to process the form even when the FormBuilderXMLRenderer
 *  is printing out the form.  Specifically, this is used to print
 *  out default values and error messages.
 */ 
public class ComponentTraverse extends Traversal {
        
    public static final String FORMBUILDER_FORM_DEFAULTS = 
        "formbuilder:formDefaults";
    public static final String FORMBUILDER_FORM_DEFAULT_VALUE = 
        "formbuilder:formDefaultValue";
    public static final String FORMBUILDER_FORM_ERROR = 
        "formbuilder:formError";

    private final String FORMBUILDER_XML_NS = 
        FormBuilderUtil.FORMBUILDER_XML_NS;

    public static final String TYPE = "type";
    public static final String PARAMETER_NAME = "parameterName";

    private PageState m_state;
    private FormData m_data;
    private Element m_parent;

    public ComponentTraverse(PageState state, FormData data, Element parent) {
        m_state = state;
        m_data = data;
        m_parent = parent;
    }

    public void act(Component c) {
        try {
            if (c instanceof Widget) {
                Widget w = (Widget)c;
                String modelName = w.getParameterModel().getName();
                if (c instanceof Date) {
                    // we have to special case Date becuase it is 
                    // actually several widgets in one.
                    String dayVar = modelName + ".day";
                    String defaultDay = m_state.getRequest()
                        .getParameter(dayVar);
                    String monthVar = modelName + ".month";
                    String defaultMonth = m_state.getRequest()
                        .getParameter(monthVar);
                    String yearVar = modelName + ".year";
                    String defaultYear = m_state.getRequest()
                        .getParameter(yearVar);

                    if (defaultDay != null || defaultMonth != null ||
                        defaultYear != null) {
                        Element info = createDefaultElement(modelName);
                        if (defaultDay != null) {
                            Element value = createDefaultValueElement(info);
                            value.addAttribute(TYPE, dayVar);
                            value.setText(defaultDay);
                        }
                        if (defaultMonth != null) {
                            Element value = createDefaultValueElement(info);
                            value.addAttribute(TYPE, monthVar);
                            value.setText(defaultMonth);
                        }
                        if (defaultYear != null) {
                            Element value = createDefaultValueElement(info);
                            value.addAttribute(TYPE, yearVar);
                            value.setText(defaultYear);
                        }
                    }
                } else {
                    String[] defaultValues = m_state.getRequest()
                        .getParameterValues(modelName);
                    if (defaultValues != null) {
                        Element info = createDefaultElement(modelName);
                        for (int i = 0; i < defaultValues.length; i++) {
                            Element value = 
                                createDefaultValueElement(info);
                            value.setText(defaultValues[i]);
                        }
                    }
                }

                if (m_data != null) {
                    Iterator iter = m_data.getErrors(w.getName());
                    while (iter.hasNext()) {
                        Element errors = m_parent.newChildElement
                            (FORMBUILDER_FORM_ERROR, FORMBUILDER_XML_NS);
                        errors.addAttribute
                            ("message",
                             (String) ((GlobalizedMessage)iter.next())
                             .localize(m_state.getRequest()));
                        errors.addAttribute("id", w.getName());
                    }
                }
            } 
        } catch (ClassCastException ex) {
            // Nada
        }
    }

    private Element createDefaultElement(String modelName) {
        Element element= m_parent.newChildElement
            (FORMBUILDER_FORM_DEFAULTS, FORMBUILDER_XML_NS);
        element.addAttribute(PARAMETER_NAME, modelName);
        return element;
    }
            
    private Element createDefaultValueElement(Element parent) {
        return parent.newChildElement
            (FORMBUILDER_FORM_DEFAULT_VALUE, FORMBUILDER_XML_NS);
    }
}
