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
package com.arsdigita.persistence;


/**
 * A simple listener to allow tasks to be performed
 * after a transaction is committed or rolled back.
 *
 * A typical use of this listener would be handling
 * repopulation of a data object cache (cf SiteNode).
 *
 * To prevent infinite recursion in the case where
 * the listener itself uses a transaction, listener
 * invocations are one time events - ie the listener
 * is removed immediately after it has run.
 *
 * @see com.arsdigita.persistence.TransactionContext
 * @author Daniel Berrange
 */

public interface TransactionListener {

    /**
     * Called immediately before the transaction has committed
     */
    public void beforeCommit(TransactionContext txn);

    /**
     * Called immediately after the transaction has committed
     */
    public void afterCommit(TransactionContext txn);

    /**
     * Called immediately before the transaction has aborted
     */
    public void beforeAbort(TransactionContext txn);

    /**
     * Called immediately after the transaction has aborted
     */
    public void afterAbort(TransactionContext txn);
}
