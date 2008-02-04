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
package com.arsdigita.bebop.portal;

import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.portal.Portlet;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.util.UncheckedWrapperException;


public class PortletConfigFormSection extends ResourceConfigFormSection {

    private RequestLocal m_parentResource;
    private RequestLocal m_currentResource;
    private PortletType m_portletType;

    private TextField m_title;
    private TextArea m_desc;

    public PortletConfigFormSection(ResourceType resType,
                                    RequestLocal parentAppRL) {
        m_portletType = (PortletType)resType;
        m_parentResource = parentAppRL;
        m_portletType.disconnect(); 
        setup();
    }
    
    public PortletConfigFormSection(RequestLocal application) {
        m_currentResource = application;
        
        setup();
    }
    
    private void setup() {
        addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e)
                    throws FormProcessException {
                    PageState state = e.getPageState();
                    
                    if (m_currentResource != null) {
                        Portlet portlet = (Portlet)m_currentResource.get(state);
                        initWidgets(state, portlet);
                    } else {
                        initWidgets(state, null);
                    }
                }
            });
        addValidationListener(new FormValidationListener() {
                public void validate(FormSectionEvent e)
                    throws FormProcessException {
                    PageState state = e.getPageState();
                    
                    if (m_currentResource != null) {
                        Portlet portlet = (Portlet)m_currentResource.get(state);
                        validateWidgets(state, portlet);
                    } else {
                        validateWidgets(state, null);
                    }
                }
            });
        
        addWidgets();
    }

    protected void addWidgets() {
        m_title = new TextField(new StringParameter("title"));
        m_title.setSize(35);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new StringInRangeValidationListener(1, 200));

        m_desc = new TextArea(new StringParameter("desc"));
        m_desc.setRows(5);
        m_desc.setCols(35);
        m_desc.addValidationListener(new StringInRangeValidationListener(0, 4000));
        
        add(new Label("Title:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_title);
        add(new Label("Description:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_desc);
    }
    
    protected void initWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        
        if (portlet != null) {
            m_title.setValue(state, portlet.getTitle());
            m_desc.setValue(state, portlet.getDescription());
        } else {
            m_title.setValue(state, m_portletType.getTitle());
            m_desc.setValue(state, m_portletType.getDescription());
        }
    }
    
    protected void validateWidgets(PageState state,
                                   Portlet portlet) 
        throws FormProcessException {
    }
    
    public Resource createResource(PageState state) {
        Resource parent = (Resource)m_parentResource.get(state);
        Portlet portlet = Portlet.createPortlet(m_portletType,
                                                parent);
        
        try {
            processWidgets(state, portlet);
        } catch (FormProcessException ex) {
            throw new UncheckedWrapperException("cannot create resource", ex);
        }
        
        return portlet;
    }

    public void modifyResource(PageState state) {
        Portlet portlet = (Portlet)m_currentResource.get(state);
        
        try {
            processWidgets(state, portlet);
        } catch (FormProcessException ex) {
            throw new UncheckedWrapperException("cannot create resource", ex);
        }
    }

    protected void processWidgets(PageState state,
                                  Portlet portlet)
        throws FormProcessException {
        portlet.setTitle((String)m_title.getValue(state));
        portlet.setDescription((String)m_desc.getValue(state));
    }
}
