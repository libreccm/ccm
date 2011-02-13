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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Provides a registry of query engine implementations for
 * various sets of filters.  Application programmers do not need
 * to access instances of this class directly, rather they
 * should use the <code>process</code> method in the <code>Search</code>
 * class.
 *
 * @see com.arsdigita.search.QueryEngine
 */
public class QueryEngineRegistry {

    private static Map s_engines = new HashMap();

    private static final Logger s_log =
        Logger.getLogger(QueryEngineRegistry.class);

    static {
        s_log.debug("Static initalizer starting...");
        registerEngine(IndexerType.NOOP,
                       new FilterType[] {},
                       new NoopQueryEngine());
        s_log.debug("Static initalizer finished.");
    }

    /**
     * Registers a new query engine implementation for an
     * indexer capable of accepting a specific set of filter
     * types.
     * @param indexer the search engine type
     * @param filters the filter types supported
     * @param engine the engine implementation
     */
    public static void registerEngine(IndexerType indexer,
                                      FilterType[] filters,
                                      QueryEngine engine) {
        Assert.exists(indexer, IndexerType.class);

        registerEngine(indexer.getKey(), filters, engine);
    }

    /**
     * Registers a new query engine implementation for an
     * indexer capable of accepting a specific set of filter
     * types.
     * @param indexer the search engine type
     * @param filters the filter types supported
     * @param engine the engine implementation
     */
    public static void registerEngine(String indexer,
                                      FilterType[] filters,
                                      QueryEngine engine) {
        Assert.exists(indexer, String.class);
        Assert.exists(filters, FilterType.class);
        Assert.exists(engine, QueryEngine.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Register engine " + engine.getClass() + " for " + indexer);
            for (int i = 0 ; i < filters.length ; i++) {
                s_log.debug("  Filter: " + filters[i].getKey() + " (" +
                            filters[i].getClass() + ")");
            }
        }

        s_engines.put(new EngineKey(indexer, filters),
                      engine);
    }

    /**
     * Gets the search engine implementation capable of processing
     * the specified set of filters on a given search indexer.
     * @param indexer the search engine type
     * @param filters the filter types requested
     * @return a search engine implementation, or null
     */
    public static QueryEngine getEngine(String indexer,
                                        FilterType[] filters) {
        Assert.exists(indexer, String.class);
        Assert.exists(filters, FilterType.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Lookup engine for " + indexer);
            for (int i = 0 ; i < filters.length ; i++) {
                s_log.debug("  Filter: " + filters[i].getKey() + " (" +
                            filters[i].getClass() + ")");
            }
        }
        QueryEngine engine = null;
        for (Iterator it = s_engines.keySet().iterator(); it.hasNext(); ) {
            EngineKey current = (EngineKey) it.next();
            if (current.containsAllFilters(indexer,filters)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Found match: " + current);
                }
                engine = (QueryEngine) s_engines.get(current);
                break;
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning: " + engine);
        }
        return engine;
    }


    private static class EngineKey {
        private String m_indexer;
        private Set m_filters;

        public EngineKey(String indexer,
                         FilterType[] filters) {
            m_indexer = indexer;
            m_filters = new HashSet();
            for (int i = 0 ; i < filters.length ; i++) {
                m_filters.add(filters[i]);
            }
        }

        public boolean containsAllFilters(String indexer, FilterType[] filters) {
            return m_indexer.equals(indexer)
                && m_filters.containsAll(Arrays.asList(filters));
        }

        public int hashCode() {
            return m_filters.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof EngineKey)) {
                return false;
            }

            EngineKey key = (EngineKey)o;

            if (!m_indexer.equals(key.m_indexer)) {
                return false;
            }

            return m_filters.equals(key.m_filters);
        }

        public String toString() {
            return "EngineKey[indexer: " + m_indexer + ", filters: " + m_filters + "]";
        }
    }
}
