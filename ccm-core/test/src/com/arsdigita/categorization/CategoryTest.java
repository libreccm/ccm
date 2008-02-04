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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.AssertionError;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * This performs some smoke tests on the Category class. Specifically, it makes
 * sure that every method works as advertised and that the system talks to the
 * database correctly.
 *
 *
 * @author Randy Graebner
 * @version 1.0
 */
public class CategoryTest extends CategoryTestCase {

    private final static Logger s_log = Logger.getLogger(CategoryTest.class);

    Category category1;
    Category category2;

    /**
     * Constructs a CategoryTest with the specified name.
     *
     * @param name Test case name.
     **/
    public CategoryTest( String name ) {
        super( name );
    }

    public void setUp() {
        category1 = new Category();
        category2 = new Category();
    }

    /*
      TODO - we still need to incorporate the following 4 methods in to
      the testCategoryConstructors
      public Category(DataObject categoryObjectData, CategoryFilter cf)
      public Category(ObjectType type)
      public Category(OID oid, CategoryFilter cf)
      public Category(OID categoryID, String name, String description)
      public Category(OID categoryID, String name, String description, String url)
    */

    /**
     *  Tests all of the contstructors to make sure that none of them
     *  fail except for the Category(ObjectType) and Category(OID)
     *  because they are tested elsewhere
     */
    public void testCategoryConstructors() {
        String name = "this is the test name";
        String description = "this is the test description";
        String url = "this-is-the-test-url";

        // test the empty Category constructor
        final Category tc1 = new Category();
        OID testID = tc1.getOID();

        assertNotNull("testID", testID);
        tc1.delete();

        // test the default type constructor
        final Category tc2 = new Category(Category.BASE_DATA_OBJECT_TYPE);
        tc2.delete();

        // now we test the passing in name and description
        final Category tc3 = new Category(name, description);

        final Category ntc3 = new Category(tc3.getOID());
        assertEquals("ntc3's name", name, ntc3.getName());
        assertEquals("ntc3's description", description, ntc3.getDescription());
        ntc3.delete();

        // now we test the passing in name, description, and URL
        Category tc4 = new Category(name, description, url);

        final Category ntc4 = new Category(tc4.getOID());
        assertEquals("ntc4's name", name, ntc4.getName());
        assertEquals("ntc4's name", description, ntc4.getDescription());
        assertEquals("ntc4's url", url,ntc4.getURL());
        ntc4.delete();

        // now we test with passing in the ID, name, and description
        Category tc5 = new Category(name, description);
        testID = tc5.getOID();

        tc5 = null;

        // we clear tc5 and recreate to make sure that it stores the name and
        // description
        Category tc6 = new Category(testID);
        assertEquals("tc6's name", name, tc6.getName());
        assertEquals("tc6's description", description, tc6.getDescription());
        // test that the retrieved category can be saved
        tc6.save();
        tc6.delete();
    }


    /**
     *  This tests the setEnabled and isEnabled
     */
    public void testIsEnabled() {
        OID categoryID = category1.getOID();
        category1.setEnabled(true);
        category1 = null;

        category1 = new Category(categoryID);

        assertTrue("category1 is enabled", category1.isEnabled());

        category1.setEnabled(false);
        category1.save();
        category1 = null;
        category1 = new Category(categoryID);
        assertTrue("category1 is not enabled", !category1.isEnabled());

    }


    public void testAddMapping() {
        category2.addChild(category1);

        // make sure that you can retrieve a mapping that has already been
        // added
        assertContains(category2.getChildren(), category1);

        // make sure it will not allow you to add a loop
        try {
            category1.addChild(category2);
            fail("Adding a loop should throw an exception but it did not");
        } catch (CategorizationException e) {
            ;
        }

        // make sure it does not allow you to add it to itself
        try {
            category1.addChild(category1);
            fail("Adding a category to itself should throw an exception but " +
                 "it did not");
        } catch (CategorizationException e) {
        }

        // test adding a mapping to an object
        Group g = new Group();
        g.setName("test group");
        category2.addChild(g);

        CategorizedObject co = new CategorizedObject(g);
        assertContains(category2.getObjects(Group.BASE_DATA_OBJECT_TYPE), co);

        assertNotContains(category2.getChildren(), co);

        // Testing a problem Stas had: TODO: Duplicate if possible
        // http://developer.arsdigita.com/acs5/sdm/one-ticket?ticket_id=146137
        BigDecimal id = category1.getID();
        Category cat = new Category(new OID(Category.BASE_DATA_OBJECT_TYPE, id));
        cat.addChild(g);

        assertSize(cat.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 1);
        assertContains(cat.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), new CategorizedObject(g));
        // make sure it loads correctly from the DB...
        Category catb = new Category(new OID(Category.BASE_DATA_OBJECT_TYPE, id));
        assertSize(catb.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), 1);
        assertContains(catb.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE), new CategorizedObject(g));

        cat.removeChild(g);
    }


    /**
     *  Tests the different scenarios for deleting cateogries
     *  we are only testing deleteCategory(OID oid) and not
     *  deleteCategory(Category category) because the first
     *  one relies heavily on the second
     */
    public void testDelete() {
        // we create a Category and save it
        Category category = new Category("delete me", "description");
        OID testID = category.getOID();

        // now we add a child to it
        Category childCategory = new Category("delete me's child", "description2");
        category.addChild(childCategory);
        childCategory.setDefaultParentCategory(category);

        // trying to delete the category should throw a Categorization exception
        try {
            category.delete();
            fail("The category.delete() call did not throw an exception");
        } catch (CategorizationException e) {
            ;
        }

        // now we delete the child
        childCategory.delete();

        // and we finally delete the category
        try {
            category.delete();
        } catch (CategorizationException e) {
            fail("SDM #142388: " +
                 "The category.delete() call threw a Categorization " +
                 "Exception when it should not have : " + e.getMessage());
        }

        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
        }


        // now, we do the same thing with an object instead of with
        // a category as the child

        // we create a Category and save it
        category = new Category("delete me two", "description");
        testID = category.getOID();

        // now we add a child to it
        Group g = new Group();
        g.setName("test group");
        category.addChild(g);

        // trying to delete the category should throw a Categorization exception
        try {
            category.delete();
            fail("The category.delete() with user call did not throw an exception");
        } catch (CategorizationException e) {
        }

        // now we delete the child mapping
        g.delete();

        // and we finally delete the category
        category.delete();

        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
        }
    }

    public void testDeleteCategoryAndOrphan() {
        // we create a Category and save it
        Category category = new Category();
        OID testID = category.getOID();

        //it should work just fine for a category with no children
        category.deleteCategoryAndOrphan();

        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception:" + testID);
        } catch (DataObjectNotFoundException e) {
            ;
        }


        // now we try it with category children
        category = new Category();
        testID = category.getOID();

        Category child1 = new Category();
        category.addChild(child1);
        category.save();
        Category child2 = new Category();
        category.addChild(child2);
        Category child3 = new Category();
        category.addChild(child3);

        // now we delete
        category.deleteCategoryAndOrphan();
        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
            ;
        }

        // and now make sure that child1, child2, and child3 are still around
        category = new Category(child1.getOID());
        category = new Category(child2.getOID());
        category = new Category(child3.getOID());
        // it made it this far so it worked

        // now try the same thing with only objects
        category = new Category();
        testID = category.getOID();

        Group groupChild1 = new Group();
        groupChild1.setName("a");
        category.addChild(groupChild1);
        Group groupChild2 = new Group();
        groupChild2.setName("a");
        category.addChild(groupChild2);
        Group groupChild3 = new Group();
        groupChild3.setName("a");
        category.addChild(groupChild3);

        // now we delete
        category.deleteCategoryAndOrphan();

        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
            ;
        }

        Group group = null;

        // and now make sure that groupChild1, groupChild2,
        // and groupChild3 are still around
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild1.getID()));
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild2.getID()));
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild3.getID()));
        // it made it this far so it worked

        // now we try the same thing with both categories and
        // groups
        category = new Category();
        testID = category.getOID();

        category.addChild(child1);
        category.addChild(child2);
        category.addChild(child3);
        category.addChild(groupChild1);
        category.addChild(groupChild2);
        category.addChild(groupChild3);

        // now we delete
        category.deleteCategoryAndOrphan();

        // and make sure that it is no longer there
        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
            ;
        }

        // and now make sure that all six children are around
        category = new Category(child1.getOID());
        category = new Category(child2.getOID());
        category = new Category(child3.getOID());
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild1.getID()));
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild2.getID()));
        group = new Group(new OID(Group.BASE_DATA_OBJECT_TYPE,
                                  groupChild3.getID()));
        // it made it this far so it worked

        child1.delete();
        child2.delete();
        child3.delete();
        groupChild1.delete();
        groupChild2.delete();
        groupChild3.delete();
    }

    public void testGetMapping() {
        Category category1 = new Category();
        Category category2 = new Category();

        Group group1 = new Group();
        Group group2 = new Group();
        group1.setName("group1");
        group2.setName("group2");

        category1.addChild(category2);
        category1.addChild(group1);
        category1.addChild(group2);
        category2.addChild(group2);

        assertContains(category1.getChildren(), category2);

        assertContains(category1.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group1));

        assertContains(category1.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group2));

        assertContains(category2.getObjects(Group.BASE_DATA_OBJECT_TYPE),
                       new CategorizedObject(group2));

        group1.delete();
        group2.delete();
        category1.delete();
        category2.delete();
    }

    public void testDeleteCategorySubtree() {
        Category category1 = new Category();
        category1.setName("category1");
        Category category2 = new Category();
        category2.setName("category2");
        Category category3 = new Category();
        category3.setName("category3");
        Category category4 = new Category();
        category4.setName("category4");
        Category category5 = new Category();
        category5.setName("category5");

        category1.addChild(category2);
        category3.addChild(category2);
        category4.addChild(category2);
        category1.addChild(category3);
        category4.addChild(category3);
        category5.addChild(category4);
        category5.addChild(category3);
        category3.setDefaultParentCategory(category1);
        category4.setDefaultParentCategory(category5);

        OID category2ID = category2.getOID();
        OID category3ID = category3.getOID();
        OID category4ID = category4.getOID();
        OID category5ID = category5.getOID();

        // let's first delete category 5 which should also delete category 4
        // but it should not delete category 3
        category5.deleteCategorySubtree();

        long num = new Category(category3ID).getParentCategoryCount();
        assertEquals("number of parents for Category3", 1, num);

        // let's make sure that neither 5 nor 4 exist any more
        try {
            new Category(category4ID);
            fail("should have thrown an exception: " + category4ID);
        } catch (DataObjectNotFoundException e) {
        }

        try {
            new Category(category5ID);
            fail("should have thrown an exception: " + category5ID);
        } catch (DataObjectNotFoundException e) {
        }

        // deleting category1 should not delete category 2
        category1.deleteCategorySubtree();

        // let's make sure that category2 is still around
        new Category(category2ID);
        category2.delete();
    }

    /**
     *  We want to now test the ability to set and get category
     *  properties.  This tests the following methods:
     *  public String getName()
     *  public void setName(String value)
     *  public String getDescription()
     *  public void setDescription(String value)
     *  public String getURL()
     *  public void setURL(String value)
     *  public OID getOID()
     */
    public void testSetGetProperties() {

        // create a new category
        Category testCategory = new Category();
        String name = "This is the category Name";
        String description = "This is the category Description";
        String url = "this-is-the-category-url";

        testCategory.setName(name);
        testCategory.setDescription(description);
        testCategory.setURL(url);
        OID testID = testCategory.getOID();

        Category dupeTestCategory = new Category(testID);
        assertEquals("dupeTestCagegory's name", name, dupeTestCategory.getName());
        assertEquals("dupeTestCategory's descrption", description, dupeTestCategory.getDescription());
        assertEquals("dupeTestCategory's url", url, dupeTestCategory.getURL());

        // lets test the equality stuff
        assertEquals("reflexive", dupeTestCategory, dupeTestCategory);
        assertEquals("dupeTestCategory and testCategory", dupeTestCategory, testCategory);
        Category test = new Category();
        assertTrue("The category.equals() worked when it should not have ",
                   !dupeTestCategory.equals(test));
        test.delete();
    }


    public void FAILStestGetChildCategories() {
        // This test is marked as FAILS because SDM #185774 is still open.

        // create four categories
        Category parent = new Category();
        Category categoryOne = new Category("one", "One");
        Category categoryTwo = new Category("two", "two");
        Category categoryThree = new Category("three", "three");
        Category categoryFour = new Category("four", "four");

        parent.addChild(categoryOne);
        parent.addChild(categoryTwo);
        parent.addChild(categoryThree);
        parent.addChild(categoryFour);

        // We'll try doing this several times, due to SDM #138526.
        for (int i = 1; i <= 5; i++) {
            CategoryCollection childCategories = parent.getChildren();
            int nChildren = (int) childCategories.size();

            if (i > 1) {
                assertEquals("Persistence Error: SDM #138526: ", 4, nChildren);
            } else {
                assertEquals("back (on loop " + i + ")", 4, nChildren);
            }

            boolean found = contains(childCategories, categoryThree);

            if (i > 1) {
                assertTrue("Persistence Error: SDM #138526: " +
                           "One of the categories added is not there (on loop " +
                           i + "); found children " + childCategories +
                           ", was looking for " + categoryThree, found);
            } else {
                assertTrue("One of the categories added is not there (on loop " +
                           i + "); found children " + childCategories +
                           ", was looking for " + categoryThree, found);
            }
        }

        /* Test for SDM #185774 - errors when saving retrieved child categories
         */
        final CategoryCollection children = parent.getChildren();
        while (children.next()) {
            ((Category) children.getDomainObject()).save();
        }
        children.close();

        categoryOne.delete();
        categoryThree.delete();
        categoryTwo.delete();
        categoryFour.delete();
        parent.delete();
    }

    public void testEquals() {
        Category c1 = new Category();
        Category c2 = new Category();

        assertEquals("reflexive", c1, c1);
        assertTrue("Category1 does not equals Category2", !c1.equals(c2));

        Category c1b = new Category(c1.getOID());
        Category c2b = new Category(c2.getOID());

        assertEquals("c1 equals c1b", c1, c1b);
        assertTrue("Category1 does not equals Category2b", !c1.equals(c2b));

        // These are really testing acsobject more than category
        Category c3 = new Category();
        Category c4 = new Category();
        assertEquals("reflexive equality", c3, c3);
        assertTrue("Uninitialized objects should not match", !c3.equals(c4));

        c1.delete();
        c2.delete();
    }

    public void testGetChildObjectAssociations() {

        // create four categories
        Category parent = new Category();
        Group groupOne = new Group();
        Group groupTwo = new Group();
        Group groupThree = new Group();
        Group groupFour = new Group();
        groupOne.setName("one");
        groupTwo.setName("two");
        groupThree.setName("three");
        groupFour.setName("four");

        parent.addChild(groupOne);
        parent.addChild(groupTwo);
        parent.addChild(groupThree);
        parent.addChild(groupFour);

        OID parentID = parent.getOID();

        // We'll try doing this several times, due to SDM #138526.
        for (int i = 1; i <= 5; i++) {
            final ACSObjectCollection childObjects = parent.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
            int nChildren = (int) childObjects.size();

            if (i > 1) {
                assertEquals("Persistence Error: SDM #138526", 4, nChildren);
            } else {
                assertEquals("4 children objects were added", 4, nChildren);
            }

            // for some reason, childObjects.contains() is not working
            // so I using an iterator
            boolean found = false;

            while (childObjects.next()) {
                Group object = (Group) childObjects.getDomainObject();
                found = object.getName().equals("three") || found;
            }

            if (i > 1) {
                assertTrue("Persistence Error: SDM #138526: " +
                           "One of the Groups added is not there (on loop " +
                           i + ")", found);
            } else {
                assertTrue("One of the Groups added is not there (on loop " +
                           i + ")", found);
            }
        }

        // clean up
        parent.deleteCategoryAndOrphan();
        groupOne.delete();
        groupTwo.delete();
        groupThree.delete();
        groupFour.delete();
    }


    /**
     *  Tests the different scenarios for deleting cateogries
     */
    public void testDeleteCategory() {
        // we create a Category and save it
        Category category = new Category();
        OID testID = category.getOID();

        // now we add a child to it
        Category childCategory = new Category();
        category.addChild(childCategory);
        childCategory.setDefaultParentCategory(category);

        // trying to delete the category should throw a Persistence exception
        // since delete does not allow deleting with children.
        try {
            category.delete();
            fail("should have thrown an exception");
        } catch (CategorizationException e) {
            ;
        }

        // now we delete the child
        childCategory.delete();

        // and we finally delete the category. It shouldn't fail
        // SDM #142388
        category.delete();

        try {
            category = new Category(testID);
            fail("should have thrown an exception: " + testID);
        } catch (DataObjectNotFoundException e) {
            ;
        }
    }

    /**
     * Test to make sure that categories can be created with empty descriptions.
     * See SDM feature #158259.
     */
    public void testCategoryNoDescription() {
        Category testCategory = new Category("my category", null);
        testCategory.save();
    }

    public void testRetrieveAllEvent() {
        Session session = SessionManager.getSession();
        DataCollection dc =
            session.retrieve("com.arsdigita.categorization.Category");

        long start = dc.size();



        new Category("cat", "test cat").save();
        new Category("cat", "test cat").save();
        new Category("cat", "test cat").save();
        new Category("cat", "test cat").save();
        Category cat = new Category("cat", "test cat");

        dc = session.retrieve("com.arsdigita.categorization.Category");

        long end = dc.size();

        assertEquals("end-start", 5, end-start);

        cat.delete();

        dc = session.retrieve("com.arsdigita.categorization.Category");

        end = dc.size();

        assertEquals("end-start", 4, end-start);
    }

    public void testIsAbstract() {
        OID categoryID = category1.getOID();
        category1.setAbstract(true);
        category1.save();

        assertTrue("isAbstract", new Category(categoryID).isAbstract());

        category1.setAbstract(false);
        assertTrue("is not abstract", !new Category(categoryID).isAbstract());

        // try to categorized something under an abstract category
        category1.setAbstract(true);

        // we should be able to add a Category
        category1.addChild(category2);

        // we should not be able to add an object
        Group group = new Group();
        group.setName("this group");

        try {
            category1.addChild(group);
            category1.save();
            fail("should not been able to add an object as a child ");
        } catch (AssertionError e) {
            ;
        }
    }

    public void testTokenize() {
        Category.TokenizedPath parts = new Category.TokenizedPath("//foo/bar//baz/quux");
        assertTrue("has next", parts.next());
        assertEquals("1st", "foo", parts.getToken());
        assertTrue("has next", parts.next());
        assertEquals("2nd", "bar", parts.getToken());
        assertTrue("has next", parts.next());
        assertEquals("3rd", "baz", parts.getToken());
        assertTrue("has next", parts.next());
        assertEquals("4th", "quux", parts.getToken());
        assertFalse("has next", parts.next());
    }

    public void testGetChildrenByURL() {
        final Category arts = new Category("Arts", null, "arts");
        final Category movies = new Category("Movies", null, "movies");
        arts.addChild(movies);
        Category[] cats = arts.getChildrenByURL("movies");
        assertNotNull("cats", cats);
        assertEquals("children, including self", 2, cats.length);
        assertEquals("arts", arts, cats[0]);
        assertEquals("movies", movies, cats[1]);

        Category[] empty = arts.getChildrenByURL("notfound");
        assertNull("empty", empty);
    }
}
