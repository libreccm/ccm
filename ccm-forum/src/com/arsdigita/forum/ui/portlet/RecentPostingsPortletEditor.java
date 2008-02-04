/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.forum.ui.portlet;

import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.kernel.ResourceType;

import com.arsdigita.bebop.portal.PortletConfigFormSection;

import com.arsdigita.portal.Portlet;
import com.arsdigita.forum.portlet.RecentPostingsPortlet;


public class RecentPostingsPortletEditor extends PortletConfigFormSection {

    private TextField m_count;
    
    public RecentPostingsPortletEditor(ResourceType resType,
                                       RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }
    
    public RecentPostingsPortletEditor(RequestLocal application) {
        super(application);
    }


    protected void addWidgets() {
        super.addWidgets();

        m_count = new TextField(new IntegerParameter("count"));
        m_count.setSize(5);
        m_count.addValidationListener(new NotNullValidationListener());
        m_count.setHint("Enter the maximum number of posts to display");
        add(new Label("Max number of posts:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_count);

    }
    
    protected void initWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        super.initWidgets(state, portlet);
        
        if (portlet != null) {
            RecentPostingsPortlet myportlet = (RecentPostingsPortlet)portlet;
            
            m_count.setValue(state, myportlet.getNumPosts());
        } else {
            m_count.setValue(state, new Integer(5));
        }
    }
    
    protected void processWidgets(PageState state,
                                  Portlet portlet)
        throws FormProcessException {
        super.processWidgets(state, portlet);
        
        RecentPostingsPortlet myportlet = (RecentPostingsPortlet)portlet;
        
        myportlet.setNumPosts((Integer)m_count.getValue(state));
    }
}
