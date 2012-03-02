/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.ui.login.UserAuthenticationListener;
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
 *
 * @author pb
 */
public class PortalSiteMapServlet extends BaseApplicationServlet {

    /** Logger instance for debugging */
    private static final Logger s_log = Logger.getLogger(PortalSiteMapServlet.class);

    private static final String XSL_HOOK = "portal-sitemap";

    private static final PresentationManager s_presManager =
                                             Templating.getPresentationManager();

    private static Page s_indexPage = buildSiteMapPage();
    


    /**
     * User extension point, do some initializing
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {
        s_log.debug("PortalSiteMapServlet doInit() initialization executing!");

        // do nothing for now.

    }
        
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
        s_log.debug("PortalSiteMApServlet.doService called for request '" +
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
        doc = s_indexPage.buildDocument(sreq, sresp);
        s_presManager.servePage(doc, sreq, sresp);

    }

    /**
     * 
     * @return
     */
    static Page buildSiteMapPage() {
        Page page = PageFactory.buildPage(XSL_HOOK, "Portal Site Map");
        PortalSiteMapPanel sitemapPanel = new PortalSiteMapPanel(page);
        page.add(sitemapPanel);
        page.addRequestListener(new UserAuthenticationListener());
        
        page.lock();
        return page;
    }
    
}
