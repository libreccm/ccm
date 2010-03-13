/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <p>A signal that requests to commit or abort the current transaction
 * and to send a redirect to a new URL.  BaseServlet traps this signal
 * when it is thrown and finishes the transaction before it sends the
 * redirect to the response.  This way the client cannot see state
 * inconsistent with work performed in the previous request.</p>
 *
 * <p><code>RedirectSignal</code>s are usually sent after doing work
 * on behalf of the user:</p>
 *
 * <blockquote><pre>
 * private final void saveUserSettings(final HttpServletRequest sreq) {
 *     m_user.setGivenName("Gibbon");
 *     m_user.setFamilyName("Homily");
 *
 *     m_user.save();
 *
 *     // The boolean argument true signifies that we want to commit
 *     // the transaction.
 *     throw new RedirectSignal(URL.here(sreq, "/user-detail.jsp"), true);
 * }
 * </pre></blockquote>
 *
 * @see com.arsdigita.web.BaseServlet
 * @see com.arsdigita.web.LoginSignal
 * @see com.arsdigita.web.ReturnSignal
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RedirectSignal.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class RedirectSignal extends TransactionSignal {
    public static final String versionId =
        "$Id: RedirectSignal.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(RedirectSignal.class);

    private final String m_url;

    public RedirectSignal(final String url, final boolean isCommitRequested) {
        super(isCommitRequested);

        if (Assert.isEnabled()) {
            Assert.exists(url, "String url");
            Assert.isTrue(url.startsWith("http") || url.startsWith("/"),
                              "The URL is relative and won't dispatch " +
                              "correctly under some servlet containers; " +
                              "the URL is '" + url + "'");
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Request for redirect to URL '" + url + "'",
                        new Throwable());
        }

        m_url = url;
    }

    public RedirectSignal(final URL url, final boolean isCommitRequested) {
        this(url.toString(), isCommitRequested);
    }

    public final String getDestinationURL() {
        return m_url;
    }
}
