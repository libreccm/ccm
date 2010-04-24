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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.JSPApplicationDispatcher;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.security.Util;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.XML;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * <p> The Service Dispatcher handles common services shared across all
 * CMS instances. This dispatcher is called by the Subsite dispatcher.</p>
 *
 * <p>Here are the steps for a request to
 * <tt>http://yourserver/cms-service/cheese</tt>:</p>
 *
 * <ol>
 *   <li><p>A client sends a request to the web server, which passes it on to
 *   the global ACS dispatcher.</p></li>
 *
 *   <li><p>The global ACS dispatcher examines the first part of the URL,
 *   notices that the Content Center application is mounted at
 *   <tt>/cms-service</tt> and hands the request to the CMS
 *   Service dispatcher.</p></li>
 *
 *   <li><p>The CMS Service dispatcher determines whether a <tt>Page</tt>
 *   has been registered to the URL <tt>/cheese</tt>.</p></li>
 *
 *   <li><p>If no <tt>Page</tt> is registered to the URL <tt>/cheese</tt>,
 *   then the CMS Service dispatcher hands the request to the
 *   {@link com.arsdigita.dispatcher.JSPApplicationDispatcher},
 *   which serves JSPs and static content from the
 *   <tt>/packages/cms/www</tt>.</p></li>
 * </ol>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #13 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ServiceDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ServiceDispatcher extends LockableImpl implements Dispatcher {

    /** Error logging.  */
    private static Logger s_log =
        Logger.getLogger(ServiceDispatcher.class.getName());


    /** The path of the file that maps resources. */
    private final static String MAP_FILE = "WEB-INF/resources/cms-service-map.xml";

    /** Mapping between a relative URL and the class name of a ResourceHandler. */
    private static HashMap s_pageClasses = new HashMap();

    /**
     * Instantiated ResourceHandler cache. This allows for lazy loading.
     */
    private static SimpleCache s_pages = new SimpleCache();


    private Dispatcher m_notFoundHandler;
    private ArrayList m_trailingSlashList = new ArrayList();


    public ServiceDispatcher() {
        super();

        m_trailingSlashList = new ArrayList();
        requireTrailingSlash("");

        setNotFoundDispatcher(JSPApplicationDispatcher.getInstance());

        readFromFile(MAP_FILE);
    }


    /**
     * Handles requests made to the Content Center package.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException, ServletException {

        DeveloperSupport.startStage("ServiceDispatcher.dispatch");

        String url = actx.getRemainingURLPart();
        String originalUrl = actx.getOriginalURL();

        // Empty remaining URL and doesn't end in trailing slash:
        // probably want to redirect.
        if ( m_trailingSlashList.contains(url) && !originalUrl.endsWith("/") ) {
            DispatcherHelper.sendRedirect(response, originalUrl + "/");
            return;
        }

        // Check user access.
        checkUserAccess(request, response, actx);

        ResourceHandler page = getResource(url);
        if ( page != null ) {
            // Serve the page.
            page.init();
            page.dispatch(request, response, actx);
        } else {
            // Fall back on the JSP application dispatcher.
            m_notFoundHandler.dispatch(request, response, actx);
        }

        DeveloperSupport.endStage("ServiceDispatcher.dispatch");
    }

    /**
     * sets the default page to display if no page can be found for the
     * URL in the page map on dispatch.
     */
    public final void setNotFoundDispatcher(Dispatcher d) {
        m_notFoundHandler = d;
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
     * @deprecated subclasses of ResourceHandler should check the
     * permissions directly on the ContentItem being served.  This
     * method currently acts as a no-op
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     **/
    protected void checkUserAccess(HttpServletRequest request,
                                   HttpServletResponse response,
                                   RequestContext actx)
        throws ServletException {

        //    User user = KernelHelper.getCurrentUser(request);

        // If the user is not logged in, redirect to the login page.
        // Disabled totally bogus security check
        // This should be done by the resource handler impl itself
        // which can check real permissions
        //if ( user == null ) {
        //  redirectToLoginPage(request, response);
        // Break.
        //}
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
            +"?"+UserContext.RETURN_URL_PARAM_NAME
            +"="+UserContext.encodeReturnURL(req);
        try {
            LoginHelper.sendRedirect(req, resp, url);
        } catch (IOException e) {
            s_log.error("IO Exception", e);
            throw new ServletException(e.getMessage(), e);
        }
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
    private final void readFromFile(final String file) {

        if ( isLocked() ) {
            s_log.error(
                        "error in readFromFile: Cannot configure resources more than once.");
            throw new RuntimeException(
                                       "error in readFromFile: Cannot configure resources once the dispatcher is locked.");
        }

        XML.parseResource(file, newParseConfigHandler(s_pageClasses));

        lock();
    }


    /**
     * <b><font color="red">Expirimental</font></b>
     * Returns a SAX event handler object for setting up a MapDispatcher
     * using an XML config file.
     * @param map A map to configure
     * @return a SAX DefaultHandler object for handling SAX events
     * @pre md.m_map != null
     */
    protected DefaultHandler newParseConfigHandler(Map map) {
        return new PageClassConfigHandler(map);
    }


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

        public void characters(char[] ch, int start, int len) {
            for (int i = 0; i < len; i++) {
                m_buffer.append(ch[start + i]);
            }
        }

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
