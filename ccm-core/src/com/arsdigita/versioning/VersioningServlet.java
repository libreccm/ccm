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
package com.arsdigita.versioning;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.AbstractTransactionListener;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.versioning.DevSupport.NodeFilter;
import com.arsdigita.web.BaseServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This servlet, along with a couple of helper JSPs, is used for debugging.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-07-01
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 **/
public final class VersioningServlet extends BaseServlet {
    private final static Logger s_log =
        Logger.getLogger(VersioningServlet.class);

    private final static String CMD   = "cmd";
    private final static String OID   = "oid";
    private final static String TITLE = "title";
    private final static String JSP_DIR = "/packages/versioning/";

    public void doService(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {

        if ( !Versioning.getConfig().isDebugUIEnabled() ) {
            throw new ServletException("this service is disabled");
        }

        TransactionContext txn = SessionManager.getSession().
            getTransactionContext();
        txn.addTransactionListener(new AbstractTransactionListener() {
                public void beforeCommit(TransactionContext txn) {
                    Assert.fail("uncommittable transaction");
                }
            });

        Kernel.getContext().getTransaction().setCommitRequested(false);

        String cmd = req.getParameter(CMD);
        if ( cmd == null || "typeSearch".equals(cmd) ) {
            mainPage(req, resp);
        } else if ( "showTxns".equals(cmd) ) {
            txnsPage(req, resp);
        } else if ( "rollback".equals(cmd) ) {
            rollbackPage(req, resp);
        } else if ( "graph".equals(cmd) ) {
            versioningGraphPage(req, resp);
        } else {
            throw new ServletException("unknown cmd=" + cmd);
        }
    }

    private RequestDispatcher getDispatcher(String path) {
        return getServletContext().getRequestDispatcher(path);
    }

    private void mainPage(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        VersionedObjects versionedObjects =
            new VersionedObjectsImpl(req.getParameter("objectType"));
        req.setAttribute(TITLE, "Versioning Log");
        req.setAttribute("versionedObjects", versionedObjects);
        getDispatcher(JSP_DIR + "main.jsp").forward(req, resp);
    }

    private void txnsPage(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String serOID = getParam(req, OID);
        OID oid = (OID) Adapter.deserialize(serOID, Types.OID);
        String tagged = req.getParameter("tagged");
        TransactionCollection txnColl = null;
        if ( tagged != null && "yes".equals(tagged) ) {
            txnColl = Versions.getTaggedTransactions(oid);
        } else {
            txnColl = Versions.getTransactions(oid);
        }
        req.setAttribute(TITLE, "Transactions");
        req.setAttribute("txns", txnColl);
        req.setAttribute(OID, oid);
        req.setAttribute("encodedOID", URLEncoder.encode(serOID));
        getDispatcher(JSP_DIR + "txns.jsp").forward(req, resp);
    }

    private void rollbackPage(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        OID oid = (OID) Adapter.deserialize(getParam(req, OID), Types.OID);
        BigInteger txnID = new BigInteger(getParam(req, "txnID")); 

        RollbackLogger rb = new RollerBacker(oid, txnID);
        req.setAttribute("logger", rb);

        getDispatcher(JSP_DIR + "rollback.jsp").forward(req, resp);
    }


    private void versioningGraphPage(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String graphType = getParam(req, "graphType");
        req.setAttribute(TITLE, "Versioning Graph");
        GraphPrinter printer = new GraphPrinterImpl(new NodeFilter() {
                public boolean test(String qName, boolean isReachable) {
                    return isReachable && qName != null && qName.startsWith("com");
                }
            });

        req.setAttribute("graphPrinter", printer);
        getDispatcher(JSP_DIR + "graph.jsp").forward(req, resp);
    }

    private static String getParam(HttpServletRequest req, String name)
        throws ServletException {

        String result = req.getParameter(name);
        if ( result == null ) {
            throw new ServletException(name + " was not supplied");
        }
        return result;
    }

    /**
     * <p>An iterator over a collection of versioned oids. You should not need
     * to use this interface.</p>
     *
     * @see VersioningServlet     
     **/
    public interface VersionedObjects {
        OID getOID();
        boolean hasNext();
        Object next();
        int size();
    }

    /**
     * <p>Logs operations performed during rollback.  The output goes to the
     * writer specified via {@link
     * VersioningServlet.RollbackLogger#setWriter(Writer)}.  You should not need
     * to use this interface. </p>
     *
     * @see VersioningServlet
     */
    public interface RollbackLogger {
        OID getOID();
        BigInteger getTxnID();
        void setWriter(Writer writer);
        void rollback();
        void printException(Throwable throwable) throws ServletException;
    }

    /**
     * <p>Prints the versioning dependency graph to the specified writer.  You
     * should not need to use this interface. </p>
     *
     * @see VersioningServlet
     **/
    public interface GraphPrinter {
        void setWriter(Writer writer);
        void printGraph();
    }

    private static class VersionedObjectsImpl
        implements Constants, VersionedObjects {

        private List m_oids;
        private Iterator m_iter;
        private OID m_curOID;

        public VersionedObjectsImpl(String type) {
            DataCollection dc = SessionManager.getSession().retrieve(CHANGE_DATA_TYPE);
            dc.addFilter(OBJ_ID + " like '%" + type + "%'");
            Set oids = new HashSet();

            while ( dc.next() ) {
                oids.add(dc.get(OBJ_ID));
            }

            m_oids = new ArrayList(oids);
            Collections.sort(m_oids);
            m_iter = m_oids.iterator();
        }

        public boolean hasNext() {
            return m_iter.hasNext();
        }

        public Object next() {
            String next = (String) m_iter.next();
            m_curOID = (OID) Adapter.deserialize(next, Types.OID);
            return next;
        }

        public int size() {
            return m_oids.size();
        }

        public OID getOID() {
            return m_curOID;
        }
    }


    private static class GraphPrinterImpl implements GraphPrinter {
        private final NodeFilter m_filter;
        private PrintWriter m_writer;

        GraphPrinterImpl(NodeFilter filter) {
            m_filter = filter;
        }

        public void setWriter(Writer writer) {
            m_writer = new PrintWriter(writer);
        }

        public void printGraph() {
            DevSupport.versioningGraphToDot(m_filter, m_writer);
        }
    }
}
