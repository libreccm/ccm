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
package com.arsdigita.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.log4j.Logger;

/**
 *  Dummy ServletContext object for unit testing of form methods that
 *  include requests in their signatures.
 *
 * @version $Id: DummyServletContext.java 750 2005-09-02 12:38:44Z sskracic $
 */

public class DummyServletContext implements ServletContext {

    private HashMap m_attributes = new HashMap();
    private HashMap m_dispachers = new HashMap();
    private static final Logger s_log = Logger.getLogger(DummyServletContext.class);


    public Object getAttribute(String name) {
        return null;
    }

    public java.util.Enumeration getAttributeNames() {
        return null;
    }

    public ServletContext getContext(String uripath) {
        return null;
    }

    public java.lang.String getInitParameter(String name) {
        return null;
    }

    public java.util.Enumeration getInitParameterNames() {
        return null;
    }

    public int getMajorVersion() {
        return 2;
    }

    public String getMimeType(String file) {
        return null;
    }

    public int getMinorVersion() {
        return 2;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return (RequestDispatcher) m_dispachers.get(name);
    }

    public String getRealPath(String path) {
        String root = System.getProperty("test.webapp.dir");
        if (path.equals("/")) {
            return root;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return root + path;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public java.net.URL getResource(String path) {
        return null;
    }

    public java.io.InputStream getResourceAsStream(String name) {
        final String path = getRealPath(name);

        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            s_log.error("Couldn't get file stream for resource " + name + " at path " + path, e);
            return null;
        }
    }

    public String getServerInfo() {
        return "Bogus Server/1.0";
    }

    public Servlet getServlet(String name) {
        return null;
    }

    public java.util.Enumeration getServletNames() {
        return null;
    }

    public java.util.Enumeration getServlets() {
        return null;
    }

    public void log(Exception exception, String msg) {
        return;
    }

    public void log(String msg) {
        return;
    }

    public void log(String message, Throwable throwable) {
        return;
    }

    public void removeAttribute(String name) {
        return;
    }

    public void setAttribute(String name, Object object) {
        return;
    }

    public Set getResourcePaths(String path) {
        return null;
    }

    public String getServletContextName() {
        return null;
    }

    public void addDispacher(String name, final Servlet servlet) {
        RequestDispatcher rd = new RequestDispatcher() {
            public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                servlet.service(servletRequest, servletResponse);
            }

            public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                throw new UnsupportedOperationException("Not yet supported");
            }
        };

        m_dispachers.put(name, rd);
    }

}
