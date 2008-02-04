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

package com.arsdigita.london.search;


import com.arsdigita.search.QueryEngine;
import com.arsdigita.search.Search;
import com.arsdigita.search.ResultSet;
import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.FilterSpecification;



/**
 * This provides the basic remote search query engine
 * @see com.arsdigita.search.QueryEngine
 */
public class RemoteQueryEngine implements QueryEngine {

    /*
     * Processes a query specification generating a document
     * result set
     * @param spec the query specification
     * @return the document result set
     */
    public ResultSet process(QuerySpecification spec) {
        String terms = spec.getTerms();
        
        if (terms == null || "".equals(terms)) {
            return Search.EMPTY_RESULT_SET;
        }
        
        FilterSpecification[] filters = spec.getFilters();
        if (filters.length != 1 ||
            !filters[0].getType().equals(new HostFilterType())) {
            throw new RuntimeException(
                "Only the 'Host' filter type is supported");
        }
        
        HostFilterSpecification hosts = (HostFilterSpecification)filters[0];
        
        if (terms != null && !"".equals(terms)) {
            SearchGroup search = new SearchGroup(SearchJobQueue.getInstance(),
                                                 terms,
                                                 hosts.getServers());
            return new RemoteResultSet(search);
        } else {
            return Search.EMPTY_RESULT_SET;
        }
    }
}
