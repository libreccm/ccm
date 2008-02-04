/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
 
package com.arsdigita.cms.docmgr.search;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.arsdigita.cms.docmgr.ui.DMConstants;
import com.arsdigita.lucene.LuceneSearch;


/**
 * @author hbrock@redhat.com
 * @version $Revision: #1 $ $Date: 2003/08/20 $
 */
public class LuceneSearchResults implements SearchResults {

    private LuceneSearch m_search;
    private Integer m_first;
    private Integer m_last;
    private static final int SCORE_MULTIPLIER = 100;

    public LuceneSearchResults(LuceneSearch search) {
        m_search = search;        
    }   

    /**
     * @see com.arsdigita.cms.docmgr.search.SearchResults#getTotalSize()
     */
    public long getTotalSize() {
        return (long) m_search.size();
    }

    /**
     * @see com.arsdigita.cms.docmgr.search.SearchResults#setRange(Integer, Integer)
     */
    public void setRange(Integer first, Integer last) {
        m_first=first;
        m_last=last;
    }

    /**
     * @see com.arsdigita.cms.docmgr.search.SearchResults#getResults()
     */
    public Iterator getResults() {
        // create a new collection for the results and preallocate
        // space for the hits, as we know how many there are
        Collection results = new Vector(m_search.size());
        SearchResult result;
        int i = 1;
        
        if (m_first != null && m_last != null) {
            while (i < m_first.intValue()) {
                m_search.next();
                i++;
            }    
            
            while ( m_search.next() && i <= m_last.intValue() ) {
                result = new SearchResult(
                    m_search.getID(),
                    m_search.getTitle(),
                    //Dispatcher.getRedirectURL(m_search.getID(),
                    //                          m_search.getType()),
                    "file?"+DMConstants.FILE_ID_PARAM_NAME+"="+
                    m_search.getID().toString(),
                    m_search.getSummary(),
                    new BigDecimal(SCORE_MULTIPLIER * m_search.getScore()));
                results.add(result);
                i++;
            }
        } else {
            while ( m_search.next() ) {
                result = new SearchResult(
                    m_search.getID(),
                    m_search.getTitle(),
                    "file?"+DMConstants.FILE_ID_PARAM_NAME+"="+
                    m_search.getID().toString(),
                    m_search.getSummary(),
                    new BigDecimal(SCORE_MULTIPLIER * m_search.getScore()));
                results.add(result);
            }
        }
        m_search.close();
        return results.iterator();
    }

}
