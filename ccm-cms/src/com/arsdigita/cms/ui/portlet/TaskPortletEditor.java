/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.portlet;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.cms.portlet.TaskPortlet;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.Portlet;



public class TaskPortletEditor extends PortletConfigFormSection {
    
    private int m_maxTasks;
    private TextField m_numTasks;
    private ResourceType m_resType;

    public TaskPortletEditor(ResourceType resType,
                             RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }
    
    public TaskPortletEditor(RequestLocal application) {
        super(application);
    }
    
    
    protected void addWidgets() {
        super.addWidgets();

        m_numTasks = new TextField(new IntegerParameter("numTasks"));
        m_numTasks.setSize(3);
        m_numTasks.addValidationListener(new NotNullValidationListener());
        
        add(new Label("Number of Tasks to display:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_numTasks);
    }
    
    protected void initWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        super.initWidgets(state, portlet);
        
        if (portlet != null) {
            TaskPortlet myportlet = (TaskPortlet)portlet;
            
            m_numTasks.setValue(state, new Integer(myportlet.getMaxNumTasks()));
        } else {
            m_numTasks.setValue(state, new Integer(5));
        }
    }
    
    protected void processWidgets(PageState state,
                                  Portlet portlet)
        throws FormProcessException {
        super.processWidgets(state, portlet);
        
        TaskPortlet myportlet = (TaskPortlet)portlet;
        myportlet.setMaxNumTasks(((Integer)m_numTasks.getValue(state)).intValue());
    }
}
