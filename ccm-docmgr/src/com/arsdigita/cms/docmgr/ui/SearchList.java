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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.docmgr.search.SearchResult;

public class SearchList extends List {
    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(SearchList.class);

    public SearchList(ListModelBuilder model) {
        super();

        setModelBuilder(model);
        setCellRenderer(new SearchResultRenderer());
        //setEmptyView(new Label("No items matched your search"));
    }

    /**
     * A component that displays a simple search result
     **/
    public static class SearchResultDisplay extends SimpleContainer {
        private ExternalLink m_title;
        private Label m_score;
        private Label m_summary;

        public SearchResultDisplay(SearchResult result) {
            super();

            if (result != null) {
                
                String url = result.getUrlStub();
                m_title = new ExternalLink(result.getLink(), url);
                add(m_title);
                
                String score = result.getScore().toString();
                if (score.length() > 5) {
                    score = score.substring(0, 5);
                }

                m_score = new Label(" (score: " + score + ") ");
                m_score.setClassAttr("score");
                add(m_score);

                String summary = result.getSummary();
                if( null != summary ) {
                    m_summary = new Label( summary, false );
                    m_summary.setClassAttr("summary");
                    add(m_summary);
                }
            }
            
            setClassAttr("searchResult");
        }
    }

    public static class SearchResultRenderer implements ListCellRenderer {
        public Component getComponent(List list, 
                                      PageState state, 
                                      Object value, 
                                      String key, 
                                      int index, 
                                      boolean isSelected) {

            SearchResult res = (SearchResult) value;
            return new SearchResultDisplay(res);
        }
    }
}
