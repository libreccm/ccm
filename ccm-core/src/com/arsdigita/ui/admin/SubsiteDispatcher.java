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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.Page;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.Initializer;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * This is the base dispatcher extended by the Admin, User and Group
 * dispatchers.  It defines a dispatch method that verifies that the
 * user requesting the page is logged in and has site-wide
 * administration privileges.
 *
 * @author Ron Henderson
 * @version $Id: SubsiteDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
class SubsiteDispatcher extends BebopMapDispatcher {

    private static final Logger s_log = Logger.getLogger
        (SubsiteDispatcher.class);

    /**
     * Adds one URL to the dispatcher's page map.  If isIndex is true,
     * the page is also mapped to the empty string "" so that it can
     * handle the special case of a request for the directory.
     */
    public void addPage(String url, Page p, boolean isIndex) {
        if (isIndex) {
            super.addPage("", p);
        }

        super.addPage(url, p);
    }

    /**
     * Examines each request for a page in the admin section to verify
     * that the user requesting the page is logged in and authorized
     * as a system-wide administrator.
     */
    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
            throws IOException, javax.servlet.ServletException {
        // Always dispatch the "access denied" page

        String requestURI = req.getRequestURI();

        if (requestURI.endsWith("/denied/")) {
            super.dispatch(req, resp, ctx);
            return;
        }

        Party party = Kernel.getContext().getParty();

        if (party == null) {
            // The user is not logged in; redirect to the login page.

            final String path = Initializer.getSecurityHelper().getLoginURL
                (req);

            final ParameterMap params = new ParameterMap();

            params.setParameter("return_url",
                                Web.getContext().getRequestURL());

            throw new RedirectSignal(URL.there(req, path, params), false);
        } else {
            // Check if user has an admin privilege.

            String processedURL = ctx.getProcessedURLPart();

            SiteNode node;

            node = SiteNode.getSiteNode(processedURL);

            PermissionDescriptor admin = new PermissionDescriptor
                (PrivilegeDescriptor.ADMIN, node, party);

            // Admins should always see the very latest data
            DispatcherHelper.cacheDisable(resp);

            if (PermissionService.checkPermission(admin)) {
                super.dispatch(req, resp, ctx);
            } else {
                final URL url = URL.there(req, node.getURL() + "denied");

                throw new RedirectSignal(url, false);
            }
        }
    }
}
