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
import com.arsdigita.util.Cache;

import org.apache.log4j.Logger;

/**
 * Implements a timed-expiry result set cache.
 */
public class TimedResultCache implements ResultCache {
    
    private static final Logger s_log = Logger.getLogger(TimedResultCache.class);

    private Cache m_cache;    
    
    private int m_count;

    /**
     * Create a new result cache with room for
     * 'size' entries, valid for a specified
     * maximum age.
     * @param count number of documents per result set to cache
     * @param size max entries to cache
     * @param maxAge maximum age in milliseconds
     */
    public TimedResultCache(int count,
                            long size,
                            long maxAge) {
        m_cache = new Cache(size, maxAge);
        m_count = count;
    }

    /**
     * Stores a result set in the cache
     * @param query the query to store results for
     * @param results the results to cache
     */
    public void put(QuerySpecification query,
                    ResultSet results) {
        Assert.exists(query, QuerySpecification.class);
        Assert.exists(results, ResultSet.class);

        CachedResultSet wrapper = new CachedResultSet(query,
                                                      results,
                                                      m_count);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Storing result for " + query + 
                        " count " + wrapper.getCount() +
                        " time " + wrapper.getQueryTime() + 
                        " engine " + wrapper.getEngine());
        }

        m_cache.put(query, wrapper);
    }
    
    /**
     * Retrieves a result set from the cache
     * @param query the query to get results for
     * @return the cached result set, or null
     */
    public ResultSet get(QuerySpecification query) {
        Assert.exists(query, QuerySpecification.class);

        ResultSet results = (ResultSet)m_cache.get(query);
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Retrieved result for " + query + 
                        (results == null ? " none cached" : (
                        " count " + results.getCount() +
                        " time " + results.getQueryTime() + 
                        " engine " + results.getEngine())));
        }

        return results;
    }
}
