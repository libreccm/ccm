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
package com.arsdigita.docmgr;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;


/**
 * A simple implementation of the HTTP servlet request interface for
 * testing.  It only provides methods to set and get the Content-Type
 * header. 
 */

class TestServletRequest implements HttpServletRequest {

    private String m_contentType;
    private HashMap parameters;

    private static String CONTENT_TYPE = "Content-Type";
    
    public TestServletRequest() {
        // empty
    }

    void setContentType(String contentType) {
        m_contentType = contentType;
    }
    
    public String getHeader(String name) {
        if (name.equals(CONTENT_TYPE)) {
            return m_contentType;
        } else {
            return null;
        }
    }

    // The remaining methods are not used

    public String getAuthType() { 
        throw new UnsupportedOperationException();
    }

    public String getContextPath() { 
        throw new UnsupportedOperationException();
    }
  
    public Cookie[] getCookies() { 
        throw new UnsupportedOperationException();
    }

    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    public java.util.Enumeration getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    public java.util.Enumeration getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    public String getMethod() {
        throw new UnsupportedOperationException();
    }

    public String getPathInfo() {
        throw new UnsupportedOperationException();
    }
    
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    public String getQueryString() {
        throw new UnsupportedOperationException();
    }

    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    public String getRequestURI() {
        throw new UnsupportedOperationException();
    }

    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }
    
    public java.security.Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }
        
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    // Unused method from ServletRequest

    public java.lang.Object getAttribute(java.lang.String name) {
        throw new UnsupportedOperationException();
    }

    public java.util.Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public int getContentLength() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getContentType() {
        throw new UnsupportedOperationException();
    }

    public ServletInputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    public java.util.Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    public java.util.Enumeration getLocales() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getParameter(java.lang.String name) {
        throw new UnsupportedOperationException();
    }
    
    public java.util.Enumeration getParameterNames() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String[] getParameterValues(java.lang.String name) {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getProtocol() {
        throw new UnsupportedOperationException();
    }
    
    public java.io.BufferedReader getReader() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    public RequestDispatcher getRequestDispatcher(java.lang.String path) {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getScheme() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getServerName() {
        throw new UnsupportedOperationException();
    }

    public java.lang.String getRealPath(java.lang.String path) {
        throw new UnsupportedOperationException();
    }

    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(java.lang.String name) {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(java.lang.String name, java.lang.Object o) {
        throw new UnsupportedOperationException();
    }
    
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
