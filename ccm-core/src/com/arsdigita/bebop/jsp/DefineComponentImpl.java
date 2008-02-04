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
import javax.servlet.jsp.JspException;

/**
 * Base class for instantiating any Bebop component with JSP tags.  It
 * is usually better to make a define:... tag specific to your
 * component class, but you may use this class instead of a specific tag
 * for creating your component.
 * <p>usage:
 * <pre>&lt;define:component classname="component.class.name"/></pre>
 * or
 * <pre>&lt;define:component classname="component.class.name"> ...
 * ...
 * &lt;/define:component></pre>
 */

public class DefineComponentImpl extends DefineContainer {
    public static final String versionId = "$Id: DefineComponentImpl.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_className;
    private Component m_component;

    public int doStartTag() throws JspException {
        if (m_className == null) {
            throw new JspException("classname must be specified");
        }
        try {
            m_component = (Component)Class.forName(m_className).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new JspException(cnfe.toString());
        } catch (InstantiationException ie) {
            throw new JspException(ie.toString());
        } catch (IllegalAccessException iae) {
            throw new JspException(iae.toString());
        }
        return super.doStartTag();
    }

    public Component getComponent() {
        return m_component;
    }

    public void setClassname(String s) {
        m_className = s;
    }
}
