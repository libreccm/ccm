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
package com.arsdigita.ui.sitemap;


import com.arsdigita.ui.util.GlobalizationUtil ;

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.globalization.GlobalizedMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Dispatcher for SiteMap Admin functionality
 *
 * @version $Id: SiteMapDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SiteMapDispatcher extends BebopMapDispatcher {

    private static final Logger s_log =
        Logger.getLogger(SiteMapDispatcher.class);

    static final String SEARCH_KEY = "search";

    /**
     * Constructor.  Instantiates the subsite url/page mapping.
     */
    public SiteMapDispatcher() {
        super();

        s_log.debug("Construct SiteMapDispatcher");

        Map  m = new HashMap();
        m.put("", buildAdminPage());
        m.put("denied", buildDeniedPage());
        setMap(m);
    }

    /***
     * 
     * @param req
     * @param resp
     * @param ctx
     * @throws IOException
     * @throws javax.servlet.ServletException
     */
    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
            throws IOException, javax.servlet.ServletException {
        /**
         * Authenicate user.
         */
        String requestURI = Web.getContext().getRequestURL().getRequestURI();

        if (requestURI.endsWith("/denied")) {
            super.dispatch(req, resp, ctx);
            return;
        }

        UserContext userCtx = Web.getUserContext();

        // If the user is not logged in, then redirect to the log in page.

        if (!userCtx.isLoggedIn()) {
            throw new LoginSignal(req);
        } else {
            // Check if user has an admin privilege.
            String processedURL = ctx.getProcessedURLPart();

            SiteNode node;

            node = SiteNode.getSiteNode(processedURL);

            User user = userCtx.getUser();

            if (user == null) {
                throw new RuntimeException("User does not exist");
            }

            PermissionDescriptor admin = new PermissionDescriptor
                (PrivilegeDescriptor.ADMIN, node, user);

            // Turn off caching for everything...
            DispatcherHelper.cacheDisable(resp);

            if (PermissionService.checkPermission(admin)) {
                super.dispatch(req, resp, ctx);
            } else {
                final URL url = URL.there(req, node.getURL() + "denied");

                throw new RedirectSignal(url, false);
            }
        }
    }

    /**
     * "Access Denied" page for the SiteMap.
     */
    private Page buildDeniedPage() {
        Page p = PageFactory.buildPage("admin",
                                 new Label(new GlobalizedMessage
                                           ("ui.admin.dispatcher.accessDenied",
                                            "com.arsdigita.ui.admin.AdminResources" )));

        Label label = new Label(GlobalizationUtil.globalize(
                                    "ui.sitemap.access_denied_to_sitemap"));
        label.setClassAttr("AccessDenied");
        p.add(label);

        p.lock();

        return p;
    }

    /**
     * Admin Page for the SiteMap application
     * @return
     */
    private Page buildAdminPage() {
        Page p = PageFactory.buildPage("admin", "Sitemap Administration");

        SiteListing listing = new SiteListing();
        listing.setClassAttr("navbar");

        SingleSelectionModel m = listing.getTree().getSelectionModel();
        SiteMapAdminPane details = new SiteMapAdminPane(m, listing.getCFGLink());

        BoxPanel box = new BoxPanel();
        box.setClassAttr("main");
        box.add(details);

        SplitPanel panel = new SplitPanel();
        panel.setClassAttr("sidebarNavPanel");
        panel.setLeftComponent(listing);
        panel.setRightComponent(box);

        p.add(panel);
        p.lock();

        return p;
    }

}
