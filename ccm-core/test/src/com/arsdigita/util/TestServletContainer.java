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

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.cactus.ServletURL;
import org.apache.log4j.Logger;

/*
* Created by IntelliJ IDEA.
* User: jorris
* Date: Oct 20, 2002
* Time: 4:50:01 PM
* To change this template use Options | File Templates.
*/
public class TestServletContainer {

    private HttpServletDummyRequest m_req;
    private HttpServletDummyResponse m_res;
    private HashMap m_servletMapping = new HashMap();
    private static Logger s_log = Logger.getLogger(TestServletContainer.class);

    public TestServletContainer(HttpServletDummyRequest req, HttpServletDummyResponse res) {
        m_req = req;
        m_res = res;

        m_req.setContainer(this);
        m_res.setContainer(this);
    }

    public void addServletMapping(String name, Servlet servlet) {
        s_log.debug("Adding mapping: " + name);
        m_servletMapping.put(name, servlet);
    }

    public void dispatch(Servlet servlet) throws Exception {
        servlet.service(m_req, m_res);
    }

    public HttpServletDummyRequest getRequest() {
        return m_req;
    }


    public HttpServletDummyResponse getResponse() {
        return m_res;
    }


    RequestDispatcher getDispatcher(final String path) {
        RequestDispatcher rd = null;
        String requestPath = path;

        final int queryIdx = requestPath.indexOf('?');
        if (-1 != queryIdx) {
            requestPath = requestPath.substring(0, queryIdx);
        }

        final Servlet servlet = getServletForURI(requestPath);

        if (null != servlet) {
            rd = new RequestDispatcher() {
                public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                    servlet.service(servletRequest, servletResponse);
                }

                public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                    throw new ServletException("Include not implemented");
                }
            };
        } else {
            s_log.debug("Could not get dispacher for path:" + requestPath);
            s_log.debug("Full path with query:" + path);


        }

        return rd;

    }

    private Servlet getServletForURI(String path) {
        s_log.debug("Looking for: " + path);
        Servlet servlet = (Servlet) m_servletMapping.get(path);
        if (null == servlet) {
            if (path.endsWith("/") ) {
                servlet = (Servlet) m_servletMapping.get(path.substring(0, path.length() - 1));
            }
        }
        if (null == servlet) {
            StringTokenizer tok = new StringTokenizer(path, "/");
            String newPath = "/";
            while(tok.hasMoreTokens() && null == servlet) {
                newPath += tok.nextToken();
                servlet = (Servlet) m_servletMapping.get(newPath);
                if (null == servlet) {
                    newPath += "/";
                    servlet = (Servlet) m_servletMapping.get(newPath);
                }
            }
        }
        if (null == servlet) {
            s_log.debug("Can't find servlet!");
            java.util.Iterator iter = m_servletMapping.keySet().iterator();
            while (iter.hasNext()) {
                String url = (String) iter.next();
                s_log.debug("Have URL: " + url);
                s_log.debug("The servlet is:" + m_servletMapping.get(url));

            }

        }

        return servlet;
    }

    void sendRedirect(String location) throws IOException {
        if (!location.startsWith("/")) {
            throw new IllegalArgumentException("Only supports relative redirects. Can't redirect to: " + location);
        }

        final int pathInfoStart = location.lastIndexOf('/');
        String servletPath = location.substring(0, pathInfoStart);
        final int queryStart = location.indexOf('?');
        String pathInfo;
        String queryString = null;
        if (queryStart != -1) {
            pathInfo = location.substring(pathInfoStart, queryStart);
            queryString = location.substring(queryStart);
        } else {
            pathInfo = location.substring(pathInfoStart);
        }

        ServletURL url = m_req.getServletURL();
        url.setPathInfo(pathInfo);
        url.setServletPath(servletPath);
        url.setQueryString(queryString);
        try {

            Servlet servlet = getServletForURI(url.getServletPath());
            servlet.service(m_req, m_res);

        } catch (ServletException e) {
            throw new UncheckedWrapperException("Servlet Exception in redirect.", e);
        }
    }
}
