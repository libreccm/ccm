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
 */

package com.arsdigita.london.util;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;


/**
 * <p>
 * A class for safely running a piece of code within
 * a dedicated database transaction. It ensures that
 * if any exceptions are thrown, the transaction is
 * safely aborted.
 * </p>
 * <p>
 * Example usage is as follows:
 * </p>
 * <pre>
 *   Transaction txn = new Transaction() {
 *        protected void doRun() {
 *           ...do some stuff...
 *        }
 *   };
 *   txn.run();
 * </pre>
 * <p>
 * This class implements the Runnable interface, so if 
 * this is intended to be used for a background
 * thread, then instead of calling txn.run() the following
 * code can be used:
 * <pre>
 *   Thread thread = new Thread(new Transaction() {
 *        protected void doRun() {
 *           ...do some stuff...
 *        }
 *   });
 *   thread.start();
 * </pre>
 * 
 * @see com.arsdigita.persistence.SessionManager
 * @see com.arsdigita.persistence.TransactionContext
 */
public abstract class Transaction implements Runnable {
    
    private TransactionContext m_context;
    
    /**
     * Create a new transaction using the default persistence
     * session.
     */
    public Transaction() {
        m_context = SessionManager.getSession().getTransactionContext();
    }

    /**
     * Create a new transaction using the named persistence
     * session. The named session must have been configured
     * prior to creating this object
     *
     * @param session the name of the persistence session
     */
    public Transaction(String session) {
        m_context = SessionManager.getSession(session).getTransactionContext();
    }
    
    /**
     * Starts a new transaction, invokes the doRun
     * method and then commits / aborts the transaction
     */
    public final void run() {
        try {
            m_context.beginTxn();
            
            doRun();

            m_context.commitTxn();
        } finally {
            if (m_context.inTxn()) {
                m_context.abortTxn();
            }
        }
    }
    
    /**
     * Subclasses should implement this method to do whatever
     * work they require.
     */
    protected abstract void doRun();
}
