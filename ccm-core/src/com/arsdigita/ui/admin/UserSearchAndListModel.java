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
 * A utility class to support the user search functionality.
 *
 * @version $Id: UserSearchAndListModel.java 1508 2007-03-22 00:04:22Z apevec $
 */

class UserSearchAndListModel implements SearchAndListModel {

    /**
     * A DataQuery used to perform the search
     */

    private DataQuery m_query;

    // data keys
    private static final String USER_ID     = "userID";
    private static final String SCREEN_NAME = "screenName";
    private static final String FIRST_NAME  = "firstName";
    private static final String LAST_NAME   = "lastName";

    private static final String EXCLUDE_GROUP_ID = "excludeGroupId";

    private static final String RETRIEVE_USERS =
        "com.arsdigita.kernel.RetrieveUsers";

    private BigDecimal m_excludedGroupId = new BigDecimal(0);

    /**
     * Specify an exclude group id will return only users that are currently
     * not a member of the group.
     */

    public void setExcludedGroupID(BigDecimal excludeId) {
        m_excludedGroupId = excludeId;
    }

    /**
     * Specify the query to use for searching the user table.
     *
     * @param query is a String that specifies what to search for.
     */

    public void setQuery(String query) {
        StringBuffer sb = new StringBuffer();
        sb.append("searchName like lower('%");
        sb.append(Utilities.prepare(query));
        sb.append("%')");

        Session session = SessionManager.getSession();
        m_query = session.retrieveQuery(RETRIEVE_USERS);
        m_query.setParameter(EXCLUDE_GROUP_ID, m_excludedGroupId);
        m_query.addFilter(sb.toString());
	m_query.addOrder("lower(" + FIRST_NAME + ")");
	m_query.addOrder("lower(" + LAST_NAME + ")");
    }

    /**
     * Get the key (userID) for the current element.
     */

    public String getKey() {
        if (m_query == null) {
            return null;
        }
        return m_query.get(USER_ID).toString();
    }

    /**
     * Get one element from the search result set.  Returns a string
     * containing:
     *
     * <pre>
     *   givenName familyName -- screenName
     * </pre>
     *
     * <p>where <code>screenName</code> is only included if defined.
     * If no query has been defined it returns null.
     *
     * @return the name (person and screen) of the user
     */

    public Object getElement() {
        if (m_query == null) {
            return null;
        }

        // $FIRST_NAME $LAST_NAME -- $SCREEN_NAME
        StringBuffer sb = new StringBuffer();
        sb.append(m_query.get(FIRST_NAME));
        sb.append(" ");
        sb.append(m_query.get(LAST_NAME));

        // only add this if defined
        String screenName = (String) m_query.get(SCREEN_NAME);
        if (screenName != null) {
            sb.append(" -- ").append(screenName);
        }

        return sb.toString();
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
