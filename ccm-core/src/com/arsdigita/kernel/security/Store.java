/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.security.SecureRandom;
import java.math.BigInteger;

import org.apache.log4j.Logger;

/**
 * Provides access to the key store in which the secret is stored.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-12-18
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 **/
final class Store implements KeyStorage {
    final static Store INSTANCE = new Store();

    private final static Logger s_log = Logger.getLogger(Store.class);

    private final static String TYPE = "com.arsdigita.kernel.security.KeyStore";
    private final static String ID    = "id";
    private final static String OWNER = "owner";
    private final static String STORE = "store";

    private final static String OWNER_VALUE = "kernel.security";
    private final static BigInteger ID_VALUE = new BigInteger("0");

    private byte[] m_secret;

    Store() {}

    static byte[] newKey() {
        byte[] key = new byte[LegacyInitializer.SECRET_KEY_BYTES];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public synchronized void init() {
        if ( hasBeenInitialized() ) {
            throw new UncheckedWrapperException
                ("key store had been initialized");
        }
        init(ID_VALUE, OWNER_VALUE, newKey());
    }

    private boolean hasBeenInitialized() {
        DataCollection dc = SessionManager.getSession().retrieve(TYPE);
        dc.addEqualsFilter(ID, ID_VALUE);
        boolean result = dc.size() > 0;
        dc.close();
        return result;
    }


    /**
     * This method is exposed as package-scoped solely for the purpose of
     * white-box unit-testing.
     *
     * @throws NullPointerException if any of the parameters is null.
     **/
    void init(BigInteger id, String owner, byte[] store) {
        if ( id == null ) { throw new NullPointerException("id"); }
        if ( owner == null ) { throw new NullPointerException("owner"); }
        if ( store == null ) { throw new NullPointerException("store"); }
        if ( store.length < 1 ) {
            throw new IllegalArgumentException("empty store");
        }

        DataObject dobj = SessionManager.getSession().create(TYPE);
        dobj.set(ID, id);
        dobj.set(OWNER, owner);
        dobj.set(STORE, store);
        dobj.save();
    }

    synchronized byte[] loadSecret() {
        if ( m_secret != null ) { return m_secret; }

        DataObject dobj = SessionManager.getSession().retrieve
            (new OID(TYPE, ID_VALUE));
        m_secret = (byte[]) dobj.get(STORE);
        if ( m_secret == null ) {
            throw new IllegalStateException
                ("the store is null");
        }
        if ( m_secret.length != LegacyInitializer.SECRET_KEY_BYTES ) {
            throw new IllegalArgumentException
                ("wrong length. expected=" + LegacyInitializer.SECRET_KEY_BYTES +
                 ", but got " + m_secret.length);
        }
        return m_secret;
    }
}
