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
package com.arsdigita.db;

import java.math.BigDecimal;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SequenceTest extends TestCase {


    public static String sequenceName = "acs_object_id_seq";

    public SequenceTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    protected void setUp() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new SequenceTest("testSequences"));
        return suite;
    }

    public void testSequences() throws SQLException,ClassNotFoundException {
        java.sql.Connection conn = ConnectionManager.getConnection();

        BigDecimal seqValue1 = null;
        BigDecimal seqValue2 = null;

        seqValue1 = Sequences.getNextValue(sequenceName,conn);

        assertNotNull(seqValue1);

        seqValue2 = Sequences.getCurrentValue(sequenceName,conn);

        assertNotNull(seqValue2);
        assertEquals("nextval followed by currval didn't get the " +
                     "same thing.  This might just mean someone else " +
                     "called nextval in the middle.",
                     seqValue1,seqValue2);

        seqValue1 = Sequences.getNextValue(sequenceName,conn);

        assertTrue(! seqValue1.equals(seqValue2));

        ConnectionManager.returnConnection(conn);
    }

}
