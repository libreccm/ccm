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
package com.arsdigita.util.servlet;

import com.arsdigita.util.Assert;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Static convenience methods for dealing with cookies.
 *
 * <a href="http://wp.netscape.com/newsref/std/cookie_spec.html">http://wp.netscape.com/newsref/std/cookie_spec.html</a>
 *
 * <a href="http://www.faqs.org/rfcs/rfc2109.html">http://www.faqs.org/rfcs/rfc2109.html</a>
 *
 * @see javax.servlet.http.HttpServletRequest#getCookies()
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Cookies.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Cookies {

    private static final Logger s_log = Logger.getLogger(Cookies.class);

    private Cookies() {
	// Empty
    }

    /**
     * Gets the cookie whose name is <code>name</code> from the
     * request.  If there is no such cookie, we return null.
     *
     * @param sreq The request from which to fetch the cookie
     * @param name The <code>String</code> key to use to look up the
     * cookie
     * @return The <code>Cookie</code> whose name is <code>name</code>
     * or null if there is no such cookie
     */
    public static final Cookie get(final HttpServletRequest sreq,
				   final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sreq, HttpServletRequest.class);
            Assert.exists(name, String.class);
        }

        final Cookie[] cookies = sreq.getCookies();

        if (cookies == null) {
            return null;
        } else {
            for (int i = 0; i < cookies.length; i++) {
                final Cookie cookie = cookies[i];

                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }

            return null;
        }
    }

    /**
     * Gets the <code>String</code> value of a cookie with
     * <code>name</code> from the request object <code>sreq</code>.
     * If there is no such cookie, <code>null</code> is returned.
     *
     * @param sreq The request object
     * @param name The <code>String</code> name of the cookie to look
     * for
     * @return The <code>String</code> value of the cookie or
     * <code>null</code> if it is not present on <code>sreq</code>
     */
    public static final String getValue(final HttpServletRequest sreq,
					final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sreq, HttpServletRequest.class);
            Assert.exists(name, String.class);
        }

	final Cookie cookie = Cookies.get(sreq, name);

	if (cookie == null) {
	    return null;
	} else {
	    return cookie.getValue();
	}
    }

    /**
     * Sets a cookie on the response with the given expiration time.
     * This method generates a cookie with the defaults of {@link
     * javax.servlet.http.Cookie#Cookie(String, String)}.  The cookie
     * object is returned to allow custom settings.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to set
     * @param value The <code>String</code> value of the cookie to set
     * @return The <code>Cookie</code> instance created and set by
     * this method
     */
    public static final Cookie set(final HttpServletResponse sresp,
				   final String name,
				   final String value,
				   final int expiry) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
            Assert.exists(value, String.class);
        }

        final Cookie cookie = Cookies.set(sresp, name, value);

        cookie.setMaxAge(expiry);

        return cookie;
    }

    /**
     * Sets a session cookie on the response.  This method generates a
     * cookie with the defaults of {@link
     * javax.servlet.http.Cookie#Cookie(String, String)}.  The cookie
     * object is returned to allow custom settings.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to set
     * @param value The <code>String</code> value of the cookie to set
     * @return The <code>Cookie</code> instance created and set by
     * this method
     */
    public static final Cookie set(final HttpServletResponse sresp,
				   final String name,
				   final String value) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
            Assert.exists(value, String.class);
        }

        final Cookie cookie = new Cookie(name, value);

	// XXX Deal with the case where it's already been added.

        sresp.addCookie(cookie);

        return cookie;
    }

    /**
     * Deletes the named cookie by setting its maximum age to 0.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to
     * delete
     */
    public static final void delete(final HttpServletResponse sresp,
				    final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
        }

        Cookies.set(sresp, name, "", 0);
    }
}
