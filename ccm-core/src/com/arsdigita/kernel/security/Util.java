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

import com.arsdigita.util.Classes;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility package of static security methods
 *
 * @author Christian
 *            Brechb&uuml;hler
 */
public class Util {
    private static SecurityHelper s_helper = null;

    /** This class needs not to be instantiated. */
    private Util() {}

    /**
     * Returns the security helper instance.
     *
     * @return the security helper instance.
     *
     **/
    public static SecurityHelper getSecurityHelper() {
        if (s_helper == null) {
            s_helper = (DefaultSecurityHelper) Classes.newInstance
                (DefaultSecurityHelper.class);
        }
        return s_helper;
    }

    /**
     * Set the security helper instance.
     * package local
     */
    static void setSecurityHelper(Object helper) {
        s_helper = (SecurityHelper)helper;
    }

    /**
       Get a using a callback.

       @return an HttpServletRequest
       @throws LoginException if an error occurs.
    */
    static HttpServletRequest getRequest(CallbackHandler handler)
        throws LoginException {

        try {
            HTTPRequestCallback cb = new HTTPRequestCallback();
            handler.handle(new Callback[] { cb });
            return cb.getRequest();
        } catch (IOException e) {
            throw new KernelLoginException("IO error getting HTTP request", e);
        } catch (UnsupportedCallbackException e) {
            throw new KernelLoginException("Error getting HTTP request", e);
        }
    }
}
