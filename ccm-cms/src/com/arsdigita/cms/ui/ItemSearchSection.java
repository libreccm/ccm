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
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ui.search.ItemQueryComponent;

import com.arsdigita.search.ui.ResultsPane;
import com.arsdigita.search.ui.QueryGenerator;
import com.arsdigita.search.QuerySpecification;

/**
 * Contains a form for specifying search parameters, as well as a
 * {@link com.arsdigita.search.ui.ResultsPane} which will perform 
 * the search and display the results
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearchSection.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemSearchSection extends FormSection 
    implements Resettable, QueryGenerator {

    private static final org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(ItemSearchSection.class);

    public static final String SINGLE_TYPE_PARAM = "single_type";


    private ItemQueryComponent m_query;
    private Component m_results;

    /**
     * Construct a new <code>ItemSearchSection</code> component
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public ItemSearchSection(String context) {
        this(null, context);
    }
    /**
     * Construct a new <code>ItemSearchSection</code> component
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param name The name of the search parameter for the particular FormSection
     */
    public ItemSearchSection(String name, String context) {
        super(new SimpleContainer());
        String thisName = (name == null ? "itemSearch" : name);
        
        m_query = createQueryGenerator(context);
        m_results = createResultsPane(m_query);
        
        addQueryGenerator(this);
        addResultsPane(this);
        addFormListener();

        setClassAttr("itemSearch");
    }

    public boolean hasQuery(PageState state) {
        return m_query.hasQuery(state);
    }

    public QuerySpecification getQuerySpecification(PageState state) {
        return m_query.getQuerySpecification(state);
    }

    public void reset(PageState state) {
        m_results.setVisible(state, false);
    }

    protected ItemQueryComponent createQueryGenerator(String context) {
        return new ItemQueryComponent(context);
    }

    protected Component createResultsPane(QueryGenerator generator) {
        ResultsPane pane = new ResultsPane(generator);
        pane.setRelativeURLs(true);
        return pane;
    }

    protected void addResultsPane(Container container) {
        container.add(m_results);
    }
    
    protected void addQueryGenerator(Container container) {
        container.add(m_query);
    }

    protected void processQuery(PageState state) {
        m_results.setVisible(state, m_query.hasQuery(state));
    }

    protected void addFormListener() {
        addProcessListener(new SearchFormProcessListener());
    }

    // Hide results by default
    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_results, false);
        p.addGlobalStateParam(new BigDecimalParameter(SINGLE_TYPE_PARAM));
    }

    /**
     * Displays the "keywords" and "content types" widgets
     */
    private class SearchFormProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e) throws FormProcessException {
            PageState s = e.getPageState();
            processQuery(s);
        }
    }
}
