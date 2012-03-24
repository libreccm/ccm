/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
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

package com.arsdigita.cms;

import com.arsdigita.cms.dispatcher.ResourceHandler;
import com.arsdigita.cms.dispatcher.SimpleCache;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.security.Util;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationFileResolver;
import com.arsdigita.web.BaseApplicationServlet;

import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CMS Workspace (content-center) application servlet serves all request made 
 * within the Content Center application. 
 * 
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: WorkspaceServlet.java 2161 2011-02-02 00:16:13Z pboy $
 */
public class WorkspaceServlet extends BaseApplicationServlet {

    /**Error logging    */
    private static Logger s_log = Logger
                                  .getLogger(WorkspaceServlet.class.getName());

    /** The path of the file that maps resources.     */
    public final static String DEFAULT_MAP_FILE =
                               "/WEB-INF/resources/content-center-old-map.xml";

    /** Mapping between a relative URL and the class name of a ResourceHandler.*/
    private static HashMap s_pageClasses = WorkspaceSetup.getURLToClassMap();
    private static HashMap s_pageURLs = WorkspaceSetup.getClassToURLMap();

    /**
     * Instantiated ResourceHandlers cache. This allows for lazy loading.
     */
    private static SimpleCache s_pages = new SimpleCache();

//     private Dispatcher m_notFoundHandler;
    private ArrayList m_trailingSlashList = new ArrayList();

    /** Path to directory containg ccm-cms template files                    */
    private String m_templatePath;

    /** Resolvers to find templages (JSP) and other stuff stored in file system.*/
    private ApplicationFileResolver m_resolver;


    /**
     * Use parent's class initialization extension point to perform additional
     * initialisation tasks.
     */
    @Override
    protected void doInit() {
        if (s_log.isDebugEnabled()) {
            s_log.info("starting doInit method");
        }
        m_trailingSlashList = new ArrayList();
        requireTrailingSlash("");

        /** Set Template base path for JSP's                                  */
        // ToDo: Make it configurable by an appropriate config registry entry!
//        m_templatePath = CMS.getConfig().getTemplateRoot();
        m_templatePath = "/templates/ccm-cms/content-center";
        Assert.exists(m_templatePath, String.class);
        Assert.isTrue(m_templatePath.startsWith("/"),
                     "template-path must start with '/'");
        Assert.isTrue(!m_templatePath.endsWith("/"),
                     "template-path must not end with '/'");
        /** Set TemplateResolver class                                        */
        m_resolver = Web.getConfig().getApplicationFileResolver();
    }


    /**
     * Implements the (abstract) doService method of BaseApplicationServlet to
     * create the Workspace page.
     * 
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     *      (HttpServletRequest, HttpServletResponse, Application)
     */
    protected void doService( HttpServletRequest sreq, 
                              HttpServletResponse sresp, 
                              Application app)
                   throws ServletException, IOException {

        if (s_log.isDebugEnabled()) {
            s_log.info("starting doService method");
        }
        DeveloperSupport.startStage("ContentCenterServlet.doService");

        Workspace workspace = (Workspace) app;

        RequestContext ctx = DispatcherHelper.getRequestContext();        
        String url = ctx.getRemainingURLPart();  // here SiteNodeRequestContext
        String originalUrl = ctx.getOriginalURL();
        String requestUri = sreq.getRequestURI();

        // An empty remaining URL or a URL which doesn't end in trailing slash:
        // probably want to redirect.
        if ( m_trailingSlashList.contains(url) && !originalUrl.endsWith("/") ) {
            DispatcherHelper.sendRedirect(sresp, originalUrl + "/");
            return;
        }

        // Check user access.
   //   checkUserAccess(sreq, sresp);

        ResourceHandler page = getResource(url);
        if ( page != null ) {

            // Check user access.
            checkUserAccess(sreq, sresp);
            // Serve the page.
            page.init();
            page.dispatch(sreq, sresp, ctx);
        } else {
            // Fall back on the JSP application dispatcher.
            // NOTE: The JSP must ensure the proper authentication and
            //       authorisation if required!
            if (s_log.isInfoEnabled()) {
                s_log.info("NO page registered to serve the requst url.");
            }
            
            RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                      sreq, sresp, app);
            if (rd != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got dispatcher " + rd);
                }
                sreq = DispatcherHelper.restoreOriginalRequest(sreq);
                rd.forward(sreq,sresp);
            } else {
                sresp.sendError(404, requestUri + " not found on this server.");
            }

        }

        
        DeveloperSupport.endStage("ContentCenterServlet.doService");
        if (s_log.isDebugEnabled()) {
            s_log.info("doService method completed");
        }
    }

    /**
     * Service Method returns the URL stub for the class name,
     * can return null if not mapped
     */
    public static String getURLStubForClass(String classname) {
        s_log.debug("Getting URL Stub for : " + classname);
        Iterator itr = s_pageURLs.keySet().iterator();
        while (itr.hasNext()) {
            String classname2 = (String)itr.next();
            s_log.debug("key: " + classname + " value: " +
                        (String)s_pageURLs.get(classname2));
        }
        String url = (String)s_pageURLs.get(classname);
        return url;
    }

    /**
     * Fetch a page based on the URL stub.
     *
     * @param url The URL stub following the site-node URL
     * @return A ResourceHandler or null if none exists.
     * @pre (url != null)
     */
    protected ResourceHandler getResource(String url) throws ServletException {

        // First check the pages cache for existing pages.
        ResourceHandler page = (ResourceHandler) s_pages.get(url);
        if ( page == null ) {

            // Next check if the URL maps to a page class.
            String pageClassName = (String) s_pageClasses.get(url);
            if ( pageClassName != null ) {

                Class pageClass;
                try {
                    pageClass = Class.forName(pageClassName);
                } catch (ClassNotFoundException e) {
                    s_log.error("error fetching class for ResourceHandler", e);
                    throw new ServletException(e);
                }

                // Try and instantiate the page.
                try {
                    page = (ResourceHandler) pageClass.newInstance();
                } catch (InstantiationException e) {
                    s_log.error("error instantiating a ResourceHandler", e);
                    throw new ServletException(e);
                } catch (IllegalAccessException e) {
                    s_log.error("error instantiating a ResourceHandler", e);
                    throw new ServletException(e);
                }

                page.init();
                s_pages.put(url, page);
            }
        }
        return page;
    }

    /**
     * Map a page to a URL.
     *
     * @param url The URL
     * @param className The name of the ResourceHandler class
     * @pre (url != null && className != null)
     */
    protected void addResource(String url, String className) {
        s_pageClasses.put(url, className);
        s_pageURLs.put(className, url);
    }

    /**
     * Release the page at the specified URL.
     *
     * @param url The URL
     * @pre (url != null)
     */
    public static void releaseResource(String url) {
        s_pages.remove(url);
    }

    
    /**
     * Verify that the user is logged in and is able to view the
     * page. Subclasses can override this method if they need to, but
     * should always be sure to call super.checkUserAccess(...)
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     **/
    protected void checkUserAccess(final HttpServletRequest request,
                                   final HttpServletResponse response //,
///                                 final RequestContext actx
                                  )
                   throws ServletException {
        
        if (!Web.getUserContext().isLoggedIn()) {
            throw new LoginSignal(request);

        }
    }

    /**
     * Redirects the client to the login page, setting the return url to
     * the current request URI.
     *
     * @exception ServletException If there is an exception thrown while
     * trying to redirect, wrap that exception in a ServletException
     **/
    protected void redirectToLoginPage(HttpServletRequest req,
                                       HttpServletResponse resp)
        throws ServletException {
        String url = Util.getSecurityHelper()
                         .getLoginURL(req)
                         +"?"+LoginHelper.RETURN_URL_PARAM_NAME
                         +"="+UserContext.encodeReturnURL(req);
        try {
            LoginHelper.sendRedirect(req, resp, url);
        } catch (IOException e) {
            s_log.error("IO Exception", e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Adds a URL to the list of URLs that are required to have trailing
     * slashes.  A request for url will be redirected to url + "/"
     * if the original URL request (what you see in your browser)
     * doesn't include a trailing slash.  This is required for
     * creating virtual directories, so that relative URLs and redirects
     * work.
     */
    // public void requireTrailingSlash(String url) {
    private void requireTrailingSlash(String url) {
        m_trailingSlashList.add(url);
    }

}
