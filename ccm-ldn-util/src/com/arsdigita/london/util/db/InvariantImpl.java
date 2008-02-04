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

package com.arsdigita.london.util.db;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.GenericDataQuery;
import com.arsdigita.util.Assert;


import org.apache.log4j.Logger;

public class InvariantImpl implements Invariant {

    private static final String RESULT = "result";

    private static final Logger s_log = Logger.getLogger(InvariantImpl.class);
    
    private String m_description;
    private String m_query;
    private String m_expected;

    InvariantImpl(String description,
                  String query,
                  String expected) {
        Assert.exists(query, String.class);
        Assert.exists(expected, String.class);

        m_description = description;
        m_query = query;
        m_expected = expected;
    }
    
    public String getDescription() {
        return m_description;
    }

    public void check() throws
        InvariantViolationException {

        s_log.info("Validate " + m_query);

        DataQuery query = new GenericDataQuery(
            SessionManager.getSession(),
            m_query,
            new String[] { RESULT });
        
        if (!query.next()) {
            throw new InvariantViolationException(
                m_query,
                "No rows returned by database");
        }
        
        Object res = query.get(RESULT);
        String value = res == null ? null : res.toString();
        if (!m_expected.equals(value)) {
            query.close();
            throw new InvariantViolationException(
                m_query,
                "Count " + value + " not equal to " + m_expected);
        }
        
        if (query.next()) {
            throw new InvariantViolationException(
                m_query,
                "Too many rows returned by database");
        }
    }
}
