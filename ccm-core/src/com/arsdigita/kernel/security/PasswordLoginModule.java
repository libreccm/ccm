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

import java.io.IOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.log4j.Logger;

/**
 * Superclass of LoginModules that authenticate users using a username and
 * password.  Fetches the username/password from shared data if possible,
 * otherwise queries the user directly using callbacks.  Saves the
 * username/password in shared data for use by other LoginModules.
 *
 * @author Sameer Ajmani
 * @version $Id: PasswordLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public abstract class PasswordLoginModule implements LoginModule {

    private static final Logger s_log =
                         Logger.getLogger(PasswordLoginModule.class.getName());

    /** Key for username in shared data map. */
    public static final String NAME_KEY = "javax.security.auth.login.name";
    /** Key for password in shared data map. */
    public static final String PASSWORD_KEY = "javax.security.auth.login.password";

    // fields set by initialize()
    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;

    /**
     * Implements LoginModule.
     * 
     * @param subject
     * @param handler
     * @param shared
     * @param options 
     */
    public void initialize(Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        m_subject = subject;
        m_handler = handler;
        m_shared = shared;
        m_options = options;
        // TODO: support "debug" option
    }

    /**
     * Retreives the username and password and calls the
     * <code>checkPassword</code> method.
     *
     * @return <code>true</code>.
     *
     * @throws LoginException if an error occurs.  Propagates exceptions
     * thrown by the <code>checkPassword</code> method.
     *
     * @see #checkPassword(String, char[])
     */
    public boolean login() throws LoginException {
        s_log.debug("START login");
        checkPassword(getUsername(), getPassword());
        s_log.debug("SUCCESS login");
        return true;
    }

    /**
     * Attempts to read username from shared data map; otherwise retreives
     * it using a NameCallback.
     *
     * @return the username.
     *
     * @throws LoginException if an error occurs.
     */
    protected String getUsername() throws LoginException {
        // get name from shared data
        // TODO: only if *Pass option set
        String username = (String)m_shared.get(NAME_KEY);
        if (username != null) {
            return username;
        }
        // get name using callback and save in shared data
        try {
            NameCallback cb = new NameCallback("Username: ");
            m_handler.handle(new Callback[] {cb});
            username = cb.getName();
            m_shared.put(NAME_KEY, username);
            return username;
        } catch (UnsupportedCallbackException e) {
            throw new KernelLoginException("Could not get username", e);
        } catch (IOException e) {
            throw new KernelLoginException("Could not get username", e);
        }
    }

    /**
     * Attempts to read password from shared data map; otherwise retreives
     * it using a PasswordCallback.
     *
     * @return the password.
     *
     * @throws LoginException if an error occurs.
     */
    protected char[] getPassword() throws LoginException {
        // get password from shared data
        // TODO: only if *Pass option set
        char[] password = (char[])m_shared.get(PASSWORD_KEY);
        if (password != null) {
            return password;
        }
        // get password using callback and save in shared data
        try {
            PasswordCallback cb = new PasswordCallback("Password: ",
                                                       false);
            m_handler.handle(new Callback[] {cb});
            password =  cb.getPassword();
            m_shared.put(PASSWORD_KEY, password);
            return password;
        } catch (UnsupportedCallbackException e) {
            throw new KernelLoginException("Could not get password", e);
        } catch (IOException e) {
            throw new KernelLoginException("Could not get password", e);
        }
    }

    /**
     * Checks whether the given username/password combination is valid.
     *
     * @param username the username to check
     * @param password the password to check
     *
     * @throws AccountNotFoundException if the account does not exist.
     * @throws AccountExpiredException if the account has expired.
     * @throws AccountLockedException if the account is locked.
     * @throws FailedLoginException if the password is invalid.
     * @throws LoginException if an error occurs.
     */
    protected abstract void checkPassword
        (String username, char[] password) throws LoginException;

    // implements LoginModule
    public abstract boolean commit() throws LoginException;

    // implements LoginModule
    public abstract boolean abort() throws LoginException;

    // implements LoginModule
    public abstract boolean logout() throws LoginException;
}
