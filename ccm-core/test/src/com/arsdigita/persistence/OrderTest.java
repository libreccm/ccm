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
import java.util.HashSet;
import java.util.Set;

/**
 * Test
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #17 $ $Date: 2004/08/16 $
 */

public abstract class OrderTest extends PersistenceTestCase {

    

    public OrderTest(String name) {
        super(name);
    }

    protected String m_testType = null;

    abstract String getModelName();

    public void testOrderCRUD() {
        // Create an Order.
        DataObject order = getSession().create(getModelName() + ".Order");
        order.set("id", new BigDecimal(1));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();
        order.save();
        // I don't really know how to test if this worked, short of
        // querying with raw JDBC.

        // Retrieve an Order.
        order = getSession().retrieve(new OID(getModelName() + ".Order", 1));
        assertEquals("Buyer was not retrieved correctly.",
                     "Rafael H. Schloming",
                     order.get("buyer"));
        assertEquals("Seller was not retrieved correctly.",
                     null, order.get("seller"));
        assertEquals("ShippingAddress was not retrieved correctly.",
                     "102 R Inman St.\nCambridge MA, 02139",
                     order.get("shippingAddress"));
        assertTrue("Shipping date was not retrieved correctly.",
               order.get("shippingDate") instanceof java.util.Date);
        assertEquals("hasShipped was not retrieved correctly.",
                     Boolean.FALSE, order.get("hasShipped"));

        // Update an Order.
        order = getSession().retrieve(new OID(getModelName() + ".Order", 1));
        order.set("seller", "IBM");
        order.save();

        // Retrieve the updated Order.
        order = getSession().retrieve(new OID(getModelName() + ".Order", 1));
        assertEquals("Buyer was not retrieved correctly.",
                     "Rafael H. Schloming",
                     order.get("buyer"));
        assertEquals("Seller was not updated correctly.",
                     "IBM", order.get("seller"));
        assertEquals("ShippingAddress was not retrieved correctly.",
                     "102 R Inman St.\nCambridge MA, 02139",
                     order.get("shippingAddress"));

        DataQuery query =
            getSession().retrieveQuery(getModelName() + ".OrdersNumberOfLineItems");
        Filter f = query.addFilter("order_id = :order_id");
        f.set("order_id", order.get("id"));
        long numberLineItems = query.size();
        if (numberLineItems != 0) {
            fail("Number of line items should be 0. We got: " + numberLineItems);
        }
    }

    public void testOrderLineItemAssociation() {
        // Create an Order.
        DataObject order = getSession().create(getModelName() + ".Order");
        order.set("id", new BigDecimal(1));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        // Retrieve an empty persistent association.
        DataAssociation items = null;
        Object value = order.get("items");
        try {
            items = (DataAssociation) value;
        } catch (ClassCastException e) {
            fail("DataAssociation was not retrieved correctly: " +
                 value.getClass());
        }

        // Add objects to a persistent association.
        Set added = new HashSet();
        long numberOfLineItems = 10;
        long maxPrice = 0;
        for (int i = 0; i < numberOfLineItems; i++) {
            DataObject li = getSession().create(getModelName() + ".LineItem");
            li.set("id", new BigDecimal(2 + i));
            li.set("name", "Item " + i);
            maxPrice += 2.99 + i;
            li.set("price", new Float(maxPrice));
            li.set("inStock", (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE);
            li.set("order", order);
            items.add(li);
            added.add("Item " + i);
        }
        order.save();

        // Retrieve a persistent association with items in it.
        order = getSession().retrieve(new OID(getModelName() + ".Order", 1));
        items = (DataAssociation) order.get("items");
        Set retrieved = new HashSet();
        DataObject object = null;
        while (items.next()) {
            retrieved.add(items.get("name"));
            assertTrue("inStock is " + items.get("inStock").getClass() +
                   ", should be a Boolean",
                   items.get("inStock") instanceof Boolean);
            object = items.getDataObject();
        }

        if (object == null) {
            fail("object was not property initialized and therefore the loop " +
                 "did not execute");
        }

        if (!"dynamic".equals(m_testType)) {
            try {
                DataObject orders = (DataObject)object.get("order");
                object.set("unsettableOrder", orders);
                object.save();
                fail("trying to execute an event that is not defined should " +
                     "throw an error.");
            } catch (PersistenceException e) {
                // continue
            }
        }


        order = getSession().retrieve(new OID(getModelName() + ".Order", 1));
        items = (DataAssociation) order.get("items");
        retrieved = new HashSet();
        while (items.next()) {
            retrieved.add(items.get("name"));
            assertTrue("inStock is not a boolean",
                   items.get("inStock") instanceof Boolean);
        }
        assertEquals("Retrieved items don't match added items.",
                     added,
                     retrieved);

        DataObject li = getSession().retrieve(new OID(getModelName() + ".LineItem", 2));
        order = (DataObject) li.get("order");
        assertEquals("Order was not retrieved correctly.",
                     new BigDecimal(1),
                     order.get("id"));
        assertEquals("Buyer was not retrieved correctly.",
                     "Rafael H. Schloming",
                     order.get("buyer"));
        assertEquals("Seller was not retrieved correctly.",
                     null, order.get("seller"));
        assertEquals("ShippingAddress was not retrieved correctly.",
                     "102 R Inman St.\nCambridge MA, 02139",
                     order.get("shippingAddress"));

        DataQuery ordersMaxPrices =
            getSession().retrieveQuery(getModelName() + ".OrdersMaxPrices");
        assertTrue(ordersMaxPrices.next());

        assertTrue("Order was not retrieved correctly.",
                   1 == ((Number)ordersMaxPrices.get("orderId")).intValue());
        assertEquals("Buyer was not retrived correctly.",
                     "Rafael H. Schloming",
                     ordersMaxPrices.get("buyer"));
        assertEquals("Seller was not retrived correctly.",
                     null,
                     ordersMaxPrices.get("seller"));
        assertEquals
            ("maxPrice was not retrived correctly.",
             (new BigDecimal(maxPrice))
             .setScale(4, BigDecimal.ROUND_HALF_UP),
             ((BigDecimal) ordersMaxPrices.get("maxPrice"))
             .setScale(4, BigDecimal.ROUND_HALF_UP));
        assertTrue(!ordersMaxPrices.next());

        DataCollection allItems = getSession().retrieve(getModelName() + ".LineItem");
        while (allItems.next()) {
            BigDecimal id = (BigDecimal) allItems.get("id");
            assertEquals("LineItem was not retrieved correctly " +
                         "in data collection.",
                         "Item " + (id.intValue() - 2),
                         allItems.get("name"));

            li = allItems.getDataObject();
            OID oid = li.getOID();
            assertEquals("OID was not retrieved correctly.",
                         id,
                         oid.get("id"));

            order = (DataObject) li.get("order");
            assertEquals("Order was not retrieved correctly from " +
                         "line item contained in data collection.",
                         new BigDecimal(1),
                         order.get("id"));
        }

        DataQuery query =
            getSession().retrieveQuery(getModelName() + ".OrdersNumberOfLineItems");
        Filter f = query.addFilter("order_id = :order_id");
        f.set("order_id", order.get("id"));
        long actualNumberLineItems = query.size();
        if (actualNumberLineItems != numberOfLineItems) {
            fail("Number of line items should be " + numberOfLineItems +
                 ". We got: " + actualNumberLineItems);
        }

        // Make sure we can call size twice
        actualNumberLineItems = query.size();
        if (actualNumberLineItems != numberOfLineItems) {
            fail("Second call to size() failed.");
        }
    }

    public void testOrderExt() {
        // Create an orderExt.
        DataObject orderExt = getSession().create(getModelName() + ".OrderExt");
        assertNotNull(orderExt);
        orderExt.set("id", new BigDecimal(100));
        orderExt.set("text", "This is a test of an extended object type");
        orderExt.set("buyer", "Bryzek");
        orderExt.set("shippingAddress",
                     "2036 Shattuck Ave");
        orderExt.set("shippingDate",
                     new java.sql.Date(System.currentTimeMillis()));
        orderExt.set("hasShipped", Boolean.FALSE);

        orderExt.save();

        // Retrieve an orderExt.
        orderExt = getSession().retrieve(new OID(getModelName() + ".OrderExt", 100));
        assertEquals("Buyer was not retrieved correctly.",
                     "Bryzek",
                     orderExt.get("buyer"));
        assertEquals("ShippingAddress was not retrieved correctly.",
                     "2036 Shattuck Ave",
                     orderExt.get("shippingAddress"));
        assertTrue("Shipping date was not retrieved correctly.",
               orderExt.get("shippingDate") instanceof java.util.Date);
        assertEquals("hasShipped was not retrieved correctly.",
                     Boolean.FALSE, orderExt.get("hasShipped"));
        assertEquals("Text was not properly set",
                     "This is a test of an extended object type",
                     orderExt.get("text"));

        // Update an orderExt.
        orderExt = getSession().retrieve(new OID(getModelName() + ".OrderExt", 100));
        orderExt.set("text", "Trying to reset just the text");
        orderExt.save();

    }

    protected DataObject makeOrder(int numItems) {
        Session ssn = getSession();
        DataObject order = ssn.create(getModelName() + ".Order");

        order.set("id", new BigDecimal(1));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);

        DataAssociation items = (DataAssociation) order.get("items");

        for (int i = 0; i < numItems; i++) {
            DataObject item = ssn.create(getModelName() + ".LineItem");
            item.set("id", new BigDecimal(i));
            item.set("name", "Item " + i);
            item.set("price", new Float(i + 0.99));
            item.set("order", order);
            items.add(item);
        }

        return order;
    }

    public void testSavingCompositions() {
        int numItems = 10;

        DataObject order = makeOrder(numItems);
        order.save();

        DataAssociation items = (DataAssociation) order.get("items");

        assertEquals("order not saved properly", numItems, items.size());


        DataAssociationCursor cursor = items.cursor();
        while (cursor.next()) {
            DataObject item = cursor.getDataObject();
            item.set("price", new Float(0));
        }

        order.save();

        cursor = items.cursor();
        cursor.addFilter("price != 0");
        assertEquals("saves not cascaded", 0, cursor.size());
    }

    public void testDeletingCompositions() {
        int numItems = 10;
        DataObject order = makeOrder(numItems);
        order.save();

        DataAssociation items = (DataAssociation) order.get("items");
        DataCollection dc =
            getSession().retrieve(getModelName() + ".LineItem");

        assertEquals("order not saved properly", numItems, items.size());

        long start = dc.size();

        DataAssociationCursor cursor = items.cursor();
        while (cursor.next()) {
            cursor.remove();
        }

        order.save();

        long end = dc.size();
        assertEquals("deletes not cascaded", numItems, start - end);
    }

    public void testClear() {
        int numItems = 10;
        DataObject order = makeOrder(numItems);
        order.save();

        DataAssociation items = (DataAssociation) order.get("items");

        assertEquals("order not saved properly", numItems, items.size());

        items.clear();
        order.save();

        assertEquals("items not cleared properly", 0, items.size());
    }

}
