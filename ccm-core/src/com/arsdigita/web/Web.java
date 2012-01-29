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


import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.kernel.security.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * An entry point for functions of the web package.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Web.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class Web {

    public static final String ROOT_WEBAPP = "ROOT";

    private static final Logger s_log = Logger.getLogger(Web.class);

    private static final ThreadLocal s_request =
        new InternalRequestLocal();
    private static final ThreadLocal s_servletContext =
        new InternalRequestLocal();
    private static final ThreadLocal s_userContext =
        new InternalRequestLocal();

    private static final Map s_contexts = new HashMap();

    static final WebContext s_initialContext = new WebContext();
    private static ThreadLocal s_context;

    private static WebConfig s_config;

    final static void init(final HttpServletRequest sreq,
                           final ServletContext sc,
                           final UserContext uc) {
        Assert.exists(sreq, HttpServletRequest.class);
        Assert.exists(sc, ServletContext.class);
        Assert.exists(uc, UserContext.class);

        s_request.set(sreq);
        s_servletContext.set(sc);
        s_userContext.set(uc);
    }

    /**
     * Gets the web context object from the current thread.
     *
     * @return A <code>WebContext</code> object; it cannot be null
     */
    public static WebContext getContext() {
        if (s_context == null) {
            s_context = new WebContextLocal();
        }
        return (WebContext) s_context.get();
    }

    /**
     * Gets the configuration record for code in the web package.
     *
     * @return A <code>WebConfig</code> configuration record; it
     * cannot be null
     */
    public static WebConfig getConfig() {
        if (s_config == null) {
            s_config = new WebConfig();
            s_config.require("ccm-core/web.properties");
        }
        return s_config;
    }

    /**
     * Gets the servlet request object of the current thread.
     *
     * @return The current <code>HttpServletRequest</code>; it can be
     * null
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) s_request.get();
    }

    /**
     * Gets the servlet context of the current thread.
     *
     * @return The current <code>ServletContext</code>; it can be null
     *
     */
    public static final ServletContext getServletContext() {
        return (ServletContext) s_servletContext.get();
    }

    /**
     * Gets the servlet context matching a URI. The URI
     * is relative to the root of the server and must start 
     * and end with a '/'. 
     *
     * This should be used in preference to ServletContext#getContext(String)
     * since on all versions of Tomcat, this fails if the path of the
     * context requested is below the current context.
     * @param uri the context URI
     * @return the servlet context matching uri, or null
     */
    public static final ServletContext getServletContext(String uri) {
        Assert.isTrue(uri.startsWith("/"), "uri must start with /");
        Assert.isTrue(uri.endsWith("/"), "uri must end with /");
        return (ServletContext)s_contexts.get(uri);
    }

    /**
     * Registers a servlet context against a URI. Only intended
     * to be used by ContextRegistrationServlet
     */
    static final void registerServletContext(String uri,
                                             ServletContext ctx) {
        s_log.debug("Mapping " + ctx + " to " + uri);
        Assert.isTrue(s_contexts.get(uri) == null,
                     "a context mapping exists at " + uri);
        s_contexts.put(uri, ctx);
    }

    /**
     * Unregisters the servlet context against a URI. Only intended
     * to be used by ContextRegistrationServlet
     */
    static final void unregisterServletContext(String uri) {
        s_log.debug("Unmapping " + uri);
        s_contexts.remove(uri);
    }

    /**
     * Gets the user context object of the current thread.
     *
     * @return The current <code>UserContext</code> object; it can be
     * null
     */
    public static final UserContext getUserContext() {
        return (UserContext) s_userContext.get();
    }

    /**
     * Finds a concrete URL corresponding to an abstract 
     * webapp resource. The format of the resource is 
     * as follows: "/[webapp list]/[path]". The 'webapp
     * list' component is a comma separate list of webapps
     * to search for the second component 'path'. So, if the 
     * 'resource' is:
     * <pre>
     *  /myproj,ccm-cms,ROOT/themes/heirloom/apps/content-section/index.sl
     * </pre>
     * then this method will look for resources at
     * <pre>
     *  /myproj/themes/heirloom/apps/content-section/index.sl
     *  /ccm-cms/themes/heirloom/apps/content-section/index.sl
     *  /ROOT/themes/heirloom/apps/content-section/index.sl
     * </pre>
     * @param resource the resource name
     * @return the URL for the resource, or null
     */
    public static URL findResource(String resource) {
        ResourceSpec spec = parseResource(resource);
        
        return findResource(spec.getWebapps(),
                            spec.getPath());
    }

    /**
     * Finds a concrete URL corresponding to an abstract 
     * webapp resource. The first argument is a list of
     * webapp paths to search through for the path. So
     * if the webapps param is { 'myproj', 'ccm-cms', 'ROOT' }
     * and the path parma is '/themes/heirloom/apps/content-section/index.xsl'
     * then the paths that are searched are:
     * <pre>
     *  /myproj/themes/heirloom/apps/content-section/index.sl
     *  /ccm-cms/themes/heirloom/apps/content-section/index.sl
     *  /ROOT/themes/heirloom/apps/content-section/index.sl
     * </pre>
     * @param webapps the list of webapps
     * @param path the resource path
     * @return the URL for the resource, or null
     */
    public static URL findResource(String[] webapps,
                                   String path) {
        ServletContext ctx = findResourceContext(webapps,
                                                 path);
        
        URL url = null;
        try {
            url = (ctx == null ? null :
                   ctx.getResource(path));
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot get URL for " + path, ex);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("URL for " + path + " is " + url);
        }
        return url;
    }
    
    
    /**
     * Follows the same rules as findResource(String), but
     * instead returns an input stream for reading the
     * resource
     * @param resource the resource name
     * @return the input stream for the resource, or null
     */
    public static InputStream findResourceAsStream(String resource) 
        throws IOException {
        ResourceSpec spec = parseResource(resource);

        return findResourceAsStream(spec.getWebapps(),
                                    spec.getPath());
    }

    /**
     * Follows the same rules as findResource(String[], String), but
     * instead returns an input stream for reading the
     * resource
     * @param webapps the list of webapps
     * @param path the resource path
     * @return the input stream for the resource, or null
     */
    public static InputStream findResourceAsStream(String[] webapps,
                                                   String path)
        throws IOException {

        URL url = findResource(webapps, path);
        
        return url == null ? null :
            url.openStream();
    }


    /**
     * Follows the same rules as findResource(String), but
     * instead returns a request dispatcher for serving
     * the resource
     *
     * @param resource the resource name
     * @return the request dispatcher for the resource, or null
     */
    public static RequestDispatcher findResourceDispatcher(String resource) {
        ResourceSpec spec = parseResource(resource);
        
        return findResourceDispatcher(spec.getWebapps(),
                                      spec.getPath());
    }

    /**
     * Follows the same rules as findResource(String[], String), but
     * instead returns a request dispatcher for serving
     * the resource
     *
     * @param webapps the list of webapps
     * @param path the resource path
     * @return the request dispatcher for the resource, or null
     */
    public static RequestDispatcher findResourceDispatcher(String[] webapps,
                                                           String path) {
        ServletContext ctx = findResourceContext(webapps,
                                                 path);
        
        return ctx == null ? null : ctx.getRequestDispatcher(path);
    }


    private static ServletContext findResourceContext(String[] webapps,
                                                      String path) {
        for (int i = (webapps.length - 1) ; i >= 0 ; i--) {
            String ctxPath = ROOT_WEBAPP.equals(webapps[i]) ? 
                "" : webapps[i];
            if (!ctxPath.startsWith("/")) {
                ctxPath = "/" + ctxPath;
            }
            if (!ctxPath.endsWith("/")) {
                ctxPath = ctxPath + "/";
            }
            ServletContext ctx = getServletContext(ctxPath);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Servlet context for " + ctxPath + " is " + ctx);
            }

            if (ctx != null) {
                try {
                    URL url = ctx.getResource(path);
                    if (url != null) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Got URL " + url + " for " + path);
                        }
                        return ctx;
                    } else {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("No URL present for " + path);
                        }
                    }
                } catch (IOException ex) {
                    throw new UncheckedWrapperException(
                        "cannot get resource " + path, ex);
                }
            }
        }
        return null;
    }


    //
    // Private classes and methods
    //

    private static ResourceSpec parseResource(String resource) {
        if (resource == null ||
            resource.length() < 2) {
            throw new IllegalArgumentException(
                "Resource spec is too short: " + resource);
        }

        int offset = resource.indexOf("/", 1);
        if (offset == -1) {
            throw new IllegalArgumentException(
                "Cannot find second '/' in resource spec : " + resource);
        }
        
        String webappList = resource.substring(1, offset);
        String path = resource.substring(offset);
        
        String[] webapps = StringUtils.split(webappList, ',');
        
        if (s_log.isInfoEnabled()) {
            s_log.info("Web app list " + webappList + " path " + path);
        }
        
        return new ResourceSpec(webapps, path);
    }

    private static class ResourceSpec {
        private String[] m_webapps;
        private String m_path;
            
        public ResourceSpec(String[] webapps,
                            String path) {
            m_webapps = webapps;
            m_path = path;
        }
        
        public String[] getWebapps() {
            return m_webapps;
        }

        public String getPath() {
            return m_path;
        }
    }

    private static class WebContextLocal extends InternalRequestLocal {
        protected Object initialValue() {
            return Web.s_initialContext.copy();
        }

        protected void clearValue() {
            ((WebContext) get()).clear();
        }
    }
}
