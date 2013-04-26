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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

// Developer's Note:
// Class is currently in a transistory state. ServiceServlet itself does process
// the request following the new legacy free web application model (i.e. as a
// servlet based on BaseApplicationSerevlet / HTTPServlet).
// The methods used to invoke the service classes follow the legacy dispatcher
// model as set up by BaseApplicationServlet (see #makeLegacyContext). They 
// should be refactored to work without LegacyContext, probably as a servlet 
// as well or a legacy free dispatcher / ResourceHandler.

/**
 * CMS Service application servlet serves all request made for the CMS 
 * service application. 
 * 
 * In many cases a service will open a (Web) page, e.g. the download page for
 * files of a page to provide details or feedback/results of the service. But
 * a service may also work without any visual output.
 * 
 * URLs of the available services are stored in a XML file which is processed
 * into a cache of services on a request by request basis (lazy loading).
 * 
 * ServiceServlet is called by BaseApplicationServlet which has determined that
 * ServiceServlet is associated with a request URL.
 * 
 * The CMS Service determines whether a <tt>Page</tt> has been registered to
 * the URL and if so passes the request to that serviceResource.
 *
 * If no <tt>Page</tt> is registered to the URL, then the CMS Service hands 
 * the request to the TemplateResolver to find an appropriate JSP file.
 * 
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: ServiceServlet.java 2161 2011-02-02 00:16:13Z pboy $
 */
public class ServiceServlet extends BaseApplicationServlet {

    /**Error logging    */
    private static Logger s_log = Logger
                                  .getLogger(ServiceServlet.class.getName());


    /** The path of the file that maps resources (relative urls - corresponding
     *  class names).                                                         */
    private final static String MAP_FILE = "WEB-INF/resources/cms-service-map.xml";

    /** Mapping between a relative URL and the class name of a service.       */
    private static HashMap s_serviceClasses = new HashMap();

    /** Instantiated services cache. This allows for lazy loading of the class
     *  (i.e. irs ResourceHandler) for each service.                          */
    private static SimpleCache s_services = new SimpleCache();

    /** Path to directory containg ccm-cms template files, used in case of fall
     *  back, when no service class is found in s_serviceClasses rsp. MAP_FILE */
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

        /* Process mapping file and fill up s_serviceClasses.                 */
        readFromFile(MAP_FILE);

        /** Set Template base path for JSP's                                  */
        m_templatePath = ContentSection.getConfig().getTemplateRoot();
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
     * perform the services.
     * @param sreq 
     * @param sresp 
     * @param app 
     * @throws ServletException 
     * @throws IOException 
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

        /* Developer's Note:
         * Legacy context, established by BaseApplicationServlet, currently
         * KernelContext. Not used in ServiceServlet, but required to invoke
         * the Resource to provide the service by interface definition. 
         * Cuurently (version 6.6.8) not used in any service class.
         */
        RequestContext ctx = DispatcherHelper.getRequestContext();        

        /* Get the service being requested, i.e. the remaining URL following
         * the servlet address ( ccm/cms-service by default)                  */
        String url = sreq.getPathInfo();
        if (url.length() > 1 && url.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" serviceResource, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  
             * The service classes are currently not real applications, so no
             * adaptation required here.                                      */
            // url = url.substring(0, url.length() - 1);
        }

        // User access can not be checked here, but has to be checked by each
        // service!

        /* Determine the service requested by url                             */
        ResourceHandler serviceResource = getResource(url);
        
        if ( serviceResource != null ) {
            // Serve the serviceResource.
            serviceResource.init();
            serviceResource.dispatch(sreq, sresp, ctx);
        } else {
            // Fall back on the JSP application dispatcher.
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
                sresp.sendError(404, sreq.getRequestURI() + " not found on this server.");
            }

        }


        DeveloperSupport.endStage("ServiceServlet.doService");
        if (s_log.isDebugEnabled()) {
            s_log.info("doService method completed");
        }
    }


    /**
     * Determines the Resource (ie class) to serve a requested service based
     * on its url.
     *
     * Returns a RecourceHandler for the requested service if it could be found,
     * i.e. a dispatcher class whose dispatch method invokes the service.
     * 
     * @param url The URL stub following the site-node URL
     * @return A ResourceHandler or null if none exists.
     * @pre (url != null)
     */
    private ResourceHandler getResource(String url) throws ServletException {

        // First check the pages cache for existing pages.
        ResourceHandler page = (ResourceHandler) s_services.get(url);
        if ( page == null ) {

            // Next check if the URL maps to a serviceResource class.
            String pageClassName = (String) s_serviceClasses.get(url);
            if ( pageClassName != null ) {

                Class pageClass;
                try {
                    pageClass = Class.forName(pageClassName);
                } catch (ClassNotFoundException e) {
                    s_log.error("error fetching class for ResourceHandler", e);
                    throw new ServletException(e);
                }

                // Try and instantiate the serviceResource.
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
                s_services.put(url, page);
            }
        }
        return page;
    }


    /**
     *
     * Initializes URL-to-Page (class) mappings from a file.
     *
     * Format of the file is XML:
     * <pre>
     * &lt;dispatcher-configuration>
     *   &lt;url-mapping
     *     &lt;url>my-serviceResource&lt;/url>
     *     OR &lt;serviceResource-class>com.arsdigita.Page&lt;/page-class>
     *   &lt;url-mapping
     * &lt;/dispatcher-configuration>
     * </pre>
     */
    private void readFromFile(final String file) {

        XML.parseResource(file, new PageClassConfigHandler(s_serviceClasses));

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
