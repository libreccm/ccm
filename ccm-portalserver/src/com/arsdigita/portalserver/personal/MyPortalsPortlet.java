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
package com.arsdigita.portalserver.personal;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.web.Application;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

public class MyPortalsPortlet extends AppPortlet {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.personal.MyWorkspacesPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public MyPortalsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new MyPortalsPortletRenderer(this);
    }

    protected DataQuery getMyPortalsDataQuery(Party party) {
     // Assert.assertTrue
        Assert.isTrue
            (!isNew(),
             "You must save this portlet before you call " +
             "getMyPortalsDataQuery(User) on it.");

        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.personal.MyWorkspaces");

     // Assert.assertNotNull(query, "query");
        Assert.exists(query, "query");

        query.setParameter("userID", party.getID());

        Application parent = getParentApplication();

     // Assert.assertNotNull(parent, "parent");
        Assert.exists(parent, "parent");

        query.setParameter
            ("personalWorkspaceID", getParentApplication().getID());

        return query;
    }
}

class MyPortalsPortletRenderer extends AbstractPortletRenderer {
    private MyPortalsPortlet m_portlet;

    public MyPortalsPortletRenderer(MyPortalsPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parent) {
        Party party = Kernel.getContext().getParty();

    //  Assert.assertNotNull(party, "party");
        Assert.exists(party, "party");

        DataQuery query = m_portlet.getMyPortalsDataQuery(party);

        GridPanel panel = new GridPanel(1);

        String title = null;
        String primaryURL = null;
        boolean isEmpty = true;

        while (query.next()) {
            isEmpty = false;

            // id = query.get("id");
            title = (String) query.get("title");
            primaryURL = (String) query.get("primaryURL");

        //  Assert.assertNotNull(title, "title");
        //  Assert.assertNotNull(primaryURL, "primaryURL");
            Assert.exists(title, "title");
            Assert.exists(primaryURL, "primaryURL");

            panel.add(new Link(title, primaryURL));
        }

        query.close();

        if (isEmpty) {
            panel.add(new Label(GlobalizationUtil.globalize("cw.workspace.personal.none")));
        }

        panel.generateXML(pageState, parent);
    }
}
