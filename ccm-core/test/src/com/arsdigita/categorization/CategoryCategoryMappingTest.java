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

import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import java.math.BigDecimal;
import org.apache.log4j.Logger;


/**
 * This performs some smoke tests on the CategoryCategoryMapping class.
 * Specifically, it makes sure that every method works as advertised and that
 * the system talks to the database currectly
 *
 *
 * @author Randy Graebner
 * @version 1.0
 * @see com.arsdigita.kernel
 * @see com.arsdigita.categorization
 */
public class CategoryCategoryMappingTest extends CategoryTestCase {

    private final static Logger s_log = Logger.getLogger
        (CategoryCategoryMappingTest.class);

    /**
     * Constructs a CategoryCategoryMappingTest with the specified name.
     *
     * @param name Test case name.
     **/
    public CategoryCategoryMappingTest( String name ) {
        super( name );
    }


    public void testIsDefault() {
        Category category2 = new Category();
        Category category = new Category();
        category2.setDefaultParentCategory(category);
        assertEquals("default parent",
                     category, category2.getDefaultParentCategory());

        category2.setDefaultParentCategory(null);

        try {
            Category cat = category2.getDefaultParentCategory();
            fail("The default parent was set to null but one was returned " +
                 "anyway: " + cat);
        } catch (CategoryNotFoundException e) {
            ;
        }
    }


    /**
     *  This tests the sort_key stuff.
     */
    public void testSortKey() {
        Category category2 = new Category();
        Category category = new Category();
        category2.setDefaultParentCategory(category);

        DataObject object =
            DomainServiceInterfaceExposer.getDataObject(category2);
        final DataAssociationCursor parents =
            ((DataAssociation)object.get("parents")).cursor();
        assertTrue("has next", parents.next());
        parents.getLink().set(Category.SORT_KEY, new BigDecimal(3));
        parents.close();

        final DataAssociationCursor parents2 =
            ((DataAssociation) object.get("parents")).cursor();
        assertTrue("has next", parents2.next());
        int sortKey = ((BigDecimal) parents2.getLink().get(Category.SORT_KEY))
            .intValue();
        parents2.close();

        assertTrue("The sort key should have been 3 but it was actually " +
                   sortKey, sortKey == 3);
    }

    /**
     * Test deleteAndRemap() of a category with a related category and a
     * child.
     */
    public void testDeleteAndRemap() {
        Category base = new Category();
        Category child = new Category();
        Category other = new Category();
        Category gchild = new Category();
        child.setDefaultParentCategory(base);
        child.addRelatedCategory(other);
        gchild.setDefaultParentCategory(child);
        child.deleteCategoryAndRemap();
        assertEquals("grandchild should be remapped to base",
                     base, gchild.getDefaultParentCategory());
    }

    /**
     * Test sorting (swapWithXXXX)
     */
    public void testSorting() throws Exception {
        Category category1 = new Category();
        Category category2 = new Category();
        Category category3 = new Category();
        Category category4 = new Category();

        Category category = new Category();

        category.addChild(category1);
        assertSize(category.getChildren(), 1);
        assertContains(category.getChildren(), category1);

        category.addChild(category2);

        assertSize(category.getChildren(), 2);
        assertContains(category.getChildren(), category1);
        assertContains(category.getChildren(), category2);

        // add the same child again to make sure that there is not an error
        category.addChild(category2);

        category.removeChild(category1);
        assertSize(category.getChildren(), 1);
        assertContains(category.getChildren(), category2);

        // add and remove to make sure successive items does not error out
        category.addChild(category3);
        category.addChild(category4);
        assertSize(category.getChildren(), 3);
        category.removeChild(category3);
        category.removeChild(category4);
        assertSize(category.getChildren(), 1);
        assertContains(category.getChildren(), category2);

        // we save after every add to guarantee order
        category.addChild(category3);
        category.addChild(category4);
        category.addChild(category1);
        assertSize(category.getChildren(), 4);

        assertContains(category.getChildren(), category1);
        assertContains(category.getChildren(), category2);
        assertContains(category.getChildren(), category3);
        assertContains(category.getChildren(), category4);

        final CategoryCollection cats1 = category.getChildren();
        // the order at this point should be 2, 3, 4, 1 so let's confirm
        cats1.sort(true);
        assertTrue("has next", cats1.next());
        assertEquals("first item",  category2, cats1.getDomainObject());
        assertTrue("has next", cats1.next());
        assertEquals("second item", category3, cats1.getDomainObject());
        assertTrue("has next", cats1.next());
        assertEquals("third item", category4, cats1.getDomainObject());
        assertTrue("has next", cats1.next());
        assertEquals("last item", category1, cats1.getDomainObject());

        cats1.close();

        category.swapWithNext(category3);

        final CategoryCollection cats2 = category.getChildren();
        cats2.sort(true);
        assertTrue("has next", cats2.next());
        assertEquals("first item", category2, cats2.getDomainObject());
        assertTrue("has next", cats2.next());
        assertEquals("second item", category4, cats2.getDomainObject());
        assertTrue("has next", cats2.next());
        assertEquals("third item", category3, cats2.getDomainObject());
        assertTrue("has next", cats2.next());
        assertEquals("last item", category1, cats2.getDomainObject());

        cats2.close();
        category.swapWithPrevious(category4);

        final CategoryCollection cats3 = category.getChildren();
        cats3.sort(true);
        assertTrue("has next", cats3.next());
        assertEquals("first item", category4, cats3.getDomainObject());
        assertTrue("has next", cats3.next());
        assertEquals("second item", category2, cats3.getDomainObject());
        assertTrue("has next", cats3.next());
        assertEquals("third item", category3, cats3.getDomainObject());
        assertTrue("has next", cats3.next());
        assertEquals("last item", category1, cats3.getDomainObject());
    }


    /**
     * Test sorting (swapWithXXXX)
     */
    public void testSortingWithNonConsecutiveKeys() throws Exception {
        Category parent1 = new Category();
        parent1.setName("parent1");
        Category parent2 = new Category();
        parent2.setName("parent2");

        Category child1 = new Category();
        child1.setName("child1");
        Category child2 = new Category();
        child2.setName("child2");

        parent1.addChild(child1);
        parent2.addChild(child1);

        parent1.addChild(child2);
        parent2.addChild(child2);

        // now, child 1 should be first and child 2 should be second
        final CategoryCollection cats1 = parent1.getChildren();
        cats1.sort(true);
        assertTrue("has next", cats1.next());
        assertEquals("first item", child1, cats1.getDomainObject());

        assertTrue("has next", cats1.next());
        assertEquals("first item", child2, cats1.getDomainObject());

        cats1.close();
        // now, swap and we should get the opposite order
        parent1.swapWithNext(child1);

        final CategoryCollection cats2 = parent1.getChildren();
        cats2.sort(true);
        assertTrue("has next", cats2.next());
        assertEquals("first item", child2, cats2.getDomainObject());
        assertTrue("has next", cats2.next());
        assertEquals("first item", child1, cats2.getDomainObject());
        cats2.close();
    }
}
