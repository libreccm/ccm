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
package com.arsdigita.util;

import com.arsdigita.dispatcher.DispatcherHelper;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.cactus.ServletURL;

/**
 Dummy request object for unit testing of form methods that include
 requests in their signatures.
 */

public class HttpServletDummyRequest implements HttpServletRequest {

    private HashMap parameters = new HashMap();
    private HashMap attributes = new HashMap();
    private HttpSession m_session;
    private boolean m_isSecure;
    private final static String REQUEST_CONTEXT_ATTR =
            "com.arsdigita.dispatcher.RequestContext";

    public ServletURL getServletURL() {
        return m_servletURL;
    }

    private ServletURL m_servletURL;
    private TestServletContainer m_container;

    public HttpServletDummyRequest() {
        this(true);
    }
    public HttpServletDummyRequest(boolean isDebug) {
        this("localhost", "", "/someservlet", "/", null, isDebug);
    }


    public HttpServletDummyRequest(String serverName,
        String contextPath, String servletPath, String pathInfo,
        String queryString) {
        this(serverName, contextPath, servletPath, pathInfo, queryString, true);
    }

    public HttpServletDummyRequest(String serverName,
        String contextPath, String servletPath, String pathInfo,
        String queryString, boolean isDebug) {
        m_servletURL = new ServletURL(serverName, contextPath, servletPath, pathInfo, queryString);

        initializeRequestContext(isDebug);

    }

    void setContainer(TestServletContainer container) {
        m_container = container;
    }
    private void initializeRequestContext(boolean isDebug) {
        DummyRequestContext requestContext
                = new DummyRequestContext(this, new DummyServletContext(), isDebug);

        DispatcherHelper.setRequest(this);

        DispatcherHelper.setRequestContext(this, requestContext);

    }

    public java.lang.Object getAttribute(java.lang.String name) {
        return attributes.get(name);
    }

    public java.util.Enumeration getAttributeNames() {
        return getNamesHelper(attributes);
    }

    public java.lang.String getParameter(java.lang.String name) {
        LinkedList valuesList= (LinkedList)(parameters.get(name));
        return ((valuesList!=null)? (String)(valuesList.getFirst()) : null);
    }

    public java.util.Enumeration getParameterNames() {
        return getNamesHelper(parameters);
    }

    public java.lang.String[] getParameterValues(java.lang.String name) {
        LinkedList valuesList = (LinkedList)(parameters.get(name));
        if (valuesList != null) {
            /*
            this annoying loop is because we
            cannot directly cast Object[] to String[]
            */
            Object[] _objectArray=valuesList.toArray();
            String [] _stringArray=new String[_objectArray.length];
            for (int i=0;i<_objectArray.length;i+=1) {
                _stringArray[i]=(String)_objectArray[i];
            }
            return _stringArray;
        } else {
            return null;
        }
    }

    /*
    naming convention here may seem odd.
    we keep it as setParameterValues rather than setParameterValue
    since we are appending to the list of values rather than overwriting it
    */
    public void setParameterValues(String name, String value) {
        LinkedList valuesList = (LinkedList)parameters.get(name);
        if (valuesList==null) {
            valuesList=new LinkedList();
        }
        if (value!=null) {
            valuesList.add(value);
        }
        parameters.put(name,valuesList);
    }

    public void setParameterValues(String name, String[] values) {
        if (values.length==0) {
            return;
        }
        LinkedList valuesList = (LinkedList)parameters.get(name);
        if (valuesList==null) {
            valuesList=new LinkedList();
        }
        for (int i=0;i<values.length;i+=1) {
            if (values[i]!=null) {
                valuesList.add(values[i]);
            }
        }
        parameters.put(name,valuesList);
    }
    public Object removeParameterValue(String name) {
        return parameters.remove(name);
    }


    private java.util.Enumeration getNamesHelper(HashMap h) {
        Set variableNamesSet = h.keySet();
        Iterator variableNamesIterator;
        Vector temporary = new Vector();
        if (variableNamesSet!=null) {
            variableNamesIterator= variableNamesSet.iterator();
            while (variableNamesIterator.hasNext()) {
                temporary.add(variableNamesIterator.next());
            }
        }
        return temporary.elements();
    }

    public java.lang.String getAuthType() { return null; }

    public Cookie[] getCookies() { return null; }

    public long getDateHeader(java.lang.String name) { return (long)0; }

    public java.lang.String getHeader(java.lang.String name) { return null; }

    public java.util.Enumeration getHeaders(java.lang.String name) { return null; }

    public java.util.Enumeration getHeaderNames() {
        return new Enumeration() {
            public boolean hasMoreElements() {
                return false;
            }

            public Object nextElement() {
                return null;
            }
        };
    };

    public int getIntHeader(java.lang.String name) { return 0; }

    public java.lang.String getMethod() {
       return "GET";
    }

    public java.lang.String getPathInfo() {
	        return m_servletURL.getPathInfo();
    }


    public java.lang.String getPathTranslated() { return null; }

    public java.lang.String getContextPath() { 
        return m_servletURL.getContextPath();
    }

    public java.lang.String getQueryString() {
           return m_servletURL.getQueryString();
    }

    public java.lang.String getRemoteUser() { return null; }

    public boolean isUserInRole(java.lang.String role) { return false; }

    public java.security.Principal getUserPrincipal() { return null; }

    public java.lang.String getRequestedSessionId() { return null; }


    public java.lang.String getRequestURI() {
        return getServletPath() + getPathInfo();
    }

    public java.lang.String getServletPath() {
        return m_servletURL.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        if (m_session == null && create) {
            m_session = new HttpDummySession();
        }
        return m_session;

    }

    public HttpSession getSession() {
        return getSession(true);
    }


    public void setSession(HttpSession s) {
        m_session = s;
    }

    public boolean isRequestedSessionIdValid() { return true; }

    public boolean isRequestedSessionIdFromCookie() { return true; }

    public boolean isRequestedSessionIdFromURL() { return true; }

    public boolean isRequestedSessionIdFromUrl() { return true; }

    //methods for ServletRequest Interface

    public java.lang.String getCharacterEncoding() { return null; }

    public int getContentLength() { return 0; }

    public java.lang.String getContentType() { return null; }

    public ServletInputStream getInputStream()
            throws java.io.IOException { return null; }


    public java.lang.String getProtocol() { return null; }

    public java.lang.String getScheme() { return "http"; }

    public java.lang.String getServerName() { return "localhost"; }

    public int getServerPort() { return 8080; }

    public java.io.BufferedReader getReader()
            throws java.io.IOException { return null; }

    public java.lang.String getRemoteAddr() { return null; }

    public java.lang.String getRemoteHost() { return null; }

    public void setAttribute(java.lang.String name,
                             java.lang.Object o) {

        attributes.put(name, o);
    }

    public void removeAttribute(java.lang.String name) { return; }

    public java.util.Locale getLocale() { return Locale.ENGLISH; }

    public java.util.Enumeration getLocales() { return null; }

    public boolean isSecure()
    {
        return m_isSecure;
    }
    public void setIsSecure(boolean secure) {
        m_isSecure = secure;
    }
    public RequestDispatcher getRequestDispatcher(java.lang.String path) {
        return m_container.getDispatcher(path);
    }

    public java.lang.String getRealPath(java.lang.String path) { return null; }
    public Map getParameterMap() {
        return parameters;
    }

    public StringBuffer getRequestURL() {
        return null;
    }

    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
    }

}
