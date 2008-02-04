/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.bookmarks;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;

import com.arsdigita.dispatcher.AccessDeniedException;


import com.arsdigita.web.Application;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;

import com.arsdigita.ui.login.UserAuthenticationListener;

import java.io.IOException;
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
 * @param privilegeName a String that represents the privlege name for the
 *                 privilege a user must have to see the page.
 *
 */
public class ApplicationAuthenticationListener
        extends UserAuthenticationListener implements RequestListener {
    public static final String versionId =
        "$Id: //apps/bookmarks/dev/src/com/arsdigita/bookmarks/ApplicationAuthenticationListener.java#2 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/04/01 16:53:21 $";

    private static final Logger s_log = Logger.getLogger
        (ApplicationAuthenticationListener.class);

    private String m_privilegeName;

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
    public void pageRequested(RequestEvent event) {
        PageState state = event.getPageState();

        PrivilegeDescriptor privDescriptor =
            PrivilegeDescriptor.get(m_privilegeName);

        PermissionDescriptor permDescriptor = new PermissionDescriptor
            (privDescriptor,
             Application.getCurrentApplication(state.getRequest()),
             Kernel.getContext().getParty());

        if (!PermissionService.checkPermission(permDescriptor)) {
            denyRequest(state);
        }

    }

    /**
     * Action performed if authentication failed.  Override this to
     * perform a perform a specific action after the authentication
     * check.
     */
    public void denyRequest(PageState state) {
        if (Kernel.getContext().getParty() == null) {
            UserContext.redirectToLoginPage(state.getRequest());
            return;
        }

        throw new AccessDeniedException();
    }
}
