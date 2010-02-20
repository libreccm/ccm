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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.util.ServletUtils;

import java.net.URLConnection;
import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Static methods that can be used to include a password with
 * requests to the local host.  These methods can be used to increase
 * the security of the requests to the local host that publishToFile
 * makes to get the html pages to publish.  Method
 * setLocalRequestPassword sets the password (cookie) before making
 * the request to the localhost.  Code that processes the request
 * would use method validLocalRequest to check for the cookie
 * (password) value.
 *
 * @author Jeff Teeters
 *
 * @version $Revision: #12 $ $DateTime: 2004/08/17 23:15:09 $
 * @ version $Id
 */
public class LocalRequestPassword {

    private static Logger s_log =
        Logger.getLogger(LocalRequestPassword.class);

    // Password used to ensure that requests from local server are from the server
    public static final String LOCAL_REQUEST_PASSWORD_NAME = "localrequestpassword";
    public static String m_localRequestPassword = null;


    /**
     * Get a password for use in making a cookie used for server requests to itself.
     * This can be used to increase the security of the system because it allows for a check
     * to make sure the request is from the server.
     */
     static private String getLocalRequestPassword() {
         if (m_localRequestPassword == null) {
             // not set, initialize to random number
             byte[] secret = new byte[16];
             (new SecureRandom()).nextBytes(secret);
             Base64 encoder = new Base64();
             m_localRequestPassword = new String(encoder.encode(secret));
             if (s_log.isDebugEnabled()) {
                 s_log.debug
                     ("localRequestPassword is " + m_localRequestPassword);
             }
         }
         return m_localRequestPassword;
     }


    /***
     * Add a cookie to a connection, preserving any previous cookies.
     * @param con connection that the cookie is added to.
     ***/
    static public void addCookie(URLConnection con, String key, String value) {
        final String COOKIE = "cookie";
        String addition = key + "=" + value;
        String cookie = con.getRequestProperty(COOKIE);
        if (cookie != null)
            cookie = addition + "; " + cookie;
        else
            cookie = addition;
        con.setRequestProperty(COOKIE, cookie);
    }


    /***
     * Set a cookie with the password for a local request.
     * @param con connection that the cookie is added to.
     ***/
    static public void setLocalRequestPassword(URLConnection con) {
        addCookie(con, LOCAL_REQUEST_PASSWORD_NAME, getLocalRequestPassword());
    }


    /***
     * Return true if passed in request contains a valid localRequestPassword.
     * @param request request being processed, that should contain cookie
     ***/
    static public boolean validLocalRequest(HttpServletRequest request) {
        String foundPassword;
        foundPassword = ServletUtils.getCookieValue ( request,
                                         LOCAL_REQUEST_PASSWORD_NAME);
        return getLocalRequestPassword().equals(foundPassword);
    }
}
