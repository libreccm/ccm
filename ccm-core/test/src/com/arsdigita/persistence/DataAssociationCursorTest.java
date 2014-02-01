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

/**
 * DataCollectionImplTest
 *
 * This class tests DataCollectionImplTest, using the Node.pdl data definition.
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author Jon Orris
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */
public class DataAssociationCursorTest extends DataCollectionTest {


    OrderAssociation m_orderAssoc;

    public DataAssociationCursorTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        super.persistenceTearDown();
    }

    /**
     * Used by JUnit (called before each test method)
     **/
    protected void setUp() {
        m_orderAssoc = new OrderAssociation( getSession() );
    }

    /**
     * Used by JUnit (called after each test method)
     **/
    protected void tearDown() {
        m_orderAssoc.tearDown();
    }

    public void testGetDataAssociation() {
        DataAssociation items = m_orderAssoc.getLineItems();
        DataAssociationCursor cursor = items.cursor();

        assertEquals("Cursor didn't return parent association!", items, cursor.getDataAssociation());
    }

    public void testRemove() {
        DataAssociationCursor cursor = getItemsCursor();
        BigDecimal deadId = new BigDecimal(3);
        while(cursor.next()) {
            if( cursor.get("id").equals(deadId) ) {
                cursor.remove();
            }
        }

        cursor.rewind();
        boolean found = false;
        while(cursor.next() && !found) {
            found = cursor.get("id").equals(deadId);
        }

        assertFalse("Id " + deadId + " not removed from DataAssociation!",
		    found);
    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testSetOrder() {

    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testAddOrder() {

    }

    /**
     *  This tests the ability to add multiple filters to a data query
     */
    public void testAddFilter() {

    }

    protected DataQuery getDefaultQuery() {
        return getDefaultCollection();
    }

    protected DataCollection getDefaultCollection() {
        return getItemsCursor();
    }

    protected ObjectType getDefaultObjectType() {
        return m_orderAssoc.getLineItemType();
    }

    private DataAssociationCursor getItemsCursor()  {
        return m_orderAssoc.getLineItems().cursor();
    }
}
