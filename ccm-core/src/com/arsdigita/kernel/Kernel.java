/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import com.arsdigita.kernel.security.SecurityConfig;
import java.math.BigDecimal;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * <p>A central location for commonly used kernel services and their
 * accessories.</p>
 *
 * <p><b>Context.</b> {@link #getContext()} fetches the context record ({@link
 * com.arsdigita.kernel.KernelContext}) of the current thread.</p>
 *
 * <p><b>The system party.</b> {@link #getSystemParty()} returns the
 * party used to perform work as "the system".</p>
 *
 * @author Rafael Schloming
 * @author Richard Li
 * @author Justin Ross
 * @see com.arsdigita.kernel.KernelConfig
 * @see com.arsdigita.kernel.KernelContext
 * @see com.arsdigita.kernel.KernelExcursion
 *
 * @version $Id: Kernel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Kernel {

    /** Private logger instance for debugging   */
    private static final Logger s_log = Logger.getLogger(Kernel.class);

    /** The ID of the user that represents "the public", i.e. a non-logged-in
     *  user. Created by insert-users.sql (during load step)                  */
    private static final BigDecimal PUBLIC_USER_ID = new BigDecimal(-200);

    /** Public (i.e. a non-logged-in) User object (retrieved by PUBLIC_USER_ID)*/
    private static User s_publicUser;

    /* The Kernel Initial Context object                                      */
    private static KernelContext s_initialContext;

    /* The /actual) Kernel Context object                                     */
    private static ThreadLocal s_context;

    /* The Kernel Configuration object                                        */
    private static KernelConfig s_config;

    /* The Kernel Security Configuration object                               */
    private static SecurityConfig s_securityConfig;

    /** Status flag wether the class is initialized or not.                   */ 
    private static boolean initialized = false;

    /**
     * Internal service routine used by varios methods to initialize the
     * class.
     */
    private static void init() {

        if (initialized) {
            return;
        }
        
        s_initialContext = new KernelContext();
        s_config = new KernelConfig();
        s_securityConfig = new SecurityConfig();
        
        s_initialContext.setLocale(Locale.getDefault());
        s_config.load();
        s_securityConfig.load();
        s_context = new ThreadLocal() {
            @Override
            public Object initialValue() {
                return s_initialContext;
            }
        };
        
        initialized = true;
    }

    /**
     * Provides the Kernel Config object for client classes.
     * @return  ConfigObject
     */
    public static KernelConfig getConfig() {
        init();
        return s_config;
    }

    /**
     * Provides the Kernel Security Config object for client classes.
     * @return SecurityConfigObject
     */
    public static SecurityConfig getSecurityConfig() {
        init();
        return s_securityConfig;
    }

    /**
     * The email address of the built-in system party.
     */
    public static final String SYSTEM_PARTY_EMAIL =
        "acs-system-party@acs-system";
    private static Party s_systemParty = null;

    /**
     * Get the context record of the current thread.
     *
     * @post return != null
     */
    public static KernelContext getContext() {
        init();
        return (KernelContext) s_context.get();
    }

    static void setContext(KernelContext context) {
        init();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Set context to " + context.getDebugInfo());
        }
        s_context.set(context);
    }

    /**
     * Get the system party, the agent of any work the system performs,
     * as apart from what some user or group does. 
     * 
     * Returns null if the system party is not defined.
     */
    public static Party getSystemParty() {
        init();
        return s_systemParty;
    }

    static void setSystemParty(Party party) {
        init();
        s_systemParty = party;
    }

    /**
     * Get the User that represents "the public", i.e. non-logged-in
     * users.
     */
    public static User getPublicUser() {
        init();
        if (s_publicUser == null) {
            // We could synchronize this method, but we don't really care if the
            // User object gets loaded more than once.
            s_publicUser = User.retrieve(PUBLIC_USER_ID);
            // Disconnect the object so we can use it across multiple transactions,
            //  and so it cannot be modified/deleted.
            s_publicUser.disconnect();
        }

        return s_publicUser;
    }
}
