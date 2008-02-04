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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class VersioningEventProcessorTest extends BaseTestCase
    implements Const {

    private static final Logger s_log =
        Logger.getLogger(VersioningEventProcessorTest.class);

    private Session m_ssn;

    public VersioningEventProcessorTest(String name) {
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


    /**
     * Test the dependence graph for the "versioning.events" model
     **/
    public void testEventprocGraph() {
        ObjectTypeMetadata otmd = ObjectTypeMetadata.getInstance();
        assertTrue(VT1 + VERSIONED_TYPE, otmd.isVersionedType(VT1));
        assertTrue(VT1E + VERSIONED_TYPE, otmd.isVersionedType(VT1E));
        assertTrue(VT2 + VERSIONED_TYPE, otmd.isVersionedType(VT2));
        assertTrue(C1 + COVERSIONED_TYPE, otmd.isCoversionedType(C1));
        assertTrue(C2 + COVERSIONED_TYPE, otmd.isCoversionedType(C2));
        assertTrue(VT3 + VERSIONED_TYPE, otmd.isVersionedType(VT3));
        assertTrue(RT1 + RECOVERABLE, otmd.isRecoverable(RT1));
        assertTrue(RET1 + RECOVERABLE, otmd.isRecoverable(RET1));
        assertTrue(UVCT1 + UNREACHABLE, otmd.isUnreachable(UVCT1));
        assertTrue(UVCT2 + UNREACHABLE, otmd.isUnreachable(UVCT2));
        assertTrue(VT4 + VERSIONED_TYPE, otmd.isVersionedType(VT4));
        assertTrue(VT5 + VERSIONED_TYPE, otmd.isVersionedType(VT5));
        assertTrue(UT1 + UNREACHABLE, otmd.isUnreachable(UT1));
        assertTrue(UT2 + UNREACHABLE, otmd.isUnreachable(UT2));
        assertTrue(VUT1 + VERSIONED_TYPE, otmd.isVersionedType(VUT1));
    }

    public void testSetSimple() {
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

        vt1 = refetch(vt1);
        vt1.set(CONTENT, "revised content");
        vt1.set(NAME, "vt1 revised");
        vt1.set(INT_ATTR, new BigInteger("2"));
        vt1.save();
        fakeCommit();

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

    /**
     * Same as {@link #testSetSimple()} with one difference: it tags a txn in
     * which no changes were made to any data objects.
     **/
    public void testSetSimpleWithEmptyTxn() {
        DataObject vt1 = Util.newDataObject(VT1);
        final String name = "vt1";
        vt1.set(NAME, name);
        final String content = "vt1 content";
        vt1.set(CONTENT, content);
        final BigInteger intAttr = new BigInteger("1");
        vt1.set(INT_ATTR, intAttr);
        vt1.save();
        fakeCommit();

        // we are tagging a Txn in which no changes have been made
        final String vt1Tag = "vt1 tag";
        tag(vt1.getOID(), vt1Tag);

        fakeCommit();

        vt1 = refetch(vt1);
        vt1.set(CONTENT, "revised content");
        vt1.set(NAME, "vt1 revised");
        vt1.set(INT_ATTR, new BigInteger("2"));
        vt1.save();
        fakeCommit();

        vt1 = refetch(vt1);
        vt1.set(NAME, "vt1 re-revised");
        vt1.set(CONTENT, "re-revised content");
        vt1.set(INT_ATTR, new BigInteger("3"));
        vt1.save();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt1.getOID(), vt1Tag);
        assertNotNull("tagged vt1 change", txnID);

        DataObject rb = rollback("setSimpleWithEmptyTxn", vt1.getOID(), txnID);

        assertEquals("content", content, (String) rb.get(CONTENT));
        assertEquals("name", name, (String) rb.get(NAME));
    }

    /**
     * Tests whether multiple oids can be tagged within the same txn.
     **/
    public void testMultipleTagsPerTxn() {
        DataObject vt1a = Util.newDataObject(VT1);
        vt1a.set(NAME, "vt1a");
        vt1a.set(CONTENT, "vt1a content");
        vt1a.set(INT_ATTR, new BigInteger("1"));
        vt1a.save();
        final String vt1aTag = "vt1a tag";
        tag(vt1a.getOID(), vt1aTag);

        DataObject vt1b = Util.newDataObject(VT1);
        vt1b.set(NAME, "vt1b");
        vt1b.set(CONTENT, "vt1b content");
        vt1b.set(INT_ATTR, new BigInteger("2"));
        vt1b.save();

        final String vt1bTag = "vt1b tag";
        tag(vt1b.getOID(), vt1bTag);

        fakeCommit();

        BigInteger txnID1 = Versions.
            getMostRecentTxnID(vt1a.getOID(), vt1aTag);
        assertNotNull("tagged vt1a change", txnID1);

        BigInteger txnID2 = Versions.
            getMostRecentTxnID(vt1b.getOID(), vt1bTag);
        assertNotNull("tagged vt1a change", txnID2);
    }


    public void testSetRequiredCompoundWithDelete() {
        DataObject rt1a = Util.newDataObject(RT1);
        rt1a.set(NAME, "rt1a");
        rt1a.set(INT_ATTR, new BigInteger("123"));
        final OID rt1aOID = rt1a.getOID();
        DataObject vt3 = Util.newDataObject(VT3);
        final String name = "vt3";
        vt3.set(NAME, name);
        vt3.set(RT1_ATTR, rt1a);
        vt3.save();
        rt1a.save();
        final String vt3Tag = "vt3 tag";
        tag(vt3.getOID(), vt3Tag);
        fakeCommit();

        vt3 = refetch(vt3);
        vt3.set(NAME, "vt3 revised");
        vt3.save();
        rt1a = refetch(rt1a);
        final String rt1aRevisedName = "rt1a revised";
        rt1a.set(NAME, rt1aRevisedName);
        BigInteger rt1aRevisedIntAttr = new BigInteger("234");
        rt1a.set(INT_ATTR, rt1aRevisedIntAttr);
        rt1a.save();
        fakeCommit();

        DataObject rt1b = Util.newDataObject(RT1);
        rt1b.set(NAME, "rt1b");
        rt1b.set(INT_ATTR, new BigInteger("321"));

        vt3 = refetch(vt3);
        vt3.set(NAME, "vt3 re-revised");
        vt3.set(RT1_ATTR, rt1b);
        vt3.save();
        rt1a = refetch(rt1a);
        rt1a.delete();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt3.getOID(), vt3Tag);
        assertNotNull("tagged vt3 change", txnID);

        DataObject rb = rollback("setRequiredCompoundWithDelete",
                                 vt3.getOID(), txnID);

        DataObject actual = (DataObject) rb.get(RT1_ATTR);
        assertEquals("vt3.rt1==rt1a", rt1aOID, actual.getOID());
        String actualName = (String) actual.get(NAME);
        assertNotNull("actualName", actualName);
        assertEquals("rt1.name not rolled back past its last value",
                     rt1aRevisedName,
                     (String) actual.get(NAME));
        assertEquals("rt1.intAttr not rolled back past its last value",
                     rt1aRevisedIntAttr,
                     (BigInteger) actual.get(INT_ATTR));
    }

    public void testSetRequiredCompound() {
        DataObject rt1a = Util.newDataObject(RT1);
        rt1a.set(NAME, "rt1a");
        rt1a.set(INT_ATTR, new BigInteger("123"));
        final OID rt1aOID = rt1a.getOID();
        DataObject vt3 = Util.newDataObject(VT3);
        final String name = "vt3";
        vt3.set(NAME, name);
        vt3.set(RT1_ATTR, rt1a);
        vt3.save();
        rt1a.save();
        final String vt3Tag = "vt3 tag";
        tag(vt3.getOID(), vt3Tag);
        fakeCommit();

        vt3 = refetch(vt3);
        vt3.set(NAME, "vt3 revised");
        vt3.save();
        final String rt1aRevisedName = "rt1a revised";
        rt1a = refetch(rt1a);
        rt1a.set(NAME, rt1aRevisedName);
        rt1a.set(INT_ATTR, new BigInteger("234"));
        rt1a.save();
        fakeCommit();

        DataObject rt1b = Util.newDataObject(RT1);
        rt1b.set(NAME, "rt1b");
        rt1b.set(INT_ATTR, new BigInteger("321"));

        vt3 = refetch(vt3);
        vt3.set(NAME, "vt3 re-revised");
        vt3.set(RT1_ATTR, rt1b);
        vt3.save();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt3.getOID(), vt3Tag);
        assertNotNull("tagged vt3 change", txnID);

        DataObject rb = rollback("setRequiredCompound", vt3.getOID(), txnID);
        DataObject actual = (DataObject) rb.get(RT1_ATTR);
        assertEquals("vt3.rt1==rt1a", rt1aOID, actual.getOID());
        assertEquals("rt1.name not rolled back",
                     rt1aRevisedName,
                     (String) actual.get(NAME));
    }

    public void testDoubleAdd() {
        DataObject rt1a = Util.newDataObject(RT1);
        rt1a.set(NAME, "rt1a");
        rt1a.set(INT_ATTR, new BigInteger("123"));

        DataObject vt3 = Util.newDataObject(VT3);
        final String name = "vt3";
        vt3.set(NAME, name);
        vt3.set(RT1_ATTR, rt1a);
        vt3.save();

        final String vt3Tag = "vt3 tag";
        tag(vt3.getOID(), vt3Tag);

        fakeCommit();

        vt3 = refetch(vt3);
        vt3.set(NAME, "vt3 revised");
        DataObject ret1 = Util.newDataObject(RET1);
        ret1.set(NAME, "ret1");
        add(vt3, RET1S, ret1);
        // repeated add
        add(vt3, RET1S, ret1);
        vt3.save();
        fakeCommit();

        vt3 = refetch(vt3);
        ret1 = refetch(ret1);
        remove(vt3, RET1S, ret1);
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt3.getOID(), vt3Tag);
        assertNotNull("tagged vt3 change", txnID);

        DataObject rb = rollback("doubleAdd", vt3.getOID(), txnID);
        DataAssociationCursor cur = ((DataAssociation) rb.get(RET1S)).cursor();
        assertEquals("0 instances of ret1", 0, cur.size());
    }


    private static void dumpVT2C1UVCTs(String msg) {
        Util.dumpVersioningLog(msg);

        Util.dumpVT2(msg);
        Util.dumpC1(msg);
        Util.dumpC2(msg);
        Util.dumpUVCT1(msg);
        Util.dumpUVCT2(msg);
    }

    public void testDirtSimpleAddRemoveOneWayComponent() {
        final Set expectedC1S = new HashSet(2);
        final Set expectedUVCT1S = new HashSet(1);
        final Set expectedUVCT2S = new HashSet(1);

        DataObject vt2 = Util.newDataObject(VT2);
        vt2.set(NAME, "vt2");
        vt2.set(UNVER_ATTR, "unversioned attribute");

        DataObject uvct1a = Util.newDataObject(UVCT1);
        uvct1a.set(NAME, "ucvt1a");
        add(vt2, UVCT1S, uvct1a);

        DataObject uvct2a = Util.newDataObject(UVCT2);
        uvct2a.set(NAME, "ucvt2a");
        add(vt2, UVCT2S, uvct2a);

        DataObject c1a = Util.newDataObject(C1);
        c1a.set(NAME, "c1a");
        add(vt2, C1S, c1a);
        expectedC1S.add(c1a.getOID());

        DataObject c2 = Util.newDataObject(C2);
        OID c2OID = c2.getOID();
        c2.set(NAME, "c2");
        vt2.set(C2_ATTR, c2);

        DataObject c1b = Util.newDataObject(C1);
        c1b.set(NAME, "c1b");
        add(vt2, C1S, c1b);
        c1b.save();
        expectedC1S.add(c1b.getOID());
        fakeCommit();

        DataObject c1c = Util.newDataObject(C1);
        c1c.save();
        c1c.set(NAME, "c1c");
        c1c.delete();

        vt2 = refetch(vt2);
        vt2.save();
        final String vt2Tag = "vt2 tag";
        tag(vt2.getOID(), vt2Tag);
        fakeCommit();

        vt2 = refetch(vt2);
        c1b = refetch(c1b);
        uvct1a = refetch(uvct1a);
        uvct2a = refetch(uvct2a);
        c2 = refetch(c2);

        remove(vt2, C1S, c1b);
        remove(vt2, UVCT1S, uvct1a);
        remove(vt2, UVCT2S, uvct2a);
        c2.set(NAME, "c2 revised");

        DataObject uvct1b = Util.newDataObject(UVCT1);
        uvct1b.set(NAME, "ucvt1b");
        add(vt2, UVCT1S, uvct1b);
        expectedUVCT1S.add(uvct1b.getOID());

        DataObject uvct2b = Util.newDataObject(UVCT2);
        uvct2b.set(NAME, "ucvt2b");
        add(vt2, UVCT2S, uvct2b);
        expectedUVCT2S.add(uvct2b.getOID());

        vt2.set(NAME, "vt2 revised");
        final String unverRevised = "unversioned attribute revised";
        vt2.set(UNVER_ATTR, unverRevised);
        vt2.set(C2_ATTR, null);
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt2.getOID(), vt2Tag);
        assertNotNull("tagged vt2 change", txnID);

        DataObject rbVT2 = rollback("dirtSimpleAddRemoveOneWayComponent",
                                    vt2.getOID(), txnID);

        assertEquals("unversioned attr not rolled back",
                     unverRevised,
                     (String) rbVT2.get(UNVER_ATTR));

        assertAssocEquals("vt2.uvct1s contains ucvt1b",
                          expectedUVCT1S,
                          (DataAssociation) rbVT2.get(UVCT1S));

        assertAssocEquals("vt2.uvct2s contains ucvt2b",
                          expectedUVCT2S,
                          (DataAssociation) rbVT2.get(UVCT2S));

        DataObject rbC2 = (DataObject) rbVT2.get(C2_ATTR);
        assertNotNull("rolled back c2", rbC2);
        assertEquals("c2 oid", c2OID, rbC2.getOID());
        assertEquals("c2 name", "c2", (String) rbC2.get(NAME));
    }

    public void testComponentModifications() {
        DataObject vt2 = Util.newDataObject(VT2);
        vt2.set(NAME, "vt2");
        vt2.set(UNVER_ATTR, "unversioned attribute");

        DataObject c2 = Util.newDataObject(C2);
        OID c2OID = c2.getOID();
        final String c2Name = "c2";
        c2.set(NAME, c2Name);
        vt2.set(C2_ATTR, c2);
        fakeCommit();

        final String vt2Tag = "vt2 tag";
        tag(vt2.getOID(), vt2Tag);
        fakeCommit();

        // Change C2 but don't change VT2. This tests that we properly roll back
        // components, when we rollback their composite.
        c2 = refetch(c2);
        c2.set(NAME, "c2 revised");
        fakeCommit();

        BigInteger txnID = Versions.getMostRecentTxnID(vt2.getOID(), vt2Tag);
        assertNotNull("tagged vt2 change", txnID);

        DataObject rbVT2 = rollback("dirtComponentModifications",
                                    vt2.getOID(), txnID);

        DataObject rbC2 = (DataObject) rbVT2.get(C2_ATTR);
        assertEquals("c2 name", c2Name, (String) rbC2.get(NAME));
    }



    public void testShallowAddRemoveOneWayComponent() {
        final Set expected = new HashSet(2);

        /*
         * Add/remove the following components to/from vt2:
         * c1a:
         *   add, remove, tag, add
         * c1b:
         *   add,         tag, remove
         * c1c:
         *   add, remove, tag
         * c1d:
         *   add,         tag, remove, add, remove
         *
         * Upon rollback to the tagged state, vt2 should have c1b and c1d.
         */
        DataObject vt2 = Util.newDataObject(VT2);
        vt2.set(NAME, "vt2");
        vt2.set(UNVER_ATTR, "unversioned attribute");
        DataObject c1a = Util.newDataObject(C1);
        c1a.set(NAME, "c1a");
        add(vt2, C1S, c1a);
        final OID c1aOID = c1a.getOID();

        DataObject c1b = Util.newDataObject(C1);
        c1b.set(NAME, "c1b");
        add(vt2, C1S, c1b);
        final OID c1bOID = c1b.getOID();
        expected.add(c1bOID);

        DataObject c1c = Util.newDataObject(C1);
        c1c.set(NAME, "c1c");
        add(vt2, C1S, c1c);

        DataObject c1d = Util.newDataObject(C1);
        c1d.set(NAME, "c1d");
        c1d.save();

        expected.add(c1d.getOID());
        fakeCommit();

        vt2 = refetch(vt2);
        c1a = refetch(c1a);
        c1c = refetch(c1c);
        remove(vt2, C1S, c1a);
        remove(vt2, C1S, c1c);
        vt2.set(NAME, "vt2 before second commit");
        vt2.save();

        final String vt2Tag = "vt2 tag";
        tag(vt2.getOID(), vt2Tag);

        fakeCommit();

        DataObject c1aResurrected =  m_ssn.create(c1aOID);
        vt2 = refetch(vt2);
        c1b = refetch(c1b);
        c1d = refetch(c1d);
        add(vt2, C1S, c1aResurrected);
        remove(vt2, C1S, c1b);
        final OID c1dOID = c1d.getOID();
        remove(vt2, C1S, c1d);
        vt2.set(NAME, "vt2 before third commit");
        vt2.save();
        fakeCommit();

        DataObject c1dResurrected = m_ssn.create(c1dOID);
        vt2 = refetch(vt2);
        add(vt2, C1S, c1dResurrected);
        vt2.save();
        fakeCommit();

        vt2 = refetch(vt2);
        c1dResurrected = refetch(c1dResurrected);
        remove(vt2, C1S, c1dResurrected);
        vt2.set(NAME, "vt2 before forth commit");
        vt2.save();

        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt2.getOID(), vt2Tag);
        assertNotNull("tagged vt2 change", txnID);

        DataObject rb = rollback("shallowAddRemoveOneWayComponent",
                                 vt2.getOID(), txnID);

        assertAssocEquals("vt2.c1s contains c1b and c1d",
                          expected,
                          (DataAssociation) rb.get(C1S));
    }

    public void testAddTagChangeComponent() {
        final Set expected = new HashSet(2);

        /*
         * Add/remove the following components to/from vt2:
         * c1a:
         *   add, tag, change
         *
         * Upon rollback to the tagged state, vt2 should have c1a should be
         * rolled back to the tagged state, thus undoing the change that has
         * occured after the tagged point.
         */
        DataObject vt2 = Util.newDataObject(VT2);
        vt2.set(NAME, "vt2");
        vt2.set(UNVER_ATTR, "unversioned attribute");
        DataObject c1a = Util.newDataObject(C1);
        final String c1aName = "c1a";
        c1a.set(NAME, c1aName);
        add(vt2, C1S, c1a);
        vt2.save();

        final String vt2Tag = "vt2 tag";
        tag(vt2.getOID(), vt2Tag);
        fakeCommit();

        c1a = refetch(c1a);
        c1a.set(NAME, "c1a revised");
        c1a.save();
        fakeCommit();

        c1a = refetch(c1a);
        BigInteger txnID =
            Versions.getMostRecentTxnID(vt2.getOID(), vt2Tag);

        rollback("addAddTagChangeComponent", vt2.getOID(), txnID);
        assertEquals("c1a's rolled back name", c1aName, (String) c1a.get(NAME));
    }


    /**
     * This tests a versioned object type that extends an non-versioned
     * (unreachable) object type. The thing we are testing is whether the
     * attributes inherited from the supertype are versioned.
     **/
    public void testSupertypeAttributes() {
        final String testName = "supertypeAttributes";
        DataObject vut1 = Util.newDataObject(VUT1);
        final String name = "vut1";
        vut1.set(NAME, name);
        final String vut1Attr = "vut1 attr";
        vut1.set(VUT1_ATTR, vut1Attr);
        vut1.save();
        final String vut1Tag = "vut1 tag";
        tag(vut1.getOID(), vut1Tag);

        fakeCommit();

        vut1 = refetch(vut1);
        vut1.set(NAME, "vut1 revised");
        vut1.set(VUT1_ATTR, "revised vut1 attr");
        vut1.save();
        fakeCommit();

        vut1 = refetch(vut1);
        vut1.set(NAME, "vut1 re-revised");
        vut1.save();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vut1.getOID(), vut1Tag);
        assertNotNull("tagged vut1 change", txnID);

        DataObject rb = rollback(testName, vut1.getOID(), txnID);

        assertEquals("vut1 attr", vut1Attr, (String) rb.get(VUT1_ATTR));
        assertEquals("supertype attr", name, (String) rb.get(NAME));
    }

    public void testNonRequiredCollections() {
        DataObject vt5 = Util.newDataObject(VT5);
        final String vt5name = "vt5";
        vt5.set(NAME, vt5name);
        DataObject ut2 = Util.newDataObject(UT2);
        ut2.set(NAME, "ut2");
        add(vt5, UT2S, ut2);
        vt5.save();
        final String vt5Tag = "vt5 tag";
        tag(vt5.getOID(), vt5Tag);

        fakeCommit();

        vt5 = refetch(vt5);
        ut2 = refetch(ut2);
        vt5.set(NAME, "vt5 revised");
        remove(vt5, UT2S, ut2);
        ut2.delete();
        vt5.save();
        fakeCommit();

        vt5 = refetch(vt5);
        vt5.set(NAME, "vt5 re-revised");
        vt5.save();
        fakeCommit();

        BigInteger txnID = Versions.
            getMostRecentTxnID(vt5.getOID(), vt5Tag);
        assertNotNull("tagged vt5 change", txnID);

        DataObject rb = rollback("nonRequiredCollections", vt5.getOID(), txnID);

        assertEquals("name", vt5name, (String) rb.get(NAME));
        assertAssocEquals("no ut2s",
                          new HashSet(),
                          (DataAssociation) rb.get(UT2S));
    }

    public void testAttributeTypes() {
        VT4 vt4 = new VT4();
        final BigDecimal bigDecimal = new BigDecimal(12345);
        vt4.setBigDecimal(bigDecimal);
        final BigInteger bigInteger = new BigInteger("23456");
        vt4.setBigInteger(bigInteger);
        final byte[] blob = getTestBlob(10240, 256);
        vt4.setBlob(blob);
        final Boolean bool = Boolean.TRUE;
        vt4.setBoolean(bool);
        final Byte byteValue = new Byte((byte) 127);
        vt4.setByte(byteValue);
        final Character character = new Character('c');
        vt4.setCharacter(character);
        final Date date = new Date();
        vt4.setDate(date);
        final Double doubleValue = new Double(19);
        vt4.setDouble(doubleValue);
        // FIXME: if the precision is increased to, say, 3.1415927, the
        // floatValue assertion fails on my Oracle instance, although it passes
        // on Postgres. I reduced the number of significant digits to make the
        // test pass for now.  See
        // http://post-office.corp.redhat.com/archives/ccm-engineering-list/2003-June/msg00006.html
        // (Message-Id: <200306111631.47365.vadimn@redhat.com>) for details.
        final Float floatValue = new Float(3.14159);
        vt4.setFloat(floatValue);
        final Integer integer = new Integer(511);
        vt4.setInteger(integer);
        final Long longValue = new Long(100000L);
        vt4.setLong(longValue);
        final Short shortValue = new Short((short) 30);
        vt4.setShort(shortValue);
        final String clob = longString();
        vt4.setClob(clob);
        final String string = "foo";
        vt4.setString(string);

        vt4.getDataObject().save();

        final String vt4Tag = "vt4 tag";
        final OID oid = vt4.getDataObject().getOID();
        tag(oid, vt4Tag);

        fakeCommit();

        vt4 = new VT4(m_ssn.retrieve(oid));
        vt4.setBigDecimal(new BigDecimal(54321));
        vt4.setBigInteger(new BigInteger("65432"));
        vt4.setBlob(getTestBlob(5120, 128));
        vt4.setBoolean(Boolean.FALSE);
        vt4.setByte(new Byte((byte) 31));
        vt4.setCharacter(new Character('z'));
        vt4.setDate(new Date(System.currentTimeMillis() - 100000));
        vt4.setDouble(new Double(84));
        vt4.setFloat(new Float(2.718281828));
        vt4.setInteger(new Integer(2003));
        vt4.setLong(new Long(101010L));
        vt4.setShort(new Short((short) 70));
        vt4.setClob(clob + clob);
        vt4.setString("bar");
        vt4.getDataObject().save();
        BigInteger txnID = Versions.getMostRecentTxnID(oid, vt4Tag);

        VT4 rb = new VT4(rollback("attributeTypes", oid, txnID));

        assertEquals("big decimal", bigDecimal, rb.getBigDecimal());
        assertEquals("big integer", bigInteger, rb.getBigInteger());
        assertTrue("blob", Arrays.equals(blob, rb.getBlob()));
        assertEquals("boolean", bool, rb.getBoolean());
        assertEquals("byte", byteValue, rb.getByte());
        assertEquals("character", character, rb.getCharacter());
        Date vt4date = rb.getDate();
        assertNotNull("vt4date", vt4date);
        assertTrue("equal up to within 1 second",
                   Math.abs(date.getTime() - vt4date.getTime()) < 1000);
        assertEquals("double", doubleValue, rb.getDouble());
        assertEquals("float", floatValue, rb.getFloat());
        assertEquals("integer", integer, rb.getInteger());
        assertEquals("long", longValue, rb.getLong());
        assertEquals("short", shortValue, rb.getShort());
        assertEquals("clob", clob, rb.getClob());
        assertEquals("string", string, rb.getString());
    }

    private static byte[] getTestBlob(int size, int modulo) {
        byte[] result = new byte[size];
        for (int ii=0; ii<size; ii++) {
            result[ii] = (byte) (ii % modulo);
        }
        return result;
    }

    private static void add(DataObject container, String property,
                            DataObject contained) {

        ((DataAssociation) container.get(property)).add(contained);
    }

    private static void remove(DataObject container, String property,
                               DataObject contained) {

        ((DataAssociation) container.get(property)).remove(contained);
    }

    /**
     * Asserts that the associations returns all of the data objects whose oids
     * are in the set. Asserts that the set contains all of the oids for data
     * objects returned by the data association. Closes the association before
     * returning.
     **/
    private static void assertAssocEquals(String msg,
                                          Set expected,
                                          DataAssociation daActual) {

        DataAssociationCursor cursor = daActual.cursor();
        Set actual = new HashSet(expected.size());

        while ( cursor.next() ) {
            actual.add(((DataObject) cursor.getDataObject()).getOID());
        }
        cursor.close();
        assertEquals(msg, expected, actual);
    }

    private static String longString() {
        StringBuffer sb = new StringBuffer(5000);
        for (int ii=0; ii<100; ii++) {
            sb.append("I will not write this one hundred and one times." +
                      Constants.LINE_SEP);
        }
        return sb.toString();
    }
}
