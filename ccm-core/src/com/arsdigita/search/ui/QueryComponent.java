/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.Search;
import com.arsdigita.util.Assert;


/**
 * A base class for generating a query specification
 * from the state. Subclasses must implement two
 * methods, one for getting the query terms,
 * the other for getting a set of filter specs.
 */
public abstract class QueryComponent extends SimpleContainer 
    implements QueryGenerator {
    
    public QueryComponent(String name) {
        setTag(Search.XML_PREFIX + name);
        setNamespace(Search.XML_NS);
    }
    
    /**
     * Determine if a query specification is available
     * @return true if the user has entered some search terms
     */
    public boolean hasQuery(PageState state) {
        String terms = getTerms(state);

        return (terms != null &&
                !"".equals(terms));
    }
    
    /**
     * Returns the current query specification
     */
    public QuerySpecification getQuerySpecification(PageState state) {
        Assert.isTrue(hasQuery(state));

        String terms = getTerms(state);
        
        FilterSpecification[] filters = getFilters(state);
        
        if (filters == null) {
            return new QuerySpecification(terms, true);
        } else {
            return new QuerySpecification(terms, true, filters);
        }
    }
    
    /**
     * Returns the current query terms
     * @return the query terms, or null
     */
    protected abstract String getTerms(PageState state);
    
    /**
     * Returns the current filter specifications
     * @return the list of filter specs
     */
    protected abstract FilterSpecification[] getFilters(PageState state);
}
