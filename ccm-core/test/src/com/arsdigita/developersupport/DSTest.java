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
package com.arsdigita.developersupport;

import java.sql.SQLException;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class DSTest extends TestCase {

    public DSTest(String name) {
        super(name);
    }

    static int s_requestStartCount = 0;
    static int s_requestAddPropertyCount = 0;
    static int s_requestEndCount = 0;
    static int s_logQueryCount = 0;
    static int s_logCommentCount = 0;

    protected void setUp() {
        DeveloperSupport.clearListeners();
        s_requestStartCount = 0;
        s_requestAddPropertyCount = 0;
        s_requestEndCount = 0;
        s_logQueryCount = 0;
        s_logCommentCount = 0;
    }

    private Object m_dummy_request = "dummy request";
    private String m_dummy_prop = "dummy prop";
    private Object m_dummy_value = "dummy value";
    private String m_dummy_connid = "100";
    private String m_dummy_type = "dummy type";
    private String m_dummy_query = "dummy query";
    private HashMap m_dummy_hashmap = new HashMap();
    private long m_dummy_time = (long)200;
    private java.sql.SQLException m_dummy_sqle =
        new java.sql.SQLException("dummy sqle");
    private String m_dummy_comment = "dummy comment";

    private void doDS() {
        DeveloperSupport.requestStart(m_dummy_request);
        DeveloperSupport.requestAddProperty(m_dummy_request,
                                            m_dummy_prop,
                                            m_dummy_value);
        DeveloperSupport.requestEnd(m_dummy_request);
        DeveloperSupport.logQuery(m_dummy_connid,
                                  m_dummy_type,
                                  m_dummy_query,
                                  m_dummy_hashmap,
                                  m_dummy_time,
                                  m_dummy_sqle);
        DeveloperSupport.logComment(m_dummy_comment);
    }

    private void assertCallCount(int n) {
        assertEquals("requestStartCount", s_requestStartCount, n);
        assertEquals("requestAddProperty", s_requestEndCount, n);
        assertEquals("requestEndCount", s_requestEndCount, n);
        assertEquals("logQueryCount", s_logQueryCount, n);
        assertEquals("logCommentCount", s_logCommentCount, n);
    }

    public void testNoListeners() {
        doDS();
        assertCallCount(0);
    }

    public void testOneListeners() {
        DeveloperSupport.addListener(new DummyDSListener());
        doDS();
        assertCallCount(1);
    }

    public void checkDSLResults(DummyDSListener dsl) {

    }

    public void testListenersRequestStart() {
        DummyDSListener dsl = new DummyDSListener();
        DeveloperSupport.addListener(dsl);
        DeveloperSupport.requestStart(m_dummy_request);
        assertEquals(m_dummy_request, dsl.lastrequest);
    }

    public void testListenersRequestAddProperty() {
        DummyDSListener dsl = new DummyDSListener();
        DeveloperSupport.addListener(dsl);
        DeveloperSupport.requestAddProperty(m_dummy_request,
                                            m_dummy_prop,
                                            m_dummy_value);
        assertEquals(m_dummy_request, dsl.lastrequest);
        assertEquals(m_dummy_prop, dsl.lastprop);
        assertEquals(m_dummy_value, dsl.lastvalue);
    }

    public void testListenersRequestEnd() {
        DummyDSListener dsl = new DummyDSListener();
        DeveloperSupport.addListener(dsl);
        DeveloperSupport.requestEnd(m_dummy_request);
        assertEquals(m_dummy_request, dsl.lastrequest);
    }

    public void testListenersLogQuery() {
        DummyDSListener dsl = new DummyDSListener();
        DeveloperSupport.addListener(dsl);
        DeveloperSupport.logQuery(m_dummy_connid,
                                  m_dummy_type,
                                  m_dummy_query,
                                  m_dummy_hashmap,
                                  m_dummy_time,
                                  m_dummy_sqle);
        assertEquals(m_dummy_connid, dsl.lastconn_id);
        assertEquals(m_dummy_type, dsl.lasttype);
        assertEquals(m_dummy_query, dsl.lastquery);
        assertEquals(m_dummy_time, dsl.lasttime);
        assertEquals(m_dummy_sqle, dsl.lastsqle);
    }

    public void testListenersLogComment() {
        DummyDSListener dsl = new DummyDSListener();
        DeveloperSupport.addListener(dsl);
        DeveloperSupport.logComment(m_dummy_comment);
        assertEquals(m_dummy_comment, dsl.lastcomment);
    }


    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testBLAH() methods to the suite.
        //
        return new TestSuite(DSTest.class);
    }


    class DummyDSListener extends DeveloperSupportListener {
        public Object lastrequest;
        public String lastprop;
        public Object lastvalue;
        public String lastconn_id;
        public String lasttype;
        public String lastquery;
        public HashMap lastbindvars;
        public long lasttime;
        public java.sql.SQLException lastsqle;
        public String lastcomment;

        public void requestStart(Object request) {
            s_requestStartCount++;
            lastrequest = request;
        }
        public void requestAddProperty(Object request,
                                       String property,
                                       Object value) {
            s_requestAddPropertyCount++;
            lastrequest = request;
            lastprop = property;
            lastvalue = value;
        }
        public void requestEnd(Object request) {
            s_requestEndCount++;
            lastrequest = request;
        }
        public void logQuery(String connection_id,
                             String type,
                             String query,
                             HashMap bindvars,
                             long time,
                             java.sql.SQLException sqle) {
            s_logQueryCount++;
            lastconn_id = connection_id;
            lasttype = type;
            lastquery = query;
            lastbindvars = bindvars;
            lasttime = time;
            lastsqle = sqle;
        }
        public void logComment(String comment) {
            s_logCommentCount++;
            lastcomment = comment;
        }

    }



}
