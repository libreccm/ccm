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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigInteger;
import org.apache.log4j.Logger;

public class TypesTest extends BaseTestCase {
    private static final Logger s_log =  Logger.getLogger(TypesTest.class);

    public TypesTest(String name) {
        super(name);
    }

    public void testTypes() {
        DataCollection dc = SessionManager.getSession().retrieve
            ("com.arsdigita.versioning.JavaClass");
        int count = 0;
        while (dc.next()) {
            // retrieve the matching enum constant to make sure that each type
            // in the db has a matching instance of the Types enum.
            Types.getType((BigInteger) dc.get("id"));
            count++;
        }
        assertEquals("number of types", 16, count);
    }

    public void testLiskovSubstitutability() {
        Types bigType = Types.getType(BigInteger.class);
        Types biggerType = Types.getType(BiggerInteger.class);
        Types biggestType = Types.getType(BiggestInteger.class);
        assertEquals("BiggerInteger maps to BigInteger", bigType, biggerType);
        assertEquals("BiggestInteger maps to BigInteger", bigType, biggestType);
    }

    public void testUnknownType() {
        boolean hasBeenRaised = false;
        try {
            Types.getType(TotallyRandomClass.class);
        } catch (UnknownTypeException ex) {
            hasBeenRaised = true;
        }
        assertTrue("UnknownTypeException has been properly raised", hasBeenRaised);
    }

    private static class BiggerInteger extends BigInteger {
        public BiggerInteger(String val) {
            super(val);
        }
    }

    private static class BiggestInteger extends BiggerInteger {
        public BiggestInteger(String val) {
            super(val);
        }
    }

    private static class TotallyRandomClass {}
}
