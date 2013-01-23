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
import java.math.BigInteger;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Provide access to the JSESSIONID value as a BigDecimal.  This class
 * used to secure the JSESSIONID using a cryptographic hash, but we
 * have decided to rely on the web container to secure the JSESSIONID
 * because the web container is responsible for setting JSESSIONID
 * anyway.  We cannot simply remove this class because
 * KernelRequestContext has a getSessionContext method and is "Stable".
 *
 * @author Rob Mayoff
 * @version $Id: SessionContext.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SessionContext {

    private static final Logger s_log =
        Logger.getLogger(SessionContext.class.getName());

    private HttpServletRequest m_req;
    private BigDecimal m_sessionID;

    /**
     * Creates a session context from an HTTP request.  Package-private.
     * Code should access this class via
     * KernelHelper.getKernelRequestContext(req).getSessionContext().
     **/
    SessionContext(HttpServletRequest req,
                   HttpServletResponse res) {
        m_req = req;
    }

    /**
     * Returns the current session ID.
     *
     * @return the session ID.
     **/
    public BigDecimal getSessionID() {
        if (m_sessionID == null)
            throw new IllegalStateException("session ID is not defined");
        return m_sessionID;
    }

    /**
     * Converts the session ID to a BigDecimal for backward
     * compatibility.
     * Package-private.
     *
     * @throws LoginException if unable to load session ID.
     **/
    void loadSessionID(CallbackHandler handler) throws LoginException {
        s_log.debug("START loadSessionID()");
        try {
            m_sessionID = new BigDecimal
                (new BigInteger(1, Util.getRequest(handler)
                                .getSession().getId().getBytes(Crypto.CHARACTER_ENCODING)));
        } catch (java.io.UnsupportedEncodingException e) {
            throw new KernelLoginException("Could not get sessionID: ", e);
        }
        s_log.debug("sessionID = " + m_sessionID);
    }
}
