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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.templating.Templating;
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

/**
 * Web Developer Support Application Servlet class, central entry point to create and process the applications UI.
 *
 * We should have subclassed BebopApplicationServlet but couldn't overwrite doService() method to add permission
 * checking. So we use our own page mapping. The general logic is the same as for BebopApplicationServlet.
 *
 * {@see com.arsdigita.bebop.page.BebopApplicationServlet}
 *
 * @author Jens Pelzetter
 * @author pb
 */
public class AdminServlet extends BaseApplicationServlet implements AdminConstants {

    private static final long serialVersionUID = -3912367600768871630L;
    /**
     * Logger instance for debugging
     */
    //private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());
    /**
     * URL (pathinfo) -> Page object mapping. Based on it (and the http request url) the doService method to selects a
     * page to display
     */
    private final Map<String, Page> pages = new HashMap<String, Page>();

    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     *
     * @throws ServletException
     */
    @Override
    public void doInit() throws ServletException {
        addPage("/", buildAdminIndexPage());     // index page at address ~/ds
        //  addPage("/index.jsp", buildIndexPage()); // index page at address ~/ds

    }

    /**
     * Central service method, checks for required permission, determines the requested page and passes the page object
     * to PresentationManager.
     * 
     * @param sreq
     * @param sresp
     * @param app
     * @throws ServletException
     * @throws IOException  
     */
    public final void doService(final HttpServletRequest sreq,
                                final HttpServletResponse sresp,
                                final Application app) throws ServletException, IOException {
        // ///////    Some preparational steps                   ///////////////
        /* Determine access privilege: only logged in users may access DS   */
        final Party party = Kernel.getContext().getParty();
        if (party == null) {
            throw new LoginSignal(sreq);
        }
        /* Determine access privilege: Admin privileges must be granted     */
        final PermissionDescriptor admin = new PermissionDescriptor(PrivilegeDescriptor.ADMIN, app, party);
        if (!PermissionService.checkPermission(admin)) {
            throw new AccessDeniedException("User is not an administrator");
        }
        /* Want admin to always show the latest stuff...                     */
        DispatcherHelper.cacheDisable(sresp);


        // ///////   Everything OK here - DO IT                  ///////////////
        String pathInfo = sreq.getPathInfo();
        Assert.exists(pathInfo, "String pathInfo");
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }

        final Page page = pages.get(pathInfo);
        if (page == null) {
            sresp.sendError(404, "No such page for path " + pathInfo);
        } else {
            final Document doc = page.buildDocument(sreq, sresp);
            Templating.getPresentationManager().servePage(doc, sreq, sresp);
        }
    }

    /**
     * Adds one pair of Url - Page to the internal hash map, used as a cache.
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
        Assert.isTrue(pathInfo.charAt(0) == '/', "path starts not with '/'");

        pages.put(pathInfo, page);
    }

    /**
     * Index page for the admin section
     */
    private Page buildAdminIndexPage() {

        final Page page = PageFactory.buildPage("admin", PAGE_TITLE_LABEL);
        page.addGlobalStateParam(USER_ID_PARAM);
        page.addGlobalStateParam(GROUP_ID_PARAM);
        page.addGlobalStateParam(APPLICATIONS_ID_PARAM);

        /* 
         * Create User split panel. 
         * Note: Will change soon. 
         */
        //final AdminSplitPanel userSplitPanel = new AdminSplitPanel(USER_NAVBAR_TITLE);

//        final UserBrowsePane browsePane = new UserBrowsePane();
//        userSplitPanel.addTab(USER_TAB_SUMMARY, new UserSummaryPane(userSplitPanel, browsePane));
//        userSplitPanel.addTab(USER_TAB_BROWSE, browsePane);
//        userSplitPanel.addTab(USER_TAB_SEARCH, new UserSearchPane(userSplitPanel, browsePane));
//        userSplitPanel.addTab(USER_TAB_CREATE_USER, new CreateUserPane(userSplitPanel));
        
        // Create the Admin's page tab bar
        final TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setIdAttr("page-body");

        /**
         * Create and add info tab
         */
        //tabbedPane.addTab(INFO_TAB_TITLE, new AdminInfoTab());        
        /*
         * Create and add the user and group tabs.
         */
        //tabbedPane.addTab(USER_TAB_TITLE, userSplitPanel);
        final GroupAdministrationTab groupAdminTab = new GroupAdministrationTab();
        tabbedPane.addTab(USER_TAB_TITLE, new UserAdministrationTab(tabbedPane, groupAdminTab));        
        tabbedPane.addTab(GROUP_TAB_TITLE, groupAdminTab);      
        
        /*
         * Create application administration panel
         */
        tabbedPane.addTab(APPLICATIONS_TAB_TITLE, new ApplicationsAdministrationTab());

//        browsePane.setTabbedPane(tabbedPane);
//        browsePane.setGroupAdministrationTab(groupAdminTab);      

        //Add System information tab
        tabbedPane.addTab(SYSINFO_TAB_TITLE, new SystemInformationTab());
        
        page.add(tabbedPane);
        page.lock();

        return page;
    }

}
