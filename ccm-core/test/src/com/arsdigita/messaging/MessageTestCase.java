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

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Base test case for Messaging.  Provides some common utilities and
 * setUp() / tearDown() methods for Messaging tests.
 *
 * @version $Id: MessageTestCase.java 1940 2009-05-29 07:15:05Z terry $
 */

public class MessageTestCase extends BaseTestCase {


    /**
     * All tests need a Party to represent the Message sender.
     */

    protected Party from;

    public MessageTestCase (String name) {
        super(name);
    }

    protected void setUp() {

        try {
            super.setUp();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        from = getRandomUser();
    }

    /**
     * Private method to generate a random user for testing
     */

    private User getRandomUser() {

        String key   = String.valueOf(System.currentTimeMillis());
        String email = key + "-message-test@arsdigita.com";
        String first = key + "-message-test-given-name";
        String last  = key + "-message-test-family-name";

        User user = new User();
        user.setPrimaryEmail(new EmailAddress(email));

        PersonName name = user.getPersonName();
        name.setGivenName(first);
        name.setFamilyName(last);

        user.save();

        return user;
    }


    /**
     * A utility for debugging.
     */

    public void printMessage (Message msg) {
        try {
            System.out.println("");
            System.out.println("From: ");
            System.out.println("Subject: " + msg.getSubject());
            System.out.println("Sent-Date: " + msg.getSentDate());
            System.out.println("Message-Type: " + msg.getBodyType());
            System.out.println("Message-ID: " + msg.getID());
            System.out.println(msg.getBody());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * A single trivial test case.
     */

    public void testMessageCreate001() {
        Message msg = new Message();
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(MessageTestCase.class);
    }
}
