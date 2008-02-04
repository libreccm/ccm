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
import org.apache.log4j.Logger;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * This test verifies that the Statements which are created by Persistence
 * are closed when their ResultSet is closed, by looking for appropriate
 * log statements.
 *
 * @author David Eison
 */
public class StatementClosingTest extends Log4jBasedTestCase {

    public static final String versionId = "$Id: StatementClosingTest.java 749 2005-09-02 12:11:57Z sskracic $";

    private Session ssn;

    boolean originalCloseValue = false;

    public StatementClosingTest(String name) {
        super(name);
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     */
    public void setUp() throws Exception {
        super.setUp();

        ssn = getSession();
        if (true) throw new Error("fix: originalCloseValue = ssn.getTransactionContext().getAggressiveClose();");
        runFinalization(false);
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void tearDown() throws Exception {
        super.tearDown();

        throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(originalCloseValue);");
    }

    public void testStatementClosing() {
        StringMatchFilter closeFilter = new StringMatchFilter();
        String closeString = "Closing Statement because resultset was closed";
        closeFilter.setStringToMatch(closeString);
        closeFilter.setAcceptOnMatch(true);

        log.addFilter(closeFilter);
        log.addFilter(new DenyAllFilter());

        if (true) throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(true);");

        // do something simple
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.save();

        runFinalization();

        assertLogDoesNotContain(closeString);

        DataCollection dc = ssn.retrieve("examples.Datatype");
        dc.addEqualsFilter("id", BigInteger.ZERO);
        dc.next();
        dc.close();
        assertLogContains(closeString);
    }

    /**
     * The point of this test is to see if making a data association and
     * calling cursor on it requires one to close the data association
     * explicitly.
     */
    public void testDataAssociationClosing() {
        StringMatchFilter daFilter = new StringMatchFilter();
        String daString = "Statement was not closed by programmer";
        daFilter.setStringToMatch(daString);
        daFilter.setAcceptOnMatch(true);
        log.addFilter(daFilter);

        StringMatchFilter closeFilter = new StringMatchFilter();
        String closeString = "close: ";
        closeFilter.setStringToMatch(closeString);
        closeFilter.setAcceptOnMatch(true);
        log.addFilter(closeFilter);

        log.addFilter(new DenyAllFilter());

        OrderAssociation oa = new OrderAssociation( ssn );
        DataAssociation items = oa.getLineItems();
        DataAssociationCursor cursor = items.cursor();
        int i = 0;
        while (cursor.next()) {
            i++;
        }
        assertTrue("Iterations should match cursor size but did not, " + i +
                   " vs " + cursor.size(),
                   cursor.size() == i);
        assertTrue("Sizes should match but did not, only found " + i +
                   " line items",
                   OrderAssociation.NUM_ITEMS == i);
        items = null;
        cursor = null;

        runFinalization();

        assertLogDoesNotContain(daString);
        assertLogContains(closeString);
    }

    private void runFinalization() {
        runFinalization(true);
    }

    private void runFinalization(boolean logging) {
        if (!logging) { Logger.getRoot().removeAppender(log); }

        // do everything we can to encourage garbage collection
        System.gc();
        try {
            Thread.sleep(100);
        } catch  (InterruptedException e) {}
        System.runFinalization();

        if (!logging) { Logger.getRoot().addAppender(log); }
    }
}
