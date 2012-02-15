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
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationFileResolver;
import com.arsdigita.web.BaseApplicationServlet;

import com.arsdigita.web.Web;
import com.arsdigita.xml.XML;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

/**
 * CMS Service application servlet serves all request made for the CMS 
 * service application. 
 * 
 * URLs of the available services are stored in a XML file which is processed
 * into a cache of services on a request by request basis (lazy loading).
 * 
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: WorkspaceServlet.java 2161 2011-02-02 00:16:13Z pboy $
 */
public class ServiceServlet extends BaseApplicationServlet {

    /**Error logging    */
    private static Logger s_log = Logger
                                  .getLogger(ServiceServlet.class.getName());


    /** The path of the file that maps resources (relative urls - corresponding
     *  class names).                                                         */
    private final static String MAP_FILE = "WEB-INF/resources/cms-service-map.xml";

    /** Mapping between a relative URL and the class name of a ResourceHandler.*/
    private static HashMap s_pageClasses = new HashMap();

    /**
     * Instantiated ResourceHandler cache. This allows for lazy loading.
     */
    private static SimpleCache s_pages = new SimpleCache();

    /** List of URLs which require a trailing slash. These are required for
     * creating virtual directories, so that relative URLs and redirects
     * work.                                                                  */
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

        /* Initialize List with an empty URL. Later URL's are added which are
         * provided w/o trailing slash rsp. file extension.                   */
        requireTrailingSlash("");

        /* Process mapping file.                                              */
        readFromFile(MAP_FILE);

        m_templatePath = ContentSection.getConfig().getTemplateRoot();
        Assert.exists(m_templatePath, String.class);
        Assert.isTrue(m_templatePath.startsWith("/"),
                     "template-path must start with '/'");
        Assert.isTrue(!m_templatePath.endsWith("/"),
                     "template-path must not end with '/'");
        m_resolver = Web.getConfig().getApplicationFileResolver();

    }
        
    /**
     * Implements the (abstract) doService method of BaseApplicationServlet to
     * create the Worspace page.
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
        DeveloperSupport.startStage("ServiceServlet.doService");

        Service service = (Service) app;

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
        // Deprecated and here implemented as a No-OP method!
        /* heckUserAccess(request, response, actx);     */

        ResourceHandler page = getResource(url);
        if ( page != null ) {
            // Serve the page.
            page.init();
            page.dispatch(sreq, sresp, ctx);
        } else {
            // Fall back on the JSP application dispatcher.
        //  m_notFoundHandler.dispatch(request, response, actx);
            if (s_log.isInfoEnabled()) {
                s_log.info("NOT serving content item");
            }
            
            /* Store content section in http request to make it available
             * or admin index,jsp                                             */
            // sreq.setAttribute(CONTENT_SECTION, section);

            RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                      sreq, sresp, app);
            if (rd != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got dispatcher " + rd);
                }
                sreq = DispatcherHelper.restoreOriginalRequest(sreq);
                rd.forward(sreq,sresp);
            } else {
            //  sresp.sendError(404, packageURL + " not found on this server.");
                sresp.sendError(404, requestUri + " not found on this server.");
            }

        }


        DeveloperSupport.endStage("ServiceServlet.doService");
        if (s_log.isDebugEnabled()) {
            s_log.info("doService method completed");
        }
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
     *
     * Initializes URL-to-Page/Dispatcher/Servlet mappings from a file.
     *
     * Format of the file is XML:
     * <pre>
     * &lt;dispatcher-configuration>
     *   &lt;url-mapping
     *     &lt;url>my-page&lt;/url>
     *     OR &lt;page-class>com.arsdigita.Page.class&lt;/page-class>
     *   &lt;url-mapping
     * &lt;/dispatcher-configuration>
     * </pre>
     */
    private void readFromFile(final String file) {

    //  XML.parseResource(file, newParseConfigHandler(s_pageClasses));
        XML.parseResource(file, new PageClassConfigHandler(s_pageClasses));

    }

    /**
     * Adds a URL to the list of URLs that are required to have trailing
     * slashes.  A request for url will be redirected to url + "/"
     * if the original URL request (what you see in your browser)
     * doesn't include a trailing slash.  This is required for
     * creating virtual directories, so that relative URLs and redirects
     * work.
     */
    public void requireTrailingSlash(String url) {
        m_trailingSlashList.add(url);
    }


    /**
     * Returns a SAX event handler object for setting up a MapDispatcher
     * using an XML config file.
     * @param map A map to configure
     * @return a SAX DefaultHandler object for handling SAX events
     * @pre md.m_map != null
     */
//  protected DefaultHandler newParseConfigHandler(Map map) {
//      return new PageClassConfigHandler(map);
//  }


    /**
     *
     * SAX event handler class for parsing configuration file.
     */
    protected static class PageClassConfigHandler extends DefaultHandler {

        private Map m_map;
        private StringBuffer m_buffer;
        private String m_url;
        private String m_className;

        public PageClassConfigHandler(Map map) {
            m_map = map;
            m_buffer = new StringBuffer();
        }

        @Override
        public void characters(char[] ch, int start, int len) {
            for (int i = 0; i < len; i++) {
                m_buffer.append(ch[start + i]);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qn) {
            if ( qn.equals("url") ) {
                m_url = m_buffer.toString().trim();
            } else if ( qn.equals("page-class") ) {
                m_className = m_buffer.toString().trim();
            } else if ( qn.equals("url-mapping") ) {
                m_map.put(m_url, m_className);
            }
            m_buffer = new StringBuffer();
        }
    }

}
