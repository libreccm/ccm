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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.arsdigita.cms.docmgr.ui.DMConstants;
import com.arsdigita.search.intermedia.SearchDataQuery;


/**
 * @author hbrock@redhat.com
 * @version $Revision: #1 $ $Date: 2003/08/20 $
 */
public class IntermediaSearchResults implements SearchResults {

    private SearchDataQuery m_dataQuery;
    private long m_size;

    public IntermediaSearchResults(SearchDataQuery q) {
        m_dataQuery = q;
        m_size = m_dataQuery.size();
    }
    
    /**
     * Returns the size of the wrapped SearchDataQuery
     * @see com.arsdigita.cms.docmgr.search.SearchResults#getTotalSize()
     */
    public long getTotalSize() {
        return m_size;
    }

    /**
     * @see com.arsdigita.cms.docmgr.search.SearchResults#setRange(int, int)
     */
    public void setRange(Integer first, Integer last) {
        m_dataQuery.setRange(first, last);
    }

    /**
     * @see com.arsdigita.cms.docmgr.search.SearchResults#getResults()
     */
    public Iterator getResults() {
        SearchResult m_result;
        Collection shortResults = new ArrayList();
        while (m_dataQuery.next()) {
            shortResults.add(new SearchResult( m_dataQuery.getID(),
                                               m_dataQuery.getLinkText(),
                                               //Dispatcher.getRedirectURL(m_dataQuery.getID(),
                                               //                          m_dataQuery.getObjectType()),
                                               "file?"+DMConstants.FILE_ID_PARAM_NAME+"="+
                                               m_dataQuery.getID().toString(),
                                               m_dataQuery.getSummary(),
                                               m_dataQuery.getScore())
                            );
        }                                         
            
        m_dataQuery.close();
        return shortResults.iterator();
    }

}
