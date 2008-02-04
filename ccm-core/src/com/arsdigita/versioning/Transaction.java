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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.math.BigInteger;



// merged versioning

/**
 * Transaction
 *
 * @version $Id: Transaction.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class Transaction {
    public static Transaction retrieve(BigInteger id) {
        Session ssn = SessionManager.getSession();
        return new Transaction
            (ssn.retrieve(new OID(Constants.TXN_DATA_TYPE, id)));
    }

    private DataObject m_txn;
    private DataAssociation m_tags;

    Transaction(DataObject txn) {
        m_txn = txn;
        m_tags = (DataAssociation) m_txn.get(Constants.TAGS);
    }

    /**
     * @return the BigInteger ID of this transaction
     */

    public BigInteger getID() {
        return (BigInteger) m_txn.get(Constants.ID);
    }

    public TagCollection getTags() {
        return new TagCollection(m_tags.cursor());
    }

    /**
     * Get the user who created this transaction
     */
    public User getUser() {
        DataObject user = (DataObject) m_txn.get(Constants.MOD_USER);
        return (User) DomainObjectFactory.newInstance(user);
    }

    /**
     * Get the creation date of this transaction
     */
    public java.util.Date getTimestamp() {
        return (java.util.Date) m_txn.get(Constants.TIMESTAMP);
    }

    /**
     * Get the IP of the user who created this transaction
     */
    public String getIP() {
        return (String) m_txn.get(Constants.MODIFYING_IP);
    }

    /**
     * Returns a String representation of this transaction, useful for
     * debugging.
     *
     * @return a String representation of this transaction.
     */

    public String toString() {
        return m_txn.toString();
    }
}
