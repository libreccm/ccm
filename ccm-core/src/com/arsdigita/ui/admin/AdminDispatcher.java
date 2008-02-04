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


import com.arsdigita.ui.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Dispatcher for Admin functionality
 *
 * @author David Dao 
 * @author Ron Henderson 
 * @version $Id: AdminDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class AdminDispatcher extends SubsiteDispatcher
    implements AdminConstants
{
    /**
     * Constructor.  Instantiates the subsite url/page mapping.
     */

    public AdminDispatcher() {
        addPage("", buildAdminIndexPage(), true);
        addPage("denied", buildDeniedPage());
    }

    /**
     * Generic "Access Denied" page for the admin section.  This can
     * be customized by editing the admin stylesheet.
     */
    private Page buildDeniedPage() {
        Page p = PageFactory.buildPage("admin", new Label(new GlobalizedMessage
                                                          ("ui.admin.dispatcher.accessDenied", BUNDLE_NAME)));

        Label label = new Label(GlobalizationUtil.globalize("ui.admin.access_denied"));
        label.setClassAttr("AccessDenied");
        p.add(label);

        p.lock();

        return p;
    }

    /**
     * Index page for the admin section
     */
    private Page buildAdminIndexPage() {

        Page p = PageFactory.buildPage("admin", PAGE_TITLE_LABEL);

        p.addGlobalStateParam(USER_ID_PARAM);
        p.addGlobalStateParam(GROUP_ID_PARAM);

        /**
         * Create User split panel.
         */
        AdminSplitPanel userSplitPanel =
            new AdminSplitPanel(USER_NAVBAR_TITLE);

        UserBrowsePane browsePane = new UserBrowsePane();

        userSplitPanel.addTab(USER_TAB_SUMMARY,
                              new UserSummaryPane(userSplitPanel, browsePane));
        userSplitPanel.addTab(USER_TAB_BROWSE,
                              browsePane);
        userSplitPanel.addTab(USER_TAB_SEARCH,
                              new UserSearchPane(userSplitPanel, browsePane));
        userSplitPanel.addTab(USER_TAB_CREATE_USER,
                              new CreateUserPane(userSplitPanel));

        /**
         * Create main administration tab.
         */
        GroupAdministrationTab groupAdministrationTab = new GroupAdministrationTab();
        TabbedPane tb = new TabbedPane();
        tb.setIdAttr("page-body");

        tb.addTab(USER_TAB_TITLE, userSplitPanel);
        tb.addTab(GROUP_TAB_TITLE, groupAdministrationTab);

        browsePane.setTabbedPane(tb);
        browsePane.setGroupAdministrationTab(groupAdministrationTab);

        p.add(tb);
        p.lock();

        return p;
    }
}
