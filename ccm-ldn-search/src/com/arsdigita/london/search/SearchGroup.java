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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

public class SearchGroup {
    private static final Logger s_log =
        Logger.getLogger(SearchGroup.class);

    private SearchJobQueue m_queue;
    private Server[] m_servers;
    private String m_terms;

    private ArrayList m_results = new ArrayList();

    private int m_resultsPending;

    private static int s_timeout;

    public SearchGroup(SearchJobQueue queue,
                       String terms,
                       Server[] servers) {
        m_queue = queue;
        m_terms = terms;
        m_servers = servers;

        m_resultsPending = m_servers.length;
    }

    public static void setSearchTimeout( int timeout ) {
        s_timeout = timeout;
    }

    /**
     * Returns a collection of results from all specified servers, merged 
     * and sorted by score descending.
     * 
     * @return java.util.Collection
     */                        
    public synchronized Collection search() {
        if (isComplete()) {
            return m_results;
        }

        for (int i = 0 ; i < m_servers.length ; i++) {
            s_log.debug("Adding SearchJobs " + i + ":" + m_servers[i]);

            SearchJob job = new SearchJob( m_terms, m_servers[i], this );
            m_queue.addSearchJob( job );
        }

        long timeout = System.currentTimeMillis() + s_timeout;

        while ( !isComplete() && System.currentTimeMillis() < timeout ) {
            s_log.debug("Waiting for completion or time out");
            try {
                wait( s_timeout / 4 );
            } catch( InterruptedException ex ) { }
        }

        if (!isComplete()) {
            s_log.debug("Timed out with no results");
        }

        /* BaseDocument now implements Comparable, allowing sorting by 
         * score.
         */
        s_log.debug("search(): Sorting results");
        Collections.sort(m_results); 
                        
        return m_results;
    }

    public boolean isComplete() {
        return m_resultsPending == 0;
    }

    public void addResults( Collection results ) {
        m_results.addAll( results );

        synchronized( this ) {
            m_resultsPending--;
            notify();
        }
    }

    /**
     * @return java.util.Collection
     * @deprecated Results are only currently retrieved from the search()
     * method. This method is not used.
     */
    public Collection getResults() {
        return m_results;
    }
}
