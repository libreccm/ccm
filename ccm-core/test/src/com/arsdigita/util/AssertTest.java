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
package com.arsdigita.util;

import junit.framework.TestCase;

/** 
 * @version $Id: AssertTest.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class AssertTest extends TestCase {

    public AssertTest(String name) {
        super(name);
    }

    public void testAssert() {
        Assert.setEnabled(false);

        junit.framework.Assert.assertTrue(!Assert.isEnabled());

        Assert.setEnabled(true);

        junit.framework.Assert.assertTrue(Assert.isEnabled());

        try {
            com.arsdigita.util.Assert.truth(false, "Expected true");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.falsity(true, "Expected false");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.exists(null, Object.class);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.locked(new Unlocked());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unlocked(new Locked());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(new Object(), new Object());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal("whoa", "dude");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(null, new Object());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(new Object(), null);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            final Object one = new Object();

            com.arsdigita.util.Assert.unequal(one, one);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unequal(null, null);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unequal("dude", "dude");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        // Tests for the deprecated assert methods

        try {
            com.arsdigita.util.Assert.assertTrue(false);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertTrue(false, "Is false!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null, "Is null!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null, "NullString");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("", "emptyString!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    private class Locked extends LockableImpl {
        Locked() {
            lock();
        }
    }

    private class Unlocked extends LockableImpl {
        // Empty
    }
}
