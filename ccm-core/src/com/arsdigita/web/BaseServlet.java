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

import com.arsdigita.dispatcher.RequestEvent;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.kernel.DatabaseTransaction;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.ResourceManager;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>The base servlet for CCM. It manages database transactions, prepares an
 * execution context for the request, and traps and handles requests to
 * redirect.</p>
 *
 * <b>Subject zu change!</b>
 *
 * Note: Database initialization (Startup() ) has been moved to
 *       CCMApplicationContextListener).
 *
 * <p>Users of this class may implement 
 * {@link #doService(HttpServletRequest,HttpServletResponse)} 
 * to service a request in this environment.</p>
 *
 * @see com.arsdigita.web.BaseApplicationServlet
 * @see com.arsdigita.web.BaseJSP
 * @see com.arsdigita.web.RedirectSignal
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: BaseServlet.java 738 2005-09-01 12:36:52Z sskracic $
 */
public abstract class BaseServlet extends HttpServlet {

    private static final Logger s_log = Logger.getLogger(BaseServlet.class);

    /** The name of the request attribute used to store the originally
     *  requested URL.                                                         */
    public static final String REQUEST_URL_ATTRIBUTE =
                               BaseServlet.class.getName() + ".request_url";

    /**
     * Initializer uses parent class's initializer to setup the servlet request /
     * response and application context. Usually a user of this class will not
     * overwrite this method but the user extension point doInit to perform
     * local initialization tasks!
     * 
     * @param sconfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(final ServletConfig sconfig) throws ServletException {
        if (s_log.isInfoEnabled()) {
            s_log.info("Initializing servlet " + sconfig.getServletName() +
                       " (class: " + getClass().getName() + ")");
        }

        super.init(sconfig);

        // The classes  ResourceManager and CCMResourceManager handle a 
        // very similiar scope of tasks.
        // ToDo: integrate both into a single class, e.g. CCMResourceManager
        // to simplify and clean-up of the code!
        ResourceManager.getInstance().setServletContext(getServletContext());
        
        doInit();
    }

    protected void doInit() throws ServletException {
        // Empty
    }

    @Override    
    public void destroy() {
        if (s_log.isInfoEnabled()) {
            s_log.info
                ("Destroying servlet " + getServletConfig().getServletName());
        }

        doDestroy();
    }

    protected void doDestroy() {
        // Empty
    }

    private void internalService(final HttpServletRequest sreq,
                                 final HttpServletResponse sresp)
            throws ServletException, IOException {
        if (s_log.isInfoEnabled()) {
            s_log.info("Servicing request " + new URL(sreq).toDebugString());
        }

        final Thread thread = Thread.currentThread();
        thread.setName("client-" + sreq.getRemoteAddr());

        // Restore reused request-local objects to a request-ready
        // state.

        InternalRequestLocal.prepareAll(sreq);

        // Legacy support

        DispatcherHelper.setRequest(sreq);

        final DatabaseTransaction transaction = new DatabaseTransaction();
        
        DeveloperSupport.requestStart(new RequestEvent(sreq, sresp, null, true));

        try {
            transaction.begin();

            // Build up context data for this request.

            final UserContext uc = getUserContext(sreq, sresp);

            Web.init(sreq, getServletContext(), uc);
            Web.getWebContext().setRequestURL(getRequestURL(sreq));

            final ServletException[] servletException = { null };
            final IOException[] ioException = { null };

            new KernelExcursion() {
                @Override
                protected final void excurse() {
                    setLocale(sreq.getLocale());
                    setSessionID(sreq.getSession().getId());
                    setTransaction(transaction);

                    if (uc.isLoggedIn()) {
                        s_log.debug("User is logged in; storing user in " +
                                    "context record");
                        setParty(uc.getUser());
                    }

                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Finished preparing the context for " +
                                    "this request");
                        s_log.debug("Current state of WebContext:\n" +
                                    Web.getWebContext().getCurrentState());
                        s_log.debug(Kernel.getContext().getDebugInfo());
                    }

                    // Now we're ready to service the request.

                    /* call flushAll on all non error paths so listeners     *
                     * run in correct context (bug 108499)                   */

                    try {
                        DeveloperSupport.startStage("BaseServlet.doService");

                        doService(sreq, sresp);

                        SessionManager.getSession().flushAll();
                    } catch (ServletException se) {
                        servletException[0] = se;
                        if (findRedirectSignal(se) != null) {
                            SessionManager.getSession().flushAll();
                        }
                    } catch (IOException ioe) {
                        ioException[0] = ioe;
                    } catch (RedirectSignal rs) {
                        SessionManager.getSession().flushAll();
                        throw rs;
                    } finally {
                        DeveloperSupport.endStage("BaseServlet.doService");
                    }
                }
            }.run();

            if (servletException[0] != null) {
                throw servletException[0];
            }

            if (ioException[0] != null) {
                throw ioException[0];
            }

            transaction.end();
        } catch (RedirectSignal rs) {
            redirect(sresp, transaction, rs);
        } catch (ServletException se) {
            final RedirectSignal rs = findRedirectSignal(se);

            if (rs == null) {
                new ServletErrorReport(se, sreq, sresp).logit();
                throw se;
            } else {
                redirect(sresp, transaction, rs);
            }
        } catch (IOException ioe) {
            new ServletErrorReport(ioe, sreq, sresp).logit();
            throw ioe;
        } catch (RuntimeException re) {
            new ServletErrorReport(re, sreq, sresp).logit();
            throw re;
        } catch (Error e) {
            new ServletErrorReport(e, sreq, sresp).logit();
            throw e;
        } finally {
            // In every other case, abort.  In the common case,
            // the transaction has already been committed and this
            // call does nothing.

            transaction.abort();

            // Clean up some junk.

            InternalRequestLocal.clearAll();

            // Make sure nothing's left over in the response buffer.

            if (sresp.isCommitted()) {
                sresp.flushBuffer();
            }

            DeveloperSupport.requestEnd(new RequestEvent(sreq, sresp, null, false));

        }
    }

    /**
     * <p>The method that {@link
     * #doGet(HttpServletRequest,HttpServletResponse)} and {@link
     * #doPost(HttpServletRequest,HttpServletResponse)} call.  This is
     * the extension point for users of this class.</p>
     * 
     * @param sreq
     * @param sresp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void doService(final HttpServletRequest sreq,
                             final HttpServletResponse sresp)
            throws ServletException, IOException {
        // Empty
    }

    /**
     * <p>Processes HTTP GET requests.</p>
     *
     * @param sreq
     * @param sresp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest,HttpServletResponse)
     */
    @Override    
    protected final void doGet(final HttpServletRequest sreq,
                               final HttpServletResponse sresp)
            throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.info("Serving GET request path " + sreq.getPathInfo() +
                       " with servlet " + getServletConfig().getServletName() +
                       " (class: " + getClass().getName() + ")");
        }
        try {
            internalService(sreq, sresp);
        } catch(IOException e) {
            if (e.getMessage().contains("Failed to send AJP message")) {
                if (s_log.isDebugEnabled()) {
                    s_log.info("FAILED: Serving GET request path " + sreq.getPathInfo() +
                       " with servlet " + getServletConfig().getServletName() +
                       " (class: " + getClass().getName() + ") CAUSE: Failed to send AJP message");
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * <p>Processes HTTP POST requests.</p>
     * 
     * @param sreq
     * @param sresp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     *
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest,HttpServletResponse)
     */
    @Override    
    protected final void doPost(HttpServletRequest sreq,
                                final HttpServletResponse sresp)
            throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.info("Serving POST request path " + sreq.getPathInfo() +
                       " with servlet " + getServletConfig().getServletName() +
                       " (class: " + getClass().getName() + ")");
        }

        sreq = DispatcherHelper.maybeWrapRequest(sreq);

        internalService(sreq, sresp);
    }

    private RedirectSignal findRedirectSignal(final ServletException se) {
        Throwable root = se.getRootCause();

        while (root instanceof ServletException) {
            root = ((ServletException) root).getRootCause();
        }

        if (root instanceof RedirectSignal) {
            return (RedirectSignal) root;
        } else {
            return null;
        }
    }

    /**
     * 
     * @param sresp
     * @param transaction
     * @param rs
     * @throws IOException 
     */
    private void redirect(final HttpServletResponse sresp,
                          final DatabaseTransaction transaction,
                          final RedirectSignal rs)
            throws IOException {
        // On a request for a redirect, we make sure that we
        // commit or abort the transaction *before* we send the
        // redirect headers to the client.  This is so the client
        // does not see UI inconsistent with any I/O performed in
        // the business logic of a page.

        if (rs.isCommitRequested()) {
            transaction.commit();
        } else {
            transaction.abort();
        }

        final String url = sresp.encodeRedirectURL(rs.getDestinationURL());

        sresp.sendRedirect(url);
    }


    /**
     * 
     * @param sreq
     * @return 
     */
    private URL getRequestURL(HttpServletRequest sreq) {
        URL url = (URL) sreq.getAttribute(REQUEST_URL_ATTRIBUTE);

        if (url == null) {
            url = new URL(sreq);
        }

        return url;
    }

    /**
     * Obtains a new user context from the http request parameters.
     *
     * NOTE: protected for use by web.ApplicaitonDispatchTest
     *
     * @param sreq
     * @param sresp
     * @return The user context
     */
    protected UserContext getUserContext(HttpServletRequest sreq,
                                         HttpServletResponse sresp) {
        s_log.debug("Creating a user context");

        UserContext uc = null;

        try {
            uc = new UserContext(sreq, sresp);
        } catch (RedirectException re) {
            s_log.debug("The user needs to log in again");

            throw new RedirectSignal(re.getRedirectURL(), false);
        }

        return uc;
    }
}
