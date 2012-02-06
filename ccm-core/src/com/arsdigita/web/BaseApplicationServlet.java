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

import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.KernelRequestContext;
import com.arsdigita.kernel.security.SessionContext;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.sitenode.SiteNodeRequestContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * <p>The base servlet for CCM applications.  It manages database
 * transactions, prepares an execution context for the request, and
 * traps and handles requests to redirect.</p>
 *
 * <p>Most CCM applications will extend this class by implementing
 * {@link #doService(HttpServletRequest,HttpServletResponse,Application)} to
 * perform application-private dispatch to UI code.</p>
 *
 * <p>
 * The application will be available at the path
 * <code>www.example.org/ccm/applicationname</code>, where
 * <code>applicationname</code> is
 * the name defined for the application and <code>www.example.org</code> the
 * URL of the server.
 * </p>
 *
 * @see com.arsdigita.web.BaseServlet
 * @see com.arsdigita.web.DispatcherServlet
 * @see com.arsdigita.web.RedirectSignal
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: BaseApplicationServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class BaseApplicationServlet extends BaseServlet {

    private static Logger s_log = Logger.getLogger
        (BaseApplicationServlet.class);

    /**
     * <p>The ID of the application whose service is requested.  This
     * request attribute must be set by a previous servlet or filter
     * before this servlet can proceed.  In CCM, the default servlet,
     * {@link DispatcherServlet}, sets this attribute using the 
     * {@link BaseDispatcher}. <strong>Important:</strong> This does only work
     * if the application is called using an URL like
     * <code>http://www.example.org/ccm/application</code>!</p>
     */
    public static final String APPLICATION_ID_ATTRIBUTE =
        BaseApplicationServlet.class.getName() + ".application_id";

    /**
     * <p>The same as {@link #APPLICATION_ID_ATTRIBUTE}, but as a
     * request parameter.  This is present so applications not using
     * the dispatcher servlet may accept requests directly to their
     * servlets, provided the application ID is given in the URL.</p>
     */
    public static final String APPLICATION_ID_PARAMETER = "app-id";

    /**
     * <p>Augments the context of the request and delegates to {@link
     * #doService(HttpServletRequest,HttpServletResponse,Application)}.</p>
     *
     * @see com.arsdigita.web.BaseServlet#doService(HttpServletRequest,HttpServletResponse)
     */
    @Override
    protected final void doService(final HttpServletRequest sreq,
                                   final HttpServletResponse sresp)
                  throws ServletException, IOException {

        final Application app = getApplication(sreq);

        if (app == null) {
            sresp.sendError(404, "Application not found");
            throw new IllegalStateException("Application not found");
        }

        Web.getContext().setApplication(app);

        final RequestContext rc = makeLegacyContext
            (sreq, app, Web.getUserContext());

        DispatcherHelper.setRequestContext(sreq, rc);

        final ServletException[] servletException = { null };
        final IOException[] ioException = { null };

        new KernelExcursion() {
            protected final void excurse() {
                setLocale(rc.getLocale());
                setResource(app);

                try {
                    doService(sreq, sresp, app);
                } catch (ServletException se) {
                    servletException[0] = se;
                } catch (IOException ioe) {
                    ioException[0] = ioe;
                }
            }
        }.run();

        if (servletException[0] != null) {
            throw servletException[0];
        }

        if (ioException[0] != null) {
            throw ioException[0];
        }
    }

    /**
     * <p>The method that {@link
     * #doService(HttpServletRequest,HttpServletResponse)} calls.
     * Servlet authors should implement this method to perform
     * application-specific request handling.</p>
     *
     * @see javax.servlet.http.HttpServlet#service(HttpServletRequest,HttpServletResponse)
     */
    protected abstract void doService(HttpServletRequest sreq,
                                      HttpServletResponse sresp,
                                      Application app)
            throws ServletException, IOException;

    /**
     * 
     * @param sreq
     * @return
     */
    private Application getApplication(HttpServletRequest sreq) {
        s_log.debug("Resolving the application that will handle this request");

        BigDecimal id = (BigDecimal) sreq.getAttribute
            (APPLICATION_ID_ATTRIBUTE);

        if (id == null) {
            s_log.debug("I didn't receive an application ID with the " +
                        "servlet request; trying to get it from the " +
                        "query string");

            final String value = sreq.getParameter(APPLICATION_ID_PARAMETER);

            if (value != null) {
                try {
                    id = new BigDecimal(value);
                } catch (NumberFormatException nfe) {
                    throw new IllegalStateException
                        ("Could not parse '" + value + "' into a BigDecimal");
                }
            }
        }

        Assert.exists(id, "BigDecimal id");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Retrieving application " + id + " from the " +
                        "database");
        }

        return Application.retrieveApplication(id);
    }

    /**
     * 
     * @param sreq
     * @param app
     * @param uc
     * @return
     */
    private RequestContext makeLegacyContext(HttpServletRequest sreq,
                                             final Application app,
                                             final UserContext uc) {
        s_log.debug("Setting up a legacy context object");

        sreq = DispatcherHelper.restoreOriginalRequest(sreq);

        final InitialRequestContext irc = new InitialRequestContext
            (sreq, getServletContext());
        final SessionContext sc = uc.getSessionContext();

        final KernelRequestContext krc = new KernelRequestContext
            (irc, sc, uc);


        SiteNode node = null;
        try {
            node = SiteNode.getSiteNode(app.getPrimaryURL(),
                                        true);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("cannot find root sitenode");
        }

        if (node == null) {
            s_log.debug("There is no site node at this URL; storing a " +
                        "KernelRequestContext");

            return krc;
        } else {
            s_log.debug("Creating a SiteNodeRequestContext");

            final SiteNodeRequestContext snrc = new SiteNodeRequestContext
                (sreq, krc, node, sreq.getServletPath() + "/");

            return snrc;
        }
    }
}
