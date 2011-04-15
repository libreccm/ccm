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
package com.arsdigita.portalserver.ui.admin;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
//import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.ui.login.UserAuthenticationListener;

//import java.util.Map;
//import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * XXX JAVADOC
 * 
 */
public class PortalSiteMapDispatcher extends BebopMapDispatcher {

    private static final Logger s_log = Logger.getLogger
        (PortalSiteMapDispatcher.class);

    private static final String XSL_HOOK = "portal-sitemap";


    /**
     * 
     */
    public PortalSiteMapDispatcher() {
        s_log.info("PortalSiteMapDispatcher created!");

        Page index = buildSiteMapPage();
        addPage("", index);
        addPage("index.jsp", index);
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
