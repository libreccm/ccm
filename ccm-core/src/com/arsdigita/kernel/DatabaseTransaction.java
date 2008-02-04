/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import org.apache.log4j.Logger;

/**
 * Represents a database transaction.
 *
 * @author Justin Ross
 */
public final class DatabaseTransaction {
    public static final String versionId =
        "$Id: DatabaseTransaction.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (DatabaseTransaction.class);

    private final TransactionContext m_context;
    private boolean m_isCommitRequested;

    public DatabaseTransaction() {
        m_context = SessionManager.getSession().getTransactionContext();
        m_isCommitRequested = true;
    }

    /**
     * Starts the transaction if it hasn't already been started.
     */
    public final void begin() {
        if (!m_context.inTxn()) {
            s_log.debug("Beginning transaction");
            m_context.beginTxn();
        } else {
            s_log.debug("Tried to begin, but the transaction was already " +
                        "begun");
        }
    }

    /**
     * Finishes the transaction if it hasn't already ended.  Commits
     * or aborts based on the value of {@link #isCommitRequested()}.
     */
    public final void end() {
        if (isCommitRequested()) {
            commit();
        } else {
            abort();
        }
    }

    /**
     * Commits the transaction if it hasn't already ended.
     */
    public final void commit() {
        if (m_context.inTxn()) {
            s_log.debug("Committing transaction");
            m_context.commitTxn();
        } else {
            s_log.debug("Tried to commit, but the transaction was already " +
                        "finished");
        }
    }

    /**
     * Aborts the transaction if it hasn't already ended.
     */
    public final void abort() {
        if (m_context.inTxn()) {
            s_log.debug("Aborting transaction");
            m_context.abortTxn();
        } else {
            s_log.debug("Tried to abort, but the transaction was already " +
                        "finished");
        }
    }

    /**
     * Tells whether the transaction should be committed or aborted at
     * its end.  Transactions are by default committed.
     */
    public final boolean isCommitRequested() {
        return m_isCommitRequested;
    }

    /**
     * Requests that the transaction be committed or aborted at its
     * end.
     */
    public final void setCommitRequested(final boolean isCommitRequested) {
        m_isCommitRequested = isCommitRequested;
    }

    public String toString() {
        return super.toString() + " " +
            "[" + m_context.inTxn() + "," + isCommitRequested() + "]";
    }
}
