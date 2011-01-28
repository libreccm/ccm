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
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.log4j.Logger;

/**
 * Supports login as an aribtrary user by checking whether a user ID has
 * been set for the Subject.  If so, <code>login</code> succeeds, so
 * <code>commit</code> is called on all login modules to log the user in.
 * If no user ID is set, <code>login</code> fails.  This module should
 * appear at the beginning of a login context with the "sufficient" control
 * flag.  Note that this module does not check the privileges of the current
 * user; this must be done by the code that runs the login context.
 *
 * @author Sameer Ajmani
 *
 * @see UserContext#login(com.arsdigita.kernel.User)
 * @version $Id: AdminLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class AdminLoginModule implements LoginModule {

    private static final Logger s_log =
        Logger.getLogger(AdminLoginModule.class.getName());

    private Subject m_subject;

    public void initialize(Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        m_subject = subject;
        // TODO: support "debug" option
    }

    /**
     * Checks whether a user ID has been assigned to the Subject.
     *
     * @return <code>true</code>.
     *
     * @throws FailedLoginException if no user ID has been assigned.
     **/
    public boolean login() throws LoginException {
        if (m_subject.getPrincipals(PartyPrincipal.class).isEmpty()) {
            throw new FailedLoginException("no existing principal");
        }
        return true;
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean commit() throws LoginException {
        return true;
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean abort() throws LoginException {
        return true;
    }

    /**
     * Trivial implementation; does nothing.
     *
     * @return <code>true</code>.
     **/
    public boolean logout() throws LoginException {
        return true;
    }
}
