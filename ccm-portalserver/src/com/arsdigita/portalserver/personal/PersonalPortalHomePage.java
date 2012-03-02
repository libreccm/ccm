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
package com.arsdigita.portalserver.personal;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.ui.PortalHomePage;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.Container;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortalHomePage.java#8 $
 */
public class PersonalPortalHomePage extends PortalHomePage {

    /**
     * Constructor
     */
    public PersonalPortalHomePage() {
        super();
    }

    private final RequestLocal m_adminPermission = new RequestLocal() {
            @Override
            protected Object initialValue(PageState state) {
                UniversalPermissionDescriptor descriptor =
                    new UniversalPermissionDescriptor
                    (PrivilegeDescriptor.ADMIN,
                     Kernel.getContext().getParty());

                if (PermissionService.checkPermission(descriptor)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        };

    @Override
    protected void buildHeader(Container header) {
        // Add a link to edit profile.
        Link profileLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.personal.edit_your_profile")),  "/register/edit-profile");
        profileLink.setClassAttr("portalControlProfileLink");
        header.add(profileLink);

        // And to change passwords.
        Link passwordLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.personal.change_your_password")),  "/register/change-password");
        passwordLink.setClassAttr("portalControlProfileLink");
        header.add(passwordLink);

        // If the user is an administrator, offer her a link to
        // the Portal admin page.

        Link adminLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.personal.site_administration")),  "/portal-admin/") {
                @Override
                public boolean isVisible(PageState state) {
                    Boolean bool = (Boolean) m_adminPermission.get(state);
                    return bool.booleanValue();
                }
            };

        adminLink.setClassAttr("portalControlProfileLink");
        header.add(adminLink);

        super.buildHeader(header);
    }

    @Override
    protected void buildTitle() {
        class TitlePrintListener implements PrintListener {
            public void prepare(PrintEvent e) {
                PageState pageState = e.getPageState();

                PortalSite psite = PortalSite.getCurrentPortalSite
                    (pageState.getRequest());

            //  Assert.assertTrue(psite instanceof PersonalPortal);
                Assert.isTrue(psite instanceof PersonalPortal);

                PersonalPortal pp = (PersonalPortal) psite;

                User user = pp.getOwningUser();

                String givenName = user.getPersonName().getGivenName();

                Label target = (Label) e.getTarget();

                if (givenName.endsWith("s")) {
                    target.setLabel(givenName + "' " + pp.getTitle());
                } else {
                    target.setLabel(givenName + "'s " + pp.getTitle());
                }

                // That was notably english only.
            }
        }

        setTitle(new Label(new TitlePrintListener()));
    }

    @Override
    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.add(new Label(GlobalizationUtil.globalize("cw.workspace.personal.personal_workspace")));
        navbar.setClassAttr("portalNavbar");

        getHeader().add(navbar);
    }
}
