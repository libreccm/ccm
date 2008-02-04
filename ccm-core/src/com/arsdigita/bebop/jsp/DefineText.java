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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.jsp.JspException;

public class DefineText extends DefineComponent {

    private TextField m_text;
    private String m_type;
    private int m_maxLength;
    private int m_size;

    private final static String s_defaultParameterType =
        "com.arsdigita.bebop.parameters.StringParameter";

    private final static String s_errorMsg =
        "type must be an instance of ParameterModel";

    public static final String versionId = "$Id: DefineText.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public int doStartTag() throws JspException {
        ParameterModel pm = null;
        if (m_type == null) {
            m_type = s_defaultParameterType;
        }

        try {
            Class paramClass = Class.forName(m_type);
            Constructor ctor =
                paramClass.getConstructor(new Class[] { String.class });
            pm = (ParameterModel)ctor.newInstance(new Object[] {getName()});
            m_text = new TextField(pm);
            if (m_size > 0) {
                m_text.setSize(m_size);
            }
            if (m_maxLength > 0) {
                m_text.setMaxLength(m_maxLength);
            }
        } catch (ClassNotFoundException e) {
            throw new JspWrapperException("cannot find class " + m_type, e);
        } catch (NoSuchMethodException e) {
            throw new JspWrapperException("cannot find (String) constructor in: " + m_type, e);
        } catch (IllegalAccessException e) {
            throw new JspWrapperException("constructor is not public in: " + m_type, e);
        } catch (InstantiationException e) {
            throw new JspWrapperException("class is abstract: " + m_type, e);
        } catch (InvocationTargetException e) {
            throw new JspWrapperException("constructor threw an exception in: " + m_type, e);
        }

        // add form to parent
        int ret = super.doStartTag();
        // must put parameter model in page context after calling supereclass
        pageContext.setAttribute(getName(), pm);
        return ret;
    }

    protected final Component getComponent() {
        return m_text;
    }

    /**
     * sets the type of the parameter.  The parameter type
     * must be an instance of
     * <code>com.arsdigita.bebop.parameters.ParameterModel</code>.
     * Default is <code>StringParameter</code>.
     */
    public final void setType(String s) {
        m_type = s;
    }

    public final void setSize(String s) {
        m_size = new Integer(s).intValue();
    }

    public final void setMaxLength(String s) {
        m_maxLength = new Integer(s).intValue();
    }
}
