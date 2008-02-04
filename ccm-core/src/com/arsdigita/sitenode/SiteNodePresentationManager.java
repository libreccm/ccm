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

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.profiler.Profiler;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 * Class for managing and obtaining Stylesheets.
 *
 * @deprecated Use {@link com.arsdigita.bebop.page.PageTransformer} in conjunction with {@link com.arsdigita.templating.LegacyStylesheetResolver}.
 * @author Bill Schneider
 * @version $Id: SiteNodePresentationManager.java 562 2005-06-12 23:53:19Z apevec $
 * @deprecated use BasePresentationManager instead.
 */
public class SiteNodePresentationManager implements PresentationManager {
    public static final String versionId =
        "$Id: SiteNodePresentationManager.java 562 2005-06-12 23:53:19Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (SiteNodePresentationManager.class);

    // this maps site nodes to stylesheets

    // stylesheet transformer for this site node, composed.
    // this is the moby-stylesheet composed of multiple ss fragments.

    // TODO: NEED CACHE EVICTION!
    private Map m_stylesheet_map = new HashMap();

    private static SiteNodePresentationManager s_instance =
        new SiteNodePresentationManager();

    public static PresentationManager getInstance() {
        return s_instance;
    }

    /**
     * Returns the stylesheet associated with a specific site
     * node, if there is one.  Does not take into account stylesheets
     * associated with parent site nodes or the default style sheet
     * for the package mounted on this site node.
     * @param sn the site node
     * @param l the desired locale for output
     * @param outputType the desired output type, e.g., text/html,
     * application/wap, etc.
     * @return a stylesheet object associated with this sitenode
     * with the desired locale and output type
     */
    private Stylesheet getStylesheet(SiteNode sn,
                                     Locale l,
                                     String outputType) {
        Stylesheet[] ssSet = sn.getStylesheets(l, outputType);
        return Stylesheet.combineStylesheets(ssSet);
    }

    /**
     * Returns a Stylesheet composed of rules from the XSL stylesheet
     * from this site node, all of its parent site nodes, and the default
     * stylesheets for the packages mounted on this site node and the
     * parent site nodes.
     *
     * @return A stylesheet composed of the stylesheet rules for this
     * site node (highest precedence), the stylesheet rules for parent
     * site nodes (precedence decreasing in distance from this),
     * the default stylesheet for the mounted package on this site node,
     * and the default stylesheets for the packages mounted on ancestors
     * of this site node.  (lowest precedence)
     */
    protected Stylesheet getComposedStylesheet(SiteNodeRequestContext rctx) {
        return getComposedStylesheet(rctx.getSiteNode(),
                                     rctx.getLocale(),
                                     rctx.getOutputType());
    }

    /**
     * Returns a Stylesheet composed of rules from the XSL stylesheet
     * from this site node, all of its parent site nodes, and the default
     * stylesheets for the packages mounted on this site node and the
     * parent site nodes.
     *
     * @return A stylesheet composed of the stylesheet rules for this
     * site node (highest precedence), the stylesheet rules for parent
     * site nodes (precedence decreasing in distance from this),
     * the default stylesheet for the mounted package on this site node,
     * and the default stylesheets for the packages mounted on ancestors
     * of this site node.  (lowest precedence)
     */
    protected Stylesheet getComposedStylesheet
        (SiteNode sn, Locale l, String outputType) {

        String mapKey = sn.getNodeId() + "|"
            + l.toString() + "|" + outputType;

        Stylesheet stylesheet =
            (Stylesheet)m_stylesheet_map.get(mapKey);

        if (stylesheet != null && stylesheet.isValid()) {
            return stylesheet;
        }

        synchronized(this.getClass()) {
            // at this point our stylesheet has definitely expired, so it
            // needs to be re-built.
            stylesheet = rebuildComposedStylesheet(sn, l, outputType);
            m_stylesheet_map.put(mapKey, stylesheet);
        }
        return stylesheet;
    }

    // should this whole method be syncronized?  if thread
    // A is rebuilding a SS, we don't want thread B to rebuild it too;
    // better for thread B to just sit tight and wait for A to finish..
    private Stylesheet rebuildComposedStylesheet(SiteNode sn,
                                                 Locale l,
                                                 String outputType) {
        TransactionContext tctx =
            SessionManager.getSession().getTransactionContext();
        boolean callerStartedTransaction = tctx.inTxn();
        if (!callerStartedTransaction) {
            tctx.beginTxn();
        }
        // rebuild with any modified parent stylesheets, if necessary
        SiteNode parent = sn.getParent();
        Stylesheet thisStyle = getStylesheet(sn, l, outputType);
        Stylesheet parentStyle = null;
        while (parent != null) {
            // if no parent styles have really changed, this should
            // just be a few recursive "if isNewerThan" checks.
            parentStyle = getStylesheet(parent, l, outputType);
            if (thisStyle != null) {
                thisStyle = thisStyle.composeStylesheet(parentStyle);
            } else {
                thisStyle = parentStyle;
            }
            parent = parent.getParent();
        }
        // put in default stylesheets for packages
        Stylesheet packageStyles =
            rebuildDefaultStyles(sn, l, outputType);
        if (thisStyle != null) {
            thisStyle = thisStyle.composeStylesheet(packageStyles);
        } else {
            thisStyle = packageStyles;
        }
        if (!callerStartedTransaction) {
            tctx.commitTxn();
        }
        return thisStyle;
    }

    private Stylesheet rebuildDefaultStyles(SiteNode sn,
                                            Locale l,
                                            String outputType) {
        Stylesheet thisStyle = null;

        List remainingPackages = PackageType.getAllPackageKeys();
        // TODO: need to be more intelligent about what packages we
        // actually need!  This requires two things:
        // 1. a depends-on relation from PackageType to PT*
        // 2. some distintion between "applications" and "services"

        // walk up tree from current site node.  give precedence
        // to packages closest to us in the site map in the tree walk
        // and then backfill styles from remaining packages later.
        SiteNode parent = sn;
        while (parent != null) {
            PackageInstance pi = sn.getPackageInstance();
            int idx = remainingPackages.indexOf(pi.getType().getKey());
            if (idx > -1) {
                remainingPackages.remove(idx);
                Stylesheet thisPackage =
                    getDefaultStylesheet(pi.getType(), l, outputType);
                if (thisStyle != null) {
                    thisStyle = thisStyle.composeStylesheet(thisPackage);
                } else {
                    thisStyle = thisPackage;
                }
            }
            parent = parent.getParent();
        }

        // put in remaining packages
        // NOTE: This is the code that really kills us, performance-wise.
        // we have no way of knowing that we need bebop 99% of the
        // time but don't need CMS, etc.
        for (int i = 0; i < remainingPackages.size(); i++) {
            PackageType pType = null;
            pType = new PackageType();
            pType.setKey((String)remainingPackages.get(i));
            Stylesheet pkgSS = getDefaultStylesheet
                (pType, l, outputType);
            if (thisStyle == null) {
                thisStyle = pkgSS;
            } else {
                thisStyle = thisStyle.composeStylesheet(pkgSS);
            }
        }
        return thisStyle;
    }

    /**
     * @return the default stylesheet for a package, given a
     * locale and output type
     */
    private Stylesheet getDefaultStylesheet(
                                            com.arsdigita.kernel.PackageType pkg,
                                            Locale l,
                                            String outputType) {
        Stylesheet[] ssSet = pkg.getStylesheets(l, outputType);
        return Stylesheet.combineStylesheets(ssSet);
    }

    /**
     * Invalidates the stylesheet associated with this site node; will
     * cause the composed stylesheet and the transformer to be rebuilt.
     */
    public void invalidateStylesheet(SiteNode sn) {
        Iterator keys = m_stylesheet_map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (key.startsWith(sn.getNodeId() + "|")) {
                Stylesheet stylesheet = (Stylesheet)m_stylesheet_map.get(key);
                if (stylesheet != null) {
                    stylesheet.invalidate();
                }
            }
        }
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
     * The XSLT is chosen based on the stylesheets associated with the
     * current site node, parent site nodes, and mounted package type,
     * in that order of precedence.  The current request context must
     * be an instance of SiteNodeRequestContext.
     *
     * @param doc the Bebop page to serve
     * @param req the servlet request
     * @param resp the servlet response
     * @param params XSLT parameters (ignored by this implementation)
     * @pre DispatcherHelper.getRequestContext(req) instanceof
     *      SiteNodeRequestContext
     */
    public void servePage(Document doc,
                          HttpServletRequest req,
                          HttpServletResponse resp,
			  Map params) {
        RequestContext ctx = DispatcherHelper.getRequestContext(req);
        PrintWriter writer = null;

        try {
            DeveloperSupport.startStage("PresMgr get stylesheet");
            Profiler.startOp("XSLT");

            // always get the stylesheet; won't always actually rebuild.
            Stylesheet ss = getComposedStylesheet((SiteNodeRequestContext)ctx);
            Transformer xf = null;
            if (ss != null) {
                xf = ss.newTransformer();
            }
            if (ss == null || xf == null) {
                throw new ServletException
                    ("No stylesheet available for request!");
            }

            DeveloperSupport.endStage("PresMgr get stylesheet");
            // There is no longer any need for a database handle.
            // Added by BMQ 7/12/2001
            // This is copying code in BaseMapDispatcherServlet
            // and can be refactored to cohere with the overall design
            // better.
            completeTransaction(req);

            DeveloperSupport.startStage("PresMgr transform");
            // note: at this point we could commit our transaction!

            // before sending output, make sure we set content type
            /*
              resp.setContentType(ctx.getOutputType());
              PrintWriter writer = resp.getWriter();
              xf.setOutputProperty(
              "encoding", Globalization.getDefaultCharset(ctx.getLocale())
              );
              xf.transform(new DOMSource(doc.getInternalDocument()),
              new StreamResult(writer));
            */

            if (ctx.getDebuggingXML()) {
                resp.setContentType("text/xml");
                writer = resp.getWriter();
                writer.println(doc.toString(true));
            } else {
                if (ctx.getDebuggingXSL()) {
                    resp.setContentType("text/xml");
                    writer = resp.getWriter();
                    writer.println(ss.toString());
                } else {
                    resp.setContentType(ctx.getOutputType());
                    writer = resp.getWriter();
                    xf.transform(new DOMSource(doc.getInternalDocument()),
                                 new StreamResult(writer));
                }
            }

            DeveloperSupport.endStage("PresMgr transform");
        } catch (TransformerConfigurationException e) {
            throw new UncheckedWrapperException (e);
        } catch (TransformerException e) {
            throw new UncheckedWrapperException (e);
        } catch (ClassCastException cce) {
            throw new UncheckedWrapperException (cce);
        } catch (ServletException ex) {
            throw new UncheckedWrapperException (ex);
        } catch (IOException ex) {
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
            txc.commitTxn();
            s_log.debug("request over, committing transaction");
        } else {
            s_log.debug("trying to abort");
            txc.abortTxn();
            s_log.debug("request over, aborting transaction");
        }
    }
}
