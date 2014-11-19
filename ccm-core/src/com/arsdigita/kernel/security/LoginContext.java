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
import java.util.HashMap;
import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;

/**
 * An in-house implementation of JAAS's <code>LoginContext</code> class. Needed to workaround a bug
 * in JAAS 1.0 that requires LoginModules to be loaded by the system classloader. This class loads
 * LoginModules using <code>Class.forName()</code>. The JAAS bug will be fixed in JDK 1.4.
 *
 * @author Sameer Ajmani
 * @version $Id: LoginContext.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class LoginContext {

    private final static Logger s_log = Logger.getLogger(LoginContext.class);

    private Subject m_subject;
    private CallbackHandler m_handler;
    private Map m_shared = new HashMap();
    private LoginModule[] m_modules;
    private AppConfigurationEntry.LoginModuleControlFlag[] m_flags;

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public LoginContext(String name)
        throws LoginException {
        this(name, new Subject());
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public LoginContext(String name,
                        Subject subject)
        throws LoginException {
        this(name, subject, new CallbackHandler() {

            public void handle(Callback[] cbs)
                throws UnsupportedCallbackException {
                if (cbs.length > 0) {
                    throw new UnsupportedCallbackException(cbs[0], "CallbackHandler not defined");
                }
            }

        });
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public LoginContext(String name,
                        CallbackHandler handler)
        throws LoginException {
        this(name, new Subject(), handler);
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public LoginContext(String name,
                        Subject subject,
                        CallbackHandler handler)
        throws LoginException {
        m_subject = subject;
        m_handler = handler;

        Configuration config = Configuration.getConfiguration();
        if (config == null) {
            throw new LoginException("Login config not defined");
        }

        AppConfigurationEntry[] entries = config.getAppConfigurationEntry(name);
        if (entries == null) {
            throw new LoginException("Login config for '" + name + "' not defined");
        }

        m_modules = new LoginModule[entries.length];
        m_flags = new AppConfigurationEntry.LoginModuleControlFlag[entries.length];

        for (int i = 0; i < m_modules.length; i++) {
            String module = entries[i].getLoginModuleName();
            try {
                m_modules[i] = (LoginModule) Class.forName(module).newInstance();
                m_modules[i].initialize(m_subject, m_handler, m_shared,
                                        entries[i].getOptions());
                m_flags[i] = entries[i].getControlFlag();
            } catch (ClassNotFoundException e) {
                throw new KernelLoginException(module + " not found", e);
            } catch (ExceptionInInitializerError e) {
                throw new KernelLoginException(module + " initializer error", e);
            } catch (LinkageError e) {
                throw new KernelLoginException(module + " linkage error", e);
            } catch (IllegalAccessException e) {
                throw new KernelLoginException(module + " illegal access: "
                                                   + "requires public no-argument constructor", e);
            } catch (InstantiationException e) {
                throw new KernelLoginException(module + " instantiation exception: "
                                                   + "requires public no-argument constructor", e);
            } catch (SecurityException e) {
                throw new KernelLoginException(module + " security exception: check permissions", e);
            } catch (ClassCastException e) {
                throw new KernelLoginException(module + " not a LoginModule", e);
            }
        }
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public Subject getSubject() {
        return m_subject;
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public void login() throws LoginException {
        LoginException first = null;
        boolean gotFailure = false;
        // login
        for (int i = 0; i < m_modules.length; i++) {
            try {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Login on " + m_modules[i].getClass().getName());
                }

                m_modules[i].login();

                s_log.debug("Login succeeded");

                if (m_flags[i] == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
                    // sufficient module succeeded
                    break; // end login
                }
            } catch (LoginException e) {
                if (first == null) {
                    first = e;
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Login failed: " + m_flags[i], e);
                }

                if (m_flags[i] == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
                    // required module failed
                    gotFailure = true;
                } else if (m_flags[i] == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
                    // requisite module failed
                    gotFailure = true;
                    break; // end login
                }
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("gotFailure: " + gotFailure);
        }

        // commit
        if (!gotFailure) {
			// We want to report the first interesting exception. If we got here
            // then login succeeded, so that's no longer interesting.
            first = null;

            s_log.debug("Doing commit");

            for (int i = 0; i < m_modules.length; i++) {
                try {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Commit on " + m_modules[i].getClass().getName());
                    }

                    m_modules[i].commit();

                    s_log.debug("Commit succeeded");
                } catch (LoginException e) {
                    s_log.debug("Commit failed", e);

                    gotFailure = true;
                    if (first == null) {
                        first = e;
                    }
                }
            }
            if (!gotFailure) {
                return;
            }
            // commit failed, fall through to abort
        }

        s_log.debug("Doing abort");

        // abort
        for (int i = 0; i < m_modules.length; i++) {
            try {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Abort on " + m_modules[i].getClass().getName());
                }

                m_modules[i].abort();

                s_log.debug("Abort succeeded");
            } catch (LoginException e) {
                s_log.debug("Abort failed", e);

                gotFailure = true;
                if (first == null) {
                    first = e;
                }
            }
        }
        // throw the saved exception
        if (first != null) {
            throw first;
        }
    }

    /**
     * See <code>javax.security.auth.login.LoginContext</code>.
     */
    public void logout() throws LoginException {
        LoginException first = null;
        // logout
        for (int i = 0; i < m_modules.length; i++) {
            try {
                m_modules[i].logout();
            } catch (LoginException e) {
                if (first == null) {
                    first = e;
                }
            }
        }
        // throw the saved exception
        if (first != null) {
            throw first;
        }
    }

}
