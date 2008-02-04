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

import com.arsdigita.developersupport.DeveloperSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  Base class for
 * a generic URL-to-Dispatcher mapping dispatch.  This class may
 * be used directly by applications, or it may be subclassed to
 * be coded with a specific
 * map or override the map lookup for certain sets of URLs.
 * <p>
 * URLs are mapped to Dispatcher objects.  When the dispatch method is called,
 * this first looks up the remainingURL (from the request context) in the map and
 * dispatches to the target object.  Then we call
 * the dispatch method on the target.
 *
 * <p>A configurable "not found" dispatcher is available to handle the case
 * where no target is mapped for a URL.  By default, this is set to
 * JSPApplicationDispatcher. URLs that are not explicitly handled by a
 * dispatcher in the map will be directed to assets in the file system.
 *
 * <p><b>Note:</b> The MapDispatcher
 * does not automatically set the status code to 404 when there is no
 * exact mapping. If a not-found handler is specified, it is up to this
 * specified dispatcher to set the status code appropriately.
 *
 * <p>Note that any URLs that do not have a file extension will
 * automatically be required to have a trailing slash.  Your relative
 * links must compensate for this.
 *
 * <p>Example: Assume we set up a map dispatcher as follows:
 * <pre>
 * MapDispatcher mapDisp = new MapDispatcher();
 * // default not-found dispatcher is JSPApplicationDispatcher
 * Map m = new HashMap();
 * m.put("page1", page1Dispatcher);
 * m.put("page2", page2Dispatcher);
 * mapDisp.setMap(m);
 * </pre>
 *
 * If this map dispatcher is associated with a package type
 * "map-app" mounted on the site node "map", then request URLs will be
 * mapped to actions as follows:
 *
 * <table>
 * <tr><th>request URL <th>action </tr>
 * <tr><td>/map/page1 <td>redirected automatically to /map/page1/
 * <tr><td>/map/page1/ <td>page1Dispatcher.dispatch(...);
 * <tr><td>/map/page2.ext <td> page2Dispatcher.dispatch(...);
 * <tr><td>/map/image.gif <td>  serve file /packages/map-app/www/image.gif
 * <tr><td>/map/page.jsp <td>  serve file /packages/map-app/www/page.jsp
 * <tr><td>/map <td> redirected to /map/
 * <tr><td>/map/ <td>  serve file /packages/map-app/www/index.jsp
 * </table>
 *
 * @author Bill Schneider  */
public class MapDispatcher implements Dispatcher {

    public static final String versionId = "$Id: MapDispatcher.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Map m_map = new HashMap();
    // accesses to m_map must be synchronized

    private Dispatcher m_notFoundHandler;

    private java.io.File m_configFile;
    private long m_configFileTimeRead;

    private static final Logger s_log =
        Logger.getLogger(MapDispatcher.class.getName());

    /* Creates MapDispatcher with empty URL mapped to "/".
    **/
    public MapDispatcher() {
        m_notFoundHandler = JSPApplicationDispatcher.getInstance();
    }

    /**
     * Looks up a target in the page map based on the remaining URL.
     * If that page is found, serves it.  Otherwise serves a default
     * not-found page.
     */
    public void dispatch(HttpServletRequest req,
                         HttpServletResponse resp,
                         RequestContext ctx)
        throws IOException, ServletException {
        s_log.debug("MapDispacher.dispatch called");

        DeveloperSupport.startStage("MapDispatcher.dispatch");
        String url = ctx.getRemainingURLPart();

        Dispatcher target;
        String mapURL = null;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Searching for URL '" + url + "' in map '" + 
                        m_map + "'");
        }

        synchronized(this) {
            if (m_configFile != null &&
                m_configFile.lastModified() > m_configFileTimeRead) {
                // re-read configuration file
                readFromFile(m_configFile);
            }
            target = (Dispatcher)m_map.get(url);
            if (target == null && url.endsWith("/")) {
                String strippedURL = url.substring(0, url.length() - 1);
                target = (Dispatcher)m_map.get(strippedURL);
                mapURL = strippedURL;
            } else {
                mapURL = url;
            }
        }
        if (target == null) {
            s_log.debug
                ("No URL match found; trying to serve the not-found page");

            // if we don't have a match, try to serve default
            // not-found page
            // note: it is up to the not-found-handler to set 404
            // if so desired.

            target = m_notFoundHandler;
        }
        if (target != null) {
            preprocessRequest(req, resp, ctx, mapURL);
            target.dispatch(req, resp, ctx);
        } else {
            // default not-found page in case 404 and no provided
            // not-found error page
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            PrintWriter pw = new PrintWriter(resp.getWriter());
            pw.println("<h2>Not found</h2>");
            pw.print("Could not find the page you requested: ");
            pw.println(ctx.getOriginalURL());
            pw.close();
        }
        DeveloperSupport.endStage("MapDispatcher.dispatch");
    }

    /**
     * Sets the page map for this dispatcher.
     * @param m the page map (maps URLs to Pages).
     */
    public synchronized void setMap(Map m) {
        m_map = m;
    }

    /**
     * Provides the opportunity for subclasses to do
     * some preprocessing of a given url, before it
     * is handed off to the main dispatcher. The typical
     * action is to set the cache control policy.
     */
    protected void preprocessRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext ctx,
                                     String url) {
        // Nothing to do by default.
    }

    /**
     * Returns the page map for this dispatcher.  Because it exposes
     * the dispatcher to external mutation, this method is protected
     * so that it may only be called by subclasses and other classes
     * in com.arsdigita.dispatcher.
     * @return the URL-target page map for this dispatcher.
     */
    protected Map getMap() {
        return m_map;
    }

    /**
     * Adds a new dispatcher to the page map to handle  a particular
     * URL.
     * @param url dispatch to this handler when url ==
     * requestContext.remainingURL()
     * @param d the dispatcher
     */
    public final synchronized void addPage(String url, Dispatcher d) {
        m_map.put(url, d);
    }

    /**
     * Sets the default page to display if no page can be found for the
     * URL in the page map on dispatch.
     *
     * @param d the dispatcher to use when no other handler can be found
     * for a URL in the page map.
     */
    public final void setNotFoundDispatcher(Dispatcher d) {
        m_notFoundHandler = d;
    }

    /**
     * @deprecated Any URL in the map that does not end in an extension
     * will be treated as a virtual directory and required to have
     * a trailing slash.
     */
    public void requireTrailingSlash(String url) { }

    /**
     * 
     * Initializes URL-to-Page/Dispatcher/Servlet mappings from a file.
     *
     * Format of the file is XML:
     * <pre>
     * &lt;dispatcher-configuration>
     *   &lt;url-mapping
     *     &lt;url>my-page&lt;/url>
     *     &lt;dispatcher-class>com.arsdigita.Dispatcher.class&lt;/dispatcher-class>
     *     OR &lt;page-class>com.arsdigita.Page.class&lt;/page-class>
     *     OR &lt;servlet-name>servlet-pretty-name-from-web.xml&lt;/servlet-name>
     *
     *   &lt;url-mapping
     *   ...
     *   &lt;default-handler>
     *     (same options as above: dispatcher-class, page-class, servlet-name)
     *   &lt;/default-handler>
     * &lt;/dispatcher-url-map>
     * </pre>
     */
    public final void readFromFile(java.io.File file) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            HashMap m = new HashMap();
            this.setMap(m);
            // set to empty map so we can add mappings incrementally
            parser.parse(file, newParseConfigHandler(this));
            m_configFile = file;
            m_configFileTimeRead = System.currentTimeMillis();
        } catch (ParserConfigurationException e) {
            s_log.error("error parsing dispatcher config", e);
        } catch (SAXException e) {
            s_log.error("error parsing dispatcher config", e);
        } catch (IOException e) {
            s_log.error("error parsing dispatcher config", e);
        }
    }

    // protected visibility intentional.
    /**
     * <b><font color="red">Expirimental</font></b>
     * Returns a SAX event handler object for setting up a MapDispatcher
     * using an XML config file.
     * @param md the MapDispatcher to configure
     * @return a SAX DefaultHandler object for handling SAX events
     * @pre md.m_map != null
     */
    protected DefaultHandler newParseConfigHandler(MapDispatcher md) {
        return new ParseConfigHandler(md);
    }

    /**
     * 
     * SAX event handler class for parsing a dispatcher configuration file.
     * The format of the configuration file is
     * <pre>
     * &lt;dispatcher-configuration>
     *   &lt;url-mapping>
     *     &lt;url>/url/to/map&lt;/url>
     *     &lt;servlet-name>servlet-name-in-web-xml&lt;/servlet-name>
     *   &lt;/url-mapping>
     *   &lt;url-mapping>
     *     &lt;url>/another/url&lt;/url>
     *     &lt;dispatcher-class>mypackage.MyDispatcher&lt;/dispatcher-class>
     *   &lt;/url-mapping>
     *   ... etc ...
     *
     *   &lt;default-handler>
     *     &lt;dispatcher-class>mypackage.MyDispatcher&lt;/dispatcher-class>
     *     (or)
     *     &lt;servlet-name>servlet-name&lt;/servlet-name>
     *   &lt;/default-handler>
     * &lt;/dispatcher-configuration>
     * </pre>
     *
     * @author Bill Schneider 
     */
    protected static class ParseConfigHandler extends DefaultHandler {
        private MapDispatcher m_map;
        protected StringBuffer m_buffer;
        private String m_url;
        protected Dispatcher m_dispatcher;

        protected ParseConfigHandler(MapDispatcher m) {
            m_map = m;
            m_buffer = new StringBuffer();
        }

        public void characters(char[] ch, int start, int len) {
            for (int i = 0; i < len; i++) {
                m_buffer.append(ch[start + i]);
            }
        }

        public void endElement(String uri, String localName, String qname) {
            if (qname.equals("url")) {
                m_url = m_buffer.toString().trim();
            } else if (qname.equals("servlet-name")) {
                final String servletName = m_buffer.toString().trim();
                // wrap dispatch to servlet inside a dispatcher
                m_dispatcher = new Dispatcher() {
                        public void dispatch(HttpServletRequest req,
                                             HttpServletResponse resp,
                                             RequestContext rctx)
                            throws IOException, ServletException {
                            DispatcherHelper.forwardRequestByName(servletName,
                                                                  req, resp);
                        }
                    };
            } else if (qname.equals("dispatcher-class")) {
                try {
                    Class pclass = Class.forName(m_buffer.toString().trim());
                    m_dispatcher = (Dispatcher)pclass.newInstance();
                } catch (Exception e) {
                    s_log.error("error in parsing config file", e);
                }
            } else if (qname.equals("url-mapping")) {
                // close off this current mapping
                m_map.addPage(m_url, m_dispatcher);
            } else if (qname.equals("default-handler")) {
                // close off this current mapping
                m_map.setNotFoundDispatcher(m_dispatcher);
            }
            // on all elements
            m_buffer = new StringBuffer();
        }
    }
}
