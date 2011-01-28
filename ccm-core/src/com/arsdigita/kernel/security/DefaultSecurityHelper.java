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
package com.arsdigita.kernel.security;

import javax.servlet.http.HttpServletRequest;

/**
 *  Default implementation of
 * SecurityHelper interface.
 *
 * @author Sameer Ajmani
 * @see SecurityHelper
 * @version $Id: DefaultSecurityHelper.java 709 2005-08-17 10:03:23Z apevec $
 */
public class DefaultSecurityHelper implements SecurityHelper {

    /**
     * Determines whether the request is secure by calling
     * <code>req.isSecure()</code>.
     *
     * @return req.isSecure().
     **/
    public boolean isSecure(HttpServletRequest req) {
        return req.isSecure();
    }

    private static String[] allowedPages = new String[] {
        Initializer.LOGIN_PAGE_KEY,
        Initializer.LOGOUT_PAGE_KEY,
        Initializer.COOKIES_PAGE_KEY,
        Initializer.RECOVER_PAGE_KEY,
        Initializer.EXPIRED_PAGE_KEY,
    };

    /**
     * Determines whether the current request requires that the user be
     * logged in.
     *
     * @return <code>true</code> if the request is secure and the page is
     * not on a list of allowed pages (such as the login page and the
     * bad-password page), <code>false</code> otherwise.
     **/
    public boolean requiresLogin(HttpServletRequest req) {
        // XXX workaround, code below is broken anyway,
        //     it doesn't take into account dispatcher prefix ( /ccm )
        return false;
//        
//        // don't require login if request not secure
//        if (!isSecure(req)) {
//            return false;
//        }
//        // don't require login if page on allowed list
//        String url = req.getRequestURI();
//        for (int i = 0; i < allowedPages.length; i++) {
//            String allowed = Initializer.getFullURL(allowedPages[i], req);
//            if (url.equals(allowed)) {
//                return false;
//            }
//        }
//        // otherwise require login
//        return true;
    }

    /**
     * Returns the full URL of the login page stored in the page map.
     *
     * @return the full URL of the login page.
     **/
    public String getLoginURL(HttpServletRequest req) {
        return Initializer.getFullURL(Initializer.LOGIN_PAGE_KEY, req);
    }
}
