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
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.web.Application;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalserver.personal.PersonalPortal;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <p>ToolPage class</p>
 *
 * <p>This is a common page to be used by all applications for
 * consistent styling.</p>
 *
 * @author <a href="mailto:teadams@arsdigita.com">Tracy Adams</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 * @version $Id: ToolPage.java  pboy $
 */

/* XXX Have to control links with permissions and
 *      add access control
 */
public class ToolPage extends PortalPage {

    private static final Logger s_log = Logger.getLogger(ToolPage.class);

    /*
     * There are 2 views: user and admin.
     * m_view determines which context bar and
     * view link to show.
     */
    private String m_view;

    private Link m_viewLink;

    public ToolPage() {
        this(null);
    }

    /**
     * @param view A String that specifies which application view to
     * show.  Valid values: "user", "admin" and "null".  If view is
     * "null", there will be no right-hand navigation link.  Note -
     * We've decided not to have the right-hand navigation link at
     * all. Instead, you should create tabs.  So, once the
     * applications are migrated, view will always be null and we can
     * remove view altogether.
     */
    public ToolPage(String view) {
        super();

        m_view = view;
    }

    @Override
    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");

        navbar.add(new LoggedInLinkWrapper(new PersonalPortalLinkPrinter()));

        // This link will not show up if the current workspace is a
        // personal portal.
        Link current = new Link(new CurrentPortalLinkPrinter()) {
            @Override
                public boolean isVisible(PageState state) {
                    PortalSite psite = PortalSite.getCurrentPortalSite
                        (state.getRequest());

                    return !(psite instanceof PersonalPortal);
                }
            };
        navbar.add(current);

        if (m_view != null && m_view.equals("admin")) {
            navbar.add(new Link(new CurrentApplicationLinkPrinter()) {
                    @Override
                    public boolean isVisible(PageState ps) {
                        return userIsAdmin(ps);
                    }});
            navbar.add(new Label(GlobalizationUtil.globalize(
                           "cw.workspace.administration")) {
                    @Override
                    public boolean isVisible(PageState ps) {
                        return userIsAdmin(ps);
                    }});
        } else {
            navbar.add(new Label(new CurrentApplicationLabelPrinter()));
        }

        getHeader().add(navbar);
    }

    @Override
    protected void buildTitle() {
        class ApplicationAdminLabelPrinter implements PrintListener {
            public void prepare(PrintEvent e) {
                Label targetLabel = (Label)e.getTarget();
                PageState pageState = e.getPageState();

                Application application = Application.getCurrentApplication
                    (pageState.getRequest());

                // Assert.assertNotNull(application, "application");
                Assert.exists(application, "application");

                targetLabel.setLabel
                    (application.getTitle() + " Administration");
            }
        }

        if (m_view != null && m_view.equals("admin")) {
            setTitle(new Label(new ApplicationAdminLabelPrinter()));
        } else {
            setTitle(new Label(new CurrentApplicationLabelPrinter()));
        }
    }

    @Override
    protected void buildHeader(Container header) {
        if (m_view != null) {
            if (m_view.equals("user")) {
                m_viewLink = new Link
                    ("Administration view", "./admin/index.jsp") {
                        @Override
                        public boolean isVisible(PageState ps) {
                            return userIsAdmin(ps);
                        }};
            } else if (m_view.equals("admin")) {
                m_viewLink = new Link( new Label(GlobalizationUtil.globalize(
                                 "cw.workspace.user_view")),  "../index.jsp");
            }
        }

        if (m_viewLink != null) {
            m_viewLink.setClassAttr("portalControl");

            header.add(m_viewLink);
        }
    }

    private boolean userIsAdmin(PageState ps) {
        PermissionDescriptor permDescriptor =
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     Application.getCurrentApplication(ps.getRequest()),
                                     Kernel.getContext().getParty());
        return PermissionService.checkPermission(permDescriptor);
    }
}
