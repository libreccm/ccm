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

package com.arsdigita.london.search.ui;

import com.arsdigita.london.search.Search;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ui.ItemSearch;

import com.arsdigita.search.ui.QueryComponent;
import com.arsdigita.search.ui.ResultsPane;


public class SearchComponent extends SimpleContainer {
    
    private QueryComponent m_query;
    private Form m_form;
    private SponsoredLinksComponent m_links;
    private ResultsPane m_results;
    
    public SearchComponent(QueryComponent query) {
        this(query, null);
    }

    public SearchComponent(QueryComponent query,
                           String engine) {
        m_query = query;

        m_form = new Form("search", new SimpleContainer());
        m_form.setMethod(Form.GET);
        m_form.add(m_query);
        m_form.add(new Submit("search", "Search"));

        m_results = new ResultsPane(query, engine);
        
        add(m_form);               

        if (Search.getConfig().getShowSponsoredLinks().booleanValue()) {
            m_links = new SponsoredLinksComponent(m_query);
            add(m_links);
        }

        add(m_results);
        /*
        m_form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) {
                    m_results.setVisible(e.getPageState(),
                                         m_query.hasQuery(e.getPageState()));
                }
            });
        */
    }
    
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));
        /*
        p.setVisibleDefault(m_results, false);
        */
    }
}
