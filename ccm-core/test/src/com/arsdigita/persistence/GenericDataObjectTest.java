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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * GenericDataObjectText
 *
 * This class tests GenericDataObject, using data contained in
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */
public class GenericDataObjectTest extends PersistenceTestCase {

    public final static String versionId = "$Id: GenericDataObjectTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public GenericDataObjectTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/DataQuery.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataQueryExtra.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");

        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        super.persistenceTearDown();
    }

    /**
     * This tests to make sure that a delete followed by a save throws
     * an exception
     */
    public void testCRUD() {
        DataQuery query = getSession().retrieveQuery("examples.nodesQuery");
        long initialSize = query.size();

        DataObject node = getSession().create("examples.Node");
        node.set("id", new BigDecimal(0));
        node.set("name", "Root");
        assertTrue(node.isNew());
        assertTrue(node.isCommitted() == false);
        node.save();
        assertTrue(node.isNew() == false);
        assertTrue(node.isCommitted() == false);
        // Should have no effect.
        node.save();
        node.save();
        node.save();
        assertTrue(node.isNew() == false);
        assertTrue(node.isCommitted() == false);


        // make sure it is there
        query = getSession().retrieveQuery("examples.nodesQuery");
        assertEquals("The saving of a node did not actually save",
                     initialSize + 1, query.size());

        node.delete();

        // make sure it is not there
        query = getSession().retrieveQuery("examples.nodesQuery");
        assertTrue("The deleting of a node did not actually delete.",
               query.size() == initialSize);
        // should this be allowed?
        node.delete();

        try {
            node.save();
            fail("saving a dataobject after deleting should cause and error");
        } catch (PersistenceException e) {
            //This should happen so we fall through
        }

    }

    /**
     * Test that SQL will indeed be issued to null-out a 0..1 association
     * even if the association has not previously been fetched.  See
     * bug report 145705
     *
     * @author Patrick McNeill
     */
    public void testSetAssociationToNull() {
        DataObject parent = getSession().create("examples.Node");
        parent.set("id", new BigDecimal(42));
        parent.set("name", "Parent");
        parent.save();

        DataObject node = getSession().create("examples.Node");
        node.set("id", new BigDecimal(7));
        node.set("name", "Child");
        node.set("parent", parent);
        node.save();

        DataObject node2 = getSession().retrieve(
                                                 new OID("examples.Node", new BigDecimal(7)));

        DataObject parent2 = (DataObject)node2.get("parent");

        assertTrue("Parents not equal", parent.equals(parent2));

        DataObject node3 = getSession().retrieve(
                                                 new OID("examples.Node", new BigDecimal(7)));

        // try erasing the parent.
        node3.set("parent", null);
        node3.save();

        parent2 = (DataObject)node3.get("parent");

        assertTrue("Parent not set to null", parent2 == null);

    }




    public void testSpecialize() {
        DataObject node = getSession().create("examples.Node");
        try {
            node.specialize("grick!");
            fail("Specialized on a nonsensical ObjectType name!");
        } catch (RuntimeException e) {
        }

        try {
            node.specialize( node.getObjectType() );
        } catch (RuntimeException e) {
            fail("Failed to Specialize on self!");
        }
        DataObject party = getSession().create("examples.Party");
        party.specialize("examples.User");


        DataObject user = getSession().create(new OID("examples.User",
                                                      BigInteger.ZERO));
        user.set("email", "jorris@arsdigita.com");
        user.set("firstName", "NA");
        user.set("lastNames", "NA");
        user.save();

        party = getSession().retrieve(new OID("examples.Party",
                                              BigInteger.ZERO));
        party.specialize(user.getObjectType());
        assertEquals("NA", party.get("firstName"));
        assertEquals("NA", party.get("lastNames"));
        party.set("firstName", "Jon");
        party.set("lastNames", "Orris");
        party.save();

        try {
            party.specialize("examples.Party");
            fail("Reversed specialization!");
        } catch (RuntimeException e) {
        }

    }

    /**
     *  This makes sure that if one of the items in the retrieve event
     *  fails then the returned object is null.
     */
    public void testFailingRetrieveQuery() {
        DataObject order = getSession().create
            ("examples.OrderExtWithFailingRetrieve");

        order.set("id", new BigDecimal(1));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();
        OID oid = order.getOID();

        order = getSession().retrieve(oid);
        try {
            order.get("text");
            fail("The retrieve method for " +
                 " [examples.OrderExtWithFailingRetrieve]" +
                 "does not work so it should fail");
        } catch (PersistenceException pe) {
            // continue
        }


    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(GenericDataObjectTest.class);
    }

}
