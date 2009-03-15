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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is a base JUnit utility class.
 * It provides various methods for asserting things about collections,
 * and should be extended for test fixtures that need
 * to work with collections.
 *
 * Note that this class does no setup or teardown beyond
 * what is done by the BaseTestCase.
 *
 * @author David Eison
 * @version 1.0
 * @see com.arsdigita.categorization
 */
public class CategoryTestCase extends BaseTestCase {

    public CategoryTestCase(String name) {
        super(name);
    }

    protected void baseTearDown() {
        super.baseTearDown();
    }

    public final void assertContains(String msg, Collection col, Object obj) {
        assertTrue(msg, col.contains(obj));
    }

    public final void assertContains(Collection col, Object obj) {
        assertContains("Collection should have contained " + obj +
                       " but did not: " + col.toString(), col, obj);
    }

    public final void assertNotContains(String msg, Collection col, Object obj) {
        assertTrue(msg, !col.contains(obj));
    }

    public final void assertNotContains(Collection col, Object obj) {
        assertNotContains("Collection should not have contained " + obj +
                          " but did: " + col.toString(), col, obj);
    }


    /**
     * Tests whether the specified domain object is part of the specified
     * collection.  Closes the collection before returning.
     **/
    public static final boolean contains(DomainCollection col, DomainObject obj) {
        boolean found = false;
        while ( col.next() ) {
            DomainObject current = col.getDomainObject();
            if ( obj.equals(current) ) {
                found = true;
                break;
            }
        }
        col.close();
        return found;
    }

    /**
     * Asserts that the specified domain object is part of the specified
     * collection.  Closes the collection before returning.
     **/
    public final void assertContains(DomainCollection col, DomainObject obj) {
        assertTrue(obj.getOID() + " found", contains(col, obj));
    }

    /**
     * Asserts that the specified domain object is not part of the specified
     * collection.  Closes the collection before returning.
     **/
    public final void assertNotContains(DomainCollection col, DomainObject obj) {
        assertTrue(obj.getOID() + " not found", !contains(col, obj));
    }


    /**
     * Asserts that the specified domain object is part of the specified
     * collection.  Closes the collection before returning.
     **/
    public final void assertContains(DomainCollection col, CategorizedObject obj) {
        assertContains(col, obj.getObject());
    }

    /**
     * Asserts that the specified domain object is not part of the specified
     * collection.  Closes the collection before returning.
     **/
    public final void assertNotContains(DomainCollection col, CategorizedObject obj) {
        assertNotContains(col, obj.getObject());
    }

    /**
     * Asserts that the given collection contains the specified number
     * of objects.
     */
    public final void assertSize(Collection col, int num) {
        assertEquals("collection size", num, col.size());
    }

    /**
     * Asserts that the given domain collection contains the specified number of
     * objects.
     */
    public final void assertSize(DomainCollection col, int expectedSize) {
        assertEquals("Collection size", expectedSize, col.size());
    }


    private Object getValue(Object obj, String key) {
        Object currentValue = null;
        if (obj instanceof Category) {
            currentValue = DomainServiceInterfaceExposer
                .getDataObject((Category)obj).get(key);
        } else if (obj instanceof CategorizedObject) {
            CategorizedObject current = (CategorizedObject)obj;
            if ("sortKey".equals(key)) {
                // TODO: Add some way to get the sortkey value
                fail("Presently unable to determine value of sortKey " +
                     "field for a CategorizedObject.");
            } else if ("id".equals(key)) {
                currentValue = current.getObject().getID();
            } else if ("objectType".equals(key)) {
                currentValue = current.getObject().getSpecificObjectType();
            } else {
                fail("Unable to determine value of " + key +
                     " field for object of type " +
                     current.getClass().getName());
            }
        } else {
            fail("unable to assert order on object of type "
                 + obj.getClass().getName());
        }
        return currentValue;
    }

    /**
     * Asserts that the given collection is sorted by the key field.
     *
     * @param col A collection of Categories that should be
     *            sorted by the key field.
     * @param key Field that objects should have been sorted by.
     */
    public final void assertInOrder(Collection col, String key) {
        Object prevValue = null;
        Object currentValue = null;
        Iterator i = col.iterator();
        if (i.hasNext()) {
            prevValue = getValue(i.next(), key);
            boolean hitNull = (prevValue == null);
            while (i.hasNext()) {
                // it would be nicer to handle DomainObjects, but
                // "get" is protected and I don't think switching
                // to reflection is a good solution.  So,
                // we'll use categories and categorizedobjects.
                Object currentObject = i.next();
                currentValue = getValue(currentObject, key);

                boolean test;
                if (currentValue == null || hitNull) {
                    // null always comes at the end, so make sure the previous
                    // value wasn't null if this is the first null, and the
                    // previous value was null if this isn't the first null.
                    if (!hitNull) {
                        test = (prevValue != null);
                        hitNull = true;
                    } else {
                        test = (prevValue == null);
                    }
                } else if (currentValue instanceof BigInteger) {
                    test = ((BigInteger)currentValue)
                        .compareTo((BigInteger)prevValue) >= 0;
                } else if (currentValue instanceof Number) {
                    test = ((Number)currentValue).longValue() >=
                        ((Number)prevValue).longValue();
                } else if (currentValue instanceof String) {
                    test = ((String)currentValue).compareTo((String)prevValue) >= 0;
                } else if (currentValue instanceof Boolean) {
                    boolean cur = ((Boolean)currentValue).booleanValue();
                    boolean pre = ((Boolean)prevValue).booleanValue();
                    // make sure we don't ever go from true to false.
                    test = !(pre && !cur);
                } else {
                    throw new IllegalArgumentException(
                                                       "Unable to handle type "
                                                       + currentValue.getClass().getName());
                }
                assertTrue("Invalid sort order for field " + key +
                           ", element " + currentObject +
                           ", in collection " + col, test);
                prevValue = currentValue;
            }
        }
    }
}
