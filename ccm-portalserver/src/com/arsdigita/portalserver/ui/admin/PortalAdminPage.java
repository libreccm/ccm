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
package com.arsdigita.portalserver.ui.admin;


import com.arsdigita.portalserver.util.GlobalizationUtil; 


import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.portalserver.ApplicationAuthenticationListener;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalPage;

import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.apache.log4j.Category;

public class PortalAdminPage extends PortalPage {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PortalAdminPage.java#8 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (PortalAdminPage.class.getName());

    RequestLocal m_portalsiteRL;

    public PortalAdminPage() {
        super();
 
        this.setClassAttr("portalserveradmin");

        getHeader().setIdAttr("admin");

        m_portalsiteRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return PortalSite.getCurrentPortalSite(ps.getRequest());
                }
            };

        addRequestListener(new ApplicationAuthenticationListener("admin"));

        lock();
    }

    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new Link(new PersonalPortalLinkPrinter()));
        navbar.add(new Link(new CurrentPortalLinkPrinter()));
        navbar.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.configure_workspace")));

        getHeader().add(navbar);
    }

    protected void buildHeader(Container header) {
        Link returnLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.return_to_workspace")),  "../");

        returnLink.setClassAttr("portalControl");

        header.add(returnLink);
    }

    protected void buildBody(Container body) {
        TabbedPane pane = new TabbedPane();

        pane.setClassAttr("portalserver_admin"); 

        pane.addTab("Layout", new DisplayPane(m_portalsiteRL));
        pane.addTab("Themes", new ThemesPane(m_portalsiteRL));
        pane.addTab("Applications", ApplicationsPane.create(m_portalsiteRL));
        pane.addTab("People", PeoplePane.create(this, m_portalsiteRL));
        pane.addTab("Related Portals",
                    RelatedPortalsPane.create(m_portalsiteRL));
        pane.addTab("Basic Properties", BasicPane.create(m_portalsiteRL));

        body.add(pane);
        body.setClassAttr("portalserver_admin");
    }

    public void addStyleBlock(PageState state, Document parent) {
        PortalSite psite = PortalSite.getCurrentPortalSite(state.getRequest());

        StringBuffer buffer = new StringBuffer();

        buffer.append("<STYLE type=\"text/css\"> <!--");


          buffer.append(" table.globalHeader { background-color: rgb(225,225,225);} ");

        buffer.append(" table.bottomRule { background: rgb(162,30,30);} ");

        buffer.append(" table.topRuleNoTabs { background: rgb(162,30,30);} ");

        buffer.append("--></STYLE>");

        Element rootElement = parent.getRootElement();

        Element styleBlock = rootElement.newChildElement(
                                     "portalserver:styleblock", 
                                     "http://www.redhat.com/portalserver/1.0");

        styleBlock.setCDATASection(buffer.toString());

    }


}
