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
package com.arsdigita.versioning;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;
import java.util.Date;

// old versioning

/**
 * Encapsulates a query that returns all the tagged transactions
 * for an object. This query can be used to render a history window
 * for the object.
 *
 * @author Stanislav Freidin
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 */
public class HistoryCollection extends TransactionCollection {
    private static final String HISTORY_QUERY =
        Constants.PDL_MODEL + ".objectHistoryQuery";
    public static final String TRANS_ID     = "transID";
    public static final String TRANS        = "change";
    public static final String TIMESTAMP    = "timestamp";
    public static final String EMAIL        = "userEmail";
    public static final String COMMENT      = "comment";
    public static final String SHOW_CURRENT = "showCurrent";
    public static final String DUMMY_ID     = "dummyID";

    public static final BigDecimal DUMMY_ID_VALUE = new BigDecimal(-42);

    private DataQuery m_query;
    private int m_version;

    /**
     * Retrieve version history for the given object; retrieves
     * all tagged transactions in reverse chronological order
     * @param obj the object
     * @param showCurrent true if the draft version of the object should
     *   be appended at the end of the history, false otherwise
     */
    public static HistoryCollection getHistory(
        VersionedACSObject obj, boolean showCurrent
    ) {
        DataQuery q = SessionManager.getSession().retrieveQuery(HISTORY_QUERY);
        q.setParameter(DUMMY_ID, DUMMY_ID_VALUE);
        q.setParameter(SHOW_CURRENT, new Boolean(showCurrent));
        return new HistoryCollection(q);
    }

    /**
     * Encapsulates the given query.
     */
    protected HistoryCollection(DataQuery q) {
        super(new DataQueryDataCollectionAdapter(q, TRANS));
        m_query = q;
        m_version = 0;
    }

    public boolean next() {
        boolean next = super.next();
        if (next) {
            m_version++;
        }
        return next;
    }

    /**
     * Returns the current transaction.
     */
    public Transaction getTransaction() {
        DataObject trans = (DataObject)m_query.get(TRANS);
        return new Transaction(trans);
    }

    /**
     * Returns the current transaction ID; useful for generating URLs
     */
    public BigDecimal getTransactionID() {
        return (BigDecimal)m_query.get(TRANS_ID);
    }

    /**
     * Returns the current user's email.
     */
    public String getUserEmail() {
        return (String)m_query.get(EMAIL);
    }

    /**
     * Returns the current version number.
     */
    public BigDecimal getVersion() {
        return new BigDecimal(m_version);
    }

    /**
     * Returns the creation date of the current version.
     */
    public Date getTimestamp() {
        return (Date)m_query.get(TIMESTAMP);
    }

    /**
     * Returns the transaction tag, which counts as the comment for the
     * transaction.
     */
    public String getComment() {
        return (String)m_query.get(COMMENT);
    }

    /**
     * Get the internal data query; useful for data tables
     */
    public DataQuery getQuery() {
        return m_query;
    }

    /**
     * @deprecated with no replacement
     */
    public static boolean isDummyID(BigDecimal id) {
        return (DUMMY_ID_VALUE.equals(id));
    }
}
