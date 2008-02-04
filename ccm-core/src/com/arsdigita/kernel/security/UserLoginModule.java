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

import com.arsdigita.kernel.Kernel;

/**
 * Authenticates a user (loads a user ID) from a credential stored in the
 * current HTTP request.
 *
 * @author Sameer Ajmani
 **/
public abstract class UserLoginModule extends CredentialLoginModule {

    public static final String versionId = "$Id: UserLoginModule.java 1477 2007-03-14 10:27:16Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(UserLoginModule.class.getName());

    /**
     * Name of the non-secure user authentication credential.
     **/
    public static final String NORMAL_CREDENTIAL_NAME
        = "ad_user_login";

    /**
     * Name of the secure user authentication credential.
     **/
    public static final String SECURE_CREDENTIAL_NAME
        = "ad_user_login_secure";

    // fields set by initialize()
    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;

    public UserLoginModule(CredentialManager manager) {
        super(manager);
    }

    // implements LoginModule
    public void initialize(Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        super.initialize(subject, handler, shared, options);
        m_subject = subject;
        m_handler = handler;
        m_shared  = shared;
        m_options = options;
    }

    /**
     * Returns the name of the credential.
     *
     * @return <code>SECURE_CREDENTIAL_NAME</code> if the current request is
     * secure, otherwise returns <code>NORMAL_CREDENTIAL_NAME</code>.
     **/
    protected String getCredentialName()
        throws LoginException {
        if (isSecure()) {
            return SECURE_CREDENTIAL_NAME;
        } else {
            return NORMAL_CREDENTIAL_NAME;
        }
    }

    /**
     * Returns the lifetime of the credential in milliseconds.
     *
     * @return <code>FOREVER_SECS</code> in milliseconds if the user
     * requests permanent login, otherwise returns <code>TIMEOUT_SECS</code>
     * in milliseconds.
     **/
    protected long getLifetime() throws LoginException {
	Integer setting = Kernel.getSecurityConfig().getCookieDurationMinutes();
	long longSetting = setting == null ? TIMEOUT_SECS : (long)setting.intValue() * 60;
        return 1000 * (getForever() ? FOREVER_SECS : longSetting);
    }

    /**
     * Determines whether the user's session should be invalidated.
     *
     * @param value the new value for the credential
     *
     * @return true if the credential is set and has the wrong value.
     **/
    protected boolean shouldInvalidateSession(String value)
        throws LoginException {
        return credentialIsSet() && !credentialHasValue(value);
    }
}
