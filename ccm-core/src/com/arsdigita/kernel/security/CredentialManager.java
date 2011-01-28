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

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

/**
 * Provides methods for saving, retrieving, and deleting a string value from
 * HTTP requests and responses.  Used with
 * <code>CredentialLoginModule</code> to get, set, and delete client
 * authentication credentials.
 *
 * @see CredentialLoginModule
 *
 * @author Sameer Ajmani
 * @version $Id: CredentialManager.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class CredentialManager {

    /** Logging instance */
    private static final Logger s_log =
        Logger.getLogger(URLManager.class.getName());

    /** The login module associated with this manager  */
    private CredentialLoginModule m_module;

    /**
     * Constructs a new <code>CredentialManager</code>.
     */
    protected CredentialManager() {
    }

    /**
     * Returns the <code>CredentialLoginModule</code> associated with this
     * <code>CredentialManager</code>.
     */
    protected CredentialLoginModule getModule() {
        return m_module;
    }

    /**
     * Initializes this with the login context information.  Called by
     * <code>CredentialLoginModule.initialize()</code>.
     *
     * @param module the <code>CredentialLoginModule</code> associated with
     * this <code>CredentialManager</code>.
     */
    protected void initialize(CredentialLoginModule module,
                              Subject subject,
                              CallbackHandler handler,
                              Map shared,
                              Map options) {
        m_module = module;
    }

    /**
     * Retrieves the value of the credential named by
     * <code>getModule().getCredentialName()</code>.
     *
     * @return the credential value, never null.
     *
     * @throws CredentialNotFoundException if credential does not exist.
     *
     * @throws LoginException if an error occurs.
     */
    protected abstract String getValue()
        throws LoginException;

    /**
     * Determines whether <code>setValue()</code> should be called.
     *
     * @param value the new value for the credential
     *
     * @return true if <code>setValue()</code> should be called
     *
     * @throws LoginException if an error occurs.
     */
    protected abstract boolean shouldSetValue(String value)
        throws LoginException;

    /**
     * Sets the credential named by
     * <code>getModule().getCredentialName()</code> to the given value.
     *
     * @param value the new value for the credential
     *
     * @throws LoginException if an error occurs.
     */
    protected abstract void setValue(String value)
        throws LoginException;

    /**
     * Deletes the credential named by
     * <code>getModule().getCredentialName()</code>.
     *
     * @throws LoginException if an error occurs.
     */
    protected abstract void deleteValue()
        throws LoginException;
}
