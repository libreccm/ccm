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

import java.util.Iterator;
import java.util.Collections;

import org.apache.log4j.Logger;

public class CachedResultSet implements ResultSet {
    
    private static Logger s_log = Logger.getLogger(CachedResultSet.class);

    private QuerySpecification m_spec;
    private long m_count;
    private long m_time;
    private Document[] m_results;
    private String m_engine;
    private ResultSet m_realResultSet = null;

    public CachedResultSet(QuerySpecification spec,
                           ResultSet results,
                           int size) {
        m_spec = spec;
        m_count = results.getCount();
        m_time = results.getQueryTime();
        m_engine = results.getEngine();

        if (m_count < size) {
            s_log.info("Cache size: " + size + 
                       ", is greater than result count: " + m_count);
            size = (int)m_count;
        }

        m_results = new Document[size];
        
        Iterator resultsIter = size == 0 ?
            Collections.EMPTY_LIST.iterator() :
            results.getDocuments(0, size);
        int i = 0;
        while (resultsIter.hasNext() && i < size) {
            m_results[i++] = (Document)resultsIter.next();
        }
        if (i != size) {
            s_log.warn("Result count: " + i + 
                       ", was smaller than expected: " + size);
            size = i;
            Document cachedResults[] = new Document[size];
            for (i = 0 ; i < size ; i++) {
                cachedResults[i] = m_results[i];
            }
            m_results = cachedResults;
        }
        results.close();
    }
    
    /**
     * Gets an iterator for the page of results between
     * <code>offset</code> and <code>offset+count</code>
     *
     * @param offset the first hit, starting from 0
     * @param count the maximum number of results to return
     * @return an iterator of Document objects
     * @throws java.lang.IllegalOperationException if close has been called
     */
    public Iterator getDocuments(long offset,
                                 long count) {
        if (offset > m_count) {
            throw new IndexOutOfBoundsException(
                "offset: " + offset + 
                ",is greater than result count: " + m_count);
        }
        
        // We only store first X results,
        // If the last index is greater than what
        // we've cached & less than what's available,
        // then re-run the query.
        long last = offset + count;
        if (last > m_results.length &&
            last <= m_count) {
            s_log.info("Requested range: " + offset + "->" + (offset + count) + 
                       " is outsize cache size " + m_results.length);
            m_realResultSet = Search.process(m_spec, 
                                               Search.NOP_RESULT_CACHE, 
                                               m_engine);
            return m_realResultSet.getDocuments(offset, count);
        }
        
        return new ResultIterator(m_results,
                                  (int)offset,
                                  (int)count);
    }
    
    /**
     * Returns the total number of results in the set
     * @return the result count
     */
    public long getCount() {
        return m_count;
    }

    /**
     * Return the query engine used to generate the 
     * result set
     * @return the query engine
     */
    public String getEngine() {
        return m_engine;
    }

    /**
     * Return the elapsed time for generating this result
     * set from the original query specification.
     * @return the query time in milliseconds
     */
    public long getQueryTime() {
        return m_time;
    }

    /**
     * Releases any resources associated with this result
     * set. This is a no-op if the result set doesn't have
     * any system resources allocated
     */
    public void close() {
        if(m_realResultSet != null && m_realResultSet instanceof ResultSet) {
            m_realResultSet.close();
        } else {
            // noop
        }
    }

    private class ResultIterator implements Iterator {
        private Document[] m_results;
        private int m_current;
        private int m_remaining;

        public ResultIterator(Document[] results,
                              int offset,
                              int count) {
            m_results = results;

            int remaining = m_results.length - offset;
            if (remaining < count) {
                count = remaining;
            }

            m_current = offset;
            m_remaining = count;
        }
        
        public boolean hasNext() {
            return m_remaining > 0;
        }
        
        public Object next() {
            Object result = m_results[m_current];
            m_current++;
            m_remaining--;
            return result; 
        }
        
        public void remove() {
            throw new UnsupportedOperationException(
                "cannot remove search results");
        }
    }
}
