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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;

import org.apache.log4j.Logger;

/**
 * <p>A base servlet for use by JSP authors who want to use the
 * execution environment that <code>BaseServlet</code> provides.  A
 * JSP will use this class if the extends attribute of the page
 * declaration is set to this class, as in this example:</p>
 *
 * <blockquote><pre>
 * <%@page extends="com.arsdigita.web.BaseJSP" %>
 * <b><%= Web.getContext().getUser().toString() %></b>
 * </pre></blockquote>
 *
 * @see com.arsdigita.web.BaseServlet
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: BaseJSP.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class BaseJSP extends BaseServlet implements HttpJspPage {

    /** A logger instance, primarily to assist debugging .                    */
    private static Logger s_log = Logger.getLogger(BaseJSP.class);

    /**
     * <p>Invokes the <code>_jspService(sreq, sresp)</code> method in
     * the environment prepared by <code>BaseServlet</code>.</p>
     *
     * @see #_jspService(HttpServletRequest,HttpServletResponse)
     * @see com.arsdigita.web.BaseServlet
     * @see com.arsdigita.web.BaseServlet#doService(HttpServletRequest,HttpServletResponse)
     */
    @Override
    protected final void doService(final HttpServletRequest sreq,
                                   final HttpServletResponse sresp)
            throws ServletException, IOException {
        // The exception handling that Tomcat and Resin JSP compilers
        // use wraps runtime exceptions and errors in
        // ServletExceptions.  We unwrap them here so that BaseServlet
        // can avoid doing contortions for JSPs.

        try {
            _jspService(sreq, sresp);
        } catch (ServletException se) {
            final Throwable cause = se.getRootCause();

            if (cause instanceof Error) {
                // It is necessary to cast these because the Java
                // compiler complains about Throwables that are not
                // declared or caught.

                throw (Error) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw se;
            }
        }
    }

    /**
     * <p>The method that JSP compilers will override to produce the
     * body of the page.</p>
     *
     * @see javax.servlet.jsp.HttpJspPage#_jspService(HttpServletRequest,HttpServletResponse)
     */
    public abstract void _jspService(HttpServletRequest sreq,
                                     HttpServletResponse sresp)
            throws ServletException, IOException;

  public void jspInit()
  {
  }

  public void jspDestroy()
  {
  }

}
