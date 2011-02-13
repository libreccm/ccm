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
 */
package com.arsdigita.auth.http;

import com.arsdigita.util.parameter.Converters;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.AbstractParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.runtime.AbstractConfig;
import org.apache.commons.beanutils.Converter;


import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;

public class HTTPAuthConfig extends AbstractConfig {

    private static final Logger logger = Logger.getLogger(HTTPAuthConfig.class);

    static {
        logger.debug("Static initalizer starting...");
        Converters.set(Inet4AddressRange.class,
                       new Inet4AddressRangeConvertor());
        logger.debug("Static initalizer finished.");
    }
    private BooleanParameter m_isActive;
    private BooleanParameter m_isDebugMode;
    private IntegerParameter m_nonceTTL;
    private StringParameter m_serverName;
    private IntegerParameter m_serverPort;
    private Inet4AddressRangeParameter m_clientIPRange;
    private StringParameter m_keyAlias;
    private StringParameter m_keyCypher;
    private StringParameter m_keyPassword;
    private StringParameter m_keystorePassword;
    private StringParameter m_keystorePath;

    public HTTPAuthConfig() {
        m_isActive = new BooleanParameter("com.arsdigita.auth.http.active",
                                          Parameter.OPTIONAL,
                                          Boolean.FALSE);


        m_isDebugMode = new BooleanParameter("com.arsdigita.auth.http.debug",
                                             Parameter.OPTIONAL,
                                             Boolean.FALSE);


        m_nonceTTL = new IntegerParameter("com.arsdigita.auth.http.nonce_ttl",
                                          Parameter.OPTIONAL,
                                          new Integer(60));


        m_serverName = new StringParameter("com.arsdigita.auth.http.server_name",
                                           Parameter.OPTIONAL,
                                           // XXX bz108251
                                           //Parameter.REQUIRED,
                                           null);
        m_serverPort = new IntegerParameter(
                "com.arsdigita.auth.http.server_port",
                                            Parameter.OPTIONAL,
                                            new Integer(80));

        m_clientIPRange = new Inet4AddressRangeParameter(
                "com.arsdigita.auth.http.client_ip_range",
                                                         Parameter.OPTIONAL,
                                                         // XXX bz108251
                                                         //Parameter.REQUIRED,
                                                         null);

        m_keyAlias = new StringParameter("com.arsdigita.auth.http.key_alias",
                                         Parameter.OPTIONAL,
                                         "authhttp");
        m_keyCypher = new StringParameter("com.arsdigita.auth.http.key_cypher",
                                          Parameter.OPTIONAL,
                                          "RSA");
        m_keyPassword = new StringParameter(
                "com.arsdigita.auth.http.key_password",
                                            Parameter.OPTIONAL,
                                            // XXX bz108251
                                            //Parameter.REQUIRED,
                                            null);


        m_keystorePassword = new StringParameter(
                "com.arsdigita.auth.http.keystore_password",
                                                 Parameter.OPTIONAL,
                                                 // XXX bz108251
                                                 //Parameter.REQUIRED,
                                                 null);
        m_keystorePath = new StringParameter(
                "com.arsdigita.auth.http.keystore_path",
                                             Parameter.OPTIONAL,
                                             // XXX bz108251
                                             //Parameter.REQUIRED,
                                             null);

        register(m_isActive);
        register(m_isDebugMode);

        register(m_nonceTTL);

        register(m_serverName);
        register(m_serverPort);

        register(m_clientIPRange);

        register(m_keyAlias);
        register(m_keyCypher);
        register(m_keyPassword);

        register(m_keystorePassword);
        register(m_keystorePath);

        loadInfo();
    }

    public final boolean isActive() {
        return Boolean.TRUE.equals(get(m_isActive));
    }

    public final boolean isDebugMode() {
        return Boolean.TRUE.equals(get(m_isDebugMode));
    }

    public final int getNonceTTL() {
        return ((Integer) get(m_nonceTTL)).intValue();
    }

    public final String getServerName() {
        return (String) get(m_serverName);
    }

    public final int getServerPort() {
        return ((Integer) get(m_serverPort)).intValue();
    }

    public final Inet4AddressRange getClientIPRange() {
        return (Inet4AddressRange) get(m_clientIPRange);
    }

    public final String getKeyAlias() {
        return (String) get(m_keyAlias);
    }

    public final String getKeyCypher() {
        return (String) get(m_keyCypher);
    }

    public final String getKeyPassword() {
        return (String) get(m_keyPassword);
    }

    public final String getKeyStorePassword() {
        return (String) get(m_keystorePassword);
    }

    // Package protected, since we won't neccessarily always
    // store the keystore in a file on disk
    final String getKeyStorePath() {
        return (String) get(m_keystorePath);
    }

    public final InputStream getKeyStoreStream() {
        String file = getKeyStorePath();

        FileInputStream is;

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new UncheckedWrapperException(
                    "cannot read keystore file " + file, ex);
        }

        return is;
    }

    public final KeyStore getKeyStore() {
        InputStream is = getKeyStoreStream();
        KeyStore store = null;
        try {
            // No, they really don't provide a constant for
            // this magic value in java.security APIs!!!
            store = KeyStore.getInstance("JKS");
        } catch (KeyStoreException ex) {
            throw new UncheckedWrapperException(
                    "cannot get keystore instance JKS", ex);
        }

        try {
            store.load(is, getKeyStorePassword().toCharArray());
        } catch (IOException ex) {
            throw new UncheckedWrapperException(
                    "cannot load keystore from " + getKeyStorePath(), ex);
        } catch (CertificateException ex) {
            throw new UncheckedWrapperException(
                    "cannot load keystore certificates from "
                    + getKeyStorePath(), ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new UncheckedWrapperException(
                    "cannot check integrity of keystore " + getKeyStorePath(),
                    ex);
        }
        return store;
    }

    public final PublicKey getPublicKey() {
        KeyStore keystore = getKeyStore();

        Certificate cert = null;
        try {
            cert = keystore.getCertificate(getKeyAlias());
        } catch (KeyStoreException ex) {
            throw new UncheckedWrapperException(
                    "cannot get public key from keystore " + getKeyStorePath(),
                    ex);
        }

        Assert.exists(cert, Certificate.class);

        return cert.getPublicKey();
    }

    public final PrivateKey getPrivateKey() {
        KeyStore keystore = getKeyStore();

        Key key = null;
        try {
            key = keystore.getKey(getKeyAlias(),
                                  getKeyPassword().toCharArray());
        } catch (KeyStoreException ex) {
            throw new UncheckedWrapperException(
                    "cannot get private key from keystore " + getKeyStorePath(),
                    ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new UncheckedWrapperException(
                    "cannot get private key from keystore " + getKeyStorePath(),
                    ex);
        } catch (UnrecoverableKeyException ex) {
            throw new UncheckedWrapperException(
                    "cannot get private key from keystore " + getKeyStorePath(),
                    ex);
        }

        Assert.exists(key, Key.class);

        return (PrivateKey) key;
    }

    private static class Inet4AddressRangeConvertor implements Converter {

        public Object convert(Class type,
                              Object value) {
            return Inet4AddressRange.getByName((String) value);
        }
    }

    private static class Inet4AddressRangeParameter extends AbstractParameter {

        public Inet4AddressRangeParameter(final String name) {
            super(name, Inet4AddressRange.class);
        }

        public Inet4AddressRangeParameter(final String name,
                                          final int multiplicity,
                                          final Object defaalt) {
            super(name, multiplicity, defaalt, Inet4AddressRange.class);
        }
    }
}
