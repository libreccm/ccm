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

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * <p>The entry point into all the global state that CCM code expects to
 * have available to it when running, e.g. the current user, the
 * current resource, etc.</p>
 *
 * <p>This is a session object that provides an environment in which
 * code can execute. The KernelContext contains all session-specific
 * variables.  One session object is maintained per thread.</p>
 *
 * <p>Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.</p>
 *
 * @author Rafael Schloming
 * @author Richard Li
 * @author Justin Ross
 * @see com.arsdigita.kernel.Kernel
 * @see com.arsdigita.kernel.KernelExcursion
 * @version $Id: KernelContext.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class KernelContext {

    private static final Logger s_log = Logger.getLogger(KernelContext.class);

    private Resource m_resource = null;
    private Party m_party = null;
    private Party m_effectiveParty = null;
    private Locale m_locale = null;
    private String m_sessionID = null;
    private DatabaseTransaction m_transaction = null;

    KernelContext() {
        // Empty.
    }

    public final String getDebugInfo() {
        String info = "Current state of " + this + ":\n" +
            "           getResource() -> " + getResource() + "\n" +
            "              getParty() -> " + getParty() + "\n" +
            "     getEffectiveParty() -> " + getEffectiveParty() + "\n" +
            "             getLocale() -> " + getLocale() + "\n" +
            "          getSessionID() -> " + getSessionID() + "\n" +
            "        getTransaction() -> " + getTransaction();

        return info;
    }

    final KernelContext copy() {
        KernelContext result = new KernelContext();

        result.m_resource = m_resource;
        result.m_party = m_party;
        result.m_effectiveParty = m_effectiveParty;
        result.m_locale = m_locale;
        result.m_sessionID = m_sessionID;
        result.m_transaction = m_transaction;

        return result;
    }

    /**
     * @return the currently selected resource.
     */
    public final Resource getResource() {
        return m_resource;
    }

    final void setResource(Resource resource) {
        m_resource = resource;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Resource set to " + resource);
        }
    }

    /**
     * @return the party that is actually executing the session.
     */
    public final Party getParty() {
        return m_party;
    }
    /**
     * Returns the current user.
     * Backwards compatibility method which returns a user object. Developers
     * should use getParty whenever possible (party is an abstraction of users
     * as well as groups).
     */
    public static User getUser() {
        KernelContext kernelContext = Kernel.getContext();
        if ( kernelContext.getParty() instanceof User ) {
            return (User) kernelContext.getParty();
        } else {
            return null;
        }
    }

    final void setParty(Party party) {
        m_party = party;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Party set to " + party);
        }
    }

    /**
     * The effective party is the party under which you want a
     * particular set of operations to take place. This is useful when
     * you are running code under a context but need part of that code
     * to run under the guise of a different user (for example,
     * PUBLIC, ADMIN).
     *
     * @return the party assuming the role of the current party.
     */
    public final Party getEffectiveParty() {
        if (m_effectiveParty == null) {
            return m_party;
        } else {
            return m_effectiveParty;
        }
    }

    final void setEffectiveParty(Party party) {
        m_effectiveParty = party;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Effective party set to " + m_effectiveParty);
        }
    }

    /**
     * @return the locale for the current session
     */
    @Deprecated 
    /** use {@link DispatcherHelper.getNegotiatedLocale()} instead */
    public final Locale getLocale() {
        return m_locale;
    }

    final void setLocale(Locale locale) {
        m_locale = locale;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Locale set to " + m_locale);
        }
    }

    /**
     * @return the ID of the current session.
     */
    public final String getSessionID() {
        return m_sessionID;
    }

    final void setSessionID(String sessionID) {
        m_sessionID = sessionID;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Session ID set to " + m_sessionID);
        }
    }

    /**
     * @return the current database transaction.
     */
    public final DatabaseTransaction getTransaction() {
        return m_transaction;
    }

    final void setTransaction(final DatabaseTransaction transaction) {
        m_transaction = transaction;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Transaction set to " + m_transaction);
        }
    }
}
