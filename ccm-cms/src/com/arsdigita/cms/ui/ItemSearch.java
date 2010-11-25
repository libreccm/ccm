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
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.ui.QueryGenerator;

/**
 * A wrapper around the {@link ItemSearchSection} which embedds
 * the form section in a form.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearch.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemSearch extends Form implements Resettable, QueryGenerator {

    private static final org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(ItemSearch.class);
    public static final String SINGLE_TYPE_PARAM = ItemSearchSection.SINGLE_TYPE_PARAM;
    private ItemSearchSection m_section;

    /**
     * Construct a new <code>ItemSearch</code> component
     *
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public ItemSearch(String context) {
        super("itemSearch", new SimpleContainer());
        setMethod("GET");
        m_section = createSearchSection(context);
        add(m_section);
    }

    protected ItemSearchSection createSearchSection(String context) {
        return new ItemSearchSection(context);
    }

    public boolean hasQuery(PageState state) {
        return m_section.hasQuery(state);
    }

    public QuerySpecification getQuerySpecification(PageState state) {
        return m_section.getQuerySpecification(state);
    }

    public void reset(PageState state) {
        m_section.reset(state);
    }
}
