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
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;

/**
 *
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: PrefixerServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PrefixerServlet extends HttpServlet {
    public static final String versionId =
        "$Id: PrefixerServlet.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(PrefixerServlet.class);

    public static final String PREFIX_PARAMETER = "prefix";

    private String m_prefix = null;

    public final void init() throws ServletException {
        final ServletConfig config = getServletConfig();

        m_prefix = config.getInitParameter(PREFIX_PARAMETER);

        Assert.assertNotNull(m_prefix, "String m_prefix");
        Assert.assertTrue(m_prefix.startsWith("/"),
                          "The target prefix must start with a '/'");
    }

    protected final void service(final HttpServletRequest sreq, 
                                 final HttpServletResponse sresp)
            throws IOException, ServletException {
        final StringBuffer buffer = new StringBuffer();

        buffer.append(sreq.getContextPath());
        buffer.append(m_prefix);
        buffer.append(sreq.getServletPath());

        final String pathInfo = sreq.getPathInfo();

        if (pathInfo != null) {
            buffer.append(pathInfo);
        }

        final String queryString = sreq.getQueryString();

        if (queryString != null) {
            buffer.append("?");
            buffer.append(queryString);
        }

        final String result = sresp.encodeRedirectURL(buffer.toString());

        sresp.sendRedirect(result);
    }

    public final void destroy() {
        m_prefix = null;
    }
}
