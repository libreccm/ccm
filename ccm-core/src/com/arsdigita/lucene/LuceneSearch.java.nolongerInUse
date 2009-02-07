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
package com.arsdigita.lucene;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;

/**
 * @deprecated moved to com.arsdigita.search.lucene
 * @see com.arsdigita.search.lucene.LuceneSearch
 **/
public class LuceneSearch 
    extends com.arsdigita.search.lucene.LuceneSearch {

    /**
     * Search over all objects in the system. Returns objects that matches
     * the search string.
     *
     * @param searchString user specified search string
     **/
    public LuceneSearch(String searchString) {
        super(searchString);
    }

    /**
     * Search for a specific ACS object and search string.
     *
     * @param searchString user specified search string
     * @param objectType ACS object type
     **/
    public LuceneSearch(String searchString, String objectType) {
        super(searchString, objectType);
    }

    /**
     * Search over all objects in the system using a filter
     *
     * @param searchString user specified search string
     * @param f a filter
     **/
    public LuceneSearch(String searchString, Filter f) {
        super(searchString, f);
    }

    /**
     * Search given a preformed query.
     *
     * @param q a performed query
     **/
    public LuceneSearch(Query q) {
        super(q);
    }
}
