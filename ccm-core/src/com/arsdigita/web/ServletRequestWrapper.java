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
import java.util.Locale;
import java.util.Map;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


public class ServletRequestWrapper implements ServletRequest {
    private final ServletRequest m_req;

    public ServletRequestWrapper(ServletRequest req) {
        m_req = req;
    }

    public Object getAttribute(String name) {
        return m_req.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return m_req.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return m_req.getCharacterEncoding();
    }

    public int getContentLength() {
        return m_req.getContentLength();
    }

    public String getContentType() {
        return m_req.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return m_req.getInputStream();
    }

    public Locale getLocale() {
        return m_req.getLocale();
    }

    public Enumeration getLocales() {
        return m_req.getLocales();
    }

    public String getParameter(String name) {
        return m_req.getParameter(name);
    }

    public Map getParameterMap() {
        throw new UnsupportedOperationException
            ("This is a Servlet 2.3 feature that we do not currently support");
    }

    public Enumeration getParameterNames() {
        return m_req.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return m_req.getParameterValues(name);
    }

    public String getProtocol() {
        return m_req.getProtocol();
    }

    public BufferedReader getReader() throws IOException {
        return m_req.getReader();
    }

    public String getRealPath(String path) {
        return m_req.getRealPath(path);
    }

    public String getRemoteAddr() {
        return m_req.getRemoteAddr();
    }

    public String getRemoteHost() {
        return m_req.getRemoteHost();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return m_req.getRequestDispatcher(path);
    }

    public String getScheme() {
        return m_req.getScheme();
    }

    public String getServerName() {
        return m_req.getServerName();
    }

    public int getServerPort() {
        return m_req.getServerPort();
    }

    public boolean isSecure() {
        return m_req.isSecure();
    }

    public void removeAttribute(String name) {
        m_req.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        m_req.setAttribute(name, value);
    }

    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        throw new UnsupportedOperationException
            ("This is a Servlet 2.3 feature that we do not currently support");
    }

	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}
}
