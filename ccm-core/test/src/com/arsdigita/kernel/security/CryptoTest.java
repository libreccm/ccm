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

public class CryptoTest extends TestCase {

    public CryptoTest(String name) {
        super(name);
    }
    public static Test suite() {
        try {
            return new TestSuite(CryptoTest.class);
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
    public void testNewMac() {
        try {
            Mac mac = Crypto.newMac();
            byte[] input = new byte[128];
            (new SecureRandom()).nextBytes(input);
            byte[] digest = mac.doFinal(input);
        } catch (GeneralSecurityException e) {
            fail("Could not calculate MAC: "+e);
        }
    }
    public void testEncryptCycle() {
        try {
            String cleartext = "this is the clear text";
            char[] password = "Password123".toCharArray();
            String ciphertext = Crypto.encrypt(cleartext, password);
            String decrypted = Crypto.decrypt(ciphertext, password);
            assertEquals("after decryption", cleartext, decrypted);
        } catch (GeneralSecurityException e) {
            fail("Could not encrypt or decrypt: "+e);
        } catch (IllegalArgumentException e) {
            fail("Could not parse ciphertext: "+e);
        }
    }
}
