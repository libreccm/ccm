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
package com.arsdigita.notification;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.User;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test suite for digests. Only the constructors and set/get methods are tested here,
 * not the runtime behavior. Saved test data is not made persistent, transactions are
 * rolled back at the end of the tests.
 *
 * @version $Id: DigestTest.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class DigestTest extends BaseTestCase {

    static final String subject   = "This is the subject";
    static final String header    = "My Digest";
    static final String signature = "End of My Digest";
    static User from;

    public DigestTest (String name) {
        super(name);
    }

    /**
     * Test the default constructor, and verify that you cannot save a
     * Digest without setting any properties.
     */

    public void testDigestCreate001() {
        Digest d;
        try {
            d = new Digest();
            d.save();
            fail("Error: saving default constructor should fail.");
        } catch (Exception e) {
            // good
        }
    }

    /**
     * Test other constructors, saving and retrieving a digest
     */

    public void testDigestCreate002() {
        from = NotificationSuite.getUser();
        try {
            Digest d1 = new Digest(from,subject,header,signature);
            d1.save();

            Digest d2 = new Digest(d1.getOID());

            assertEquals(d1.getSubject(),   d2.getSubject());
            assertEquals(d1.getHeader(),    d2.getHeader());
            assertEquals(d1.getSignature(), d2.getSignature());
            assertEquals(d1.getFrequency(), d2.getFrequency());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Verify that you cannot save a digest with an invalid "from".
     */

    public void testDigestCreate003() {

        // try explicitly setting to null
        try {
            Digest d = new Digest(null,subject,header,signature);
            d.save();

            fail("saved digest with null from");
        } catch (NullPointerException ex) {
            // good!
        } catch (IllegalArgumentException ex) {
            // good!
        } catch(IllegalStateException st) {

        }

        // try using the default constructor and setting other properties
        try {
            Digest d = new Digest();
            d.setSubject(subject);
            d.setHeader(header);
            d.setSignature(signature);
            d.save();

            fail("saved digest with null from");
        } catch (IllegalStateException ex) {
            // good!
        }

        // try using a group with no primary email
        try {
            Group g = new Group();
            g.setName("test");
            g.save();

            Digest d = new Digest(g,subject,header,signature);
            d.save();

            fail("saved digest using a party with no primary email");
        } catch (IllegalStateException ex) {
            // good!
        }
    }

    /**
     * Test persistance behavior with a set/get method
     */
    public void testDigestSet001() {
        from = NotificationSuite.getUser();
        Digest d1 = new Digest(from,subject,header,signature);

        // test Separator
        d1.setSeparator('*', 5);
        try {
            String sep = d1.getSeparator();
            if(!sep.equals("*****"))
                fail("Separator not saved yet");
        } catch(Exception e) {
            fail(e.getMessage());
        }

        d1.save();

        Digest d2 = null;
        try {
            d2 = new Digest(d1.getOID());
        } catch(DataObjectNotFoundException nfe) {
            fail(nfe.getMessage());
        }

        assertEquals(d1.getSeparator(), d2.getSeparator());
    }


    /**
     * Test other setXXX methods and persistence of it
     */
    public void testDigestSet002() {

        from = NotificationSuite.getUser();
        Digest d1 = new Digest(from,subject,header,signature);

        // overwrite settings with following values
        int frequency2 = -10;
        User from2 = NotificationSuite.getUser();
        String header2 = "New Header";
        String signature2 = "New Signature";
        String subject2 = "New Subject";

        // change more things at once and test
        try {
            d1.setFrequency(frequency2);
            d1.setFrom(from2);
            d1.setHeader(header2);
            d1.setSignature(signature2);
            d1.setSubject(subject2);
        } catch (Exception e) {
            fail("Error: could not apply setXXX methods on Digest");
        }

        d1.save();

        Digest d2 = null;
        try {
            d2 = new Digest(d1.getOID());
        } catch(DataObjectNotFoundException nfe) {
            fail(nfe.getMessage());
        }

        assertEquals(subject2, d2.getSubject());
        assertEquals(header2, d2.getHeader());
        assertEquals(signature2, d2.getSignature());
        assertEquals(frequency2, d2.getFrequency().intValue());

        User from2c = null;
        try {
            from2c =  (User)d2.getFrom();
        } catch (DataObjectNotFoundException nfe) {
            fail(nfe.getMessage());
        }
        assertEquals(from2, from2c);
    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(DigestTest.class);
    }
}
