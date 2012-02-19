/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.webdevsupport.ui;

import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.StringParameter;

/**
 * Log4j category level form
 *
 *
 * Created: Mon Jul 29 14:01:52 2002
 *
 * @author Daniel Berrange
 */

public class CategoryForm extends Form {

    private TextField m_category;
    private SingleSelect m_level;

    public CategoryForm() {
        super("log4j");

        m_category = new TextField(new StringParameter("category"));
        m_category.setSize(40);
        add(new Label("Logger:"));
        add(m_category);

        m_level = new SingleSelect(new StringParameter("level"));
        m_level.addOption(new Option("DEBUG", "Debug"));
        m_level.addOption(new Option("INFO", "Info"));
        m_level.addOption(new Option("WARN", "Warn"));
        m_level.addOption(new Option("ERROR", "Error"));
        m_level.addOption(new Option("FATAL", "Fatal"));
        add(new Label("Level:"));
        add(m_level);

        add(new Submit("Update"));
    }


    public void setLogger(PageState state,
                          String cat) {
        m_category.setValue(state, cat);
    }

    public String getLogger(PageState state) {
        return (String)m_category.getValue(state);
    }

    public void setLevel(PageState state,
                         String level) {
        m_level.setValue(state, level);
    }

    public String getLevel(PageState state) {
        return (String)m_level.getValue(state);
    }
}
