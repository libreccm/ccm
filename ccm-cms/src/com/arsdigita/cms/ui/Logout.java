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
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.KernelRequestContext;
import com.arsdigita.kernel.security.UserContext;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;


/**
 * A page that logs the current user out
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: Logout.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Logout extends CMSPage {

    final static String URL_AFTER_LOGOUT = Utilities.getWebappContext() + "/";

    private static final Logger s_log = Logger.getLogger(Logout.class);
    public Logout() {
        super();
    }

    public void process(PageState state) throws ServletException {

        try {
            KernelRequestContext kctx = (KernelRequestContext)
                DispatcherHelper.getRequestContext(state.getRequest());
            UserContext uctx = kctx.getUserContext();
            uctx.logout();

        } catch (LoginException e) {
            s_log.error("Error logging out", e);
            throw new ServletException( (String) GlobalizationUtil.globalize("cms.ui.logout_failed").localize(),  e);
        }

        try {
            state.getResponse().sendRedirect(URL_AFTER_LOGOUT);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
