/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;


import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.docmgr.search.SearchResult;
import com.arsdigita.cms.docmgr.search.SearchResults;

public class SearchListModelBuilder extends AbstractListModelBuilder 
    implements PaginationModelBuilder {

    private static final Category s_log =
        Category.getInstance(SearchListModelBuilder.class);

    private SearchForm m_form; 
    public SearchListModelBuilder(SearchForm form) {

        // By taking the form as an argument we don't have to worry
        // about what type of search it is.
        m_form = form;
    }

    public int getTotalSize(Paginator p, PageState ps) {
        SearchResults results = m_form.getSearchHits(ps);
        if (results == null) {
            return 0;
        } else {
            int totalSize = (int)results.getTotalSize();
            s_log.debug("Setting paginator: size = " + (new Integer((int)results.getTotalSize())).toString() +
            "first = " + (new Integer(p.getFirst(ps))).toString() + "last = " + (new Integer(p.getLast(ps))).toString());
            results.setRange(new Integer(p.getFirst(ps)),
                new Integer(p.getLast(ps) + 1));
            p.setPageSize(ps, 10);
            s_log.debug("totoalsize is "+(new BigDecimal(totalSize)).toString());
            return totalSize;
        }
    }

    public boolean isVisible(PageState state) {
        return m_form.isVisible(state);
    }


    public ListModel makeModel(List list, PageState state) {
        SearchResults results = m_form.getSearchHits(state);
        if (results == null) {
            return List.EMPTY_MODEL;
        } else {
            return new SearchListModel(results);
        }
    }

    private class SearchListModel implements ListModel {
    
        private Iterator m_results;
        private Object m_result;

        public SearchListModel(SearchResults results) {
            if ( results != null ) {
                m_results = results.getResults();
                s_log.debug("In SearchListModelBuilder - Iterator ID: " + m_results.toString());
            }
        }

    
        public boolean next() {
            if ( m_results != null && m_results.hasNext() ) {
                m_result = m_results.next();
                return true;
            }
            return false;
        }

        public Object getElement() {
            if ( m_result != null ) {
                return m_result;
            }
            
            return null;
        }

        public String getKey() {
            SearchResult result = (SearchResult) getElement();
            return (result.getID().toString());
        }

        public void setRange(Integer start,
                             Integer end) {
            //        m_results.setRange(start, end);
        }
    }
}
