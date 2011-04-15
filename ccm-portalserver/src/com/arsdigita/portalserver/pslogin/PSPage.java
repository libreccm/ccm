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
package com.arsdigita.portalserver.pslogin;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.security.Initializer;
import com.arsdigita.util.Assert;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.personal.PersonalPortal;

// /////////////////////////////////////////////////////////////////////////////
//
//  Has to be refactored!
//  Uses methods in c.ad.kernel.security.Initializer which have been modified
//  and moved to ui
//
//  CURRENTLY NOT WORKING
//
// /////////////////////////////////////////////////////////////////////////////

/**
 * This Page redirects a user to his personal portal.
 * It sets up that portal if it has not been initialized
 * @author <a href="mailto:bryanche@arsdigita.com">Bryan Che</a>
 **/
public class PSPage extends Page {

    //constructor
    public PSPage() {
        addRequestListener(new PSPageRequestListener());
        lock();
    }
}

class PSPageRequestListener implements RequestListener {

    public void pageRequested(RequestEvent e) {
        User user;
        String sUrl = "";
        Party party = Kernel.getContext().getParty();
        if (party == null) {
        // URL f. login Page neuerdings über web.ui.....
        //  sUrl = Initializer.getURL(Initializer.LOGIN_PAGE_KEY);
        } else {
            try {
                user = User.retrieve(party.getOID());

                // Get the user's personal portal
                PortalSite psite = PersonalPortal.
                    retrievePersonalPortal(user);
                if (psite == null) {
                    psite = setupPersonalPortal(user);
                }

            sUrl = psite.getPrimaryURL();

            } catch ( DataObjectNotFoundException donfe ) {
             // sUrl = Initializer.getURL(Initializer.LOGIN_PAGE_KEY);
            }

        }

        // Redirect to the user's portal (or login)
        try {
            DispatcherHelper.sendRedirect(e.getPageState().getRequest(),
                                          e.getPageState().getResponse(),
                                          sUrl);
        } catch (java.io.IOException ioe) {
            //this method can't throw an exception in this Interface, so ignore
        }
    }

    //borrow the code for setting up a personal portal from
    //com.arsdigita.portalserver.personal.PersonalPortalCreatorDispatcher
    private PortalSite setupPersonalPortal(final User u) {
        final PortalSite[] m_psite = new PortalSite[] { null };

        KernelExcursion rootExcursion = new KernelExcursion() {
                public void excurse() {
                    setParty(Kernel.getSystemParty());

                    PersonalPortal psite = PersonalPortal.
                        createPersonalPortal(u);
              //    Assert.assertNotNull(psite, "workspace");
                    Assert.exists(psite, "workspace");
                    ResourceTypeConfig config = psite.
                        getApplicationType().getConfig();
              //    Assert.assertNotNull(config, "config");
                    Assert.exists(config, "config");

                    config.configureResource(psite);
                    psite.save();
                    PermissionService.grantPermission
                        (new PermissionDescriptor
                         (PrivilegeDescriptor.ADMIN, psite, u));

                    m_psite[0] = psite;
                }};

        rootExcursion.run();

        return m_psite[0];

    }
}
