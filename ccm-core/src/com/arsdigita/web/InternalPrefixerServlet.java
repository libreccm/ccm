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
package com.arsdigita.web;

import com.arsdigita.dispatcher.DispatcherHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Allow arbitrary prefixes to be set up in web.xml such as /textonly,
 * /printer, /debug, etc. This enables the entire site to be switched
 * to alternative modes of operation e.g., completely switching to an
 * alternative set of stylesheets. For example,
 *
 * <pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;TextOnlyServlet&lt;/servlet-name&gt;
 *   &lt;display-name&gt;Text Only Servlet&lt;/display-name&gt;
 *   &lt;servlet-class&gt;
 *    com.arsdigita.web.InternalPrefixerServlet
 *   &lt;/servlet-class&gt;
 *   &lt;init-param&gt;
 *    &lt;param-name&gt;prefix&lt;/param-name&gt;
 *    &lt;param-value&gt;/text&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;TextOnlyServlet&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/textonly/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * The above entry, in conjunction with a set of stylesheets
 * that producedtext only output, would enable a text only
 * part of the site with a /textonly/ URL prefix.
 *
 */
public class InternalPrefixerServlet extends HttpServlet {

    private static final Logger s_log =
        Logger.getLogger(InternalPrefixerServlet.class);

    private String m_prefix;

    @Override
    public void init()
        throws ServletException {
        ServletConfig conf = getServletConfig();

        m_prefix = (String)conf.getInitParameter("prefix");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Prefix is " + m_prefix);
        }
    }

    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp)
        throws ServletException,
               java.io.IOException {

        String path = req.getPathInfo();
        if (path == null || path.trim().length() == 0) {
            // if path is null or the empty string then trying
            // to get a RequestDispatcher will result in an NPE
            path = "/";
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Forwarding " + path);
        }

        DispatcherHelper.setDispatcherPrefix(req, m_prefix);

        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(path);
        rd.forward(req, resp);
    }

}
