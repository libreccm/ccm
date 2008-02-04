/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterData;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormData;

import com.arsdigita.search.Search;
import com.arsdigita.search.FilterType;

import com.arsdigita.xml.Element;

import com.arsdigita.globalization.GlobalizedMessage;


import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * This class provides a base class with infrastruture
 * for generating XML for the filter & managing a state
 * parameter in a form. Subclasses need only override
 * the generateBodyXML method.
 * @see com.arsdigita.search.ui.FilterComponent
 */
public abstract class FilterWidget extends FilterComponent {

    private static final Logger s_log = 
        Logger.getLogger(FilterWidget.class);
    
    private ParameterModel m_param;
    private Form m_form;
    
    /**
     * Creates a filter component
     * @param type the filter type
     * @param name the state parameter name
     */
    public FilterWidget(FilterType type, 
                        String name) {
        this(type,
             new StringParameter(name));
    }
    
    /**
     * Creates a filter component
     * @param type the filter type
     * @param param the state parameter
     */
    public FilterWidget(FilterType type, 
                        ParameterModel param) {
        super(type);
        m_param = param;
        
        setAttribute("param", m_param.getName());
    }

    public void register(Form form, FormModel model) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding " + m_param.getName() + " to form model");
        }

        m_param.setPassIn(true);
        model.addFormParam(m_param);
        m_form = form;
    }

    /**
     * Returns the current form to which this component
     * belongs
     * @return the form
     */
    public Form getForm() {
        return m_form;
    }
    
    /**
     * Returns the component's state parameter
     * @return the state parameter
     */
    public ParameterModel getParameter() {
        return m_param;
    }
    
    /**
     * Gets the current value of the state parameter
     */
    public Object getValue(PageState state) {
        FormData fd = m_form.getFormData(state);
        if (fd != null) {
            ParameterData data = fd.getParameter(m_param.getName());
            return data.getValue();
        }
        return null;
    }

    protected void generateErrorXML(PageState state,
                                    Element parent) {
        FormData f = getForm().getFormData(state);
        if (f == null) {
            return;
        }
        Iterator i = f.getErrors(getParameter().getName());

        while (i.hasNext()) {
            Element error = Search.newElement("error");
            error.setText(
                (String) ((GlobalizedMessage) i.next())
                .localize(state.getRequest())
            );
            parent.addContent(error);
        }
    }
    
    /**
     * The impl of this methods adds information about any
     * validation errors to the output. If overriding this
     * method, you must call super.generateBodyXML.
     */
    protected void generateBodyXML(PageState state,
                                   Element parent) {
        generateErrorXML(state, parent);
    }

}
