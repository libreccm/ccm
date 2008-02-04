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
package com.arsdigita.persistence;

import java.math.BigInteger;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * This test verifies the aggressive connection closing functionality
 * by looking for appropriate log statements.
 *
 * @author David Eison
 */
public class AggressiveConnectionCloseTest extends Log4jBasedTestCase {

    public static final String versionId = "$Id: AggressiveConnectionCloseTest.java 749 2005-09-02 12:11:57Z sskracic $";

    private Session ssn;

    boolean originalCloseValue = false;

    public AggressiveConnectionCloseTest(String name) {
        super(name);
    }

    // the idea here is to pick an incredibly dirt-simple PDL file that
    // has an insert statement
    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceSetUp();
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     */
    public void setUp() throws Exception {
        super.setUp();

        ssn = getSession();
        throw new Error("fix: originalCloseValue = ssn.getTransactionContext().getAggressiveClose();");
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void tearDown() throws Exception {
        super.tearDown();

        throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(originalCloseValue);");
    }

    public void testAggressiveClosing() {
        StringMatchFilter filterReturn = new StringMatchFilter();
        String returnString = "connectionUserCountHitZero returning connection";
        filterReturn.setStringToMatch(returnString);
        filterReturn.setAcceptOnMatch(true);
        log.addFilter(filterReturn);
        log.addFilter(new DenyAllFilter());

        if (true) throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(true);");

        // do something simple, should result in a holding on to the
        // connection
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.save();

        assertLogDoesNotContain(returnString);

        clearLog();

        // abort prev transaction, start a new one, so that we can have a
        // clean connection
        ssn.getTransactionContext().commitTxn();
        ssn.getTransactionContext().beginTxn();

        // do something else simple, should result in a return message
        dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
        assertNotNull("Should have actually retrieved something", dt);
        try {
            assertLogContains(returnString);

            if (true) throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(false);");

            // abort prev transaction, start a new one, so that we can have a
            // clean connection
            ssn.getTransactionContext().abortTxn();
            ssn.getTransactionContext().beginTxn();

            // test w/ aggressive closing off, shouldn't result in either message
            clearLog();

            dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
            dt.set("date", new java.util.Date(1000));
            dt.save();

            assertLogDoesNotContain(returnString);
        } finally {
            // delete, since we had to commit earlier.
            dt.delete();
            ssn.getTransactionContext().commitTxn();
            ssn.getTransactionContext().beginTxn();
        }
    }
}
