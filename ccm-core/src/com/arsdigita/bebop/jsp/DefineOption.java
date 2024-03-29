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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.globalization.GlobalizedMessage;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * Class for defining an option within a radio/checkbox/select group.
 * usage:
 * <pre>
 *    &lt;define:option name="label" value="value"/>
 * </pre>
 *
 * The "label" attribute is what is displayed to the user.  This
 * text may be globalized:
 *
 * <pre>
 *    &lt;define:option name="msg.key" bundle="com.arsdigita.MyBundleName"
 value="value"/>
 * </pre>
 * 
 * @version $Id: DefineOption.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefineOption extends DefineComponent {

    private Option m_option;
    private String m_value;
    private boolean m_selected;
    private String m_bundleName;

    /**
     * overrides superclass. we're not in a Container but we need to add
     * ourselves to an option group instead.
     */
    public int doStartTag() throws JspException {
        m_option = new Option(getName());
        if (m_value != null) {
            m_option.setValue(m_value);
        }
        if (m_bundleName != null) {
            // globalized message
            GlobalizedMessage gm =
                new GlobalizedMessage(getName(), m_bundleName);
            m_option.setLabel(new Label(gm));
        }
        Tag t = findAncestorWithClass(this, DefineOptionGroup.class);
        DefineOptionGroup dog = ((DefineOptionGroup)t);
        dog.addOption(m_option);
        if (m_selected) {
            dog.selectOption(m_option);
        }
        dog.doAfterBody();
        return EVAL_BODY_TAG;
    }

    protected final Component getComponent() {
        return m_option;
    }

    public final void setValue(String s) {
        m_value = s;
    }

    /**
     * If <code>s</code> is "true" then this option is selected by default.
     * @param s if "true", this option is selected.
     */
    public final void setSelected(String s) {
        if (s.equals("true") || s.equals("yes") || s.equals("selected")) {
            m_selected = true;
        }
    }

    /**
     * Sets the bundle name to use for globalizing this option's label.
     * @param s the fully-qualified resource bundle name.
     */
    public final void setBundle(String s) {
        m_bundleName = s;
    }
}
