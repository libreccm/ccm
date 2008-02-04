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
package com.arsdigita.metadata;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author Patrick McNeill
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */

public class DynamicAssociationTest extends PersistenceTestCase {
    private static Logger s_log =
        Logger.getLogger(DynamicAssociationTest.class.getName());

    private MetadataRoot m_root = MetadataRoot.getMetadataRoot();
    private List m_tables = new ArrayList();
    private List m_assocs = new ArrayList();

    public DynamicAssociationTest(String name) {
        super(name);
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/metadata/pdl/DataOperation.pdl");


        DataCollection dc = getSession().retrieve
            ("com.arsdigita.persistence.DynamicAssociation");
        dc.addEqualsFilter("modelName", "teststuff.foo");
        while (dc.next()) {
            System.out.println("Deleting....");
            dc.getDataObject().delete();
        }
        // this is here so that the "delete" operation above takes
        getSession().getTransactionContext().commitTxn();
        getSession().getTransactionContext().beginTxn();

        Iterator iter = m_tables.iterator();
        java.sql.Statement statement = null;
        try {
            statement = SessionManager.getSession()
                .getConnection()
                .createStatement();
            while (iter.hasNext()) {
                String table = (String)iter.next();
                try {
                    statement.executeUpdate("drop table " + table);
                } catch (Exception e) {
                    s_log.warn("Error executing statement " +
                               "'drop table " + table + "': " + e);
                }
            }

            Iterator assocs = m_assocs.iterator();
            while (assocs.hasNext()) {
                DynamicAssociation assoc = (DynamicAssociation) assocs.next();
//                assoc.delete();
            }
        } catch (Exception e) {
            s_log.error("Error creating statement: " + e.getMessage(), e);
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                //ignore
            }
        }

        getSession().getTransactionContext().abortTxn();
        getSession().getTransactionContext().beginTxn();


        super.persistenceTearDown();
    }


    public void testCreation() throws Exception {

        /*
        DynamicAssociation dass = new DynamicAssociation(
                                                         "teststuff.foo",
                                                         "com.arsdigita.kernel.ACSObject",
                                                         "owned",
                                                         Property.COLLECTION,
                                                         "com.arsdigita.kernel.User",
                                                         "owner",
                                                         Property.NULLABLE);

        Association assoc = dass.save();
        m_assocs.add(assoc);
        ObjectType object =
            m_root.getObjectType("com.arsdigita.kernel.ACSObject");
        ObjectType user =
            m_root.getObjectType("com.arsdigita.kernel.User");

        assertTrue("Property not found in User",
                   user.getProperty("owned") != null);
        assertTrue("Property not found in ACSObject",
                   object.getProperty("owner") != null);

        DynamicAssociation dass2 = new DynamicAssociation(
                                                          "teststuff.foo",
                                                          "com.arsdigita.kernel.ACSObject",
                                                          "owned",
                                                          "com.arsdigita.kernel.User",
                                                          "owner");

        Association assoc2 = dass2.save();
        m_assocs.add(dass2);
        assertTrue("Saved associations are different", assoc.equals(assoc2));

        try {
            dass2 = new DynamicAssociation(
                                           "teststuff.foo",
                                           "com.arsdigita.kernel.ACSObject",
                                           "container",
                                           "com.arsdigita.kernel.User",
                                           "owner");

            fail("No error thrown on bad association");
        } catch (Exception e) {
        }

        // assume there's at least one user in the system
        DataCollection collection =
            getSession().retrieve("com.arsdigita.kernel.User");
        m_tables.add
            (((JoinThrough) DynamicObjectTypeTest.getObjectMap
              (assoc.getRoleOne().getContainer()).getMapping
              (Path.get(assoc.getRoleOne().getName())))
             .getFrom().getTable().getName());

        if (collection.next()) {
            DataObject userObj = collection.getDataObject();
            OID userID = userObj.getOID();

            DataObject testObj1 =
                getSession().create("com.arsdigita.kernel.ACSObject");
            testObj1.set("id", new BigDecimal(-101));
            testObj1.set("objectType", "com.arsdigita.kernel.ACSObject");
            testObj1.set("displayName", "Test Object 1");
            testObj1.save();
            OID testID1 = testObj1.getOID();

            DataObject testObj2 =
                getSession().create("com.arsdigita.kernel.ACSObject");
            testObj2.set("id", new BigDecimal(-102));
            testObj2.set("objectType", "com.arsdigita.kernel.ACSObject");
            testObj2.set("displayName", "Test Object 2");
            testObj2.save();
            OID testID2 = testObj2.getOID();

            DataAssociation owned = (DataAssociation)userObj.get("owned");
            owned.add(testObj1);
            owned.add(testObj2);
            userObj.save();

            userObj = getSession().retrieve(userID);
            testObj1 = getSession().retrieve(testID1);
            testObj2 = getSession().retrieve(testID2);

            owned = (DataAssociation)userObj.get("owned");
            DataAssociationCursor cursor = owned.cursor();

            assertTrue("Incorrect number of objects associated",
                       cursor.size() == 2);

            boolean found1 = false;
            boolean found2 = false;

            while (cursor.next()) {
                DataObject next = cursor.getDataObject();

                if (next.equals(testObj1)) {
                    found1 = true;
                } else if (next.equals(testObj2)) {
                    found2 = true;
                } else {
                    fail("Incorrect dataobject retrieved");
                }

                cursor.remove();
            }
            userObj.save();

            assertTrue("Association was missing an object", found1 && found2);

            testObj1.delete();
            testObj2.delete();
        }

        collection.close();
        */
    }
}
