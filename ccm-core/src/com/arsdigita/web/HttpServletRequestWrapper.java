/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import java.util.Enumeration;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

public class HttpServletRequestWrapper extends ServletRequestWrapper
        implements HttpServletRequest {
    private final HttpServletRequest m_req;

    public HttpServletRequestWrapper(HttpServletRequest req) {
        super(req);

        m_req = req;
    }

    public String getAuthType() {
        return m_req.getAuthType();
    }

    public String getContextPath() {
        return m_req.getContextPath();
    }

    public Cookie[] getCookies() {
        return m_req.getCookies();
    }

    public long getDateHeader(String name) {
        return m_req.getDateHeader(name);
    }

    public String getHeader(String name) {
        return m_req.getHeader(name);
    }

    public Enumeration getHeaderNames() {
        return m_req.getHeaderNames();
    }

    public Enumeration getHeaders(String name) {
        return m_req.getHeaders(name);
    }

    public int getIntHeader(String name) {
        return m_req.getIntHeader(name);
    }

    public String getMethod() {
        return m_req.getMethod();
    }

    public String getPathInfo() {
        return m_req.getPathInfo();
    }

    public String getPathTranslated() {
        return m_req.getPathTranslated();
    }

    public String getQueryString() {
        return m_req.getQueryString();
    }

    public String getRemoteUser() {
        return m_req.getRemoteUser();
    }

    public String getRequestedSessionId() {
        return m_req.getRequestedSessionId();
    }

    public String getRequestURI() {
        return m_req.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException
            ("This is a Servlet 2.3 feature that we do not currently support");
    }

    public String getServletPath() {
        return m_req.getServletPath();
    }

    public HttpSession getSession() {
        return m_req.getSession();
    }

    public HttpSession getSession(boolean create) {
        return m_req.getSession(create);
    }

    public Principal getUserPrincipal() {
        return m_req.getUserPrincipal();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return m_req.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return m_req.isRequestedSessionIdFromUrl();
    }

    public boolean isRequestedSessionIdFromURL() {
        return m_req.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdValid() {
        return m_req.isRequestedSessionIdValid();
    }

    public boolean isUserInRole(String role) {
        return m_req.isUserInRole(role);
    }
}
