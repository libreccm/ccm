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

import java.math.BigDecimal;

/**
 * LazyLoadFailureTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public class LazyLoadFailureTest extends PersistenceTestCase {

    public final static String versionId = "$Id: LazyLoadFailureTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public LazyLoadFailureTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        super.persistenceSetUp();
    }

    public void test() {
        OID oid = new OID("examples.Node", new BigDecimal(1));
        Session ssn = SessionManager.getSession();
        DataObject node = ssn.create(oid);
        node.set("name", "Test Node");
        node.save();

        DataQuery dq = ssn.retrieveQuery("examples.lazyNodesQuery");
        dq.addEqualsFilter("node.id", new BigDecimal(1));
        if (dq.next()) {
            node = (DataObject) dq.get("node");
        } else {
            fail("Lazy query didn't return any rows.");
        }

        try {
            String name = (String) node.get("lazyProperty");
            fail("Lazy load should have bombed out but didn't. It returned: ("
                 + name + ") instead.");
        } catch (PersistenceException e) {
            // Test passes
        }
    }

}
