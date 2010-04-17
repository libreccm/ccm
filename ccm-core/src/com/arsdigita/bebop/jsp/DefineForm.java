/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.jsp;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import javax.servlet.jsp.JspException;

/** 
 *
 * @version Id: DefineForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefineForm extends DefineContainer {

    private Form m_form;
    private String m_encType;
    private String m_onSubmit;
    private String m_onReset;
    private String m_method;
    private String m_action;

    public int doStartTag() throws JspException {
        m_form = new Form(getName());
        if (m_encType != null) {
            m_form.setEncType(m_encType);
        }
        if (m_onSubmit != null) {
            m_form.setOnSubmit(m_onSubmit);
        }
        if (m_onReset != null) {
            m_form.setOnReset(m_onReset);
        }
        if (m_method != null) {
            m_form.setMethod(m_method);
        }
        if (m_action != null) {
            m_form.setAction(m_action);
        }
        // add form to parent
        return super.doStartTag();
    }

    protected final Component getComponent() {
        return m_form;
    }

    /**
     * Sets the encoding type (multipart/form-data, etc.) for the form
     * when posted.
     */
    public final void setEncType(String s) {
        m_encType = s;
    }

    /**
     * sets the browser-side script code to run on form submit.
     */
    public final void setOnSubmit(String s) {
        m_onSubmit = s;
    }

    /**
     * sets the browser-side script code to run on form reset.
     */
    public final void setOnReset(String s) {
        m_onReset = s;
    }

    /**
     * sets the HTTP method for the request on form submission, e.g.,
     * "POST", "GET".   This defaults to GET.
     */
    public final void setMethod(String s) {
        m_method = s;
    }

    /**
     * sets the URL to POST or GET on form submission.  Defaults
     * to the same page that contains the form.
     */
    public final void setAction(String s) {
        m_action = s;
    }
}
