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
import org.apache.log4j.Logger;

/**
 * RefetchTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 **/

public class RefetchTest extends PersistenceTestCase {

    public final static String versionId = "$Id: RefetchTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(RefetchTest.class);

    private static final BigInteger NODE_ID = BigInteger.ZERO;
    private static final String NODE_NAME = "Node Name";

    private static final BigInteger PARENT_ID = BigInteger.ONE;
    private static final String PARENT_NAME = "Parent Name";
    private static OID PARENT_OID = null;
    private static OID NODE_OID;
    private static final String REFETCH_TEST = "refetchTest.RefetchTest";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT = "parent";

    public RefetchTest(String name) {
        super(name);
    }


    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/RefetchTest.pdl");
        super.persistenceSetUp();
        if (PARENT_OID == null) {
            PARENT_OID = new OID(REFETCH_TEST, PARENT_ID);
        }
        if (NODE_OID == null) {
            NODE_OID = new OID(REFETCH_TEST, NODE_ID);
        }
    }

    public void test() {
        Session ssn = SessionManager.getSession();
        DataObject node = ssn.create(REFETCH_TEST);
        DataObject parent = ssn.create(REFETCH_TEST);

        parent.set(ID, PARENT_ID);
        parent.set(NAME, PARENT_NAME);
        parent.save();

        node.set(ID, NODE_ID);
        node.set(NAME, NODE_NAME);
        node.set(PARENT, parent);

        node.save();

        DataCollection nodes = ssn.retrieve(REFETCH_TEST);
        try {
            nodes.addEqualsFilter(ID, NODE_ID);
            //   s_log.warn("Node size: " + nodes.size());
            if (nodes.next()) {
                node = nodes.getDataObject();
            } else {
                fail("Node wasn't saved properly.");
            }

            DataObject newParent = ssn.retrieve(NODE_OID);

            BigInteger preID = (BigInteger) newParent.get(ID);

            node.set(PARENT, newParent);
            node.get(NAME);

            BigInteger postID = (BigInteger) newParent.get(ID);

            assertEquals(preID, postID);

        } finally {
            try {
                nodes.close();
            } catch (Exception e) {
                s_log.error("Error closing", e);
            }
        }
    }

}
