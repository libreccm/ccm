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
package com.arsdigita.kernel;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.Web;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides static methods for accessing kernel-specific data.
 *
 * $Id: KernelHelper.java 699 2005-08-12 12:35:18Z sskracic $
 */
public class KernelHelper {

    /**
     * Extracts the KernelRequestContext from the given request.
     *
     * @return the KernelRequestContext.
     *
     * @throws IllegalStateException if the current request context does not
     * subclass KernelRequestContext.
     **/
    public static KernelRequestContext getKernelRequestContext
        (HttpServletRequest req) {
        RequestContext rctx = DispatcherHelper.getRequestContext(req);
        try {
            return (KernelRequestContext)rctx;
        } catch (ClassCastException e) {
            throw new IllegalStateException
                ("Request context does not subclass KernelRequestContext: "
                 + rctx.getClass().getName());
        }
    }

    /**
     * Returns the current HTTP request.
     *
     * @return the current HTTP request.
     *
     * @deprecated Use {@link DispatcherHelper#getRequest()}.
     **/
    public static HttpServletRequest getRequest() {
        HttpServletRequest r = DispatcherHelper.getRequest();
        if (r == null) {
            throw new IllegalStateException("Request is not set");
        }
        return r;
    }

    /**
     * Returns the name or IP address of the server, for example
     * "www.redhat.com".  This may be different from the host name
     * the server runs on.  It is intended for use in constructing
     * URLs that refer back to the server but must be constructed
     * outside of an HTTP request context where this information is
     * known explicitly.
     *
     * <p>The value of serverName is controlled by enterprise.init:
     * <pre>
     * init com.arsdigita.kernel.Initializer {
     *     hostName   = "prd001.redhat.com";
     *     serverName = "www.redhat.com";
     *     serverPort = "80";
     * }
     * </pre>
     *
     * <p>If serverName is not defined in enterprise.init, this method
     * will return hostName.   If hostName is not defined it will
     * return null.
     *
     * @version $Id: KernelHelper.java 699 2005-08-12 12:35:18Z sskracic $
     * @deprecated This method now delegates to WebConfig.  Use {@link
     * com.arsdigita.web.WebConfig#getServer()} and the object it returns instead of this method.
     */

    public synchronized static String getServerName() {
        return Web.getConfig().getServer().getName();
    }

    /**
     * Returns the port number the server is running on, or null if
     * not defined.
     *
     * @deprecated This method now delegates to WebConfig.  Use {@link
     * com.arsdigita.web.WebConfig#getServer()} and the object it
     * returns instead of this method.
     */

    public synchronized static String getServerPort() {
        return new Integer(Web.getConfig().getServer().getPort()).toString();
    }

    /**
     * Returns a canonical URL for accessing the server, constructed
     * from the values of {@link #getServerName} and {@link
     * #getServerPort}.  If the server port is not defined or set to
     * the standard HTTP port 80 it will not be included in the
     * URL. If neither the server name nor port are defined, the return
     * value is simply "http://localhost/".
     *
     * @deprecated Use <code>"http://" + Web.getConfig().getServer() +
     * "/"</code> instead.
     */

    public synchronized static String getServerURL() {
        StringBuffer sb = new StringBuffer();
        sb.append("http://");

        String serverName = getServerName();
        if (null == serverName) {
            sb.append("localhost");
        } else {
            sb.append(serverName);
        }

        String serverPort = getServerPort();
        if (serverPort != null && !serverPort.equals("80")) {
            sb.append(':').append(serverPort);
        }

        return sb.toString();
    }


    /**
     * Retrieves the host name for this server (for example, "arsDigita.com").
     * The value is controlled by enterprise.init:
     * <pre>
     * init com.arsdigita.kernel.Initializer {
     * hostName = "redhat.com";
     * siteName = "Red Hat Web Site";
     * }
     * </pre>
     *
     * @return the host name.
     * @deprecated This method will no longer exist in an upcoming
     * release.
     */
    public synchronized static String getHostName() {
        return Web.getConfig().getHost().getName();
    }

    /**
     * Retrieves the site name for this server (for example, "Red Hat Web Site").
     * The value is controlled by enterprise.init:
     * <pre>
     * init com.arsdigita.kernel.Initializer {
     * hostName = "redhat.com";
     * siteName = "Red Hat Web Site";
     * }
     * </pre>
     *
     * @return the site name.
     * @deprecated Use <code>Web.getConfig().getSiteName()</code>
     * instead.
     */
    public synchronized static String getSiteName() {
        return Web.getConfig().getSiteName();
    }

    /**
     *
     *
     * Get the system administrator's email address.  It returns the
     * email address specified in kernel initializer as
     * systemAdministratorEmailAddress.  This method is only to be
     * used to obtain a reply-to address for notifications.  <b>The
     * return value may or may not correspond to an actual user
     * account on the system.</b>.
     *
     * <p>For example, when a user tries to change their password,
     * they receive confirmation via email.  This email must appear to
     * originate from a valid email address on the system.  Ideally it
     * will also correspond to a real person who can be replied to for
     * help.
     *
     * @return email address suitable for reply-to in system notifications
     * @deprecated Use <code>Kernel.getSecurityConfig().getAdminContactEmail()</code>
     */
    public static synchronized String getSystemAdministratorEmailAddress() {
        return Kernel.getSecurityConfig().getAdminContactEmail();
    }

    /**
     * @deprecated Use
     * <code>Kernel.getConfig().getPrimaryUserIdentifier()</code>
     */
    public static synchronized boolean emailIsPrimaryIdentifier() {
        return Kernel.getConfig().getPrimaryUserIdentifier().equals("email");
    }

    /**
     *
     *
     * Fetches the currently logged in user, or null.  This is a
     * convenience wrapper around {@link #getCurrentUser()}.  In the
     * general case, those are preferrable.
     *
     * @param request The HTTP request
     * @return The currently logged-in user, or null if there is none.
     * @throws RuntimeException if the logged-in user doesn't exist in
     * the database
     * @deprecated See getCurrentUser()
     **/
    public static User getCurrentUser(HttpServletRequest request) {
        return getCurrentUser();
    }

    /**
     * Returns the current user.
     *
     * @deprecated Call {@link KernelContext#getParty()} e.g.,
     * Kernel.getContext().getParty().
     */
    public static User getCurrentUser() {
        KernelContext kernelContext = Kernel.getContext();
        if ( kernelContext.getParty() instanceof User ) {
            return (User) kernelContext.getParty();
        } else {
            return null;
        }
    }

    public static Party getCurrentParty() {
        KernelContext kernelContext = Kernel.getContext();
        return kernelContext.getParty();
    }

    public static Party getCurrentEffectiveParty() {
        KernelContext kernelContext = Kernel.getContext();
        return kernelContext.getEffectiveParty();
    }
}
