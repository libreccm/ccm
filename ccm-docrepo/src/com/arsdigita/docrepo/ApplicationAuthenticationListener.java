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
package com.arsdigita.docrepo;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.ui.login.UserAuthenticationListener;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

/**
 * A RequestListener that verifies the user
 * has a given privilege on the current Application.
 *
 * The user is redirected to ACCESS_DENIED if their is
 * insufficient permission.
 *
 * XXX Permissions will be incorporated in December. This is
 *     temporary for use in our engineering production server until.
 *     that time.
 *
 * @version $Id: ApplicationAuthenticationListener.java  pboy $
 */
public class ApplicationAuthenticationListener
        extends UserAuthenticationListener implements RequestListener {

    private static final Logger s_log = Logger.getLogger
                                        (ApplicationAuthenticationListener.class);

    private String m_privilegeName;

    /**
     * Constructs listener
     *
     *  @param privilegeName a String that represents the privlege name for the
     *                 privilege a user must have to see the page.
     *
     */
    public ApplicationAuthenticationListener(String privilegeName) {
        super();
        m_privilegeName = privilegeName;
    }

    public void setRequiredPrivilege(String privilegeName) {
        m_privilegeName = privilegeName;
    }

    public String getRequiredPrivilege() {
        return m_privilegeName;
    }

    /**
     * Checks whether the user is logged in.  If not, redirects the client
     * to the login page.
     **/
    @Override
    public void pageRequested(RequestEvent event) {
        PageState state = event.getPageState();

        PrivilegeDescriptor privDescriptor =
            PrivilegeDescriptor.get(m_privilegeName);

        PermissionDescriptor permDescriptor = new PermissionDescriptor
                                              (privDescriptor,
                                               Web.getContext().getApplication(),
                                               Kernel.getContext().getParty());

        if (!PermissionService.checkPermission(permDescriptor)) {
            denyRequest(state);
        }

    }

    /**
     * Action performed if authentication failed.  Override this to perform
     * a specific action after the authentication check.
     */
    public void denyRequest(PageState state) {
        
        if (Kernel.getContext().getParty() == null) {
            Util.redirectToLoginPage(state);
            return;
        }

        throw new AccessDeniedException();
    }
}
