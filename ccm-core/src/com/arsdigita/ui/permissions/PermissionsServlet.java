/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.ui.permissions;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.ui.login.UserAuthenticationListener;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.xml.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



/**
 * Web Developer Support Application Servlet class, central entry point  to 
 * create and process the applications UI.
 * 
 * We should have subclassed BebopApplicationServlet but couldn't overwrite
 * doService() method to add permission checking. So we use our own page
 * mapping. The general logic is the same as for BebopApplicationServlet.
 * {@see com.arsdigita.bebop.page.BebopApplicationServlet}
 * 
 * @author pb
 */
public class PermissionsServlet extends BaseApplicationServlet 
                       implements PermissionsConstants{

    private static final Logger s_log = Logger.getLogger(
                                        PermissionsServlet.class.getName());

    public final static String APPLICATION_NAME = "permissions";

    /** URL (pathinfo) -> Page object mapping. Based on it (and the http
     * request url) the doService method to selects a page to display        */
    private final Map m_pages = new HashMap();


    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {

        Page index = buildIndexPage();
        Page single = buildItemPage();

        addPage("/", index);
        addPage("/index", index);
        addPage("/one", single);
        addPage("/grant", single);
        addPage("/denied", buildDeniedPage());

    //  addPage("/", buildAdminIndexPage());     // index page at address ~/ds
    //  addPage("/index.jsp", buildIndexPage()); // index page at address ~/ds

    //  addPage("/log4j", buildLog4jPage());   // Logger Adjuster at addr. ~/ds/log4j
    //  addPage("/config", buildConfigPage()); // config browser @ ~/ds/config
        // cache table browser @ ~/ds/cache-table
    //  addPage("/cache-table", buildCacheTablePage());

        // XXXX!!
        // QueryLog is a class of its own in webdevsupport, based upon
        // dispatcher.Disp and prints out all queries in a request
        //  put("query-log",  new QueryLog());

  //    addPage("/request-info",  buildRequestInfoPage());
  //    addPage("/query-info",  buildQueryInfoPage());
  //    addPage("/query-plan",  buildQueryPlanPage());

    }


    /**
     * Central service method, checks for required permission, determines the
     * requested page and passes the page object to PresentationManager.
     */
    public final void doService(HttpServletRequest sreq,
                                HttpServletResponse sresp,
                                Application app)
                      throws ServletException, IOException {

        
        
        // /////// Some preparational steps                     ///////////////

        /* Determine access privilege: only logged in users may access DS   */
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            throw new LoginSignal(sreq);
        }
        /* Determine access privilege: Admin privileges must be granted     */
        PermissionDescriptor admin = new PermissionDescriptor
            (PrivilegeDescriptor.ADMIN, app, party);
        if (!PermissionService.checkPermission(admin)) {
            throw new AccessDeniedException("User is not an administrator");
        }
        /* Want admin to always show the latest stuff...                     */
        DispatcherHelper.cacheDisable(sresp);


        // /////// Everything OK here - DO IT                   ///////////////

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

        final Page page = (Page) m_pages.get(pathInfo);

        if (page != null) {

            final Document doc = page.buildDocument(sreq, sresp);

            PresentationManager pm = Templating.getPresentationManager();
            pm.servePage(doc, sreq, sresp);

        } else {

            sresp.sendError(404, "No such page for path " + pathInfo);

        }
        
    }

    /**
     * Adds one Url-Page mapping to the internal mapping table.
     * 
     * @param pathInfo url stub for a page to display
     * @param page Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }



    private Page buildIndexPage() {
        Label title = new Label(PERMISSIONS_INDEX);
        title.setClassAttr("heading");

        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       title);
        p.addRequestListener(new UserAuthenticationListener());
        p.add(new IndexPanel());
        p.lock();
        return p;
    }

    private Page buildDeniedPage() {
        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       new Label(PAGE_DENIED_TITLE));
        Label label = new Label();
        label.setClassAttr("AccessDenied");
        p.add(label);
        p.lock();
        return p;
    }

    private Page buildItemPage() {
        PermissionsPane pane = new PermissionsPane();
        
        Page p = PageFactory.buildPage(APPLICATION_NAME,
                                       pane.getTitle());
        p.addRequestListener(new UserAuthenticationListener());
        p.add(pane);
        p.lock();
        return p;
    }

}
