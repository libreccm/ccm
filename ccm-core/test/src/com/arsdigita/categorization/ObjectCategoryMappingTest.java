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
package com.arsdigita.categorization;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

/**
 * This performs some smoke tests on the ObjectCategoryMapping class
 * Specifically, it makes sure that every method works as advertised and that
 * the system talks to the database currectly.
 *
 * @author Randy Graebner
 * @version $Revision: #19 $ $DateTime: 2004/08/16 18:10:38 $
 * @see com.arsdigita.kernel
 * @see com.arsdigita.categorization
 */
public class ObjectCategoryMappingTest extends CategoryTestCase {

    private final static Logger s_log = Logger.getLogger
        (ObjectCategoryMappingTest.class);

    Session session;

    /**
     * Constructs a ObjectCategoryMappingTest with the specified name.
     *
     * @param name Test case name.
     **/
    public ObjectCategoryMappingTest( String name ) {
        super( name );
    }


    public void setUp() {
        session = SessionManager.getSession();
    }


    /**
     *  This tests the "isDefault" stuff
     */
    public void testIsDefault() {
        Category category = new Category();
        Group group = new Group();
        group.setName("this group");

        CategorizedObject object = new CategorizedObject(group);
        object.setDefaultParentCategory(category);

        assertTrue("The default parent is correct",
                   category.equals(object.getDefaultParentCategory()));

        object.setDefaultParentCategory(null);
        object.save();

        try {
            Category cat = object.getDefaultParentCategory();
            fail("The default parent was set to null but one was returned " +
                 "anyway: " + cat);
        } catch (CategoryNotFoundException e) {
            ;
        }
    }


    /**
     *  This tests the index item stuff
     */
    public void testCategoryIndex() {
        Category category = new Category();
        Category subcategory = new Category();
        subcategory.setDefaultParentCategory(category);
        Group group = new Group();
        group.setName("this group");

        category.addChild(group);
        category.setIndexObject(group);
        category.save();

        s_log.debug("testCategoryIndex: category.getIndexObject");
        assertEquals("index object", group, category.getIndexObject());
        assertEquals("direct index object",
                     group, category.getDirectIndexObject());
        assertEquals("index object for subcategory",
                     group, subcategory.getIndexObject());
        assertNull("direct index for subcategory",
                   subcategory.getDirectIndexObject());

        category.setIndexObject(null);
        category.save();

        assertNull("category index", category.getIndexObject());
        assertNull("category direct index", category.getDirectIndexObject());
        assertNull("subcategory index", subcategory.getIndexObject());
        assertNull("subcategory direct index",
                   subcategory.getDirectIndexObject());
    }


    /**
     *  This tests adding and removing items as well as some sorting
     */
    public void testAddingAndRemoving() {
        Group group1 = new Group();
        group1.setName("this group1");
        Group group2 = new Group();
        group2.setName("this group2");
        Group group3 = new Group();
        group3.setName("this group3");
        Group group4 = new Group();
        group4.setName("this group4");

        Category category = new Category();

        category.addChild(group1);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 1);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group1);

        category.addChild(group2);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 2);

        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group1);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group2);

        // add the same child again to make sure that there is not an error
        category.addChild(group2);

        category.removeChild(group1);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 1);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group2);

        // add and remove to make sure successive items does not error out
        category.addChild(group3);
        category.addChild(group4);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 3);
        category.removeChild(group3);
        category.removeChild(group4);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 1);

        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group2);

        // we save after every add to guarantee order
        category.addChild(group3);
        category.addChild(group4);
        category.addChild(group1);
        assertSize(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 4);

        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group1);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group2);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group3);
        assertContains(category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), group4);

        final ACSObjectCollection children = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        children.addOrder("categories.link.sortKey");

        // the order at this point should be 2, 3, 4, 1 so let's confirm
        assertTrue("has next", children.next());
        assertEquals("group2 is first", group2, children.getDomainObject());

        assertTrue("has next", children.next());
        assertEquals("group3 is second", group3, children.getDomainObject());

        assertTrue("has next", children.next());
        assertEquals("group4 is third", group4, children.getDomainObject());

        assertTrue("has next", children.next());
        assertEquals("group1 is last", group1, children.getDomainObject());

        children.close();
        category.swapWithNext(group3);

        final ACSObjectCollection children2 = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        children2.addOrder("categories.link.sortKey");

        assertTrue("has next", children2.next());
        assertEquals("group2 is 1st", group2, children2.getDomainObject());

        assertTrue("has next", children2.next());
        assertEquals("group4 is 2nd",group4, children2.getDomainObject());

        assertTrue("has next", children2.next());
        assertEquals("group3 is 3rd", group3, children2.getDomainObject());

        assertTrue("has next", children2.next());
        assertEquals("group1 is last", group1, children2.getDomainObject());

        children2.close();

        category.swapWithPrevious(group4);

        final ACSObjectCollection children3 = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        children3.addOrder("categories.link.sortKey");

        assertTrue("has next", children3.next());
        assertEquals("group4 is 1st", group4, children3.getDomainObject());

        assertTrue("has next", children3.next());
        assertEquals("group2 is 2nd", group2, children3.getDomainObject());

        assertTrue("has next", children3.next());
        assertEquals("group3 is 3rd", group3, children3.getDomainObject());

        assertTrue("has next", children3.next());
        assertEquals("group1 is last", group1, children3.getDomainObject());
        children3.close();

        category.setSortKey(group1, 10);
        category.setSortKey(group2, 20);
        category.setSortKey(group3, 30);
        category.setSortKey(group4, 40);

        final ACSObjectCollection children4 = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        children4.addOrder("categories.link.sortKey");

        assertTrue("has next", children4.next());
        assertEquals("group1 is 1st", group1, children4.getDomainObject());

        assertTrue("has next", children4.next());
        assertEquals("group2 is 2nd", group2, children4.getDomainObject());

        assertTrue("has next", children4.next());
        assertEquals("group3 is 3rd", group3, children4.getDomainObject());

        assertTrue("has next", children4.next());
        assertEquals("group4 is 4th", group4, children4.getDomainObject());
    }
}
