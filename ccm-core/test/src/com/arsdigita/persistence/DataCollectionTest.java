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

public abstract class DataCollectionTest extends DataQueryTest  {


    public DataCollectionTest(String name) {
        super(name);
    }

    public void testGetDataObject()
    {
        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next())
            {
                DataObject obj = allItems.getDataObject();
                assertEquals( "Somehow failed to retrieve correct DataObject",
                              getDefaultObjectType(), obj.getObjectType());
                count++;
            }
        assertTrue( "No data objects?", count > 0);
    }

    public void testGetObjectType()
    {

        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next())
            {
                ObjectType type = allItems.getObjectType();
                assertEquals( "Somehow failed to retrieve correct object type.", getDefaultObjectType(), type);
                count++;
            }

        assertTrue( "No data objects?", count > 0);
    }

    protected abstract DataCollection getDefaultCollection();
    protected abstract ObjectType getDefaultObjectType();

}
