/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.portlet;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.util.GlobalizationUtil; 
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

public class MyWorkspacesPortlet extends AppPortlet {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.portal.portlet.MyWorkspacesPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public MyWorkspacesPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new MyWorkspacesPortletRenderer(this);
    }

    protected DataQuery getMyPortalsDataQuery(Party party) {
        Assert.isTrue
            (!isNew(),
             "You must save this portlet before you call " +
             "getMyPortalsDataQuery(User) on it.");

        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.london.portal.portlet.MyWorkspaces");
        Assert.exists(query, "query");

        query.setParameter("userID", party.getID());

        //Application parent = getParentApplication();
        Application parent = Workspace.getCurrentlySelectedWorkspace();
        Assert.exists(parent, "parent");
        query.setParameter
            ("personalWorkspaceID", parent.getID());

        return query;
    }
}

class MyWorkspacesPortletRenderer extends AbstractPortletRenderer {
    private MyWorkspacesPortlet m_portlet;

    public MyWorkspacesPortletRenderer(MyWorkspacesPortlet portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState, Element parent) {
        Party party = Kernel.getContext().getParty();

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
