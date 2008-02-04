/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.categorization;

import com.arsdigita.kernel.Group;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Temporary placeholder for tests that need to be run in isolation.
 */
public class UseContextTest extends CategoryTestCase {
    private final static Logger s_log = Logger.getLogger(UseContextTest.class);

    private final String CTX1 = "ctx1";
    private final String CTX2 = "ctx2";
    private final String CTX3 = "ctx3";

    private Category root1;
    private Category root2;
    private Category root3;
    private Group group1;
    private Group group2;
    private Group group3;

    public UseContextTest(String name) {
        super(name);
    }

    public void setUp() {
        root1 = new Category();
        root1.setName("Root 1");

        root2 = new Category();
        root2.setName("Root 2");

        root3 = new Category();
        root3.setName("Root 3");

        group1 = new Group();
        group1.setName("Group 1");

        group2 = new Group();
        group2.setName("Group 2");

        group3 = new Group();
        group3.setName("Group 3");
    }

    /**
     * An object cannot be mapped to two different root categories in the same
     * context.
     **/
    public void testObjectContextPairUniqueness() {
        Category.setRootForObject(group1, root1, CTX1);
        assertEquals("root1", root1, Category.getRootForObject(group1, CTX1));

        Category.setRootForObject(group1, root2, CTX1);
        assertEquals("root1", root2, Category.getRootForObject(group1, CTX1));
    }


    public void testGetRootCategories() {
        Map map = new HashMap(3);
        Category.setRootForObject(group1, root1, CTX1);
        map.put(CTX1, root1);
        Category.setRootForObject(group1, root2, CTX2);
        map.put(CTX2, root2);
        Category.setRootForObject(group1, root3, CTX3);
        map.put(CTX3, root3);

        Category.setRootForObject(group2, root1, CTX1);
        Category.setRootForObject(group2, root2, CTX3);

        RootCategoryCollection roots =
            Category.getRootCategories(group1);

        while (roots.next()) {
            assertContains(map, roots.getUseContext(), roots.getCategory());
        }
        roots.close();

        assertEquals("remaining mappings", new HashMap(), map);
    }

    private void assertContains(Map map, String context, Category cat) {
        assertTrue("contains a category for " + context, map.containsKey(context));
        assertEquals("category for context: " + context, map.get(context), cat);
        map.remove(context);
    }


    public void testRootCategoryObjectMap() {
        // Set initial root categories, for group 1 & 2
        Category.setRootForObject(group1, root1);
        Category.setRootForObject(group2, root2);

        Category actual = Category.getRootForObject(group1);
        assertEquals("root1 equals getRootForObject", root1, actual);

        actual = Category.getRootForObject(group2);
        assertTrue(actual.equals(root2));

        // Change the root for group 1
        Category.setRootForObject(group1, root3);

        actual = Category.getRootForObject(group1);
        assertEquals("root3 equals getRootForObject", root3, actual);

        // Clear the root for group 2
        Category.clearRootForObject(group2);

        actual = Category.getRootForObject(group2);
        assertNull("getRootForObject", actual);

        // Check the clear has only affected group2's root
        actual = Category.getRootForObject(group1);
        assertEquals("root3 equals getRootForObject", root3, actual);
    }
}
