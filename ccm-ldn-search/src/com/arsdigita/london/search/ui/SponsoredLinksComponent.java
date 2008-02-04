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

import com.arsdigita.london.search.SponsoredLink;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.search.ui.QueryComponent;
import com.arsdigita.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Display a list of SponsoredLinks that match the 
 * terms provided by the {@link QueryComponent}.
 **/
public class SponsoredLinksComponent extends Table {

    public static final String ID = "SponsoredLinks";

    private static final Logger s_log = Logger.getLogger(SponsoredLinksComponent.class);

    private static final String[] s_headers = { "Title", "URL" };
    
    public SponsoredLinksComponent(QueryComponent query) {
        super(new SponsoredLinksModelBuilder(query), s_headers);

        setIdAttr(ID);
        setDefaultCellRenderer(new SponsoredLinkCellRenderer());
    }
}

class SponsoredLinksModelBuilder extends AbstractTableModelBuilder {
    private final QueryComponent m_query;

    SponsoredLinksModelBuilder(QueryComponent query) {
        super();
        
        m_query = query;
    }

    public TableModel makeModel(Table t, PageState state) {
        String termStr = null;
        if (m_query.hasQuery(state)) {
            termStr = m_query.getQuerySpecification(state).getTerms();
        }
        if (termStr == null || (termStr = termStr.trim()).length() == 0) {
            return Table.EMPTY_MODEL;
        }

        String[] terms = StringUtils.split(termStr, ' ');
        List list = new ArrayList();
        for (int i = 0, j = terms.length; i < j; i++) {
            if (terms[i] != null && terms[i].length() >0) {
                ACSObjectCollection links = SponsoredLink.retrieveLinksForTerm(terms[i]);
                links.addOrder(SponsoredLink.TITLE);
                while (links.next()) {
                    SponsoredLink link = (SponsoredLink) links.getACSObject();
                    if (!list.contains(link)) {
                        // We use list.contain() here instead of a HashSet because
                        // we want the elements to come out of the iterator
                        // in the order we put them in.
                        list.add(link);
                    }
                }
            }
        }
        
        return new SponsoredLinksModel(list.iterator());
    }
}

class SponsoredLinksModel implements TableModel {

    private final Iterator m_links;
    private SponsoredLink m_link;

    SponsoredLinksModel(Iterator links) {
        m_links = links;
    }

    public int getColumnCount() {
        return 2;
    }

    public boolean nextRow() {
        if (m_links.hasNext()) {
            m_link = (SponsoredLink) m_links.next();
        } else {
            m_link = null;
        }

        return (m_link != null);
    }

    public Object getKeyAt(int columnIndex) {
        if (m_link != null) {
            return m_link.getID();
        } else {
            return null;
        }
    }

    public Object getElementAt(int columnIndex) {
        return m_link;
    }
}

class SponsoredLinkCellRenderer implements TableCellRenderer {
    public Component getComponent(Table table, PageState state, Object value, 
                                  boolean isSelected, Object key, int row, int column) {
        SponsoredLink link = (SponsoredLink) value;
        if (column == 0) {
            return new ExternalLink(new Label(link.getTitle()), link.getURL());
        } else if (column == 1) {
            return new Label(link.getURL());
        } else {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
    }
}
