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
package com.arsdigita.kernel.security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CredentialTest extends TestCase {

    private static SecureRandom s_random = new SecureRandom();

    public CredentialTest(String name) {
        super(name);
    }
    public static Test suite() {
        try {
            return new TestSuite(CredentialTest.class);
        } catch (final Throwable t) {
            // handles NoClassDefFoundError
            // and ExceptionInInitializerError
            return new TestCase("Create CredentialTest") {
                    public void runTest() throws Throwable {
                        throw t;
                    }
                };
        }
    }
    public void testCycle() {
        // create -> toString -> parse -> toString -> compare
        Credential c1 = null, c2 = null;
        try {
            c1 = Credential.create
                ("12345678901234567890", 1000*60);
        } catch (CredentialException e) {
            fail("Credential.create: "+e);
        }
        String s1 = c1.toString();
        try {
            c2 = Credential.parse(s1);
        } catch (CredentialException e) {
            fail("Credential.parse: "+e);
        }
        String s2 = c2.toString();
        // strings equal?
        assertEquals(s1, s2);
    }

    public void testExpired() {
        Credential c1 = null;
        try {
            c1 = Credential.create
                ("12345678901234567890", -1000*60);
            fail("Expected CredentialEncodingException");
        } catch (CredentialEncodingException e) {
            //success
        }
    }

    public void testInvalidValue() {
        Credential c1 = null;
        try {
            c1 = Credential.create
                ("12345678901234567890"+Credential.SEPARATOR,
                 1000*60);
            fail("Expected CredentialEncodingException");
        } catch (CredentialEncodingException e) {
            // success
        } catch (CredentialException e) {
            fail("Expected CredentialEncodingException, but got: "+e);
        }
    }

    public void testInvalidKey() throws GeneralSecurityException {
        byte[] key1 = Store.newKey();
        byte[] key2 = new byte[key1.length];
        System.arraycopy(key1, 0, key2, 0, key1.length);
        key2[0] = (byte) ~key2[0];

        Credential c1 = Credential.create
            ("12345678901234567890", 1000*60, Crypto.newMac(key1));
        String s = c1.toString();

        // use the wrong key
        Mac mac = Crypto.newMac(key2);

        try {
            Credential.parse(s, mac);
            fail("Expected CredentialParsingException");
        } catch (CredentialParsingException e) {
            ; // success
        } catch (CredentialException e) {
            fail("Expected CredentialParsingException, but got: "+e);
        }
    }

    public void testInvalidString() {
        Credential c1 = null, c2 = null;
        try {
            c1 = Credential.create
                ("12345678901234567890", 1000*60);
        } catch (CredentialException e) {
            fail("Credential.create: "+e);
        }
        String s1 = c1.toString();
        // use the wrong string
        String s2 = "invalid" + s1;
        try {
            c2 = Credential.parse(s2);
            fail("Expected CredentialParsingException");
        } catch (CredentialParsingException e) {
            // success
        } catch (CredentialException e) {
            fail("Expected CredentialParsingException, but got: "+e);
        }
    }
}
