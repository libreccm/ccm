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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/** 
 * 
 * @version $Id: OrionDefaultServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class OrionDefaultServlet extends HttpServlet
    implements DispatcherConstants {

    private static final Logger s_log =
        Logger.getLogger(OrionDefaultServlet.class);

    public void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException {

        ServletContext ctx = getServletContext();
        RequestDispatcher rd = ctx.getNamedDispatcher("orion.filehandler");

        try {
            rd.include(req, resp);
        } catch (Exception e) {
            s_log.error("caught error", e);
            throw new ServletException(e.getMessage());
        }
    }
}
