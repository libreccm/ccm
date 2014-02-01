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
package com.arsdigita.messaging;

import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test cases for sort keys.
 *
 * @version $Id: SortKeyTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class SortKeyTest extends BaseTestCase {


    public SortKeyTest (String name) {
        super(name);
    }

    /**
     * Verify that you cannot create invalid sort keys.
     */

    public void testSortKey001() {

        try {
            SortKey s = new SortKey("###");
            fail("created nonalpha sortkey");
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            SortKey s = new SortKey("0000");
            fail("created 4-digit sortkey");
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    /**
     * Verify generating serveral generations of sort keys.  Loops up
     * and then back and verifies recovery of the parent key.
     */

    public void testSortKey002() {

        String root = "0a5";

        SortKey key = new SortKey(root);

        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                key = key.getChild();
            } else {
                key.next();
            }
        }

        for (int i = 0; i < 4; i++) {
            key = key.getParent();
        }

        assertEquals("Failed to recover root key",
                     root,
                     key.toString());
    }

    /**
     * Verify that depth calculation is correct.
     */

    public void testSortKey003() {

        SortKey key = new SortKey();

        for (int i = 0; i < 10; i++)
            key = key.getChild();

        assertEquals("Failed to compute depth",
                     11,
                     key.getDepth());
    }

    /**
     * Verify that incrementing a key works
     */

    public void testSortKey004() {

        SortKey key = new SortKey();

        for (int i = 0; i < 1000; i++) {
            key.next();
        }

        assertEquals("sort key incremented wrong",
                     "0G8",
                     key.toString());
    }
}
