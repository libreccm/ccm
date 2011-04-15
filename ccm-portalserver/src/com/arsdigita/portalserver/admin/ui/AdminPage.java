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
package com.arsdigita.portalserver.admin.ui;

import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.bebop.table.*;
import com.arsdigita.bebop.*;
import com.arsdigita.dispatcher.*;
import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.PortalPage;
import com.arsdigita.toolbox.ui.*;
import org.apache.log4j.Logger;

import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

/**
 * AdminPage 
 * -
 * This class is the UI component for Portal-admin.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/17 $
 */
class AdminPage extends PortalPage {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/admin/ui/AdminPage.java#8 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    BoxPanel m_bpanel;

    private static Logger s_log = Logger.getLogger
        (AdminPage.class.getName());

    private RequestLocal m_children = new RequestLocal() {
            public Object initialValue(PageState ps) {
                Application app = Application.getCurrentApplication
                    (DispatcherHelper.getRequest());
                return app.getChildApplications();
            }
        };

    public AdminPage() {
       getHeader().setIdAttr("admin"); 
    }

    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new Link(new PersonalPortalLinkPrinter()));

        navbar.add(new Label(new CurrentApplicationLabelPrinter()));

        getHeader().add(navbar);
    }
    
    /**
     * This method outputs a list of links to admin functionality.
     */

    protected void buildBody(Container body) {

        m_bpanel = new BoxPanel();
        m_bpanel.setClassAttr("portaladminapps");
        body.add(m_bpanel);
        Link portalCreateLink = new Link("Create Top-Level Portals",
                                          "/portal-admin/portal-create");
        Link portalSiteMapLink = new Link("Manage Portals",
                                          "/portal-admin/portal-sitemap");
        Link managePeopleLink = new Link("Manage Users and Groups",
                                          "/admin");
        m_bpanel.add(portalCreateLink);
        m_bpanel.add(portalSiteMapLink);
        m_bpanel.add(managePeopleLink);
    }
   
    /**
      * This method is called by the generateXML() method of the parent
      * class, and writes a CSS style block into the output.
      */

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
