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

import com.arsdigita.kernel.Group;


/**
 * This is a JUnit fixture for category tests involving a family.
 * All tests will be setup as follows:
 * mom/dad/brother/kid1/kid2/kid3 will all be related to
 * category "c".
 * "bob" will be utterly unrelated to the five family members.
 * mom will be the default parent of c, and c will be the
 * default parent of kid.
 * Groups gk1, gk2, gk3, and gk4 will all be children of kid1.
 * Groups gc1, gc2, gc3, and gc4 will all be children of cat.
 *
 *
 * @author David Eison
 * @version 1.0
 * @see com.arsdigita.categorization
 */
public class CategoryFamilyTest extends CategoryTestCase {
    Category cat;
    Category mom;
    Category dad;
    Category brother;
    Category kid1;
    Category kid2;
    Category kid3;
    Category bob;
    Group gk1;
    Group gk2;
    Group gk3;
    Group gc1;
    Group gc2;
    Group gc3;
    Group gb1;
    Group gb2;

    /**
     * Constructs a CategoryTest with the specified name.
     *
     * @param name Test case name.
     **/
    public CategoryFamilyTest( String name ) {
        super( name );
    }

    protected void setUp() {
        setupMainCategory();
        mom = new Category("delete me's mom", "a test categoryq");
        dad = new Category("delete me's dad", "a test categoryz");
        brother = new Category("delete me's brother", "a test categorya");
        kid1 = new Category("delete me's kid1", "a test categoryb");
        kid2 = new Category("delete me's kid2", "a test categorya");
        kid3 = new Category("delete me's kid3", "a test categoryf");
        bob = new Category("Random other category", "a test category");

        mom.addChild(cat);
        mom.addChild(brother);
        dad.addChild(cat);
        dad.addChild(brother);
        cat.addChild(kid1);
        cat.addChild(kid2);
        cat.addChild(kid3);

        // we arbitrarily decided on a matriarchy
        cat.setDefaultParentCategory(mom);
        kid1.setDefaultParentCategory(cat);
        kid2.setDefaultParentCategory(cat);
        kid3.setDefaultParentCategory(cat);

        gk1 = new Group();
        gk1.setName("group z");
        gk2 = new Group();
        gk2.setName("group y");
        gk3 = new Group();
        gk3.setName("group x");
        gc1 = new Group();
        gc1.setName("group ca");
        gc2 = new Group();
        gc2.setName("group cy");
        gc3 = new Group();
        gc3.setName("group cb");
        gb1 = new Group();
        gb1.setName("group bobq");
        gb2 = new Group();
        gb2.setName("group boba");
        kid1.addChild(gk1);
        kid1.addChild(gk2);
        kid1.addChild(gk3);
        cat.addChild(gc1);
        cat.addChild(gc2);
        cat.addChild(gc3);
        bob.addChild(gb1);
        bob.addChild(gb2);
    }

    public void tearDown() {
    }

    /**
     * Default implementation uses the name/description constructor.
     */
    protected void setupMainCategory() {
        cat = new Category("delete me", "a test category");
    }

    public void testGetParents() {
        assertSize(cat.getParents(), 2);
        assertContains(cat.getParents(), mom);
        assertContains(cat.getParents(), dad);
        assertNotContains(cat.getParents(), brother);
        assertNotContains(cat.getParents(), cat);
        assertNotContains(cat.getParents(), kid1);
        assertNotContains(cat.getParents(), kid2);
        assertNotContains(cat.getParents(), kid3);
        assertNotContains(cat.getParents(), bob);

        assertSize(kid1.getParents(), 1);
        assertNotContains(kid1.getParents(), mom);
        assertNotContains(kid1.getParents(), dad);
        assertNotContains(kid1.getParents(), brother);
        assertContains(kid1.getParents(), cat);
        assertNotContains(kid1.getParents(), kid1);
        assertNotContains(kid1.getParents(), kid2);
        assertNotContains(kid1.getParents(), kid3);
        assertNotContains(kid1.getParents(), bob);
    }

    public void testGetDefaultParents()  {
        assertSize(cat.getDefaultAscendants(), 2);
        assertContains(cat.getDefaultAscendants(), mom);
        assertNotContains(cat.getDefaultAscendants(), dad);
        assertNotContains(cat.getDefaultAscendants(), brother);
        assertContains(cat.getDefaultAscendants(), cat);
        assertNotContains(cat.getDefaultAscendants(), kid1);
        assertNotContains(cat.getDefaultAscendants(), kid2);
        assertNotContains(cat.getDefaultAscendants(), kid3);
        assertNotContains(cat.getDefaultAscendants(), bob);

        assertSize(kid1.getDefaultAscendants(), 3);
        assertContains(kid1.getDefaultAscendants(), mom);
        assertNotContains(kid1.getDefaultAscendants(), dad);
        assertNotContains(kid1.getDefaultAscendants(), brother);
        assertContains(kid1.getDefaultAscendants(), cat);
        assertContains(kid1.getDefaultAscendants(), kid1);
        assertNotContains(kid1.getDefaultAscendants(), kid2);
        assertNotContains(kid1.getDefaultAscendants(), kid3);
        assertNotContains(kid1.getDefaultAscendants(), bob);

        assertSize(bob.getDefaultAscendants(), 1);
        assertNotContains(bob.getDefaultAscendants(), mom);
        assertNotContains(bob.getDefaultAscendants(), dad);
        assertNotContains(bob.getDefaultAscendants(), brother);
        assertNotContains(bob.getDefaultAscendants(), cat);
        assertNotContains(bob.getDefaultAscendants(), kid1);
        assertContains(bob.getDefaultAscendants(), bob);

        kid1.setDefaultParentCategory(null); // clear the default parent for kid
        assertSize(kid1.getDefaultAscendants(), 1);
        assertNotContains(kid1.getDefaultAscendants(), mom);
        assertNotContains(kid1.getDefaultAscendants(), dad);
        assertNotContains(kid1.getDefaultAscendants(), brother);
        assertNotContains(kid1.getDefaultAscendants(), cat);
        assertContains(kid1.getDefaultAscendants(), kid1);
        assertNotContains(kid1.getDefaultAscendants(), bob);
    }

    public void testGetObjectsInSubtree() {
        assertSize(cat.getDescendantObjects(), 6);
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc1));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc2));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc3));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gk1));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gk2));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gk3));

        assertSize(kid1.getDescendantObjects(), 3);
        assertNotContains(kid1.getDescendantObjects(), new CategorizedObject(gc1));
        assertNotContains(kid1.getDescendantObjects(), new CategorizedObject(gc2));
        assertNotContains(kid1.getDescendantObjects(), new CategorizedObject(gc3));
        assertContains(kid1.getDescendantObjects(), new CategorizedObject(gk1));
        assertContains(kid1.getDescendantObjects(), new CategorizedObject(gk2));
        assertContains(kid1.getDescendantObjects(), new CategorizedObject(gk3));

        assertSize(bob.getDescendantObjects(), 2);
        assertNotContains(bob.getDescendantObjects(), new CategorizedObject(gc1));
        assertNotContains(bob.getDescendantObjects(), new CategorizedObject(gc2));
        assertNotContains(bob.getDescendantObjects(), new CategorizedObject(gc3));
        assertContains(bob.getDescendantObjects(), new CategorizedObject(gb1));
        assertContains(bob.getDescendantObjects(), new CategorizedObject(gb2));

        kid1.removeChild(gk1);
        kid1.removeChild(gk2);
        kid1.removeChild(gk3);

        assertSize(cat.getDescendantObjects(), 3);
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc1));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc2));
        assertContains(cat.getDescendantObjects(), new CategorizedObject(gc3));
        assertNotContains(cat.getDescendantObjects(), new CategorizedObject(gk1));
        assertNotContains(cat.getDescendantObjects(), new CategorizedObject(gk2));
        assertNotContains(cat.getDescendantObjects(), new CategorizedObject(gk3));
    }

    // public void testCategoryOrder();

    // public void testObjectOrder();
    // TODO: write a replacement of this old test:
    // public void testSubtreeFilter();

    /**
     * Objects in subtree query is presently known to fail.
     * This is documented in the Category class.
     *
     **/
    // public void FAILStestObjectOrderSubtree();
}
