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
 */

package com.arsdigita.london.terms.ui.admin;
 

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;

import com.arsdigita.london.terms.Terms;

public class TermNameSearchForm extends Form {
    
    private TextField m_name;
    private SaveCancelSection m_buttons;

    public TermNameSearchForm() {
        super("nameSearch", new SimpleContainer(Terms.XML_PREFIX + 
                                                ":termNameSearch",
                                                Terms.XML_NS));

        m_name = new TextField("name");
        m_name.addValidationListener(new NotNullValidationListener());
        m_name.setMetaDataAttribute("label", "Name");
        m_name.setHint("Enter a name to search for");
        
        m_name.setPassIn(true);

        add(m_name);
        
        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) 
                    throws FormProcessException {
                    fireCompletionEvent(e.getPageState());
                }
            });
                   
    }
    
    public void register(Page p) {
        super.register(p);

        //p.addGlobalStateParam(m_name.getParameterModel());
    }

    public String getQuery(PageState state) {
        return (String)m_name.getValue(state);
    }
}
