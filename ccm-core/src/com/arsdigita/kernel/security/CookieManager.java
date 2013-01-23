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

import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.ServletUtils;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

/**
 * Manages a string value stored in a cookie.
 *
 * @see CookieLoginModule
 *
 * @author Sameer Ajmani
 * @version $Id: CookieManager.java 1477 2007-03-14 10:27:16Z chrisgilbert23 $
 */
public class CookieManager extends CredentialManager {

    private static final Logger s_log =
                         Logger.getLogger(CookieManager.class.getName());

    /**
     * 
     * @param module
     * @param subject
     * @param handler
     * @param shared
     * @param options 
     */
    @Override
    public void initialize(CredentialLoginModule module,
                           Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        super.initialize(module, subject, handler, shared, options);
    }

    /**
     * Determines whether <code>setValue()</code> should be called.
     *
     * @param value the new value for the credential
     *
     * @return <code>true</code> if the credential is not set or has the
     * wrong value or should be renewed, <code>false</code> otherwise.
     */
    protected boolean shouldSetValue(String value)
        throws LoginException {
        if (getModule().requestIsExcluded()) {
            return false;
        }
        return !getModule().credentialIsSet()
            || !getModule().credentialHasValue(value)
            || getModule().credentialIsOld();
    }

    /**
     * Returns the value of the cookie named
     * <code>getModule().getCredentialName()</code>.
     *
     * @return the value of the cookie named
     * <code>getModule().getCredentialName()</code>.
     *
     * @throws CredentialNotFoundException if the cookie is not in the
     * current request.
     *
     * @throws LoginException if an error occurs.
     */
    protected final String getValue()
        throws LoginException {
        s_log.debug("START getValue");
        String value = ServletUtils.getCookieValue
            (getModule().getRequest(),
             getModule().getCredentialName());
        if (value == null) {
            s_log.debug("FAILURE getValue");
            throw new CredentialNotFoundException();
        }
        s_log.debug("SUCCESS getValue: "+value);
        return value;
    }

    /**
     * Sets the cookie named <code>getModule().getCredentialName()</code> to
     * the given value.
     *
     * @throws LoginException if an error occurs.
     */
    protected final void setValue(String value)
        throws LoginException {
        // now we don't automatically set the duration to getCookieMaxAge()
        // setCookie(getModule().getCredentialName(), value, getCookieAge());
	// yes we do - cookie age was correctly set to either forever, or 
        // -1 (exists for duration of browser session). The config parameter
        // for cookie duration needs to apply to the credential timestamp 
        // in order for correct behaviour - This change has been applied
        // in UserLoginModule - chris.gilbert@westsussex.gov.uk
        setCookie(getModule().getCredentialName(), value, getCookieMaxAge());
    }

    /**
     * Deletes the cookie named
     * <code>getModule().getCredentialName()</code>.
     *
     * @throws LoginException if an error occurs.
     */
    protected final void deleteValue()
        throws LoginException {
        deleteCookie(getModule().getCredentialName());
    }

    /**
     * Deletes the named cookie.
     */
    private void deleteCookie(String name)
        throws LoginException {
        if (isCookieSet(name)) {
            s_log.debug("deleting existing cookie");
            setCookie(name, "", 0); // maxAge == 0 deletes cookie
        } else {
            s_log.debug("Not deleting cookie since it doesn't exist!");
        }
    }

    private boolean isCookieSet(String name) {
        Cookie cookies[] = null;
        try {
            cookies = getModule().getRequest().getCookies();
        } catch (LoginException ex) {
            throw new UncheckedWrapperException(ex);
        }

        if (cookies == null)
            return false;

        for (int i = 0 ; i < cookies.length ; i++)
            if (cookies[i].getName().equals(name))
                return true;

        return false;
    }

    /**
     * Sets the named cookie to the given value.
     */
    private void setCookie(String name, String value, int maxAge)
        throws LoginException {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setSecure(getModule().isSecure());
        getModule().getResponse().addCookie(cookie);
        String domain = Kernel.getSecurityConfig().getCookieDomain();
        if (domain != null) {
            cookie.setDomain(domain);
        }
        s_log.debug("Cookie set: domain - " + cookie.getDomain()
                    + " name - " + cookie.getName());
    }

    /**
     * Determines the lifespan of the cookie, using the setting
     * of the configuration, defaulting to getCookieMaxAge().
     */
    protected int getCookieAge() throws LoginException {
        Integer setting = Kernel.getSecurityConfig().getCookieDurationMinutes();
        return (setting == null ? getCookieMaxAge() : setting.intValue() * 60);
    }

    /**
     * Determines the correct max age for the cookie in seconds.  A return
     * value of -1 means the cookie should be deleted when the client's
     * browser quits.
     *
     * @return <code>FOREVER_SECS</code> if the user has requested permanent
     * login; -1 otherwise.
     */
    protected int getCookieMaxAge() throws LoginException {
        return getModule().getForever() ?
            (int)CredentialLoginModule.FOREVER_SECS : -1;
    }
}
