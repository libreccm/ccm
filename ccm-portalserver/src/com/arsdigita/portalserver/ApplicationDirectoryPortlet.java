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

import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.xml.Element;

public class ApplicationDirectoryPortlet extends AppPortlet {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ApplicationDirectoryPortlet.java#7 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.ApplicationDirectoryPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public ApplicationDirectoryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ApplicationDirectoryPortletRenderer(this);
    }
}

class ApplicationDirectoryPortletRenderer extends AbstractPortletRenderer {
    private ApplicationDirectoryPortlet m_portlet;

    public ApplicationDirectoryPortletRenderer
        (ApplicationDirectoryPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parentElement) {
        PortalSite portalsite = 
              PortalSite.getPortalSiteForAppPortlet(m_portlet);

        if (portalsite == null) {
            return;
        }

        // Variables used cursorwise.
        int counter;
        String title = null;
        String url = null;

        ApplicationCollection apps =
            portalsite.getFullPagePortalSiteApplications();

        GridPanel innerPanel = new GridPanel(1);

        for (counter = 0; apps.next(); counter++) {
            title = apps.getTitle();
            url = apps.getPrimaryURL();
            innerPanel.add(new Link(title, url));
        }

        if (counter == 0) {
            innerPanel.add(new Label(GlobalizationUtil.globalize("cw.workspace.none")));
        }

        innerPanel.generateXML(pageState, parentElement);
    }
}
