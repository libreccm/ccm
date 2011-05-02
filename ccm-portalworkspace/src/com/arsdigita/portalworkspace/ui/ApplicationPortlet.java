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

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

public class ApplicationPortlet extends AbstractPortletRenderer {

    private RequestLocal m_parent;
    private Component m_portletComponent;
    private Component m_appComponent;


    /**
     * Constructor
     * @param title
     * @param column
     * @param row
     * @param parent
     * @param portletComponent
     * @param appComponent
     */
    public ApplicationPortlet(String title, int column, int row,
                              RequestLocal parent, Component portletComponent,
                              Component appComponent) {

        Assert.exists(parent, RequestLocal.class);
        Assert.exists(portletComponent, Component.class);
        Assert.exists(appComponent, Component.class);

        m_parent = parent;
        m_portletComponent = portletComponent;
        m_appComponent = appComponent;

        setTitle(title);
        setCellNumber(column);
        setSortKey(row);

    }


    /**
     *
     * @param state
     * @param parent
     */
    protected void generateBodyXML(PageState state, Element parent) {

        Element content = parent.newChildElement("portlet:simple",
                                                 PortalConstants.PORTLET_XML_NS);

        if (m_parent.get(state) == null) {
            m_appComponent.generateXML(state, content);
        } else {
            m_portletComponent.generateXML(state, content);
        }

    }

}
