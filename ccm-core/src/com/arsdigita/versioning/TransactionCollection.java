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
package com.arsdigita.versioning;

import com.arsdigita.domain.DomainQuery;
import com.arsdigita.persistence.DataCollection;

// merged versioning

/**
 * A collection of {@link Transaction}s. Instances of this class are returned by
 * various {@link Versions} methods.
 *
 */
public class TransactionCollection extends DomainQuery {

    private DataCollection m_transactions;

    /**
     * Construct a new TransactionCollection.
     *
     * @param transactions A data collection containing the transactions.
     */
    TransactionCollection(DataCollection transactions) {
        super(transactions);
        m_transactions = transactions;
    }

    /**
     * Return the current transaction
     */
    public Transaction getTransaction() {
        return new Transaction(m_transactions.getDataObject());
    }

}
