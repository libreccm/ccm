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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.web.Web;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.util.UncheckedWrapperException;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

/**
 * An ActionListener that logs out the user.
 *
 * @author Sameer Ajmani
 **/
public class UserLogoutListener implements ActionListener {

    private static final Logger s_log =
        Logger.getLogger(UserLogoutListener.class);

    /**
     * Logs out the user.
     **/
    public void actionPerformed(ActionEvent event) {
        try {
            s_log.debug("Logging out user");
            UserContext uc = Web.getUserContext();
            uc.logout();
        } catch (LoginException e) {
            throw new UncheckedWrapperException("Logout failed", e);
        }
    }
}
