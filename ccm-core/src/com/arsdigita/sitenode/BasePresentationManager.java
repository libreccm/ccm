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
package com.arsdigita.sitenode;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.profiler.Profiler;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Debugger;
import com.arsdigita.web.TransformationDebugger;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;

/**
 * Class for managing and obtaining a Stylesheet based on the current
 * request's location in the site map.  First, we try to find a
 * stylesheet specific to this site node.  If we can't find one, then
 * we walk up the site map until we find a parent of this site node
 * that has a stylesheet associated with it.
 *
 * If we haven't found one by the time we reach the root, then we'll
 * do the same tree walk except we'll look for the stylesheet
 * associated with the <em>package</em> mounted on each site node.
 *
 * @deprecated Use {@link com.arsdigita.templating.SimplePresentationManager} instead.
 *
 * @author Bill Schneider
 * @version $Id: BasePresentationManager.java 562 2005-06-12 23:53:19Z apevec $
 */
public class BasePresentationManager implements PresentationManager {

    private static final Logger s_log = Logger.getLogger
        (BasePresentationManager.class);

    // this maps site nodes to stylesheets

    // stylesheet transformer for this site node.
    // TODO: NEED CACHE EVICTION!
    private Map m_stylesheetMap = new HashMap();

    private static BasePresentationManager s_instance =
        new BasePresentationManager();

    public static final String CACHE_NONE    = "none";
    public static final String CACHE_DISABLE = "disable";
    public static final String CACHE_USER    = "user";
    public static final String CACHE_WORLD   = "world";

    private static String m_defaultCachePolicy = CACHE_DISABLE;

    public static void setDefaultCachePolicy(final String policy) {
        m_defaultCachePolicy = policy;
    }

    /**
     * Use this instead of getWriter for servlet engines that are
     * slighlty broken. If the servlet engine uses
     * <code>getOutputStream()</code> and the presentation manager is
     * asked to serve a page, <code>getWriter()</code> will throw an
     * {@link java.lang.IllegalStateException} (see the servlet
     * spec). In that case, this method returns a print writer created
     * from the output stream for the specified response.
     *
     * In view of the above, there is no guarantee that the character
     * encoding used by the <code>PrintWriter</code> returned by this
     * method will be the one specified by the <code>charset</code>
     * parameter. To be more precise, if the <code>PrintWriter</code>
     * is constructed from the <code>OutputStream</code> returned by
     * <code>resp.getOutputStream()</code>, then it will have the
     * specified character encoding.
     *
     * If the <code>PrintWriter</code> is the one returned by
     * <code>resp.getWriter()</code> then its character encoding is
     * the one specified in the <code>charset= property</code> of the
     * setContentType(String) method, which must be called
     * <i>before</i> calling this method for the charset to take
     * effect. (See <a
     * href="http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/ServletResponse.html#getWriter()">getWriter()</a>)
     *
     * @param resp the response oject
     * @param charset the character encoding (see <a
     * href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">Character
     * Encoding</a>). If this is <code>null</code>, then the default
     * system encoding will be used (typically "ISO-8859-1").
     */
    protected static PrintWriter getPrintWriter(HttpServletResponse resp,
                                                String charset)
            throws IOException {
        try {
            return resp.getWriter();
        } catch (IllegalStateException e) {
            s_log.debug("Using getOutputStream instead of getWriter");

            if (charset == null) {
                return new PrintWriter
                    (new OutputStreamWriter(resp.getOutputStream()));
            }

            try {
                return new PrintWriter
                    (new OutputStreamWriter(resp.getOutputStream(), charset));
            } catch (UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException
                    (charset + " is not a supported charset", ex);
            }
        }
    }

    public static PresentationManager getInstance() {
        return s_instance;
    }

    /**
     * Returns a Stylesheet for the current request context, searching
     * the current site node, all of its parent site nodes, and the default
     * stylesheets for the packages mounted on this site node and the
     * parent site nodes.
     *
     * @return A stylesheet associated with the current
     * site node (highest precedence), the stylesheet for its parent
     * site nodes (precedence decreasing in distance from this),
     * the default stylesheet for the mounted package on this site node,
     * or the default stylesheets for the packages mounted on ancestors
     * of this site node.  (lowest precedence)
     */
    protected Stylesheet findStylesheet(SiteNodeRequestContext rctx) {
        return findStylesheet(rctx.getSiteNode(),
                              rctx.getLocale(),
                              rctx.getOutputType());
    }


    /**
     * Returns a Stylesheet for the current request context, searching
     * the current site node, all of its parent site nodes, and the default
     * stylesheets for the packages mounted on this site node and the
     * parent site nodes.
     *
     * @return A stylesheet associated with the current
     * site node (highest precedence), the stylesheet for its parent
     * site nodes (precedence decreasing in distance from this),
     * the default stylesheet for the mounted package on this site node,
     * or the default stylesheets for the packages mounted on ancestors
     * of this site node.  (lowest precedence)
     */
    protected synchronized Stylesheet findStylesheet
        (SiteNode sn, Locale l, String outputType) {

        String mapKey = sn.getNodeId() + "|"
            + l.toString() + "|" + outputType;

        Stylesheet stylesheet =
            (Stylesheet)m_stylesheetMap.get(mapKey);

        if (stylesheet != null && stylesheet.isValid()) {
            return stylesheet;
        }

        // at this point our stylesheet has definitely expired, so it
        // needs to be re-built.
        stylesheet = findStylesheetHelper(sn, l, outputType);
        stylesheet.disconnect();
        m_stylesheetMap.put(mapKey, stylesheet);
        return stylesheet;
    }

    private Stylesheet findStylesheetHelper(SiteNode sn,
                                            Locale l,
                                            String outputType) {
        TransactionContext tctx =
            SessionManager.getSession().getTransactionContext();
        boolean callerStartedTransaction = tctx.inTxn();
        if (!callerStartedTransaction) {
            tctx.beginTxn();
        }
        SiteNode thisNode = sn;
        Stylesheet thisStyle = null;
        while (thisNode != null && thisStyle == null) {
            // no style for this sn and we still have a parent...
            // so try the parent stylesheet
            thisStyle = thisNode.getStylesheet(l, outputType);
            thisNode = thisNode.getParent();
        }

        // iterate again, but for packages
        thisNode = sn;
        while (thisNode != null && thisStyle == null) {
            PackageInstance pi = thisNode.getPackageInstance();
            if (pi != null) {
                thisStyle = pi.getType().getStylesheet(l, outputType);
            }
            thisNode = thisNode.getParent();
        }

        if (!callerStartedTransaction) {
            tctx.commitTxn();
        }
        return thisStyle;
    }

    /**
     * Uses {@link #servePage(Document, HttpServletRequest,
     * HttpServletResponse, Map)} to implement the
     * <code>PresentationManager</code> interface.
     */
    public void servePage(final Document doc,
                          final HttpServletRequest req,
                          final HttpServletResponse resp) {
        servePage(doc, req, resp, null);
    }

    /**
     * Serves an XML Document, getting and applying the appropriate XSLT.
     * Also allows for parameters to be set in transformer.  These
     * will become top-level xsl:params in the stylesheet.  The
     * "contextPath" parameter will always be passed to XSLT, which is
     * the value of <code>req.getContextPath()</code>
     *
     * @param doc the Bebop page to serve
     * @param req the servlet request
     * @param resp the servlet response
     * @param params a set of name-value pairs to pass as parameters
     * to the Transformer
     */
    public void servePage(Document doc,
                          HttpServletRequest req,
                          HttpServletResponse resp,
                          Map params) {
        final RequestContext ctx = DispatcherHelper.getRequestContext(req);

        try {
            DeveloperSupport.startStage("PresMgr get stylesheet");
            Profiler.startOp("XSLT");

            final String defaultCharset = Globalization.getDefaultCharset
                (ctx.getLocale());

            // always get the stylesheet; won't always actually rebuild.
            Stylesheet ss = findStylesheet((SiteNodeRequestContext) ctx);

            // If no cache policy is set by ealier dispatcher,
            // then aggressively defeat caching...
            if (CACHE_WORLD.equals(m_defaultCachePolicy)) {
                DispatcherHelper.maybeCacheForWorld(resp);
            } else if (CACHE_USER.equals(m_defaultCachePolicy)) {
                DispatcherHelper.maybeCacheForUser(resp);
            } else if (CACHE_DISABLE.equals(m_defaultCachePolicy)){
                DispatcherHelper.maybeCacheDisable(resp);
            } else {
                // No default cache policy at all!
            }

            final String output = req.getParameter("output") == null ?
                "html" : req.getParameter("output");

            final PrintWriter writer;

            if (ctx.getDebuggingXML() || output.equals("xml")) {
                resp.setContentType("text/xml; charset=" + defaultCharset);
                writer = getPrintWriter(resp, defaultCharset);
                writer.print(doc.toString(true));
            } else if (ctx.getDebuggingXSL() || output.equals("xsl")) {
                resp.reset();
                resp.setContentType("application/zip");
                resp.setHeader
                    ("Content-Disposition", "attachment; filename=\"" +
                     "styles.jar\"");

                OutputStream os = resp.getOutputStream();
                byte[] jarFile =
                    ss.getAllStylesheetContents(ctx.getServletContext());
                os.write(jarFile, 0, jarFile.length);
                resp.flushBuffer();
            } else {
                Transformer xf = null;
                if (ss != null) {
                    xf = ss.newTransformer();
                }
                if (ss == null || xf == null) {
                    throw new ServletException
                        ("No stylesheet available for request!");
                }

                DeveloperSupport.endStage("PresMgr get stylesheet");

                completeTransaction(req);

                DeveloperSupport.startStage("PresMgr transform");

                // Before sending output, make sure we set content type.
                resp.setContentType(ctx.getOutputType() + "; " +
                                    "charset=" + defaultCharset);
                writer = getPrintWriter(resp, defaultCharset);

                // Transformers are not thread-safe, so we assume we have
                // exclusive use of xf here.  But we could recycle it.
                xf.clearParameters();

                if (params != null) {
                    Iterator entries = params.entrySet().iterator();

                    while (entries.hasNext()) {
                        Map.Entry ent = (Map.Entry) entries.next();

                        xf.setParameter((String) ent.getKey(), ent.getValue());
                    }
                }

                PageTransformer.addXSLParameters(xf, req);

                // This has no effect on the resulting encoding of the
                // output generated by the XSLT transformer. Why?
                // Because we pass the transformer an instance of the
                // Writer class. The Writer class provides no methods
                // for changing the encoding. So, the only thing this
                // does is, it causes the transformer to include a
                // line like <meta http-equiv="Content-Type"
                // content="text/html; charset=foo"> in the output
                // document.
                xf.setOutputProperty("encoding", defaultCharset);

                xf.transform(new DOMSource(doc.getInternalDocument()),
                             new StreamResult(writer));

                DeveloperSupport.endStage("PresMgr transform");

                if (Kernel.getConfig().isDebugEnabled()) {
                    Document origDoc = (Document) req.getAttribute
                        ("com.arsdigita.xml.Document");

                    ServletContext sctx = ctx.getServletContext();
                    String path = ss.getPath();
                    java.net.URL sheet = new File(sctx.getRealPath(path)).toURL();
                    Debugger.addDebugger
                      (new TransformationDebugger(origDoc,
                                                  doc,
                                                  sheet,
                                                  ss.getStylesheetList(sctx)));

                    writer.print(Debugger.getDebugging(req));
                }
            }
        } catch (TransformerConfigurationException tce) {
            throw new UncheckedWrapperException (tce);
        } catch (TransformerException te) {
            throw new UncheckedWrapperException (te);
        } catch (ClassCastException cce) {
            throw new UncheckedWrapperException (cce);
        } catch (IOException ex) {
            throw new UncheckedWrapperException (ex);
        } catch (ServletException ex) {
            throw new UncheckedWrapperException (ex);
        } finally {
            Profiler.stopOp("XSLT");
        }
    }

    /**
     * Complete the transaction for this request.
     *
     * @param req The <code>HttpServletRequest</code> object for the request.
     */
    private void completeTransaction(HttpServletRequest req) {
        final String JSP_EXCEPTION_ATTRIBUTE =
            "javax.servlet.jsp.jspException";

        Session sess = SessionManager.getSession();
        // Commit transaction if no error was thrown,
        // otherwise abort.
        TransactionContext txc = sess.getTransactionContext();
        if (req.getAttribute(JSP_EXCEPTION_ATTRIBUTE) == null) {
            s_log.debug("trying to commit");
            Kernel.getContext().getTransaction().end();
            s_log.debug("request over, committing transaction");
        } else {
            s_log.debug("trying to abort");
            txc.abortTxn();
            s_log.debug("request over, aborting transaction");
        }
    }
}
