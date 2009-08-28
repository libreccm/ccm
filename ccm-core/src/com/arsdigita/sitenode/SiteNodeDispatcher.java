/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.sitenode;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.DatabaseTransaction;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelDispatcherServlet;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseServlet;
import com.arsdigita.web.DispatcherServlet;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebContextExposer;
import com.arsdigita.web.WebExposer;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Main site-map dispatcher entry point for ACS.  Selects a package to
 * serve from a site map (URL-to-ACS package) in the RDBMS.  This can
 * be deployed as a servlet in web.xml, so that this becomes the main
 * entry point for an ACS application.
 *
 * <p>
 *
 * When a request comes in:
 *
 * <ol>
 *
 * <li>The BaseDispatcherServlet superclass first looks for a concrete
 * file in the webapp root that matches the requested URL.
 *
 * <li>If no file is found, we try to authenticate the user, and
 * resolve the request for a URL to a <code>PackageInstance</code>
 * using the <code>SiteNode</code> class, which represents a "site
 * map" (URL to package mapping).
 *
 * <li>We pass control to the package-level dispatcher for that package,
 * which will then resolve the URL to a particular file in the filesystem.
 *
 * </ol>
 *
 * <p>
 *
 * @author Bill Schneider
 * @version $Revision: #43 $ $Date: 2004/08/16 $
 * @since 4.5
 * @deprecated Use {@link com.arsdigita.web.DispatcherServlet} instead.
 * Pboy (2009-08-06):
 * a) SiteNodeDispatcher is still included in web.xml
 * b) content-center needs SiteNodeDispatcher
 * c) SiteNodeDispatcher is fallback in web.DispatcherServlet (cf web.xml)
 * d) webDispatcherServlet  requires a fallback as parameter
 * therefore: SideNodeDispatcher is still required at the moment.
 * ToDo: test if web.LegacyAdapterServlet, a 'servlet that does what
 * SiteNodeDispatcher does' can do the job.
 */
public class SiteNodeDispatcher extends KernelDispatcherServlet
        implements Dispatcher {
    public static final String versionId =
        "$Id: SiteNodeDispatcher.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(SiteNodeDispatcher.class.getName());

    private static SiteNodeDispatcher s_instance = null;

    // dispatcher for resolving abstract URLs in /www, /packages, etc.,
    // directories
//     private SiteNode m_rootSiteNode;

    /**
     * Returns the currently-loaded instance of the request
     * processor servlet.
     * @return The current JVM's RequestProcessor instance.
     */
    public synchronized static SiteNodeDispatcher getInstance() {
        return s_instance;
    }

//     /**
//      * Called when servlet is loaded, and sets static instance.
//      */
//     public synchronized void init() throws ServletException {
//         super.init();
//
//         try {
//             DatabaseTransaction txn = new DatabaseTransaction();
//             txn.begin();
//
//             m_rootSiteNode = SiteNode.getRootSiteNode();
//
//             txn.end();
//         } catch (IllegalStateException e) {
//             s_log.error("SiteNodeDispatcher.init", e);
//             throw new UnavailableException(
//                 "SiteNodeDispatcher Initialization Failure" +
//                 "\n  The most likely cause is that the initialization sequence did not" +
//                 "\n  complete successfully.  Check the error logs for other error messages" +
//                 "\n  and make sure you have a valid enterprise.init" +
//                 "\n\n" + e.getMessage());
//         } catch (PersistenceException e) {
//             s_log.error("SiteNodeDispatcher.init", e);
//             throw new UnavailableException(
//                 "SiteNodeDispatcher Initialization Failure" +
//                 "\n  The most likely cause is that the initialization sequence did not" +
//                 "\n  complete successfully.  Check the error logs for other error messages" +
//                 "\n  and make sure you have a valid enterprise.init" +
//                 "\n\n" + e.getMessage());
//         } catch (Exception e) {
//             s_log.error("SiteNodeDispatcher.init", e);
//             throw new UnavailableException(e.getMessage());
//         }
//    }

    /**
     * Resolves the URL to a particular package instance, using the
     * site_nodes table in the database, and passes control to the
     * individual package.
     *
     * @param req The servlet request.
     * @param resp The servlet response.
     * @param rc the request context
     * @pre rctx != null
     */
    public void dispatch(final HttpServletRequest req,
                         final HttpServletResponse resp,
                         final RequestContext rc)
            throws IOException, ServletException {
        s_log.debug("SiteNodeDispatcher.dispatch called");

        WebExposer.initializeRequestLocals(req);

        DispatcherHelper.setRequest(req);

        Assert.assertNotNull(rc, "rc");

        SiteNode sn = getSiteNode(req, rc);

        if (sn == null) {
            // This should be extremely rare, since you'll almost
            // always have something mounted at the root site node.
            resp.sendError(404, "getSiteNode returned null");
            throw new IllegalStateException("getSiteNode returned null");
        }

        final PackageInstance pkg = sn.getPackageInstance();

        if (pkg == null) {
            WebExposer.clearRequestLocals();

            resp.sendError(404, "SiteNode " + sn.getURL() +
                           " has no package associated with it.");

            throw new IllegalStateException
                ("SiteNode" + sn.getURL() +
                 " has no package associated with it.");
        }

        final boolean fallingBack =
            req.getAttribute(DispatcherServlet.FALLING_BACK_ATTRIBUTE) != null;
        String prefix = null;
        URL requestURL = null;

        if (fallingBack) {
            // We got here through DispatcherServlet, which is mapped
            // to a prefix.  Change the value of prefix so SNRC's path
            // accounting is correct.

            requestURL = (URL) req.getAttribute
                (BaseServlet.REQUEST_URL_ATTRIBUTE);

            prefix = requestURL.getContextPath() + requestURL.getServletPath();
        } else {
            // SND is mapped to / and *.jsp.  As a result, its servletPath
            // contains the whole remainder of the requestURI after the
            // context path.  Translate that servletPath to be the
            // pathInfo, since our applications assume that the dispatcher
            // is mounted at a prefix.

            String pathInfo;

            if (req.getPathInfo() == null) {
                pathInfo = req.getServletPath();
            } else {
                pathInfo = req.getServletPath() + req.getPathInfo();
            }
            
            // We initialize this temp URL first, so that we can
            // pick up the virtual host correctly.
            URL vhost = new URL(req);
            requestURL = new URL
                (req.getScheme(),
                 vhost.getServerName(),
                 vhost.getServerPort(),
                 req.getContextPath(),
                 "",
                 pathInfo,
                 new ParameterMap(req));

            prefix = "";
        }

        final SiteNodeRequestContext snrc = new SiteNodeRequestContext
            (req, rc, sn, prefix + sn.getURL());

        DispatcherHelper.setRequestContext(req, snrc);

        final Application app = Application.retrieveApplicationForSiteNode(sn);

        final UserContext uc = snrc.getUserContext();

        WebExposer.init(req, snrc.getServletContext(), uc);

        WebContextExposer wce = new WebContextExposer(Web.getContext());

        wce.init(app, requestURL);

        final ServletException[] servletException = { null };
        final IOException[] ioException = { null };

        new KernelExcursion() {
            public void excurse() {
                setResource(app);
                setLocale(snrc.getLocale());
                setSessionID(req.getSession().getId());
                setTransaction(new DatabaseTransaction());

                DeveloperSupport.startStage("SiteNodeDispatcher.dispatch");

                try {
                    // Need to set the party where redirect signals
                    // are caught.
                    setParty(getCurrentUser(req, uc));

                    doDispatch(req, resp, snrc, pkg);

                    // force listeners to run in correct context (bug 108499)
                    SessionManager.getSession().flushAll();

                } catch (RedirectSignal rs) {
                    redirect(rs, resp);

                } catch (IOException ioe) {
                    ioException[0] = ioe;
                } catch (ServletException se) {
                    // This handles the case where a RedirectSignal is thrown in the context
                    // of a JSP running under Resin. Resin wraps the JSP code with a try/catch(Throwable) block,
                    // which rethrows any unfiltered exceptions as ServletExceptions
                    // See SDM #225917

                    // This also now fixes SDM #226354. Tomcat wraps yet another ServletException
                    // around a ServletException. This way, we strip off all to find 
                    // out what's really going on.
                    Throwable root = se.getRootCause();
                    while(root instanceof ServletException) {
                        ServletException next = (ServletException) root;
                        root = next.getRootCause();
                    }
                    if (root instanceof RedirectSignal) {
                        redirect((RedirectSignal) root, resp);
                    } else {
                        ServletErrorReport report = new ServletErrorReport
                            (se, req, resp);
                        report.logit();
                        servletException[0] = se;
                    }
                } catch (RuntimeException re) {
                    ServletErrorReport report = new ServletErrorReport
                        (re, req, resp);
                    report.logit();
                    throw re;
                } finally {
                    WebExposer.clearRequestLocals();
                    DeveloperSupport.endStage("SiteNodeDispatcher.dispatch");
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
     * Handles redirect signals. Aborts or commits transaction.
     *
     * @param rs The redirect signal
     * @param resp HTTP response
     */
    private void redirect(RedirectSignal rs, final HttpServletResponse resp) {
        final String url = resp.encodeRedirectURL
            (rs.getDestinationURL());

        // The semantics of RedirectSignal require that we
        // commit or abort the transaction before we send
        // the redirect.

        TransactionContext context =
            SessionManager.getSession().getTransactionContext();

        if (context.inTxn()) {
            if (rs.isCommitRequested()) {
                context.commitTxn();
            } else {
                context.abortTxn();
            }
        }

        try {
            resp.sendRedirect(url);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    private User getCurrentUser(HttpServletRequest sreq,
                                UserContext uc) {
        User user = null;

        if (uc.isLoggedIn()) {
            user = uc.getUser();

            if (user == null) {
                // Probably an old cookie lying around.  Clear the
                // login state.

                s_log.warn("The user cookie cannot be found; clearing " +
                           "login state and redirecting to login again");

                try {
                    uc.logout();
                } catch (LoginException le) {
                    throw new UncheckedWrapperException(le);
                }

                // Throws a redirect signal.
                uc.redirectToLoginPage(sreq);
            }
        }
        return user;
    }

    private SiteNode getSiteNode(HttpServletRequest req, RequestContext rc) {
        // The parent class already found no concrete file.
        // So do site-node lookup.

        String url = null;
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            url = rc.getRemainingURLPart();
        } else {
            // We're using the new dispatcher, which is mounted at a
            // prefix such as "/ccm".  As a result, the path we care
            // about is stored in pathInfo.
            url = pathInfo;
        }
        // Totally empty URL at this point won't get found in the site
        // map.  Yes, there's no URL left to process, but we have to
        // make getSiteNode() happy.
        if (url.length() == 0) {
            url = "/";
        }

        SiteNode sn = null;

        // look up the package that we're dispatching to in the
        // site map
        try {
            // look up the package that we're dispatching to in the
            sn = SiteNode.getSiteNode(url, true);
        } catch (DataObjectNotFoundException nfe) {
            // Do nothing.  We look for a null return value in the
            // calling code.
        }

        return sn;
    }

    private void doDispatch(HttpServletRequest req,
                            HttpServletResponse resp,
                            SiteNodeRequestContext rci,
                            PackageInstance pkg)
            throws IOException, ServletException {

        try {
            DeveloperSupport.endStage("SiteNodeDispatcher.dispatch getPackage");
            DeveloperSupport.startStage("SiteNodeDispatcher getDispatcher");

            final Dispatcher dispatcher = pkg.getType().getDispatcher();

            DeveloperSupport.endStage("SiteNodeDispatcher getDispatcher");
            DeveloperSupport.startStage("SiteNodeDispatcher subdispatch");

            if (s_log.isDebugEnabled()) {
                s_log.debug(Kernel.getContext().getDebugInfo());
            }

            dispatcher.dispatch(req, resp, rci);

            DeveloperSupport.endStage("SiteNodeDispatcher subdispatch");
        } catch (ClassNotFoundException e) {
            s_log.error("Error loading dispatcher", e);
            throw new ServletException("Error loading dispatcher", e);
        } catch (InstantiationException e) {
            throw new ServletException("Error loading dispatcher", e);
        } catch (IllegalAccessException e) {
            throw new ServletException("Error loading dispatcher", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new ServletException("Error loading dispatcher", e);
        }
    }
}
