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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.UserAuthentication;
import java.math.BigDecimal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

/**
 * Translates a username to a user ID using
 * <code>com.arsdigita.kernel.UserAuthentication</code>.
 *
 * @author Sameer Ajmani
 * @version $Id: UserIDLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class UserIDLoginModule extends MappingLoginModule {

    private static final Logger s_log =
        Logger.getLogger(UserIDLoginModule.class.getName());

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
        m_subject = subject;
        m_handler = handler;
        m_shared = shared;
        m_options = options;
        // TODO: support "debug" option
    }

    /**
     * Retreieves the user ID for the given username from the database.
     *
     * @param username the username to look up
     *
     * @return the user ID for the given username.
     *
     * @throws AccountNotFoundException if the account does not exist.
     * @throws LoginException if an error occurs.
     **/
    protected BigDecimal getUserID(String username)
        throws AccountNotFoundException, LoginException {
        try {
            return UserAuthentication
                .retrieveForLoginName(username)
                .getUser().getID();
        } catch (DataObjectNotFoundException e) {
            throw new AccountNotFoundException(username, e);
        }
    }
}
