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
package com.arsdigita.persistence;

import java.math.BigInteger;

/**
 * DeletionTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class DeletionTest extends PersistenceTestCase {

    

    public DeletionTest(String name) {
        super(name);
    }

    public void testRemoveOpt2manyBack() {
        Session ssn = SessionManager.getSession();
        OID TEST = new OID("test.Test", BigInteger.ZERO);
        OID ICLE = new OID("test.Icle", BigInteger.ZERO);

        DataObject icle = ssn.create(ICLE);
        icle.save();

        DataObject test = ssn.create(TEST);
        test.set("required", icle);
        test.save();

        icle.set("opt2manyBack", test);
        icle.save();

        icle.set("opt2manyBack", null);
        icle.save();

        icle = ssn.retrieve(ICLE);
        assertEquals(null, icle.get("opt2manyBack"));
    }

}
