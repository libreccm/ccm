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

import java.math.BigDecimal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.log4j.Logger;

/**
 * Superclass of LoginModules that map usernames to user IDs.  Translates
 * the username provided by the user into a numeric user ID used within ACS.
 * A subclass of this class is needed in any login configuration that uses a
 * PasswordLoginModule.
 *
 * @author Sameer Ajmani
 */
public abstract class MappingLoginModule implements LoginModule {

    private static final Logger s_log =
        Logger.getLogger(MappingLoginModule.class.getName());

    private BigDecimal m_userID = null;

    // fields set by initialize()
    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;

    // implements LoginModule
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
     * Reads the username from the shared data and calls
     * <code>getUserID</code>.
     *
     * @return <code>true</code>.
     *
     * @throws FailedLoginException if no username is available.
     *
     * @throws LoginException if an error occurs.  Propagates exceptions
     * thrown by <code>getUserID</code>.
     *
     * @see #getUserID(String)
     **/
    public boolean login() throws LoginException {
        s_log.debug("START login");
        m_userID = getUserID(getUsername());
        s_log.debug("SUCCESS login");
        return true;
    }

    /**
     * Reads the username from shared data.
     *
     * @return the username.
     *
     * @throws FailedLoginException if no username is available.
     **/
    private String getUsername() throws FailedLoginException {
        String username = (String)
            m_shared.get(PasswordLoginModule.NAME_KEY);
        if (username == null) {
            s_log.debug("FAILURE no username available");
            throw new FailedLoginException("no username available");
        }
        return username;
    }

    /**
     * Maps a username to a user ID.
     *
     * @param username the username to map
     *
     * @return the user ID corresponding to the given username.
     *
     * @throws AccountNotFoundException if no user ID exists for the given
     * username.
     *
     * @throws LoginException if an error occurs.
     **/
    protected abstract BigDecimal getUserID(String username)
        throws AccountNotFoundException, LoginException;

    /**
     * Adds the user ID to the Subject in a PartyPrincipal.
     *
     * @return <code>true</code>.
     **/
    public boolean commit() throws LoginException {
        s_log.debug("START commit");
        if (m_userID != null) {
            m_subject.getPrincipals().add(new PartyPrincipal(m_userID));
            s_log.debug("SUCCESS added new principal");
        }
        s_log.debug("END commit");
        return true;
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean abort() throws LoginException {
        s_log.debug("abort");
        return true;
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean logout() throws LoginException {
        s_log.debug("logout");
        return true;
    }
}
