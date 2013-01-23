/*
 * Copyright (C) 2012-2013 University of Bremen. All Rights Reserved.
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

import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.portalserver.ui.PortalParticipants;
import com.arsdigita.portalserver.ui.admin.PortalAdminPage;
import com.arsdigita.portalserver.util.GlobalizationUtil;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



/**
 * Provides the UI for the personal Portal for logged in users.
 * 
 * It replaces the former PersonalPortalDispatcher.
 * 
 * @author pb <pboy@barkhof.uni-bremen.de>
 * @version $Id: PersonalPortalServlet.java  pboy $
 */
public class PersonalPortalServlet extends BaseApplicationServlet {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(PersonalPortalServlet.class);

    private static final PresentationManager s_presManager =
                                             Templating.getPresentationManager();

    private static Page s_homePage = new PersonalPortalHomePage();
    private static Page s_particPage = PortalParticipants.createPage();
    private static Page s_adminPage = s_adminPage = new PortalAdminPage() {
                    @Override
                    protected void buildContextBar() {
                        DimensionalNavbar navbar = new DimensionalNavbar();
                        navbar.setClassAttr("portalNavbar");
                        navbar.add(new Link(new PersonalPortalLinkPrinter()));
                        navbar.add(new Label(GlobalizationUtil
                                             .globalize(
                                    "cw.workspace.personal.configure_workspace")));
                        getHeader().add(navbar);
                        }
                    };

    /**
     * 
     * @param sreq
     * @param sresp
     * @param app
     * @throws ServletException
     * @throws IOException 
     */
    public void doService(HttpServletRequest sreq,
                          HttpServletResponse sresp,
                          Application app)
            throws ServletException, IOException {
        s_log.debug("PortalServlet.doService called for request '" +
                    sreq.getRequestURI() + "'");

        String pathInfo = sreq.getPathInfo();
        Assert.exists(pathInfo, "String pathInfo");
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length()-1);
        }
        Document doc = null;

        if (pathInfo.endsWith("participants")) {
            doc = s_particPage.buildDocument(sreq, sresp);
        } else if (pathInfo.endsWith("admin")) {
            doc = s_adminPage.buildDocument(sreq, sresp);
        } else {
            doc = s_homePage.buildDocument(sreq, sresp);
        }

        s_presManager.servePage(doc, sreq, sresp);
    }
    
}
