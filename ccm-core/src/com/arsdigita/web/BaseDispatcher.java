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

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.profiler.Profiler;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Application;

import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>A class encapsulating the application lookup used by CCM
 * dispatchers.</p>
 *
 * @see com.arsdigita.web.Application
 * @see com.arsdigita.web.DispatcherServlet
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: BaseDispatcher.java 562 2005-06-12 23:53:19Z apevec $
 */
class BaseDispatcher {
    public static final String versionId =
        "$Id: BaseDispatcher.java 562 2005-06-12 23:53:19Z apevec $" +
        "$Author: apevec $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log = Logger.getLogger(BaseDispatcher.class);

    static final String DISPATCHED_ATTRIBUTE =
        BaseDispatcher.class.getName() + ".dispatched";

    private final static Cache s_cache = new Cache();

    public final boolean isApplicationInCache(String path) {
        return s_cache.isCached(path);
    }

    final boolean dispatch(final HttpServletRequest sreq,
                           final HttpServletResponse sresp)
            throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Dispatching request " + sreq.getRequestURI() + " [" +
                        sreq.getContextPath() + "," +
                        sreq.getServletPath() + "," +
                        sreq.getPathInfo() + "," +
                        sreq.getQueryString() + "]");
        }

        final String path = sreq.getPathInfo();

        if (requiresTrailingSlash(path)) {
            s_log.debug("The request URI needs a trailing slash; " +
                        "redirecting");

            final String prefix = DispatcherHelper.getDispatcherPrefix(sreq);
            String uri = sreq.getRequestURI();
            if (prefix != null && prefix.trim().length() > 0) {
                uri = prefix + uri;
            }
            final String query = sreq.getQueryString();

            String url = null;

            if (query == null) {
                sresp.sendRedirect(sresp.encodeRedirectURL(uri + "/"));
            } else {
                sresp.sendRedirect
                    (sresp.encodeRedirectURL(uri + "/?" + query));
            }

            return true;
        }

        Assert.exists(path, String.class);

        s_log.debug("Storing the path elements of the current request as " +
                    "the original path elements");

        sreq.setAttribute(BaseServlet.REQUEST_URL_ATTRIBUTE, new URL(sreq));

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using path '" + path + "' to lookup application");
        }

        DeveloperSupport.startStage("BaseDispatcher.lookupApplicationSpec");

        final ApplicationSpec spec = lookupApplicationSpec(path);

        DeveloperSupport.endStage("BaseDispatcher.lookupApplicationSpec");

        if (spec == null) {
            s_log.debug("No application was found; doing nothing");
            return false;
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found application " + spec.getAppID() + "; " +
                            "dispatching to its servlet");
            }

            sreq.setAttribute
                (BaseApplicationServlet.APPLICATION_ID_ATTRIBUTE,
                 spec.getAppID());
            sreq.setAttribute(DISPATCHED_ATTRIBUTE, Boolean.TRUE);
            Profiler.startOp("APP"); // +spec.getAppID() XXX get app name?
            forward(spec.getTypeContextPath(), spec.target(path), sreq, sresp);
            Profiler.stopOp("APP"); // +spec.getAppID()
            return true;
        }
    }

    private boolean requiresTrailingSlash(final String path) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Checking if this request needs a trailing slash");
        }

        if (path == null) {
            s_log.debug("The path is null; the request needs a trailing " +
                        "slash");
            return true;
        }

        if (path.endsWith("/")) {
            s_log.debug("The path already ends in '/'");
            return false;
        }

        if (path.lastIndexOf(".") < path.lastIndexOf("/")) {
            s_log.debug("The last fragment of the path has no '.', so we " +
                        "assume a directory was requested; a trailing " +
                        "slash is required");
            return true;
        } else {
            s_log.debug("The last fragment of the path appears to be a file " +
                        "name; no trailing slash is needed");
            return false;
        }
    }

    private final void forward(String contextPath,
                               final String target,
                               final HttpServletRequest sreq,
                               final HttpServletResponse sresp)
            throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Forwarding by path to target '" + target + "'");
        }
        s_log.debug("The context path is: " + contextPath);
        if (StringUtils.emptyString(contextPath)) {
            contextPath = "/";
        }
        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
        }
        // XXX We should pass servlet context down
        final ServletContext context = Web.getServletContext(contextPath);
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("From context " + Web.getServletContext() +
            " to context " + context);
        }

        final RequestDispatcher rd = context.getRequestDispatcher(target);

        Assert.exists(rd, RequestDispatcher.class);

        forward(rd, sreq, sresp);
    }

    final void forward(final RequestDispatcher rd,
                       HttpServletRequest sreq,
                       final HttpServletResponse sresp)
            throws ServletException, IOException {
        s_log.debug("Checking if this request needs to be forwarded or " +
                    "included " + sreq);

        sreq = DispatcherHelper.restoreOriginalRequest(sreq);

        if (sreq.getAttribute("javax.servlet.include.request_uri") == null) {
            s_log.debug("The attribute javax.servlet.include.request_uri " +
                        "is not set; forwarding " + sreq);

            rd.forward(sreq, sresp);
        } else {
            s_log.debug("The attribute javax.servlet.include.request_uri " +
                        "is set; including " + sreq);

            rd.include(sreq, sresp);
        }
    }

    private ApplicationSpec lookupApplicationSpec(final String path) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("*** Starting application lookup for path '" +
                        path + "' ***");
        }

        ApplicationSpec spec = s_cache.getAppSpec(path);

        if ( spec == null ) {
            s_log.debug("There's no application to be found");
        }
        return spec;
    }

    static void scheduleRefresh() {
        s_cache.scheduleRefresh();
    }

    private static class ApplicationSpec {
        private final BigDecimal m_id;
        private final String m_instanceURI;
        private final String m_typeURI;
        private final String m_typeContextPath;

        ApplicationSpec(Application app) {
            if ( app == null ) { throw new NullPointerException("app"); }

            m_id              = app.getID();
            m_instanceURI     = app.getPath();
            m_typeURI         = app.getServletPath();
            m_typeContextPath = app.getContextPath();

            if (Assert.isEnabled()) {
                Assert.exists(m_id, BigDecimal.class);
                Assert.exists(m_instanceURI, String.class);
                Assert.exists(m_typeURI, String.class);
                Assert.exists(m_typeContextPath, String.class);
            }
        }

        BigDecimal getAppID() { return m_id; }
        String getTypeContextPath() { return m_typeContextPath; }

        String target(final String path) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Building the target path from the request path '" +
                            path + "' and the spec " + this);
            }

            final StringBuffer target = new StringBuffer(128);

            target.append(m_typeURI);
            target.append(path.substring(m_instanceURI.length()));
            target.append("?");
            target.append(BaseApplicationServlet.APPLICATION_ID_PARAMETER);
            target.append("=");
            target.append(m_id);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Returning target value '" + target + "'");
            }

            return target.toString();
        }

        public boolean equals(Object obj) {
            if ( obj==null ) { return false; }

            ApplicationSpec other = (ApplicationSpec) obj;
            return m_id.equals(other.m_id) &&
                equal(m_instanceURI, other.m_instanceURI) &&
                equal(m_typeURI, other.m_typeURI) &&
                equal(m_typeContextPath, other.m_typeContextPath);

        }

        private boolean equal(String s1, String s2) {
            if (s1==s2) { return true; }
            if (s1==null) { return equal(s2, s1); }
            return s1.equals(s2);
        }

        public int hashCode() {
            return toString().hashCode();
        }

        public String toString() {
            final String sep = ", ";
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            sb.append("appID=").append(m_id).append(sep);
            sb.append("instanceURI=").append(m_instanceURI).append(sep);
            sb.append("typeURI=").append(m_typeURI).append(sep);
            sb.append("typeContextPath=").append(m_typeContextPath);
            return sb.append("]").toString();
        }
    }

    /**
     * Caches (path, AppSpec) mappings.
     **/
    private static class Cache extends PathMapCache {

        private static ThreadLocal s_handleHere = new ThreadLocal() {
                protected Object initialValue() {
                    return Boolean.FALSE;
                }
            };

        public Cache() {
            super("BaseDispatcherCache");
        }

        // implements the PathMapCache interface
        public String normalize(String path) {
            if ( path==null ) { throw new NullPointerException("path"); }
            if ( !path.startsWith("/") ) {
                throw new DataObjectNotFoundException
                    ("The URL path specified must begin with a '/'.");
            }
            return path.endsWith("/") ?
                path : path.substring(0, path.lastIndexOf('/') + 1);
        }

        // implements the PathMapCache interface
        public Object retrieve(String path) {
            if ( "/".equals(path) ) { return null; }

            final TransactionContext context =
                SessionManager.getSession().getTransactionContext();
            if ( !context.inTxn() ) {
                s_log.debug("Beginning transaction");
                context.beginTxn();
                s_handleHere.set(Boolean.TRUE);
            }

            Application app = Application.retrieveApplicationForPath(path);

            return app==null ? null : new ApplicationSpec(app);
        }

        // implements the PathMapCache interface
        public void refresh() {
            s_cache.clearAll();
        }

        void scheduleRefresh() {
            refreshAfterCommit();
        }

        synchronized ApplicationSpec getAppSpec(String path) {
            try {
                return (ApplicationSpec) super.get(path);
            } finally {
                if ( s_handleHere.get() == Boolean.TRUE ) {
                    s_handleHere.set(Boolean.FALSE);
                    SessionManager.getSession().getTransactionContext().
                        commitTxn();
                }
            }
        }
    }
}
