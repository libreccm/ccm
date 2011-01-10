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
 *
 */
package com.arsdigita.templating;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.sitenode.SiteNodeRequestContext;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>
 * This is the "legacy" stylesheet resolver class. It resolves stylesheets
 * using the old packagetype <-> stylesheet and sitenode <-> stylesheet
 * mappings in the database.
 * </p>
 * @version $Id: LegacyStylesheetResolver.java 287 2005-02-22 00:29:02Z sskracic $
 *
 * @deprecated use {@link PatternStylesheetResolver} in new code.
 */
public class LegacyStylesheetResolver implements StylesheetResolver {

    private static final Logger s_log = Logger.getLogger
        (LegacyStylesheetResolver.class);


    public URL resolve(HttpServletRequest request) {
        //    TransactionContext tctx =
        //      SessionManager.getSession().getTransactionContext();
        //    boolean callerStartedTransaction = tctx.inTxn();
        //    if (!callerStartedTransaction)
        //      tctx.beginTxn();

        // Get the site node.
        final SiteNodeRequestContext context = (SiteNodeRequestContext)
            DispatcherHelper.getRequestContext(request);

        final Locale locale = context.getLocale();
        final String output = context.getOutputType();
        final SiteNode node = context.getSiteNode();

        SiteNode sn = node;
        Stylesheet ss = null;

        while (sn != null && ss == null) {
            // No style for this site node, but we can try the parent.
            ss = sn.getStylesheet(locale, output);
            sn = sn.getParent();
        }

        sn = node;

        while (sn != null && ss == null) {
            final PackageInstance pi = sn.getPackageInstance();

            if (pi != null) {
                ss = pi.getType().getStylesheet(locale, output);
            }

            sn = sn.getParent();
        }

        if (ss == null) {
            throw new IllegalStateException
                ("No path to XSL stylesheet found");
        }

        // Get the actual path for the Stylesheet we've found.
        final String filename = ss.getPath();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Trying path " + filename);
        }


        try {
            return new URL(Web.getConfig().getDefaultScheme(),
                           Web.getConfig().getHost().getName(),
                           Web.getConfig().getHost().getPort(),
                           filename);
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }
}
