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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.jsp.JspException;

public class DefineTextArea extends DefineComponent {

    private TextArea m_textArea;
    private String m_type;
    private int m_rows;
    private int m_cols;
    private int m_wrap;
    private String m_value;

    private final static String s_defaultParameterType =
        "com.arsdigita.bebop.parameters.StringParameter";

    private final static String s_errorMsg =
        "type must be an instance of ParameterModel";
    private final static String s_wrapErrorMessage =
        "wrap must be soft, hard, or off";

    public static final String versionId = "$Id: DefineTextArea.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

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
            m_textArea = new TextArea(pm);
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

        m_textArea.setValue(m_value);
        if (m_rows > 0) {
            m_textArea.setRows(m_rows);
        }
        if (m_cols > 0) {
            m_textArea.setCols(m_cols);
        }
        m_textArea.setWrap(m_wrap);
        // add form to parent
        int ret = super.doStartTag();
        // must put parameter model in page context after calling supereclass
        pageContext.setAttribute(getName(), pm);
        return ret;
    }

    protected final Component getComponent() {
        return m_textArea;
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

    /**
     * Sets the number of rows in the text box.
     */
    public final void setRows(String s) {
        m_rows = Integer.valueOf(s).intValue();
    }

    /**
     * Sets the number of columns in the text box.
     */
    public final void setCols(String s) {
        m_cols = Integer.valueOf(s).intValue();
    }

    /**
     * Sets the wrap property for the text entry box. Legal values are
     * "off", "hard", and "soft"; these are the same as those for the
     * HTML textarea element.
     *
     * @param s the wrap property */
    public final void setWrap(String s) throws JspException {
        if (s.equals("off")) {
            m_wrap = TextArea.OFF;
        } else if (s.equals("soft")) {
            m_wrap = TextArea.SOFT;
        } else if (s.equals("hard")) {
            m_wrap = TextArea.HARD;
        } else {
            throw new JspException(s_wrapErrorMessage);
        }
    }

    /**
     * sets the default value for the text area.
     */
    public final void setValue(String s) {
        m_value = s;
    }
}
