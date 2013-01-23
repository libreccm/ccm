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
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

/**
 * Logs in a user for password recovery if the user has a valid
 * authentication URL parameter.
 *
 * @author Sameer Ajmani
 * @version $Id: RecoveryLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class RecoveryLoginModule extends UserLoginModule {

    private static final Logger s_log =
                         Logger.getLogger(RecoveryLoginModule.class.getName());

    private static final String URL_PARAM_NAME = "ad_user_recover";

    // fields set by initialize()
    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;

    /**
     * Creates a UserLoginModule that uses a URLManager to manage the
     * credential value.  Overrides <code>URLManager.shouldSetValue()</code>
     * to return <code>false</code> so that the recover credential is never
     * propagated to future requests.
     */
    public RecoveryLoginModule() {
        super(new URLManager(java.util.Collections.EMPTY_SET) {
                /**
                 * Ensures setValue() is never called: the recovery credential
                 * should not be propagated to other pages.
                 *
                 * @return false
                 **/
                @Override
                protected boolean shouldSetValue(String value)
                    throws LoginException {
                    return false;
                }
            });
    }

    // implements LoginModule
    @Override
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
     * If this login module succeeded, sets the Subject as "recovering".
     * Ignores the results of other login modules.
     *
     * @return <code>super.commit()</code>.
     *
     * @throws LoginException if an error occurs.
     */
    public boolean commit()
        throws LoginException {
        if (credentialIsSet()) {
            // this login module succeeded
            setRecovering();
        }
        return super.commit();
    }

    /**
     * Ignores the results of the other login modules and calls commit().
     *
     * @return <code>this.commit()</code>.
     *
     * @throws LoginException if an error occurs.
     */
    public boolean abort()
        throws LoginException {
        return this.commit();
    }

    /**
     * Gets the name of the credential.
     *
     * @return getParamName()
     *
     * @throws LoginException if an error occurs.
     */
    protected String getCredentialName()
        throws LoginException {
        return getParamName();
    }

    /**
     * Sets this login module's Subject as "recovering".  Adds a "recovery
     * credential" to this Subject's set of public credentials.  The
     * recovery credential is used check whether the user is recovering.
     *
     * @see #isRecovering(Subject)
     */
    private void setRecovering() {
        s_log.debug("setting recovery credential");
        m_subject.getPublicCredentials().add(getParamName());
    }

    /**
     * Determines whether the given Subject is recovering by checking
     * whether it has a recovery credential.
     *
     * @return <code>true</code> if the Subject has the recovery credential,
     * <code>false</code> otherwise.
     */
    public static boolean isRecovering(Subject subject) {
        return subject.getPublicCredentials().contains(getParamName());
    }

    /**
     * Returns the name of this login module's URL parameter.  To allow a
     * user to recover from a lost password, provide the user with a link to
     * the change password page with this URL parameter set to
     * <code>getParamValue()</code>.
     *
     * @return the name of the recovery login URL parameter.
     * @see #getParamValue(BigDecimal)
     */
    public static String getParamName() {
        return URL_PARAM_NAME;
    }

    /**
     * Returns a URL parameter value that will allow the given user to log
     * in and change their password without entering their old password.
     * Use with care, as this value also allows the user to log in as usual.
     *
     * @param userID the ID of the user that needs to recover
     *
     * @return the value of the recovery login URL parameter.
     *
     * @throws CredentialEncodingException if unable to create the value.
     *
     * @see #getParamName()
     */
    public static String getParamValue(BigDecimal userID)
        throws CredentialEncodingException {
        return Credential
            .create(userID.toString(),
                    1000 * CredentialLoginModule.TIMEOUT_SECS)
            .toString();
    }
}
