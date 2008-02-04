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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import javax.servlet.jsp.JspException;

/**
 * Defines a Submit widget at the current location in the JSP page.
 * The "name" attribute is the name of the submit widget.  The text
 * displayed to the user is globalized.  The "label" attribute is used
 * as a key in the ResourceBundle in the currently-running
 * application, and the "bundle" attribute may be used to specify a
 * fully-qualified ResourceBundle name explicitly.  The "label"
 * attribute may instead be used to provide non-globalized label text.
 *
 * <p>
 * Example, globalized:
 * <pre>
 * &lt;define:submit name="mySubmit" label="msg.key"
 *       bundle="com.arsdigita.notes.NotesResources"/>
 * </pre>
 * Example, not globalized:
 * <pre>
 * &lt;define:submit name="mySubmit" label="Submit form now!"/>
 * </pre>
 *
 * @see com.arsdigita.bebop.form.Submit */
public class DefineSubmit extends DefineComponent {

    public static final String versionId = "$Id: DefineSubmit.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Submit m_submit;
    private String m_label;
    private String m_bundleName;

    public int doStartTag() throws JspException {
        if (m_label == null) {
            m_label = getName();
        }
        GlobalizedMessage gm;
        if (m_bundleName == null) {
            gm = new GlobalizedMessage(m_label);
        } else {
            gm = new GlobalizedMessage(m_label, m_bundleName);
        }
        m_submit = new Submit(gm);
        // add form to parent
        return super.doStartTag();
    }

    protected final Component getComponent() {
        return m_submit;
    }

    public void setLabel(String s) {
        m_label = s;
    }

    public void setBundle(String s) {
        m_bundleName = s;
    }
}
