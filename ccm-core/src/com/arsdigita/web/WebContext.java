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

import com.arsdigita.web.Application;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Record;
import org.apache.log4j.Logger;

/**
 * <p>A session object that provides an environment in which code can
 * execute. The WebContext contains all session-specific variables.
 * One session object is maintained per thread.</p>
 *
 * <p>Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.</p>
 *
 * @author Rafael Schloming
 * @author Justin Ross
 */
public final class WebContext extends Record {
    public static final String versionId =
        "$Id: WebContext.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(WebContext.class);

    private Application m_application = null;
    private URL m_requestURL = null;

    private static String[] s_fields = new String[] {
        "User",
        "Application",
        "RequestURL"
    };

    WebContext() {
        super(WebContext.class, s_log, s_fields);
    }

    final WebContext copy() {
        WebContext result = new WebContext();

        result.m_application = m_application;
        result.m_requestURL = m_requestURL;

        return result;
    }

    final void init(final Application app, final URL requestURL) {
        setApplication(app);
        setRequestURL(requestURL);
    }

    final void clear() {
        m_application = null;
        m_requestURL = null;
    }

    public final User getUser() {
        UserContext context = Web.getUserContext();

        if (context == null || !context.isLoggedIn()) {
            return null;
        } else {
            return context.getUser();
        }
    }

    public final Application getApplication() {
        return m_application;
    }

    final void setApplication(final Application app) {
        m_application = app;

        mutated("Application");
    }

    public final URL getRequestURL() {
        return m_requestURL;
    }

    final void setRequestURL(final URL url) {
        Assert.exists(url, "URL url");

        m_requestURL = url;

        mutated("RequestURL");
    }
}
