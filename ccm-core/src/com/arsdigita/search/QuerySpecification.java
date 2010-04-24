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
package com.arsdigita.search;

import com.arsdigita.util.Assert;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents the set of parameters for a search query.
 * This is a combination of a search string and a number
 * of filter specifications. The search terms string is
 * considered opaque and passed straight through to the
 * underlying QueryEngine implementation. Thus the 
 * operators it supports (AND, OR, *, etc) are implementation
 * defined
 * 
 * @see com.arsdigita.search.FilterSpecification
 */
public class QuerySpecification {
    
    private String m_terms;
    private boolean m_partial;
    private List m_filters;

    /**
     * Creates a query specification for a simple search
     * string, with no filters
     * @param terms the raw search string
     */
    public QuerySpecification(String terms,
                              boolean partial) {
        this(terms, partial, new FilterSpecification[0]);
    }

    /**
     * Creates a query specification for a simple search
     * string, with a number of filters
     * @param terms the raw search string
     * @param filters a number of filter specifications
     */
    public QuerySpecification(String terms,
                              boolean partial,
                              FilterSpecification[] filters) {
        Assert.exists(terms, String.class);
        Assert.exists(filters, FilterSpecification.class);

        m_terms = terms;
        m_partial = partial;
        m_filters = new ArrayList();

        for (int i = 0 ; i < filters.length ; i++) {
            addFilter(filters[i]);
        }
    }

    public void addFilter(FilterSpecification filter) {
        m_filters.add(filter);
    }
    
    /**
     * Returns the raw search string
     * @return the search string
     */
    public String getTerms() {
        return m_terms;
    }
    
    /**
     * Returns flag indicating whether 
     * partial (ie substring) matches
     * should be allowed.
     * @return true if partial matches are allowed, false otherwise
     */
    public boolean allowPartialMatch() {
        return m_partial;
    }

    /**
     * Returns the filter specifications
     * @return the filter specifications
     */
    public FilterSpecification[] getFilters() {
        return (FilterSpecification[])
            m_filters.toArray(new FilterSpecification[m_filters.size()]);
    }
    
    /**
     * Returns a hashcode compatible with the
     * definition of the equals() method
     */
    public int hashCode() {
        // XXX lets incorporate hash codes from m_filters ?
        return m_terms.hashCode();
    }
    
    /**
     * Two query specifications compare equal if they
     * have the same search term, have the same setting
     * for partial matches and contain the same set of
     * filter specifications.
     */
    public boolean equals(Object o) {
        if (!(o instanceof QuerySpecification)) {
            return false;
        }
        QuerySpecification spec = (QuerySpecification)o;
        
        if (!spec.getTerms().equals(m_terms)) {
            return false;
        }
        if (m_partial != spec.m_partial) {
            return false;
        }
        
        return m_filters.equals(spec.m_filters);
    }
    
    
    public String toString() {
        StringBuffer str = new StringBuffer(super.toString());
        str.append("\n");
        str.append("Specification: {\n");
        str.append("  Terms: " + m_terms + "\n");
        str.append("  Filters:\n");
        for (int i = 0 ; i < m_filters.size() ; i++) {
            str.append("    " + m_filters.get(i).toString() + "\n");
        }
        str.append("}");
        
        return str.toString();
    }
}
