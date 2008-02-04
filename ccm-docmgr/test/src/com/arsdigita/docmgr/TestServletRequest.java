/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
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
