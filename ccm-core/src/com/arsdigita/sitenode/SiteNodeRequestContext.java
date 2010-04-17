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

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.globalization.ApplicationLocaleProvider;
import com.arsdigita.globalization.ClientLocaleProvider;
import com.arsdigita.globalization.LocaleNegotiator;
import com.arsdigita.kernel.KernelRequestContext;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Generic request context implementation for an application that
 * is chosen by the SiteMapDispatcher.  The SiteMapDispatcher sets up
 * a SiteNode object for the site node (URL prefix) where the ACS
 * application is "mounted".  JSPApplicationDispatcher can assume that
 * the RequestContext argument it receives is an
 * SiteNodeRequestContext.
 * @see com.arsdigita.dispatcher.JSPApplicationDispatcher
 *
 * @author Bill Schneider
 * @since 4.5
 * @version $Id: SiteNodeRequestContext.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated Use {@link com.arsdigita.web.WebContext} instead.
 */
public class SiteNodeRequestContext extends KernelRequestContext {

    private static final Logger s_log = Logger.getLogger
        (SiteNodeRequestContext.class);

    private SiteNode m_sn;
    private LocaleNegotiator m_ln;

    /**
     * Copy constructor.
     **/
    protected SiteNodeRequestContext(SiteNodeRequestContext that) {
        super(that);
        this.m_sn           = that.m_sn;
        this.m_ln           = that.m_ln;
    }

    /**
     * Build a new application context given an original app context,
     * and a destination site node.  Part of the previously-remaining URL
     * is consumed by the site node's URL, and part remains.
     **/
    public SiteNodeRequestContext(HttpServletRequest req,
                                  RequestContext parent,
                                  SiteNode sn,
                                  String pathPrefix) {
        // FIXME: assumes parent is a KernelRequestContext!
        super((KernelRequestContext) parent);

        s_log.debug("Constructing a SiteNodeRequestContext");

        // what part of the URL went into the site node?  we will need to
        // pass the remnant on the to the package dispatcher
        String snURL = sn.getURL("");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using site node URL '" + snURL + "'");
            s_log.debug("pathPrefix == '" + pathPrefix + "'");
            s_log.debug("getServletPath() -> '" + req.getServletPath() + "'");
            s_log.debug("getContextPath() -> '" + req.getContextPath() + "'");
            s_log.debug("parent.getRemainingURLPart() -> '" +
                        parent.getRemainingURLPart() + "'");
            s_log.debug("parent.getProcessedURLPart() -> '" +
                        parent.getProcessedURLPart() + "'");
        }

        int startidx = pathPrefix.length();

        String remainingUrl = parent.getRemainingURLPart();

        if (remainingUrl.length() >= startidx) {
            remainingUrl = remainingUrl.substring(startidx);
        } else {
            remainingUrl = "";
        }

        // all site nodes end in a trailing slash, so remove the
        // trailing slash if the original request URI doesn't end in a
        // trailing slash.

        if (snURL.endsWith("/") && !parent.getOriginalURL().endsWith("/")
            && remainingUrl.length() == 0) {
            snURL = snURL.substring(0, startidx - 1);
        }

        String processedUrl = parent.getProcessedURLPart() + snURL;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting processed URL part to '" + processedUrl + "'");
        }

        setProcessedURLPart(processedUrl);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting remaining URL part to '" + remainingUrl + "'");
        }

        setRemainingURLPart(remainingUrl);

        m_sn = sn;
        PackageInstance pkg = m_sn.getPackageInstance();
        if (pkg != null) {
            ApplicationLocaleProvider alp = (ApplicationLocaleProvider)
                LocaleNegotiator.getApplicationLocaleProvider();
            if (alp != null) { alp.setLocale(pkg.getLocale()); }

            ClientLocaleProvider clp = (ClientLocaleProvider)
                LocaleNegotiator.getClientLocaleProvider();
            if (clp != null) {
                clp.setTargetBundle(pkg.getTargetBundle());
                clp.setAcceptLanguages(req.getHeader("Accept-Language"));
            }

            m_ln = new LocaleNegotiator(pkg.getTargetBundle(),
                                        null,
                                        req.getHeader("Accept-Charset"),
                                        null);
        }
    }

    /**
     * @return the site node referenced by this request.
     */
    public SiteNode getSiteNode() {
        return m_sn;
    }

    public Locale getLocale() {
        if (m_ln != null) {
            return m_ln.getLocale();
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * @return the right resource bundle for the requested package,
     * in the right locale.
     */
    public ResourceBundle getResourceBundle() {
        if (m_ln != null) {
            return m_ln.getBundle();
        } else {
            return null;
        }
    }

    /**
     * Gets the base path, relative to the webapp root, where JSP-based
     * resources (and static pages) will be found.
     * @return the base path, relative to the webapp root, where
     * JSP-based resources will be found.
     * Returns with a trailing slash, e.g.,
     * "/packages/package-key/www/"
     */
    public String getPageBase() {
        return "/packages/"
            + m_sn.getPackageInstance().getKey() +
            "/www/";
    }

    public PackageInstance getPackageInstance() {
        return getSiteNode().getPackageInstance();
    }
}
