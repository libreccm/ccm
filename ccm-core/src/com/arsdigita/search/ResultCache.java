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



/**
 * A simple interface for caching sets
 * @see com.arsdigita.search.CachedResultSet
 */
public interface ResultCache {

    /**
     * Stores a result set in the cache
     * @param query the query to store results for
     * @param results the results to cache
     */
    void put(QuerySpecification query,
             ResultSet results);

    /**
     * Retrieves a result set from the cache
     * @param query the query to get results for
     * @return the cached result set, or null
     */
    ResultSet get(QuerySpecification query);

}
