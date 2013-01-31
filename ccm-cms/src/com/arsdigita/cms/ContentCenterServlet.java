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

import com.arsdigita.bebop.Page;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.SimpleCache;
import com.arsdigita.cms.ui.ItemSearchPage;
import com.arsdigita.cms.ui.contentcenter.MainPage;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.security.Util;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.web.*;
import com.arsdigita.xml.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CMS ContentCenter (content-center) application servlet serves all request
 * made within the Content Center application. 
 * 
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: ContentCenterServlet.java 2161 2011-02-02 00:16:13Z pboy $
 */
public class ContentCenterServlet extends BaseApplicationServlet {

    /** Logger instance for debugging  */
    private static Logger s_log = Logger
            .getLogger(ContentCenterServlet.class.getName());
//  DEPRECATED STUFF follows. Should be no longer used, deleted in future!
    /** The path of the file that maps resources.                             */
    public final static String DEFAULT_MAP_FILE =
                               "/WEB-INF/resources/content-center-old-map.xml";
    /** Mapping between a relative URL and the class name of a ResourceHandler.*/
    // private static HashMap s_pageClasses = ContentCenterSetup.getURLToClassMap();
    private static HashMap s_pageURLs = ContentCenterSetup.getClassToURLMap();
    /** Instantiated ResourceHandlers cache. This allows for lazy loading.    */
    private static SimpleCache s_pages = new SimpleCache();
    private ArrayList m_trailingSlashList = new ArrayList();
//  NEW STUFF here used to process the pages in this servlet
    /** URL (pathinfo) -> Page object mapping. Based on it (and the http
     *  request url) the doService method to selects a page to display        */
    private final Map m_pages = new HashMap();
//  STUFF to use for JSP extension, i.e. jsp's to try for URLs which are not
//  handled by the this servlet directly.
    /** Path to directory containg ccm-cms template files                    */
    private String m_templatePath;
    /** Resolvers to find templates (JSP) and other stuff stored in file system.*/
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
//      DEPRECATED STUFF for servlet internally process pages, maybe required
//      for JSP extension.        
        m_trailingSlashList = new ArrayList();
        requireTrailingSlash("");

//  NEW STUFF here used to process the pages in this servlet
        // Addresses previously noted in WEB-INF/resources/content-center-map.xml
        // Obviously not required.
        addPage("/", new MainPage());     // index page at address ~/ds
        //  addPage("/index/", new MainPage());     
        //addPage("/ItemSearchPage/", new ItemSearchPage());
        addPage("/item-search", new ItemSearchPage());
        //  addPage("/SearchResultRedirector/", new CCSearchResultRedirector());


//  STUFF to use for JSP extension, i.e. jsp's to try for URLs which are not
//  handled by the this servlet directly.
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
     * create the ContentCenter page.
     * 
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     *      (HttpServletRequest, HttpServletResponse, Application)
     */
    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp,
                             Application app)
            throws ServletException, IOException {

        if (s_log.isDebugEnabled()) {
            s_log.info("starting doService method");
        }
        DeveloperSupport.startStage("ContentCenterServlet.doService");

        ContentCenter workspace = (ContentCenter) app;

        /*
         *       Check user and privilegies
         */
        if (Web.getContext().getUser() == null) {   // user not logged in
            throw new LoginSignal(sreq);            // send to login page
        }
        // Check whether logged in user has access to at least one content section
        ContentSectionCollection sections = ContentSection.getAllSections();
        boolean hasAccess = false;
        while (sections.next()) {
            ContentSection section = sections.getContentSection();
            SecurityManager sm = new SecurityManager(section);
            if (sm.canAccess(sreq, SecurityManager.ADMIN_PAGES)) {
                hasAccess = true;
                break;
            }
        }
        sections.close();
        if (!hasAccess) {    // user has no access privilege 
            throw new AccessDeniedException(
                    "User is not entitled to access any content section");
            // throw new LoginSignal(sreq);            // send to login page
        }



        RequestContext ctx = DispatcherHelper.getRequestContext();
        String url = ctx.getRemainingURLPart();  // here SiteNodeRequestContext
        String originalUrl = ctx.getOriginalURL();

        String requestUri = sreq.getRequestURI();

        // New way to tetch the page
        String pathInfo = sreq.getPathInfo();
        Assert.exists(pathInfo, "String pathInfo");
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length() - 1);
        }

        // An empty remaining URL or a URL which doesn't end in trailing slash:
        // probably want to redirect.
        // Probably DEPRECATED with new access method or only relevant for jsp
        // extension
        if (m_trailingSlashList.contains(url) && !originalUrl.endsWith("/")) {
            DispatcherHelper.sendRedirect(sresp, originalUrl + "/");
            return;
        }


        final Page page = (Page) m_pages.get(pathInfo);
        if (page != null) {

            // Check user access.
            checkUserAccess(sreq, sresp);
            //Lock the page

            if (page instanceof CMSPage) {
                final CMSPage cmsPage = (CMSPage) page;
                cmsPage.init();
                cmsPage.dispatch(sreq, sresp, ctx);
            } else {
                page.lock();
                // Serve the page.            
                final Document doc = page.buildDocument(sreq, sresp);

                PresentationManager pm = Templating.getPresentationManager();
                pm.servePage(doc, sreq, sresp);
            }

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
                rd.forward(sreq, sresp);
            } else {
                sresp.sendError(404, requestUri + " not found on this server.");
            }

        }


        DeveloperSupport.endStage("ContentCenterServlet.doService");
        if (s_log.isDebugEnabled()) {
            s_log.info("doService method completed");
        }
    }    //  END doService()

    /**
     * Adds one pair of Url - Page to the internal hash map, used as a cache.
     * 
     * @param pathInfo url stub for a page to display
     * @param page Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }

    /**
     * Service Method returns the URL stub for the class name,
     * can return null if not mapped
     */
    // Currently still in use by c.ad.cms.ui.ItemSearchWidget
    public static String getURLStubForClass(String classname) {
        s_log.debug("Getting URL Stub for : " + classname);
        Iterator itr = s_pageURLs.keySet().iterator();
        while (itr.hasNext()) {
            String classname2 = (String) itr.next();
            s_log.debug("key: " + classname + " value: " + (String) s_pageURLs.get(classname2));
        }
        String url = (String) s_pageURLs.get(classname);
        return url;
    }

    /**
     * Release the page at the specified URL.
     *
     * @param url The URL
     * @pre (url != null)
     */
    // Currently still in use by c.ad.cms.dispatcher.Utilities
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
                     + "?" + LoginHelper.RETURN_URL_PARAM_NAME
                     + "=" + UserContext.encodeReturnURL(req);
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
