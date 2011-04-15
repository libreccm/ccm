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
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.xml.Element;

/**
 * 
 * @version $Id: PortalNavigatorPortlet.java  pboy $
 */
public class PortalNavigatorPortlet extends AppPortlet {

    public static final String BASE_DATA_OBJECT_TYPE =
                           "com.arsdigita.workspace.WorkspaceNavigatorPortlet";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public PortalNavigatorPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new PortalNavigatorPortletRenderer(this);
    }
}

class PortalNavigatorPortletRenderer extends AbstractPortletRenderer {
    private PortalNavigatorPortlet m_portlet;

    public PortalNavigatorPortletRenderer
        (PortalNavigatorPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parentElement) {
        PortalSite currentPortal =
            (PortalSite) m_portlet.getParentApplication();

        if (currentPortal == null) {
            return;
        }

        GridPanel panel = new GridPanel(2);

        // Variables used cursorwise.
        int counter;
        String title = null;
        String url = null;

        // Parent Portal

        PortalSite portalsite = PortalSite.getPortalSiteForApplication
            (currentPortal);

        if (portalsite != null) {
            title = portalsite.getTitle();
            url = portalsite.getPrimaryURL();
            panel.add(new Link(title, url));
            Label l = new Label(GlobalizationUtil.globalize("cw.workspace.parent"));
            l.setFontWeight(Label.ITALIC);
            panel.add(l, BlockStylable.RIGHT);
        }

        // Child Portals

        PortalSiteCollection childPortals =
            currentPortal.getChildPortalSites();

        for (counter = 0; childPortals.next(); counter++) {
            title = childPortals.getTitle();
            url = childPortals.getPrimaryURL();
            panel.add(new Link(title, url));
            Label l = new Label(GlobalizationUtil.globalize("cw.workspace.child"));
            l.setFontWeight(Label.ITALIC);
            panel.add(l, BlockStylable.RIGHT);
        }

        // Related portals

        PortalSiteCollection relatedPortals =
            currentPortal.getRelatedPortalSites();

        for (counter = 0; relatedPortals.next(); counter++) {
            title = relatedPortals.getTitle();
            url = relatedPortals.getPrimaryURL();
            panel.add(new Link(title, url));
            panel.add(new Label(""));
        }

        panel.generateXML(pageState, parentElement);
    }
}
