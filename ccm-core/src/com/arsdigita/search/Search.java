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
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.Collections;


import org.apache.log4j.Logger;


/**
 * The Search class provides an application level API
 * for querying search indexes. It implements transparent
 * caching of search results across requests for performance.
 * Applications would typically present a form to the user
 * allowing them to enter a query specification. They 
 * would then invoke the <code>process</code> method to 
 * obtain a (optionally cached) document result set. For
 * here the <code>getResults</code> method enables easy
 * pagination of documents. An example interaction may
 * look like:
 *
 * <pre>
 *   QuerySpecification querySpec = getQuerySpec(request);
 *   ResultSet results = Search.process(querySpec);
 * 
 *   int pageNum = request.getParameter("pageNum");
 *   Iterator onePage = results.getResults(pageNum*PAGE_SIZE, PAGE_SIZE);
 *   while (onePage.hasNext()) {
 *     Document doc = (Document)onePage.next();
 *     ... do something with doc ...
 *   }
 * </pre>
 */
public class Search {

    public static final Logger s_log = Logger.getLogger(Search.class);

    private static SearchConfig s_config;

    /**
     * A document result set containing no results
     */
    public static final ResultSet EMPTY_RESULT_SET =
        new EmptyResultSet();

    /**
     * The default search result cache lifetime, in milliseconds
     */
    public static final long CACHE_LIFETIME = 60 * 5 * 1000l;

    /**
     * The default search result cache size
     */
    public static final long CACHE_SIZE = 50;

    /**
     * The default search result cache size
     */
    public static final int CACHE_DOCUMENT_COUNT = 50;


    /**
     * A result cache which doesn't do any caching
     */
    public static final ResultCache NOP_RESULT_CACHE =
        new NopResultCache();

    public static final ResultCache DEFAULT_RESULT_CACHE =
        new TimedResultCache(CACHE_DOCUMENT_COUNT,
                             CACHE_SIZE,
                             CACHE_LIFETIME);
    
//  /**
//   * Constant for intermedia search indexer
//   * @deprecated use IndexerType.INTERMEDIA.getKey()
//   */
//  public static final String INDEXER_INTERMEDIA =
//      IndexerType.INTERMEDIA.getKey();

//  /**
//   * Constant for lucene search indexer
//   * @deprecated use IndexerType.LUCENE.getKey()
//   */
//  public static final String INDEXER_LUCENE =
//      IndexerType.LUCENE.getKey();
    
    /**
     * Constant for serach XML namespace prefix
     */
    public static final String XML_PREFIX = "search:";
    /**
     * Constant for search XML namespace URL
     */
    public static final String XML_NS = "http://rhea.redhat.com/search/1.0";
    
    /**
     * Retrieves the current serach configuration
     */
    public static SearchConfig getConfig() {
        if (s_config == null) {
            s_config = new SearchConfig();
            s_config.load();
        }
        return s_config;
    }
    
    /**
     * Processes a query specification, returning a
     * cached document result set. The query is processed
     * using the currently configured query engine.
     *
     * @param spec the query specification
     * @return cached search result set
     */
    public static ResultSet process(QuerySpecification spec) {
        return process(spec, 
                       DEFAULT_RESULT_CACHE,
                       getConfig().getIndexer());
    }

    /**
     * Processes a query specification, returning a
     * cached document result set. The query is processed
     * using the currently configured query engine.
     *
     * @param spec the query specification
     * @param cache the result cache to use
     * @return cached search result set
     */
    public static ResultSet process(QuerySpecification spec,
                                    ResultCache cache) {
        return process(spec, 
                       cache,
                       getConfig().getIndexer());
    }

    /**
     * Processes a query specification, returning a
     * cached document result set. The query is processed
     * using the requested query engine.
     *
     * @param spec the query specification
     * @param cache the result cache to use
     * @param engine the query engine to use
     * @return cached search result set
     */
    public static ResultSet process(QuerySpecification spec,
                                    ResultCache cache,
                                    String engine) {
        Assert.exists(spec, QuerySpecification.class);
        Assert.exists(cache, ResultCache.class);
        Assert.exists(engine, String.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing " + spec + 
                        " using cache " + cache + 
                        " on " + engine);
        }
        
        ResultSet results = cache.get(spec);
        
        if (results == null) {
            ResultSet rawResults = processInternal(spec, engine);
            try {
	            Assert.exists(rawResults, ResultSet.class);
	            cache.put(spec, rawResults);
	            // Re-fetch results, since cache may wrap them
	            results = (ResultSet) cache.get(spec);
	            if (results == null) {
	                results = rawResults;
	            }
            } finally {
            	// Free any resources used by the rawResults set.  
                if(rawResults != null && !rawResults.equals(results)) {//we should close it when isn't used anymore
                	try {
                    	rawResults.close();
            } catch(Exception e) {
                		// If there is a problem with closing the rawresults set, it is probably not
                		// fatal (ie close has been called elsewhere).  We write a line to the error log
                		// but otherwise ignore the exception allowing the code to continue normally.  Any
                		// truly unexpected issues will therefore emerge in the log.
                		s_log.error("Error closing rawresults: "+e.getMessage());
                }
            }
            }
        }
        
        return results;
    }

    /**
     * Processes a query specification, returning a
     * document result set with no caching. The query is processed
     * using the requested query engine. Applications should
     * generally use one of the process methods which allows
     * caching of result sets for greater performance.
     *
     * @param spec the query specification
     * @param engine the query engine to use
     * @return search result set
     */
    public static ResultSet processInternal(QuerySpecification spec,
                                            String engine) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing query specification " + spec);
        }

        FilterSpecification[] filters = spec.getFilters();
        FilterType[] types = new FilterType[filters.length];
        for (int i = 0 ; i < filters.length ; i++) {
            types[i] = filters[i].getType();
        }
        
        QueryEngine engineImpl = QueryEngineRegistry
            .getEngine(engine,
                       types);
        
        if (engineImpl == null) {
            throw new RuntimeException(
                "cannot find engine to process query " + spec +
                " on indexer '" + engine + "'");
        }
        
        return engineImpl.process(spec);
    }
    
    /**
     * Creates a new element in the search XML namespace,
     * prepending the default search namespace prefix.
     * So, calling newElement('foo'), creates an element
     * with a name 'search:foo', where 'search:' is bound
     * to the namespace 'http://rhea.redhat.com/search/1.0'
     */
    public static Element newElement(String name) {
        return new Element(XML_PREFIX + name,
                           XML_NS);
    }
    
    private static class EmptyResultSet implements ResultSet {
        public Iterator getDocuments(long offset,
                                     long count) {
            return Collections.EMPTY_LIST.iterator();
        }

        public String getEngine() {
            return getConfig().getIndexer();
        }
    
        public long getCount() {
            return 0l;
        }
        
        public long getQueryTime() {
            return 0l;
        }
        
        public void close() {} // no-op
    }

    private static class NopResultCache implements ResultCache {
        public void put(QuerySpecification query,
                        ResultSet results) {
            // no-op
        }
        public ResultSet get(QuerySpecification query) {
            return null;
        }
    }
}
