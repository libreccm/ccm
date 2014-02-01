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

import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import java.util.Date;

/*  This is a utiliy class for creating an Order and a DataAssociation set of
 *  LineItems. Used internally by testing classes.
 *
 */
final class OrderAssociation {

    public static final int NUM_ITEMS = 10;

    private final String m_model;

    OrderAssociation(Session session) {
        this(session, "examples");
    }

    OrderAssociation(Session session, String model) {
        m_model = model + ".";
        BigDecimal id = new BigDecimal(1);

        DataObject order = session.create(m_model + "Order");
        order.set("id", id);
        order.set("buyer", "Michael Bryzek");
        order.set("shippingAddress",
                  "2036 Shattuck Ave.\nBerkeley, CA 94704");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        m_order = (DataObject) session.retrieve
            (new OID(m_model + "Order", id));

        DataAssociation items = (DataAssociation) m_order.get("items");
        for (int i = 0; i < NUM_ITEMS; i++) {
            DataObject li = session.create(m_model + "LineItem");
            li.set("id", new BigDecimal(i));
            li.set("name", "Item " + i);
            li.set("price", new Float(2.99 + i));
            items.add(li);
            m_lineItemType = li.getObjectType();
        }
        m_order.save();

        m_lineItems = (DataAssociation) m_order.get("items");

    }

    /* Cleans out the database. Must be called by TestCase.tearDown().
     *
     */
    void tearDown() {
        m_order.delete();
        m_order = null;
        m_lineItems = null;
    }


    DataObject getOrder() {
        return m_order;
    }

    ObjectType getLineItemType() {
        return m_lineItemType;
    }

    DataAssociation getLineItems() {
        return m_lineItems;
    }

    private DataObject m_order;
    private DataAssociation m_lineItems;
    private ObjectType m_lineItemType;

}
