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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import javax.servlet.jsp.JspException;

/**
 * superclass for Define... classes that create option groups, so
 * the nested define:option tags  add the Options to the OptionGroup.
 * We do all the work for all OptionGroups here, and define an
 * abstract method "createOptionGroup" to create the specific optionGroup
 * we need.
 *
 * @author Bill Schneider
 */
public abstract class DefineOptionGroup extends DefineContainer {
    public static final String versionId = "$Id: DefineOptionGroup.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private OptionGroup m_optionGroup;
    private String m_onSelect;
    private String m_onChange;

    /**
     * subclasses must implement this method to create the option
     * group object.
     */
    abstract OptionGroup createOptionGroup();

    public int doStartTag() throws JspException {
        m_optionGroup = createOptionGroup();
        if (m_onChange != null) {
            m_optionGroup.setOnChange(m_onChange);
        }
        if (m_onSelect != null) {
            m_optionGroup.setOnSelect(m_onSelect);
        }
        int ret = super.doStartTag();
        // must put parameter model in page context after calling supereclass
        pageContext.setAttribute(getName(), m_optionGroup.getParameterModel());
        return ret;
    }

    void addOption(Option opt) {
        m_optionGroup.addOption(opt);
    }

    void selectOption(Option opt) {
        m_optionGroup.setOptionSelected(opt);
    }

    public Component getComponent() {
        return m_optionGroup;
    }

    public void setOnSelect(String code) {
        m_onSelect = code;
    }

    public void setOnChange(String code) {
        m_onChange = code;
    }
}
