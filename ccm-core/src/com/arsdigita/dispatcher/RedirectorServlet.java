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
package com.arsdigita.dispatcher;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * A servlet that redirects. It takes parameter a servlet parameter "target"
 * that is used for the destination of the redirect.
 * 
 * @version $Id: RedirectorServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class RedirectorServlet extends HttpServlet {

    private static final Logger s_log = Logger.getLogger(
        RedirectorServlet.class);

    public static final String PARAM_NAME = "target";

    public String m_target = null;

    @Override
    public void init() throws ServletException {
        ServletConfig conf = getServletConfig();
        m_target = conf.getInitParameter(PARAM_NAME);
        if (s_log.isDebugEnabled()) {
            s_log.debug("setting target to: " + m_target);
        }
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        if (s_log.isDebugEnabled()) {
            s_log.debug("redirecting: " + req.getRequestURI());
        }

        DispatcherHelper.sendExternalRedirect(resp, m_target);
    }
    
    @Override
    public void destroy() { }
}
