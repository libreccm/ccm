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

import com.arsdigita.developersupport.SQLDebugger;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigInteger;
import org.apache.log4j.Logger;

/**
 * Contains copied-and-pasted code from other *Test.java classes in this suite.
 *
 * @since 2003-10-29
 **/
public class IsolatedTest extends BaseTestCase implements Const {
    // This doesn't really fail.  Turning it off, because it's an instrumented
    // copy-and-paste duplicate of a working test.
    public final static boolean FAILS = true;

    private static final Logger s_log =
        Logger.getLogger(IsolatedTest.class);

    private Session m_ssn;

    public IsolatedTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_ssn = SessionManager.getSession();
    }

    private static DataObject rollback(String test, OID oid, BigInteger txnID) {
        RollbackListener logger = new LoggingListener(test);
        return Versions.rollback(oid, txnID, logger);
    }

    private static void tag(OID oid, String tag) {
        Versions.tag(oid, tag);
    }

    private void fakeCommit() {
        TestTransaction.testCommitTxn(m_ssn.getTransactionContext());
    }

    /**
     * Refetches the data object that has been cleared from the session cache as
     * a result of a fake commit.
     **/
    private DataObject refetch(DataObject dobj) {
        return m_ssn.retrieve(dobj.getOID());
    }

    public void testSetSimple() {
        SQLDebugger.startNewFile("simple");
        DataObject vt1 = Util.newDataObject(VT1);
        final String name = "vt1";
        vt1.set(NAME, name);
        final String content = "vt1 content";
        vt1.set(CONTENT, content);
        final BigInteger intAttr = new BigInteger("1");
        vt1.set(INT_ATTR, intAttr);
        vt1.save();
        final String vt1Tag = "vt1 tag";
        tag(vt1.getOID(), vt1Tag);

        fakeCommit();
        Util.dumpVT1("committed");
        Util.dumpChangesTable("committed");
        Util.dumpGenericOperations("committed");

        vt1 = refetch(vt1);
        vt1.set(CONTENT, "revised content");
        vt1.set(NAME, "vt1 revised");
        vt1.set(INT_ATTR, new BigInteger("2"));
        vt1.save();
        fakeCommit();

        Util.dumpVT1("revised");
        Util.dumpChangesTable("revised");
        Util.dumpGenericOperations("revised");

        SQLDebugger.close();
        vt1 = refetch(vt1);
        vt1.set(NAME, "vt1 re-revised");
        vt1.set(CONTENT, "re-revised content");
        vt1.set(INT_ATTR, new BigInteger("3"));
        vt1.save();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt1.getOID(), vt1Tag);
        assertNotNull("tagged vt1 change", txnID);

        DataObject rb = rollback("setSimple", vt1.getOID(), txnID);

        assertEquals("content", content, (String) rb.get(CONTENT));
        assertEquals("name", name, (String) rb.get(NAME));
    }
}
