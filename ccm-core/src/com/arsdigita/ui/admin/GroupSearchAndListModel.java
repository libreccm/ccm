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
package com.arsdigita.ui.admin;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;


import java.math.BigDecimal;

/**
 * A utility class to support group search functionality.
 *
 * @version $Id: GroupSearchAndListModel.java 1508 2007-03-22 00:04:22Z apevec $
 */

class GroupSearchAndListModel implements SearchAndListModel {

    /**
     * A DataQuery used to perform the search
     */

    private DataQuery m_query;

    // Query for group search.

    private static final String GROUP_ID =
        "groupID";
    private static final String GROUP_NAME =
        "name";
    private static final String RETRIEVE_GROUPS =
        "com.arsdigita.kernel.RetrieveGroups";

    // Group to exclude from the search

    private BigDecimal m_excludedGroupID = new BigDecimal(0);

    /**
     * Specify the query to use for searching the groups table.
     *
     * @param query is a String that specifies what to search for.
     */

    public void setQuery (String query) {
        StringBuffer sb = new StringBuffer();
        sb.append("searchName like lower('%");
        sb.append(Utilities.prepare(query));
        sb.append("%')");

        Session session = SessionManager.getSession();
        m_query = session.retrieveQuery(RETRIEVE_GROUPS);
        m_query.addFilter(sb.toString());

        if (m_excludedGroupID != null) {
            m_query.addFilter("groupID != " + m_excludedGroupID);
        }
    }

    /**
     * This defines a group to be excluded from the search.
     */

    public void setExcludedGroupID(BigDecimal id) {
        m_excludedGroupID = id;
    }

    /**
     * Get the key (groupID) for the current element.
     */

    public String getKey() {
        return m_query == null ? null : m_query.get(GROUP_ID).toString();
    }

    /**
     * Get one element from the search result set
     */

    public Object getElement() {
        return m_query == null ? null : m_query.get(GROUP_NAME);
    }

    /**
     * Iterate to the next result of the query.  Returns true if there
     * are more results to process.
     */

    public boolean next() {

        if (m_query == null) {
            return false;
        }

        // Once this result set reaches the end, you need to set
        // m_query = null, otherwise you will get an exception.

        boolean ret = m_query.next();

        if (ret == false) {
            m_query = null;
        }

        return ret;
    }
}
