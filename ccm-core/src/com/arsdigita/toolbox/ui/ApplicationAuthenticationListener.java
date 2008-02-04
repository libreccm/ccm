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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;

import com.arsdigita.dispatcher.AccessDeniedException;


import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.web.LoginSignal;


import org.apache.log4j.Logger;

/**
 * <p>
 * A request listener that checks a privilege against
 * the current resource as accessed from Kernel.getContext().getResource()
 * For example, to restrict a page to admin users only:
 * </p>
 * <pre>
 *  Page page = PageFactory.buildPage("myapp", "My Admin Page");
 *  page.addRequestListener(new ApplicationAuthenticationListener());
 *  page.lock();
 * </pre>
 */
public class ApplicationAuthenticationListener implements RequestListener {

    private static final Logger s_log = Logger.getLogger
        (ApplicationAuthenticationListener.class);

    private PrivilegeDescriptor m_privilege;

    /**
     * Creates a listener that checks for ADMIN privilege
     */
    public ApplicationAuthenticationListener() {
        this(PrivilegeDescriptor.ADMIN);
    }
    
    /**
     * Creates a listener that checks for an arbitrary
     * privilege
     * @param privilege the privilege to check for
     */
    public ApplicationAuthenticationListener(PrivilegeDescriptor privilege) {
        m_privilege = privilege;
    }

    /**
     * Checks whether the user is logged in.  If not, redirects the client
     * to the login page.
     */
    public void pageRequested(RequestEvent event) {
        Party party = Kernel.getContext().getParty();

        Resource resource = Kernel.getContext().getResource();

        PermissionDescriptor permDescriptor =
            new PermissionDescriptor(m_privilege,
                                     resource,
                                     party);
        
        if (!PermissionService.checkPermission(permDescriptor)) {
            if (party == null) {
                throw new LoginSignal(event.getPageState().getRequest());
            }
            throw new AccessDeniedException(
                "user " + party.getOID() + " doesn't have the " + 
                m_privilege.getName() + " privilege on " + 
                resource.getOID());
        }
    }
}
