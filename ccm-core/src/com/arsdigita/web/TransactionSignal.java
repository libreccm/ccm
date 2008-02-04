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
package com.arsdigita.web;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

/**
 * <p>A signal to <code>BaseServlet</code> requesting that the current
 * transaction be committed or aborted.  As with all exceptions,
 * throwing a <code>TransactionSignal</code> stops the execution of
 * currently running code.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TransactionSignal.java 287 2005-02-22 00:29:02Z sskracic $
 */
class TransactionSignal extends Error {
    public static final String versionId =
        "$Id: TransactionSignal.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log = Logger.getLogger(TransactionSignal.class);

    private final boolean m_isCommitRequested;

    TransactionSignal(boolean isCommitRequested) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Constructing a transaction signal with " +
                        "isCommitRequested set " + isCommitRequested);
        }

        m_isCommitRequested = isCommitRequested;

        if (m_isCommitRequested) {
            final Session session = SessionManager.getSession();

            if (session != null && session.getTransactionContext().inTxn()) {
                session.flushAll();
            }
        }
    }

    public final boolean isCommitRequested() {
        return m_isCommitRequested;
    }
}
