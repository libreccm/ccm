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
 */
package com.arsdigita.templating;

import com.arsdigita.bebop.Bebop; 
import com.arsdigita.caching.CacheTable;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.ExceptionUnwrapper;
import com.arsdigita.util.Exceptions;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An entry-point class for the functions of the templating package.
 *
 * This class maintains a cache of <code>XSLTemplate</code> objects, managed
 * via the <code>getTemplate</code> and <code>purgeTemplate</code> methods.
 *
 * @author Dan Berrange
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Templating.java 1498 2007-03-19 16:22:15Z apevec $
 */
public class Templating {

    private static final Logger s_log = Logger.getLogger(Templating.class);

    // just a tag to assure an implementation exists
//  public static final Class DEFAULT_PRESENTATION_MANAGER
//      = SimplePresentationManager.class;

    /**
     *  This is the name of the attribute that is set in the request whose
     *  value, if present, is a collection of TransformerExceptions that
     *  can be used to produce a "pretty" error.
     */
    public static final String FANCY_ERROR_COLLECTION = "fancyErrorCollection";

    // this was instantiated with hardcoded values, not anymore
    private static CacheTable s_templates = null;
    private static final TemplatingConfig s_config = new TemplatingConfig();

    static {
        s_config.load("ccm-core/templating.properties");
    }
    
    static {
        Exceptions.registerUnwrapper(
            TransformerException.class,
            new ExceptionUnwrapper() {
                public Throwable unwrap(Throwable t) {
                    TransformerException ex = (TransformerException)t;
                    return ex.getCause();
                }
            });
    }

    // now we initiate the CacheTable here
    static {
        // default cache size used to be 50, which is too high I reckon,
        // each template can eat up to 4 megs
        Integer setting = s_config.getCacheSize();
        int cacheSize = (setting == null ? 10 : setting.intValue());

        setting = s_config.getCacheAge();
        int cacheAge = (setting == null ? 60*60*24*3 : setting.intValue());

        s_templates = new CacheTable("templating", cacheSize, cacheAge);
        s_templates.setPurgeAllowed(true);
    }

    /**
     * Gets the <code>TemplatingConfig</code> record.
     *
     * @return The <code>TemplatingConfig</code> of this runtime
     */
    public static TemplatingConfig getConfig() {
        return s_config;
    }

    /**
     * Returns a new instance of the current presentation manager class. This is
     * an object which has the {@link com.arsdigita.templating.PresentationManager
     * PresentationManager} interface which can be used to transform an XML
     * document into an output stream.
     *
     * As of v ersion 6.6.0 the bebop framework is the only instance to provide
     * an implementation. To avoid class hierachie kludge we directly return the
     * bebop config here.
     *
     * @return an instance of the <code>PresentationManager</code>
     * interface
     */
    /* NON Javadoc
     * Used to be deprecated up to version 6.6.0. Reverted to non-deprecated.
     * Package templating provides the basic mechanism for CCM templating
     * machinerie and provides the Presentation Manager interface. It should be
     * able to be queried for an implementation as well.
     * @ deprecated Use {@link
     * com.arsdigita.bebop.BebopConfig#getPresentationManager()}
     * instead.
     */
    public static PresentationManager getPresentationManager() {
        return Bebop.getConfig().getPresentationManager();
    }

    /**
     * Retrieves an XSL template. If the template is already loaded in the
     * cache, it will be returned.  If the template has been modified since
     * it was first generated, it will be regenerated first.
     *
     * @param  source the <code>URL</code> to the top-level template resource
     * @return an <code>XSLTemplate</code> instance representing
     *         <code>source</code>
     */
    public static synchronized XSLTemplate getTemplate(final URL source) {
        return getTemplate(source, false, true);
    }
        
    /**
     * Retrieves an XSL template. If the template is already loaded in the
     * cache, it will be returned.  If the template has been modified since
     * it was first generated, it will be regenerated first.
     *
     * @param  source the <code>URL</code> to the top-level template resource
     * @param  fancyErrors Should this place any xsl errors in the request
     *                    for use by another class.  If this is true, the
     *                    the errors are stored for later use.
     * @param  useCache Should the templates be pulled from cache, if available?
     *                 True means they are pulled from cache.  False means
     *                 they are pulled from the disk.  If this is false
     *                 the pages are also not placed in the cache.
     * @return an <code>XSLTemplate</code> instance representing
     *         <code>source</code>
     */
    public static synchronized XSLTemplate getTemplate(final URL source,
                                                       boolean fancyErrors,
                                                       boolean useCache) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting template for URL " + source);
        }

        Assert.exists(source, URL.class);

        XSLTemplate template = null;
        if (useCache) {
            template = (XSLTemplate) s_templates.get(source.toString());
        }

        if (template == null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("The template for URL " + source + " is not " +
                           "cached; creating and caching it now");
            }

            if (fancyErrors) {
                LoggingErrorListener listener = new LoggingErrorListener();
                Web.getRequest().setAttribute(FANCY_ERROR_COLLECTION,
                                              listener.getErrors());
                template = new XSLTemplate(source, listener);
            } else {
                template = new XSLTemplate(source);
            }

            if (useCache) {
                s_templates.put(source.toString(), template);
            }
        } else if (Kernel.getConfig().isDebugEnabled()
                       && template.isModified()) {
            // XXX referencing Kernel above is a broken dependency.
            // Debug mode should be captured at a lower level,
            // probably on UtilConfig.

            if (s_log.isInfoEnabled()) {
                s_log.info("Template " + template + " has been modified; " +
                           "recreating it from scratch");
            }

            if (fancyErrors) {
                LoggingErrorListener listener = new LoggingErrorListener();
                Web.getRequest().setAttribute(FANCY_ERROR_COLLECTION,
                                              listener.getErrors());
                template = new XSLTemplate(source, listener);
            } else {
                template = new XSLTemplate(source);
            }

            s_templates.put(source.toString(), template);
        }

        return template;
    }

    /**
     * Resolves and retrieves the template for the given request.
     *
     * @param  sreq The current request object
     * @return The resolved <code>XSLTemplate</code> instance
     */
    public static XSLTemplate getTemplate(final HttpServletRequest sreq) {
        return getTemplate(sreq, false, true);
    }

    /**
     * Resolves and retrieves the template for the given request.
     *
     * @param sreq The current request object
     * @param fancyErrors Should this place any xsl errors in the request
     *                    for use by another class.  If this is true, the
     *                    the errors are stored for later use.
     * @param useCache Should the templates be pulled from cache, if available?
     *                 True means they are pulled from cache.  False means
     *                 they are pulled from the disk.  If this is false
     *                 the pages are also not placed in the cache.
     * @return The resolved <code>XSLTemplate</code> instance
     */
    public static XSLTemplate getTemplate(final HttpServletRequest sreq,
                                          boolean fancyErrors,
                                          boolean useCache) {
        Assert.exists(sreq, HttpServletRequest.class);

        final URL sheet = getConfig().getStylesheetResolver().resolve(sreq);

        Assert.exists(sheet, URL.class);

        return Templating.getTemplate(sheet, fancyErrors, useCache);
    }

    /**
     * Removes an XSL template from the internal cache.  The template
     * for <code>source</code> will be regenerated on the next request
     * for it.
     *
     * @param source the <code>URL</code> to the top-level template
     * resource
     */
    public static synchronized void purgeTemplate(final URL source) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Purging cached template for URL " + source);
        }

        Assert.exists(source, URL.class);

        s_templates.remove(source.toString());
    }

    /**
     * Removes all cached template objects.  All template objects will
     * be regenerated on-demand as each gets requested.
     */
    public static synchronized void purgeTemplates() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Purging all cached templates");
        }

        s_templates.removeAll();
    }

    /**
     * Generates a stream containing imports for a number of URLs.
     *
     * @param paths An iterator of <code>java.net.URL</code> objects
     * @return a virtual XSL file
     */
    public static InputStream multiplexXSLFiles(Iterator paths) {
        StringBuffer buf = new StringBuffer();
        Element root = new Element("xsl:stylesheet",
                                   "http://www.w3.org/1999/XSL/Transform");
        root.addAttribute("version", "1.0");

        while (paths.hasNext()) {
            URL path = (URL)paths.next();

            Element imp = root.newChildElement(
                "xsl:import",
                "http://www.w3.org/1999/XSL/Transform");
            imp.addAttribute("href", path.toString());

            if (s_log.isInfoEnabled()) {
                s_log.info("Adding import for " + path.toString());
            }
        }

        Document doc = null;
        try {
            doc = new Document(root);
        } catch (ParserConfigurationException ex) {
            throw new UncheckedWrapperException("cannot build document", ex);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("XSL is " + doc.toString(true));
        }

        return new ByteArrayInputStream(doc.toString(true).getBytes());
    }

    // Transforms a URL, short-circuiting access to the resource
    // servlet. This xfrms most http:// urls into file:// allowing
    // XSLT invalidation to work for these resources. It has the
    // added benefit of speeding up loading of XSL...
    static URL transformURL(URL url) {
        HttpHost self = Web.getConfig().getHost();

        String path = url.getPath();
        if (self.getName().equals(url.getHost()) &&
            ((self.getPort() == url.getPort()) ||
             (url.getPort() == -1 && self.getPort() == 80))) {

            if (path.startsWith("/resource")) {
                // A virtual path to the servlet
                path = path.substring("/resource".length());
                URL newURL = Web.findResource(path);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Transforming resource " + url + " to " + newURL);
                }
                return newURL;
            } else {
                // A real path to disk
                final String filename = Web.getServletContext().getRealPath(path);
                File file = new File(filename);
                if (file.exists()) {
                    try {
                        URL newURL = file.toURL();
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Transforming resource " +
                                        url + " to " + newURL);
                        }
                        return newURL;
                    } catch (MalformedURLException ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("File " + filename + " doesn't exist on disk");
                    }
                }
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("URL " + url + " is not local");
            }
        }

        return url;
    }
}

class LoggingErrorListener implements ErrorListener {
    private static final Logger s_log = 
        Logger.getLogger(LoggingErrorListener.class);

    private ArrayList m_errors;
    
    LoggingErrorListener() {
        m_errors = new ArrayList();
    }
    
    public Collection getErrors() {
        return m_errors;
    }
    
    public void warning(TransformerException e) throws TransformerException {
        log(Level.WARN, e);
    }
    
    public void error(TransformerException e) throws TransformerException {
        log(Level.ERROR, e);
    }
    
    public void fatalError(TransformerException e) throws TransformerException {
        log(Level.FATAL, e);
    }
    
    private void log(Level level, TransformerException ex) {
        s_log.log(level, "Transformer " + level + ": " +
                  ex.getLocationAsString() + ": " + ex.getMessage(),
                  ex);
        m_errors.add(ex);
    }
}
