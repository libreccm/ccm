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

package com.arsdigita.london.terms;

import com.arsdigita.domain.DomainQuery;

/**
 * This query generates a report summarizing the
 * classified item count per term in the domain.
 * You can retrieve instances of this query by
 * calling Domain#getTermItemCountSummary()
 */
public class TermItemCountQuery extends DomainQuery {

    public static final String QUERY_NAME = 
        "com.arsdigita.london.terms.getTermItemCountSummary";

    public static final String UNIQUE_ID = "uniqueID";
    public static final String NAME = "name";
    public static final String COUNT = "count";

    TermItemCountQuery(Domain domain) {
        super(QUERY_NAME);
        
        setParameter("domain", domain.getKey());
    }
    
    public Integer getUniqueID() {
        return (Integer)get(UNIQUE_ID);
    }

    public String getName() {
        return (String)get(NAME);
    }
    
    public Integer getCount() {
        return (Integer)get(COUNT);
    }
}
