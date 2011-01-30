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
import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.kernel.Kernel;

/**
 * Loads an ID from a credential stored in the current HTTP request (for
 * example, cookie or URL parameter).  If the credential is invalid but the
 * ID is loaded successfully using another <code>LoginModule</code>, this
 * module sets a new credential containing the ID.  This class uses the
 * <i>Strategy</i> design pattern to manage the persistent credential value;
 * it delegates to the <code>CredentialManager</code> provided at
 * construction to get, set, and delete the credential value.
 *
 * @see CredentialManager
 *
 * @author Sameer Ajmani
 * @version $Id: CredentialLoginModule.java 718 2005-08-18 15:34:42Z apevec $
 **/
public abstract class CredentialLoginModule implements LoginModule {

    private static final Logger s_log =
        Logger.getLogger(CredentialLoginModule.class.getName());

    /**
     * Minimum time allowed between session renewals, in seconds.
     **/
    public static final long RENEW_SECS = 60*5; // 5 mins

    /**
     * Maximum time allowed between clicks in a single session, in seconds.
     **/
    public static final long TIMEOUT_SECS = 60*20; // 20 mins

    /**
     * Maximum time that a single session can last, in seconds.
     **/
    public static final long LIFETIME_SECS = 60*60*24*2; // 2 days

    /**
     * Maximum time that a "permanent" credential can last, in seconds.
     **/
    public static final long FOREVER_SECS = 60*60*24*365*3; // 3 years

    // fields set by initialize()
    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared;
    private Map m_options;

    // cached values for accessors
    private HttpServletRequest m_req = null;
    private HttpServletResponse m_res = null;
    private Boolean m_forever = null;
    private Boolean m_secure = null;

    // the credential itself
    private Credential m_credential = null;

    // manages the credential value
    private CredentialManager m_manager;

    /**
     * Creates a new <code>CredentialLoginModule</code> associated with the
     * given <code>CredentialManager</code>.  This module uses the given
     * manager to get, set, and delete the credential value.
     **/
    public CredentialLoginModule(CredentialManager manager) {
        m_manager = manager;
    }

    /**
     * Initializes this login module and its <code>CredentialManager</code>
     * with the given login context information.  This method is called by
     * <code>LoginContext</code> after this class is instantiated.
     **/
    public void initialize(Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        m_manager.initialize(this, subject, handler, shared, options);
        m_subject = subject;
        m_handler = handler;
        m_shared  = shared;
        m_options = options;
    }

    /**
     * Checks whether the current request contains a valid credential.
     *
     * @return <code>true<code>.
     * @throws CredentialNotFoundException if the credential is missing.
     * @throws CredentialParsingException if the credential is invalid.
     * @throws CredentialExpiredException if the credential has expired.
     * @throws LoginException if an error occurs.
     **/
    public boolean login() throws LoginException {
        s_log.debug("START login");
        if (Kernel.getConfig().isSecureLoginRequired() && !isSecure()) {
            return false;
        }
        loadCredential();
        s_log.debug("END login");
        return true;
    }

    /**
     * Deletes the credential and invalidates the client session.
     *
     * @return <code>true</code>.
     * @throws LoginException if an error occurs.
     **/
    public boolean logout() throws LoginException {
        s_log.debug("START logout");
        invalidateSession();
        deleteCredential();
        s_log.debug("END logout");
        return true;
    }

    /**
     * Deletes the credential.
     *
     * @return true
     * @throws LoginException if an error occurs
     **/
    public boolean abort() throws LoginException {
        // overall login failed
        s_log.debug("START abort");
        deleteCredential();
        s_log.debug("END abort");
        return true;
    }

    /**
     * Sets the ID for the Subject to the value of the credential if no ID
     * is already set.  If needed, updates the credential.  If the Subject
     * has an ID different from the one in the credential, invalidates the
     * client session.
     *
     * @return <code>true</code>.
     * @throws FailedLoginException if unable to set ID.
     * @throws LoginException if an error occurs.
     **/
    public boolean commit() throws LoginException {
        // overall login succeeded
        s_log.debug("START commit");
        try {
            BigDecimal id = getID();
            String value = id.toString();
            if (shouldInvalidateSession(value)) {
                invalidateSession();
            }
            if (m_manager.shouldSetValue(value)) {
                setCredential(value);
            }
            s_log.debug("SUCCESS commit");
        } catch (FailedLoginException e) {
            // ignore failure, because another LoginModule may succeed
            s_log.debug("FAILURE commit (ignored)", e);
        }
        return true;
    }

    /**
     * Reads the ID from the Subject and sets it if needed.  First, checks
     * if some other LoginModule has assigned an ID to the Subject.  If not,
     * reads the ID from the credential and sets it in the Subject.
     *
     * @return the ID.
     * @throws FailedLoginException if the ID is not available.
     **/
    private BigDecimal getID()
        throws FailedLoginException {
        s_log.debug("START getID");
        try {
            // load ID from subject
            BigDecimal id = loadID();
            s_log.debug("SUCCESS getID from subject");
            return id;
        } catch (NoSuchElementException e) {
            // load ID from credential
            if (!credentialIsSet()) {
                s_log.debug("FAILURE getID not available", e);
                throw new FailedLoginException("No ID available");
            }
            try {
                String value = m_credential.getValue();
                BigDecimal id = new BigDecimal(value);
                s_log.debug("SUCCESS getID from credential");
                saveID(id);
                return id;
            } catch (NumberFormatException nfe) {
                s_log.debug("FAILURE getID number format", nfe);
                throw new FailedLoginException("No ID available");
            }
        }
    }

    /**
     * Loads the ID from the Subject.
     **/
    protected final BigDecimal loadID()
        throws NoSuchElementException {
        Set set = m_subject.getPrincipals(PartyPrincipal.class);
        return ((PartyPrincipal)set.iterator().next()).getID();
    }

    /**
     * Saves the given ID in the Subject.
     *
     * @param id the ID to save
     **/
    protected final void saveID(BigDecimal id) {
        m_subject.getPrincipals().add(new PartyPrincipal(id));
    }

    /**
     * Determines whether the current request is secure.
     *
     * @return <code>true</code> if the current request is secure,
     * <code>false</code> otherwise.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final boolean isSecure()
        throws LoginException {
        if (m_secure == null) {
            m_secure = new Boolean
                (Util.getSecurityHelper().isSecure(getRequest()));
        }
        return m_secure.booleanValue();
    }

    /**
     * Returns the name of the credential.
     * @return the name of the credential.
     * @throws LoginException if an error occurs.
     **/
    protected abstract String getCredentialName()
        throws LoginException;

    /**
     * Returns the lifetime of the credential in milliseconds.
     * @return the lifetime of the credential in milliseconds.
     * @throws LoginException if an error occurs.
     **/
    protected abstract long getLifetime()
        throws LoginException;

    /**
     * Loads the credential.
     **/
    private void loadCredential() throws LoginException {
            s_log.debug("START loadCredential");
            String value = m_manager.getValue();
            m_credential = Credential.parse(value);
            s_log.debug("SUCCESS loadCredential: expires: "
                        +m_credential.getExpiration());
    }

    /**
     * Determines whether the client session should be invalidated.
     *
     * @param value the new value for the credential
     *
     * @return <code>true</code> if the client session should be
     * invalidated, <code>false</code> otherwise.
     *
     * @throws LoginException if an error occurs.
     **/
    protected abstract boolean shouldInvalidateSession(String value)
        throws LoginException;

    /**
     * Invalidates the client session and creates a new one.
     **/
    private void invalidateSession()
        throws LoginException {
        s_log.debug("invalidateSession: before: "
                    +getRequest().getSession().getId());

        getRequest().getSession().invalidate();
        getRequest().getSession(); // create new session

        s_log.debug("invalidateSession: after: "
                    +getRequest().getSession().getId());
    }

    /**
     * Sets the credential value to the given value.
     **/
    private void setCredential(String value)
        throws LoginException {
        s_log.debug("START setCredential to "+value);
        m_credential = Credential.create(value, getLifetime());
        m_manager.setValue(m_credential.toString());
        s_log.debug("SUCCESS setCredential: expires: "
                    +m_credential.getExpiration());
    }

    /**
     * Determines whether the credential is set.  Subclasses may call this
     * to determine whether this login module succeeded.
     *
     * @return <code>true</code> if credential is set, <code>false</code>
     * otherwise.
     **/
    protected final boolean credentialIsSet() {
        return (m_credential != null);
    }

    /**
     * Determines whether the credential has the given value.
     *
     * @param value the value to check
     *
     * @return <code>true</code> if credential's value equals the given
     * value, <code>false</code> otherwise.
     *
     * @throws NullPointerException if !credentialIsSet().
     **/
    protected final boolean credentialHasValue(String value) {
        return m_credential.getValue().equals(value);
    }

    /**
     * Determines whether the credential should be renewed.  Returns
     * <code>true</code> if the credential is more than
     * <code>RENEW_SECS</code> old.
     *
     * @return <code>true</code> if credential is old; <code>false</code>
     * otherwise.
     *
     * @throws NullPointerException if !credentialIsSet().
     **/
    protected final boolean credentialIsOld() {
        long expireTime = m_credential.getExpiration().getTime() / 1000;
        long issueTime = expireTime - TIMEOUT_SECS;
        long renewTime = issueTime + RENEW_SECS;
        long currentTime = System.currentTimeMillis() / 1000;
        return renewTime < currentTime;
    }

    /**
     * Determines whether the requested URI ends in an "excluded" extension.
     * Extensions in the "excluded" list specify file types for which
     * credentials should never be set, such as image and media files.
     *
     * @return <code>true</code> if the request URI ends with an "excluded"
     * extension, <code>false</code> otherwise.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final boolean requestIsExcluded()
        throws LoginException {
        java.util.Iterator exts = Util.getExcludedExtensions();
        while (exts.hasNext()) {
            String ext = (String)exts.next();
            if (getRequest().getRequestURI().endsWith(ext)) {
                s_log.debug("got excluded extension: "
                            +getRequest().getRequestURI());
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the credential.
     **/
    private void deleteCredential()
        throws LoginException {
        m_manager.deleteValue();
        m_credential = null;
    }

    /**
     * Returns the current HTTP request.
     *
     * @return the current HTTP request.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final HttpServletRequest getRequest()
        throws LoginException
    {
        if (m_req == null) {
            m_req = Util.getRequest(m_handler);
        }
        return m_req;
    }

    /**
     * Returns the current HTTP response.
     *
     * @return the current HTTP response.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final HttpServletResponse getResponse()
        throws LoginException {
        try {
            if (m_res == null) {
                HTTPResponseCallback cb = new HTTPResponseCallback();
                m_handler.handle(new Callback[] { cb });
                m_res = cb.getResponse();
            }
            return m_res;
        } catch (IOException e) {
            throw new KernelLoginException
                ("Could not get HTTP response", e);
        } catch (UnsupportedCallbackException e) {
            throw new KernelLoginException
                ("Could not get HTTP response", e);
        }
    }

    /**
     * Determines whether the credential should last "forever" or should
     * expire at the end of this session.
     *
     * @return <code>true</code> if the credential should last "forever",
     * <code>false</code> if the credential should expire at the end of this
     * session.
     *
     * @throws KernelLoginException if an error occurs.
     **/
    protected final boolean getForever() throws LoginException {
        try {
            if (m_forever == null) {
                LifetimeCallback cb = new LifetimeCallback();
                m_handler.handle(new Callback[] { cb });
                // m_forever = new Boolean(cb.isForever());
                // performancewise better:
                m_forever = Boolean.valueOf(cb.isForever());
            }
            return m_forever.booleanValue();
        } catch (IOException e) {
            throw new KernelLoginException("Could not get lifetime", e);
        } catch (UnsupportedCallbackException e) {
            throw new KernelLoginException("Could not get lifetime", e);
        }
    }
}
