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
package com.arsdigita.dispatcher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Category;

import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * MultipartHttpServletRequest provides
 * multipart/form-data handling capabilities to facilitate file
 * uploads for servlets. This request object parses the HTTP request
 * with MIME type "multipart" and places the encoded object in a
 * stream.
 *
 * @author Karl Goldstein
 * @author Michael Pih
 * @author Uday Mathur
 * @version $Id: MultipartHttpServletRequest.java 1512 2007-03-22 02:36:06Z apevec $
 * @since 4.5
 */
public class MultipartHttpServletRequest implements HttpServletRequest {

    private static final Category s_log = Category.getInstance
        (MultipartHttpServletRequest.class);

    private HttpServletRequest m_request;
    private Map m_parameters = null;

    /**
     * Create a multipart servlet request object and parse the request.
     *
     * @param request The request
     */
    public MultipartHttpServletRequest(HttpServletRequest request)
            throws MessagingException, IOException {
        m_request = request;
        m_parameters = Collections.synchronizedMap(new HashMap());
        parseMultipartRequest(m_request);
    }

    /**
     * Create a multipart servlet request object and parse the request.
     *
     * @param request The request
     */
    public MultipartHttpServletRequest(MultipartHttpServletRequest original,
                                       HttpServletRequest current) {
        m_request = current;
        m_parameters = original.m_parameters;
    }

    public Object getAttribute(String name) {
        return m_request.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return m_request.getAttributeNames();
    }

    public String getParameter(String name) {
        String[] values = (String[]) m_parameters.get(name);

        if (values == null || values.length == 0) {
            return null;
        } else {
            return values[0];
        }
    }

    public Map getParameterMap() {
        return m_parameters;
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(m_parameters.keySet());
    }

    public String getFileName(String name) {
        return getParameter(name);
    }

    public File getFile(String name) {
        String path = getParameter(name + ".tmpfile");

        if (path == null) {
            return null;
        } else {            
            return new File(path);
        }
    }

    public String[] getParameterValues(String name) {
        return (String[]) m_parameters.get(name);
    }

    // Additional methods for HttpServletRequest

    public String getAuthType() {
        return m_request.getAuthType();
    }

    public Cookie[] getCookies() {
        return m_request.getCookies();
    }

    public long getDateHeader(String name) {
        return m_request.getDateHeader(name);
    }

    public String getHeader(String name) {
        return m_request.getHeader(name);
    }

    public Enumeration getHeaders(String name) {
        return m_request.getHeaders(name);
    }

    public Enumeration getHeaderNames() {
        return m_request.getHeaderNames();
    }

    public int getIntHeader(String name) {
        return m_request.getIntHeader(name);
    }

    public String getMethod() {
        return m_request.getMethod();
    }

    public String getPathInfo() {
        return m_request.getPathInfo();
    }

    public String getPathTranslated() {
        return m_request.getPathTranslated();
    }

    public String getContextPath() {
        return m_request.getContextPath();
    }

    public String getQueryString() {
        return m_request.getQueryString();
    }

    public String getRemoteUser() {
        return m_request.getRemoteUser();
    }

    public boolean isUserInRole(String role) {
        return m_request.isUserInRole(role);
    }

    public java.security.Principal getUserPrincipal() {
        return m_request.getUserPrincipal();
    }

    public String getRequestedSessionId() {
        return m_request.getRequestedSessionId();
    }

    public String getRequestURI() {
        return m_request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException
            ("This is a Servlet 2.3 feature that we do not currently support");
    }

    public String getServletPath() {
        return m_request.getServletPath();
    }

    public HttpSession getSession(boolean create) {
        return m_request.getSession(create);
    }

    public HttpSession getSession() {
        return m_request.getSession();
    }

    public boolean isRequestedSessionIdValid() {
        return m_request.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return m_request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return m_request.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return m_request.isRequestedSessionIdFromUrl();
    }

    //methods for ServletRequest Interface

    public String getCharacterEncoding() {
        return m_request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String encoding)
            throws java.io.UnsupportedEncodingException {
        throw new UnsupportedOperationException
            ("This is a Servlet 2.3 feature that we do not currently support");
    }

    public int getContentLength() {
        return m_request.getContentLength();
    }

    public String getContentType() {
        return m_request.getContentType();
    }

    public ServletInputStream getInputStream()
            throws IOException {
        //maybe just throw an exception here -- UM
        return m_request.getInputStream();
    }

    public String getProtocol() {
        return m_request.getProtocol();
    }

    public String getScheme() {
        return m_request.getScheme();
    }

    public String getServerName() {
        return m_request.getServerName();
    }

    public int getServerPort() {
        return m_request.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        //maybe just throw an exception here -- Uday
        return m_request.getReader();
    }

    public String getRemoteAddr() {
        return m_request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return m_request.getRemoteHost();
    }

    public void setAttribute(String name,
                             Object o) {
        m_request.setAttribute(name, o);
    }

    public void removeAttribute(String name) {
        m_request.removeAttribute(name);
    }

    public Locale getLocale() {
        return m_request.getLocale();
    }

    public Enumeration getLocales() {
        return m_request.getLocales();
    }

    public boolean isSecure() {
        return m_request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return m_request.getRequestDispatcher(path);
    }

    public String getRealPath(String path) {
        return m_request.getRealPath(path);
    }

    /*
     * Parse the body of multipart MIME-encoded request.
     */
    private void parseMultipartRequest(HttpServletRequest request)
            throws MessagingException, IOException {
        // replace JAF+JavaMail combo (broken with CoyoteInputStream)
        // with simple commons-fileupload
        try {
            ServletFileUpload upload = new ServletFileUpload(
                    new DiskFileItemFactory() );
            List items = upload.parseRequest(request);
            for (Iterator i=items.iterator(); i.hasNext(); ) {
                FileItem item = (FileItem) i.next();
                String paramName = item.getFieldName();
                if (item.isFormField()) {
                    addParameterValue(paramName, item.getString());
                } else {
                    addParameterValue(paramName, item.getName() );
                    // save file
                    File tmpFile = File.createTempFile("acs", null, null);
                    tmpFile.deleteOnExit();
                    addParameterValue(paramName + ".tmpfile", tmpFile.getPath());
                    item.write(tmpFile);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e); // XXX
        }
    }

    private void addParameterValue(String name, Object value)
        throws IOException
    {
        String[] newValues;
        String[] values = (String[]) m_parameters.get(name);

        if (values == null) {
            newValues = new String[1];
        } else {
            newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
        }

        newValues[newValues.length - 1] = convertToString( value );

        m_parameters.put(name, newValues);
    }

    private String convertToString( Object value ) throws IOException {
        if( value instanceof String ) return (String) value;

        if( value instanceof ByteArrayInputStream ) {
            StringBuffer output = new StringBuffer();

            InputStreamReader reader;
            try {
                reader = new InputStreamReader(
                    (ByteArrayInputStream) value,
                    Globalization.DEFAULT_ENCODING );
            } catch( UnsupportedEncodingException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            final int bufSize = 1024;
            char[] buffer = new char[bufSize];

            int read = bufSize;
            while( bufSize == read ) {
                read = reader.read( buffer, 0, bufSize );
                if( read > 0 ) output.append( buffer, 0, read );
            }

            return output.toString();
        }

        // Fallback to default
        return value.toString();
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
