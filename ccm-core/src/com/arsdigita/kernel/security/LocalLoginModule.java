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

import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import org.apache.log4j.Logger;

/**
 * Checks a username and password in the database using
 * <code>com.arsdigita.kernel.UserAuthentication</code>.
 *
 * @author Sameer Ajmani
 **/
public class LocalLoginModule extends PasswordLoginModule {

    public static final String versionId = "$Id: LocalLoginModule.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(LocalLoginModule.class.getName());

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
        super.initialize(subject, handler, shared, options);
        m_handler = handler;
        m_subject = subject;
        m_shared  = shared;
        m_options = options;
        // TODO: support "debug" option
    }

    /**
     * Retrieves the user account from the database and checks the password
     * against the saved value.
     *
     * @throws AccountNotFoundException if the account does not exist.
     * @throws FailedLoginException if the password is invalid.
     * @throws LoginException if an error occurs.
     **/
    protected void checkPassword(String username, char[] password)
        throws LoginException {

        s_log.debug("START checkPassword");
        UserAuthentication auth;
        try {
            s_log.debug("retreiving UserAuthentication");
            auth = UserAuthentication.retrieveForLoginName(username);
        } catch (DataObjectNotFoundException e) {
            throw new AccountNotFoundException("no such user: "+username, e);
        }
        // FIXME: do not create string from password, since cannot clear
        // from memory!  Replace below param with char[] and create a string
        // of the password hash for the database
        s_log.debug("checking password");
        if (!auth.isValidPassword(new String(password))) {
            s_log.debug("FAILURE checkPassword: bad password");
            throw new FailedLoginException("bad username/password");
        }

        s_log.debug("SUCCESS checkPassword");
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean commit() throws LoginException {
        s_log.debug("commit");
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
