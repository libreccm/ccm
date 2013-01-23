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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * A unit of data that contains a string value, an expiration date, and a
 * tamper-proof validator.  A Credential can be converted to and from a
 * string.  Credential objects may expire after construction.  Credential
 * objects are immutable: they cannot be changed after construction.
 *
 * @author Sameer Ajmani
 * @since ACS 4.5
 **/
public class Credential {

    /**
     * The character used to separate the value, expiration, and validator.
     **/
    public static final char SEPARATOR = '!';

    // Fields
    private String m_value;
    private long m_expiration;
    private byte[] m_validator;

    /**
     * Constructs credential from the given fields.  The factory methods
     * must protect this constructor from invalid and mutable parameters.
     **/
    private Credential(String value, long expiration, byte[] validator) {
        m_value = value;
        m_expiration = expiration;
        m_validator = validator;
    }

    /**
     * Returns the String representation of this credential.  Compatible
     * with <code>parse</code> method.
     *
     * @return the String representation of this credential.
     **/
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(m_value).append(SEPARATOR);
        buf.append(m_expiration).append(SEPARATOR);
        buf.append(URLEncoder.encode(new String(new Base64().encode(m_validator))));
        return buf.toString();
    }

    /**
     * Gets the value of this credential.
     *
     * @return the value of this credential.
     **/
    public String getValue() {
        return m_value;
    }

    /**
     * Gets the expiration date of this credential.
     *
     * @return the expiration date of this credential.
     **/
    public Date getExpiration()
    {
        return new Date(m_expiration);
        // NOTE: do not cache Date object (Date is mutable)
    }

    /**
     * Determines whether this credential has expired.
     *
     * @return <code>true</code> if this credential has expired,
     * <code>false</code> otherwise.
     **/
    public boolean hasExpired() {
        return m_expiration < System.currentTimeMillis();
    }

    /**
     * Constructs a new credential that expires after the given number of
     * milliseconds.
     *
     * @param value the value of the credential
     *
     * @param lifetimeMillis the lifetime of this credential in milliseconds
     *
     * @throws CredentialEncodingException if the value contains the
     * separator character, if the lifetime is negative, or if there is an
     * error creating the validator.
     **/
    public static Credential create(String value,
                                    long lifetimeMillis)
        throws CredentialEncodingException {

        final Mac mac;
        try {
            mac = Crypto.newMac();
        } catch (GeneralSecurityException ex) {
            throw new CredentialEncodingException
                ("Couldn't create a MAC", ex);
        }
        return Credential.create(value, lifetimeMillis, mac);
    }

    // intentionally package-scoped to make whitebox testing possible
    static Credential create(String value,
                             long lifetimeMillis,
                             Mac mac)
        throws CredentialEncodingException {

        if (value.indexOf(SEPARATOR) != -1) {
            throw new CredentialEncodingException
                ("value must not contain separator character ("
                 +SEPARATOR+"): "+value);
        }
        if (lifetimeMillis < 0) {
            throw new CredentialEncodingException
                ("lifetime must not be negative: "+lifetimeMillis);
        }

        try {
            long expiration = System.currentTimeMillis() + lifetimeMillis;
            return new Credential
                (value,
                 expiration,
                 createValidator(value, expiration, mac));
        } catch (ValidatorException ex) {
            throw new CredentialEncodingException(ex.getRootCause());
        }
    }

    /**
     * Constructs a new credential parsed from the given string.  Compatible
     * with toString() method.
     *
     * @return a new credential parsed from the given string.
     *
     * @throws CredentialParsingException if the string does not represent
     * a credential or if the credential is invalid.
     *
     * @throws CredentialExpiredException if the parsed credential has
     * expired.
     **/
    public static Credential parse(String credential)
        throws CredentialParsingException, CredentialExpiredException {

        final Mac mac;
        try {
            mac = Crypto.newMac();
        } catch (GeneralSecurityException ex) {
            throw new CredentialParsingException
                ("Couldn't create a MAC", ex);
        }
        return Credential.parse(credential, mac);
    }

    // intentionally package-scoped to make whitebox testing possible
    static Credential parse(String credential, Mac mac)
        throws CredentialParsingException, CredentialExpiredException {

        // split string into value, expiration, and validator
        StringTokenizer tok = new StringTokenizer(URLDecoder.decode(credential),
                                                  String.valueOf(SEPARATOR));
        if (tok.countTokens() != 3) {
            throw new CredentialParsingException("Bad format");
        }
        // read value
        String value = tok.nextToken();
        // read expiration
        long expiration;
        try {
            expiration = Long.parseLong(tok.nextToken());
        } catch (NumberFormatException e) {
            throw new CredentialParsingException("Bad expiration", e);
        }
        if (expiration < System.currentTimeMillis()) {
            throw new CredentialExpiredException
                (new Date(expiration).toString());
        }

        final byte[] validator;
        final byte[] calculated;
        try {
            validator = (new Base64()).decode(tok.nextToken()
                                      .getBytes(Crypto.CHARACTER_ENCODING));
            calculated = createValidator(value, expiration, mac);
        } catch (ValidatorException ex) {
            throw new CredentialParsingException(ex.getRootCause());
        } catch (UnsupportedEncodingException uec) {
            throw new UncheckedWrapperException(uec);
        }

        // check validator
        if (!Arrays.equals(validator, calculated)) {
            throw new CredentialParsingException("Bad validator");
        }
        // return the new credential
        return new Credential(value, expiration, validator);
    }

    /**
     * @return the validator for the given data.
     **/
    private static byte[] createValidator(String value,
                                          long expiration,
                                          Mac mac)
        throws ValidatorException {

        if ( mac == null ) { throw new NullPointerException("mac"); }

        // convert the credential material to bytes
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(value);
            dos.writeLong(expiration);
            byte[] data = baos.toByteArray();
            return mac.doFinal(data);
        } catch  (IOException ex) {
            throw new ValidatorException(ex);
        }
    }

    private static class ValidatorException extends KernelLoginException {
        public ValidatorException(Exception rootCause) {
            super(rootCause);
        }
    }
}
