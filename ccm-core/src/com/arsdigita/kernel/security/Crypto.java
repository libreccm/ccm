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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * Provides cryptographic functions and stores the server's secret key.
 *
 * @author Sameer Ajmani
 * @since ACS 4.5
 * @version $Id: Crypto.java 738 2005-09-01 12:36:52Z sskracic $
 **/
public class Crypto {

    private static final Logger s_log =
        Logger.getLogger(Crypto.class);
    private static int PBE_SALT_BYTES = 8;    // required by SunJCE
    private static int PBE_ITERATIONS = 1024; // PKCS#12 recommended
    private static String PBE_ALGO = "PBEWithMD5AndDES";
    private static String RANDOM_ALGO = "SHA1PRNG";
    private static String HASH_ALGO = "MD5";
    private static String MAC_ALGO = "HmacMD5";
    private static SecureRandom s_random = null;
    private static String PREFERRED_MAC_ALGO = null;

    public static final String CHARACTER_ENCODING = "UTF-8";

    static {
        s_log.debug("Static initalizer starting...");
        Security.addProvider(new BouncyCastleProvider());
        s_log.debug("Static initalizer finished");
    }

    /**
     * Creates a new Message Authentication Code (MAC) calculator that uses
     * the server's secret key.
     *
     * @return a new <code>Mac</code> object.
     *
     * @throws GeneralSecurityException if an error occurs.
     **/
    public static Mac newMac() throws GeneralSecurityException {
        return newMac(Store.INSTANCE.loadSecret());
    }

    // intentionally package-scoped to enable whitebox testing
    static Mac newMac(byte[] secret) throws GeneralSecurityException {
        if ( secret == null ) { throw new NullPointerException("secret"); }

        Mac mac = null;

        if ( PREFERRED_MAC_ALGO != null ) {
            mac = Mac.getInstance(PREFERRED_MAC_ALGO);
            mac.init(new SecretKeySpec(secret, "RAW"));
            return mac;
        }

        try {
            mac = Mac.getInstance(MAC_ALGO);
            PREFERRED_MAC_ALGO = MAC_ALGO;
        } catch (NoSuchAlgorithmException ex) {
            PREFERRED_MAC_ALGO = getMAC();
            s_log.info("Default " + MAC_ALGO + " not available, falling back to " +
                       PREFERRED_MAC_ALGO);
        }

        if ( mac == null ) {
            try {
                mac = Mac.getInstance(PREFERRED_MAC_ALGO);
            } catch (NoSuchAlgorithmException ex) {
                String msg =
                    "Couldn't find " + PREFERRED_MAC_ALGO + ". Make sure you have the right" +
                    "provider(s) installed. Check $JAVA_HOME/jre/lib/security/java.security";
                s_log.error(msg, ex);
                throw new KernelLoginException(msg, ex);
            }
        }

        s_log.info(mac.getAlgorithm() + " selected for MAC algorithm.");
        mac.init(new SecretKeySpec(secret, "RAW"));
        return mac;
    }

    /**
     * Creates a new message digest (hash) calculator.
     *
     * @return a new <code>MessageDigest</code> object.
     *
     * @throws GeneralSecurityException if an error occurs.
     **/
    public static MessageDigest newDigester()
        throws GeneralSecurityException {
        return MessageDigest.getInstance(HASH_ALGO);
    }


    /**
     * Returns a secure random number generator.
     *
     * @return a <code>SecureRandom</code> number generator.
     *
     * @throws GeneralSecurityException if an error occurs.
     **/
    public static SecureRandom getRandom()
        throws GeneralSecurityException {

        if (s_random == null) {
            try {
                // First preference is the SHA1PRNG
                s_random = SecureRandom.getInstance("SHA1PRNG");
            } catch ( NoSuchAlgorithmException e ) {
                s_random = SecureRandom.getInstance(getPRNG());
            }
        }
        return s_random;
    }

    private static SecretKey newKey(char[] password)
        throws GeneralSecurityException {
        return SecretKeyFactory
            .getInstance(PBE_ALGO)
            .generateSecret(new PBEKeySpec(password));
    }

    private static AlgorithmParameterSpec newParams(byte[] salt)
        throws GeneralSecurityException {
        return new PBEParameterSpec(salt, PBE_ITERATIONS);
    }

    private static Cipher newCipher(int mode, char[] password, byte[] salt)
        throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(PBE_ALGO);
        cipher.init(mode, newKey(password), newParams(salt));
        return cipher;
    }

    private static byte[] newSalt()
        throws GeneralSecurityException {
        byte[] salt = new byte[PBE_SALT_BYTES];
        getRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Encrypts a string with a key derived from a password.  Output is
     * <code>BASE64Encode(salt) + ":" +
     * BASE64Encode(encrypt(UTF8Encode(cleartext.length() + ":" +
     * cleartext)))</code>.
     *
     * @param decrypted the string to encrypt
     *
     * @param password the password from which to create an encryption key
     *
     * @return the encrypted string.
     *
     * @throws GeneralSecurityException if encryption fails.
     **/
    public static String encrypt(String decrypted, char[] password)
        throws GeneralSecurityException {
        decrypted = decrypted.length() + ":" + decrypted;
        byte[] salt, bytes;
        try {
            salt = newSalt();
            bytes = newCipher(Cipher.ENCRYPT_MODE, password, salt)
                .doFinal(decrypted.getBytes(CHARACTER_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedWrapperException(e);
        }
        Base64 encoder = new Base64();
        String encrypted = new String(encoder.encode(salt))
            + ':' + new String(encoder.encode(bytes));
        return encrypted;
    }

    /**
     * Decrypts a string with a key derived from a password.
     *
     * @param encrypted the string to decrypt
     *
     * @param password the password from which to create an decryption key
     *
     * @return the decrypted string
     *
     * @throws IllegalArgumentException if the given string is not legal
     * output of encrypt().
     *
     * @throws InvalidKeyException if the decrypted text does not pass
     * validation.
     *
     * @throws GeneralSecurityException if decryption fails otherwise.
     **/
    public static String decrypt(String encrypted, char[] password)
        throws GeneralSecurityException {
        int colon = encrypted.indexOf(':');
        if (colon < 0) {
            throw new IllegalArgumentException
                ("Expected salt:ciphertext (no colon)");
        }
        Base64 decoder = new Base64();
        byte[] salt, bytes;
        try {
            salt = decoder.decode(encrypted.substring(0, colon).
                                  getBytes(CHARACTER_ENCODING));
            bytes = decoder.decode(encrypted.substring(colon+1).
                                   getBytes(CHARACTER_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedWrapperException(e);
        }
        String decrypted;
        try {
            decrypted = new String
                (newCipher(Cipher.DECRYPT_MODE, password, salt)
                 .doFinal(bytes), CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedWrapperException(e);
        }
        colon = decrypted.indexOf(':');
        if (colon < 0) {
            throw new InvalidKeyException
                ("Expected length:cleartext (no colon)");
        }
        int length;
        try {
            length = Integer.parseInt(decrypted.substring(0, colon));
        } catch (NumberFormatException e) {
            throw new InvalidKeyException
                ("Expected length:cleartext (number format)");
        }
        decrypted = decrypted.substring(colon + 1);
        if (length != decrypted.length()) {
            throw new InvalidKeyException
                ("Expected length:cleartext (bad length)");
        }
        return decrypted;
    }

    /**
     * Benchmarks password-based encryption and decryption.
     **/
    public static void main(String[] args)
        throws GeneralSecurityException {
        String original = "this is the clear text";
        char[] password = "Password123".toCharArray();
        int samples = 10;
        for (int i = 1; i <= 8192; i = i*2) {
            PBE_ITERATIONS = i;
            String[] encrypted = new String[samples];
            String[] decrypted = new String[samples];
            // do encryptions
            long time = 0;
            for (int j = 0; j < samples; j++) {
                long start = System.currentTimeMillis();
                encrypted[j] = Crypto.encrypt(original+j, password);
                time += System.currentTimeMillis() - start;
            }
            s_log.warn("encrypt, "+PBE_ITERATIONS+" iters: "
                       +(time/samples)+" ms");
            // do decryptions
            time = 0;
            for (int j = 0; j < samples; j++) {
                long start = System.currentTimeMillis();
                decrypted[j] = Crypto.decrypt(encrypted[j], password);
                time += System.currentTimeMillis() - start;
            }
            s_log.warn("decrypt, "+PBE_ITERATIONS+" iters: "
                       +(time/samples)+" ms");
            // sanity check output
            for (int j = 0; j < samples; j++) {
                if (!decrypted[j].equals(original+j)) {
                    throw new IllegalStateException
                        ("bad decryption; original <"+original+j
                         +">, decrypted <"+decrypted[j]+">");
                }
            }
        }
    }

    /*
     * Query all available providers for first available PRNG.
     */
    private static String getPRNG() {
        // This should be rewritten to use the method call
        // Security.getAlgorithms("SecureRandom") when we switch to
        // using JDK 1.4 exclusively.
        String algorithm = null;

        Provider[] jceProviders = Security.getProviders();
        for (int i = 0 ; i < jceProviders.length ; i++) {
            Set e = jceProviders[i].entrySet();
            Iterator iterator = e.iterator();
            while ( iterator.hasNext() ) {
                String current = iterator.next().toString();
                if ( current.startsWith("SecureRandom") ) {
                    algorithm = current.substring(13,current.indexOf("="));
                    if ( algorithm.indexOf("ImplementedIn") == -1 ) {
                        return algorithm;
                    }
                }
            }
        }

        return algorithm;

    }

    /*
     * Query all available providers for appropriate MAC algorithm. If
     * no MAC providers are available, fallback to first available
     * message digest algorithm.
     */
    private static String getMAC() {
        String algorithm = null;
        String mdAlgorithm = null;
        String preferredAlgorithm = null;

        Provider[] jceProviders = Security.getProviders();
        for (int i = 0 ; i < jceProviders.length ; i++) {

            if (s_log.isDebugEnabled()) {
                s_log.info("Security provider: " + jceProviders[i].getName());
            }
            Set e = jceProviders[i].entrySet();
            Iterator iterator = e.iterator();
            while ( iterator.hasNext() ) {
                String current = iterator.next().toString();

                if (s_log.isDebugEnabled()) {
                    s_log.info("\t" + current);
                }
                if ( current.startsWith("Mac") ) {
                    algorithm = current.substring(4,current.indexOf("="));
                    if ( algorithm.indexOf("ImplementedIn") == -1 &&
                         algorithm != null ) {
                        return algorithm;
                    }
                }

                if ( current.startsWith("MessageDigest") ) {
                    mdAlgorithm = current.substring(14,current.indexOf("="));
                    if ( mdAlgorithm.indexOf("ImplementedIn") == -1 &&
                         mdAlgorithm != null ) {
                        preferredAlgorithm = mdAlgorithm;
                    }
                }

            }
        }

        return preferredAlgorithm;

    }
}
