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
package com.arsdigita.persistence;

import com.arsdigita.util.Assert;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Title:       TransactionContext class
 *              This class is intentionally NOT threadsafe;
 *              it should not be shared across threads.
 * Description: The TransactionContext class encapsulates a database transaction.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: TransactionContext.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class TransactionContext {
    private static final Logger s_cat =
        Logger.getLogger(TransactionContext.class);

    private Session m_ossn;
    // used in test infrastructure
    com.redhat.persistence.Session m_ssn;
    private Map m_attrs = new HashMap();
    private ArrayList m_listeners = new ArrayList();
    private boolean m_inTxn = false;

    TransactionContext(com.arsdigita.persistence.Session ssn) {
        m_ossn = ssn;
        m_ssn = ssn.getProtoSession();
    }

    /**
     * Begins a new transaction.
     *
     * Update 8/7/01: This now makes a connection available, but doesn't
     * actually open a connection and associate it with the thread.
     * The 'transaction' will not actually start until the first data
     * modification, at which point the connection will be married to the
     * thread.
     *
     * This should be a transparent behavior change introduced as a
     * performance optimization, SDM #159142.
     **/

    public void beginTxn() {
        s_cat.debug("Beginning transaction..."); 
        // Do nothing. This is implicit now.
        if (m_inTxn) {
            throw new IllegalStateException("double begin");
        }

        m_inTxn = true;
    }

    /**
     * Commits the current transaction.
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void commitTxn() {
        s_cat.debug("Commiting transaction...");
        boolean success = false;
        try {
            fireBeforeCommitEvent();
            m_ssn.flush();
            m_ossn.invalidateDataObjects(true, false);
            m_ssn.commit();
            success = true;
            m_inTxn = false;
            fireCommitEvent();
            s_cat.debug("Done.");       
        } finally {
            s_cat.debug("Cleaning up...");           
            clearAttributes();
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
             if (m_inTxn) {
                s_cat.warn("Warning: Cleanup after commit was reached, but m_inTxn is true.");
            }
        }
    }

    /**
     * Used by test infrastructure. Replaces the actual commit with a
     * specified runnable.
     */
    void testCommitTxn(Runnable r) {
        boolean success = false;
        try {
            fireBeforeCommitEvent();
            m_ssn.flush();
            m_ossn.invalidateDataObjects(true, false);
            r.run();
            success = true;
            m_inTxn = false;
            fireCommitEvent();
        } finally {
            clearAttributes();
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
        }
    }

    /**
     * Aborts the current transaction.
     *
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void abortTxn() {
        s_cat.warn("Aborting transaction...");
        boolean success = false;
        try {
            try {
                fireBeforeAbortEvent();
                m_ossn.invalidateDataObjects(false, false);
            } finally {
                m_ssn.rollback();
                m_inTxn = false;
            }
            success = true;
        } finally {
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
            fireAbortEvent();
            clearAttributes();
        }
        s_cat.warn("Transaction aborted.");
    }

    /**
     * Register a one time transaction event listener
     */
    public void addTransactionListener(TransactionListener listener) {
        m_listeners.add(listener);
    }

    /**
     * Unregister a transaction event listener. There is
     * generally no need to call this method, since transaction
     * listeners are automatically removed after they have been
     * invoked to prevent infinite recursion.
     */
    public void removeTransactionListener(TransactionListener listener) {
        m_listeners.remove(listener);
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * before the transaction
     */
    private void fireBeforeCommitEvent() {
        Assert.isTrue
	    (inTxn(), "The beforeCommit event was fired outside of " +
	     "the transaction");

        Object listeners[] = m_listeners.toArray();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction beforeCommit event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.beforeCommit(this);
        }
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * after the transaction
     */
    private void fireCommitEvent() {
        Assert.isTrue
	    (!inTxn(), "transaction commit event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction commit event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterCommit(this);
        }

        Assert.isTrue
	    (!inTxn(), "transaction commit listener didn't close transaction");
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * before the transaction
     */
    private void fireBeforeAbortEvent() {
        Assert.isTrue
	    (inTxn(), "The beforeAbort event was fired outside of " +
	     "the transaction");

        Object listeners[] = m_listeners.toArray();
        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction beforeAbort event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.beforeAbort(this);
        }
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * after the transaction
     */
    private void fireAbortEvent() {
        Assert.isTrue
	    (!inTxn(), "transaction abort event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction abort event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterAbort(this);
        }

        Assert.isTrue
	    (!inTxn(), "transaction abort listener didn't close transaction");
    }

    /**
     * Returns true if there is currently a transaction in progress.
     *
     * @return True if a transaction is in progress, false otherwise.
     **/

    public boolean inTxn() {
        return m_inTxn;
    }

    /**
     * Returns the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     *
     * @return The isolation level of the current transaction.
     **/

    public int getTransactionIsolation() {
        try {
            Connection conn = m_ossn.getConnection();
	    return conn.getTransactionIsolation();
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    /**
     * Sets the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     * @post getTransactionIsolation() == level
     *
     * @param level The desired isolation level.
     **/
    public void setTransactionIsolation(int level) {
        try {
            Connection conn = m_ossn.getConnection();
	    conn.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    /**
     * Set an attribute inside of this <code>TransactionContext</code>.  The
     * attribute will exist as long as the transaction is opened.  When the
     * transaction is closed or aborted, the attribute will be discarded. This
     * method is analogous to {@link
     * javax.servlet.ServletRequest#setAttribute(String, Object)}
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @post getAttribute(name) == value
     */
    public void setAttribute(String name, Object value) {
        m_attrs.put(name, value);
    }

    /**
     * Get an attribute inside of this <code>TransactionContext</code>.  The
     * attribute will exist as long as the transaction is opened.  When the
     * transaction is closed or aborted, the attribute will be discarded. This
     * method is analogous to {@link
     * javax.servlet.ServletRequest#getAttribute(String)}
     *
     * @param name the name of the attribute
     * @return the value of the attribute, or null if no attribute with
     *   this value has been stored
     */
    public Object getAttribute(String name) {
        return m_attrs.get(name);
    }

    /**
     * Remove an attribute from this <code>TransactionContext</code>.  be
     * discarded. This method is analogous to {@link
     * javax.servlet.ServletRequest#removeAttribute(String)}
     *
     * @param name the name of the attribute to remove
     * @post getAttribute(name) == null
     */
    public void removeAttribute(String name) {
        m_attrs.remove(name);
    }

    void clearAttributes() {
        m_attrs.clear();
    }

}
