/*
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
 */

package com.arsdigita.london.util;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;

import com.arsdigita.dispatcher.AccessDeniedException;


import com.arsdigita.web.Application;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;


import com.arsdigita.ui.login.UserAuthenticationListener;

import org.apache.log4j.Logger;

/**
 * A RequestListener that can check to see if a user
 * has or doesn't have a given privilege on the current
 * ApplicationInstance.
 *
 * The user is redirected to ACCESS_DENIED if their is
 * insufficient permission.
 *
 * @param privilegeName a String that represents the privlege name for the
 *                 privilege a user must have to see the page.
 *
 */
public class ApplicationAdminAuthListener 
        extends UserAuthenticationListener 
        implements RequestListener {

    private static final Logger s_log = Logger.getLogger
        (ApplicationAdminAuthListener.class);

    public ApplicationAdminAuthListener() {
        super();
    }


    /**
     * Checks whether the user is logged in.  If not, redirects the client
     * to the login page.
     */
    public void pageRequested(RequestEvent event) {
        super.pageRequested(event);

        PageState state = event.getPageState();

        /* Get the current party */
        Party party = Kernel.getContext().getParty();

        /* Get the current package */
        Application app = (Application)Kernel.getContext().getResource();

        PermissionDescriptor permDescriptor =
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     app,
                                     party);
        
        if (!PermissionService.checkPermission(permDescriptor)) {
            throw new AccessDeniedException("not an administrator for this application");
        }
    }
}
