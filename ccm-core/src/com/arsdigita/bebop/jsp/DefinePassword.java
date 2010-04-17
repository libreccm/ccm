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
import com.arsdigita.bebop.form.Password;
import javax.servlet.jsp.JspException;

/**
 * 
 * @version $Id: DefinePassword.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefinePassword extends DefineComponent {

    private Password m_password;
    private int m_maxLength;
    private int m_size;

    public int doStartTag() throws JspException {
        m_password = new Password(getName());
        m_password.setSize(m_size);
        m_password.setMaxLength(m_maxLength);
        // add form to parent
        int ret = super.doStartTag();
        // must put parameter model in page context after calling supereclass
        pageContext.setAttribute(getName(), m_password.getParameterModel());
        return ret;
    }

    protected final Component getComponent() {
        return m_password;
    }

    public final void setSize(String s) {
        m_size = new Integer(s).intValue();
    }

    public final void setMaxLength(String s) {
        m_maxLength = new Integer(s).intValue();
    }
}
