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

import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageInstanceCollection;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.sitenode.SiteNodeDispatcher;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.apache.log4j.Logger;

/**
 * This tests the instantiation of a particular package's dispatcher
 * to make sure it instantiated once.  SDM Bug #801
 *
 * @author Anukul Kapoor
 */


public class PackageDispatcherCreationCactusTest extends ServletTestCase {

    HttpServlet s;
    RequestContext rctx;

    public PackageDispatcherCreationCactusTest(String theName) {
        super(theName);
    }


    public static class TestDispatcher implements Dispatcher {

        public static int counter = 0;

        public TestDispatcher() {
            final Logger s_cat = Logger.getLogger(getClass().getName());
            s_cat.debug("construction!");
        }

        public void dispatch(HttpServletRequest req, HttpServletResponse resp,
                             RequestContext reqCtx) {
            final Logger s_cat = Logger.getLogger(getClass().getName());
            counter = 1;
            s_cat.debug("dispatch!");
            return;
        }
    }

    public void setUp() throws Exception {

        final Logger s_cat = Logger.getLogger(getClass().getName());

        Session sm = SessionManager.getSession();
        TransactionContext txn = sm.getTransactionContext();
        txn.beginTxn();
        s_cat.debug("setup: begin txn");

        try {
            PackageType pt = new PackageType();
            pt.setKey("PackageDispatcherTester");
            pt.setPrettyName("Package Dispatcher Tester");
            pt.setPrettyPlural("Package Dispatcher Testers");
            pt.setURI("http://testing.arsdigita.com/package/dispatcher");
            pt.setDispatcherClass(TestDispatcher.class.getName());
            pt.save();

            PackageInstance pkg = pt.createInstance("testDispatch");
            pkg.save();

            SiteNode sn = new SiteNode();
            sn.setName("testDispatch");
            sn.setParent(SiteNode.getRootSiteNode());
            sn.mountPackage(pkg);
            sn.save();

            txn.commitTxn();
            s_cat.debug("commit txn");
        } catch (Exception e) {
            txn.abortTxn();
            s_cat.debug("abort txn");
            throw e;
        }
    }


    public void tearDown() throws Exception {
        Session sm = SessionManager.getSession();
        TransactionContext txn = sm.getTransactionContext();
        txn.beginTxn();

        try {
            SiteNode sn = SiteNode.getSiteNode("/testDispatch");
            sn.delete();

            PackageType pt =
                PackageType.findByKey("PackageDispatcherTester");

            PackageInstanceCollection pic = pt.getInstances();
            PackageInstance pi;

            pic.rewind();
            boolean more = pic.next();
            while (more) {
                pi = pic.getPackageInstance();
                pi.delete();
                more = pic.next();
            }

            pt.delete();
            txn.commitTxn();
        } catch (DataObjectNotFoundException d) {
            txn.abortTxn();
            throw d;
        }
        TestDispatcher.counter = 0;
    }

    public void beginDispatcherCreation(WebRequest req) {
        // host, context path, servlet path, path info, query string
        req.setURL("localhost", "", "", "/testDispatch/", "");
    }


    public void testDispatcherCreation()
        throws javax.servlet.ServletException, java.io.IOException, Throwable {
        final Logger s_cat =
            Logger.getLogger(PackageDispatcherCreationCactusTest.class.getName());

        s_cat.info("PackageDispatcherCreationCactusTest.testDispatcherCreation()");

        s = new SiteNodeDispatcher();

        s.init(config);

        try {
            s_cat.debug(request.toString());
            s_cat.debug("begin request 1");
            s.service(request, response);
            s_cat.debug("end request 1");
            s_cat.debug(request.toString());
            s_cat.debug("begin request 2");

            //      DispatcherHelper.setRequestContext(request, null);
            // FIXME: need to "reset" request and response objects
            // to test dispatcher instantiation across multiple hits

            for(Enumeration e = request.getAttributeNames();
                e.hasMoreElements() ; ) {
                request.removeAttribute((String)e.nextElement());
            }

            s.service(request, response);
            s_cat.debug("begin request 3");

        } catch (ServletException se) {
            Throwable t = se;
            Throwable rootError;
            do {
                rootError = t;
                t = ((ServletException)t).getRootCause();
            } while (t instanceof ServletException);
            if (t != null) {
                rootError = t;
            }
            throw rootError;
        }
        assertEquals(TestDispatcher.counter,1);
    }

}
