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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.toolbox.ui.QueryEvent;
import com.arsdigita.toolbox.ui.QueryListener;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;

/**
 * A {@link DataTable} that displays {@link ContentPage}s based on the
 * passed-in query. Displays name, title, and content type;
 * allows sorting.
 * <p>
 *
 * The <code>DataQuery</code> <b>must</b> possess the
 * following columns:
 * <ul>
 *  <li><b>name</b> - the name of the page
 *  <li><b>title</b> - the title of the page
 *  <li><b>typeLabel</b> - the label of the content type
 * </ul>
 * <p>
 *
 * Further columns may be added with the {@link #addColumn(String, String)}
 * method, as described in {@link DataTable} <p>
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ContentPageTable.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContentPageTable extends DataTable {

    private ContentType m_type;
    private String m_queryName;
    private String m_context;

    public static final String TYPE_LABEL = "typeLabel";

    /**
     * Construct a new ContentPageTable
     *
     * @param query The name of the {@link DataQuery} that will be
     *   used by this table
     * @param context The context for the retrieved items. Should be
     *   {@link ContentItem#LIVE} or {@link ContentItem#DRAFT}
     */
    public ContentPageTable(String queryName, String context) {
        super(new QueryModelBuilder(queryName));
        m_type = null;
        m_context = context;
        m_queryName = queryName;

        setBorder("0");
        setCellSpacing("5");

        addColumn("Name", ContentPage.NAME, true, new URLCellRenderer());
        addColumn("Title", ContentPage.TITLE, true);
        addColumn("Type", TYPE_LABEL, true);
        Label empty = new Label(GlobalizationUtil.globalize("cms.ui.there_are_no_items"));
        empty.setFontWeight(Label.ITALIC);
        setEmptyView(empty);

        // Add custom filter
        addQueryListener(new SectionQueryListener());

    }

    // Add an action listener to auto-select the first column
    public void register(Page p) {
        super.register(p);
        p.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    if(!getOrderSelectionModel().isSelected(s)) {
                        setOrder(s, getColumnModel().get(0).getHeaderKey().toString());
                    }
                }
            });
    }

    /**
     * @return the current context
     */
    public String getContext() {
        return m_context;
    }

    /**
     * @param context the new context for the items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public void setContext(String context) {
        Assert.isUnlocked(this);
        m_context = context;
    }

    /**
     * Set the content type. Only items of the given content type
     * will be shown.
     *
     * @param c the content type of shown items. If null, all items
     *   will be shown
     */
    public void setContentType(ContentType c) {
        Assert.isUnlocked(this);
        m_type = c;
    }

    /**
     * @return the content types of shown items, or null if all items
     *   will be shown
     */
    public ContentType getContentType() {
        return m_type;
    }

    /**
     * @return the name of the {@link DataQuery} that this class
     *   will execute
     */
    public String getDataQueryName() {
        return m_queryName;
    }

    /**
     * Executes the specified query
     */
    private static class QueryModelBuilder extends LockableImpl
        implements DataQueryBuilder {

        private String m_queryName;

        public QueryModelBuilder(String queryName) {
            super();
            m_queryName = queryName;
        }

        public DataQuery makeDataQuery(DataTable d, PageState s) {
            Session session = SessionManager.getSession();
            DataQuery data = (DataQuery)session.retrieveQuery(m_queryName);

            return data;
        }

        public String getKeyColumn() {
            return "itemId";
        }
    }

    /**
     * Ensures that only items in the current section are shown.
     */
    private static class SectionQueryListener implements QueryListener {
        public void queryPending(QueryEvent e) {
            ContentPageTable t = (ContentPageTable)e.getSource();
            PageState s = e.getPageState();
            DataQuery data = e.getDataQuery();
            ContentType type = t.getContentType();
            ContentSection sec = CMS.getContext().getContentSection();

            if(sec == null) {
                throw new IllegalStateException( (String) GlobalizationUtil.globalize("cms.ui.no_content_section_for_page").localize());
            }

            Filter f;
            if(type != null) {
                f = data.addFilter("typeId = :typeId and parentId = :parentId");
                f.set("typeId", type.getID());
            } else {
                f = data.addFilter("parentId = :parentId");
            }

            f.set("parentId", sec.getRootFolder().getID());

            f = data.addFilter(ContentItem.VERSION + " = :version");
            f.set("version", t.getContext());
        }
    }

    /**
     * Generates the correct URL to the item based on context
     */
    private static class URLCellRenderer implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            ContentSection section = CMS.getContext().getContentSection();
            ContentPageTable t = (ContentPageTable)table;
            String name = (String)value;
            BigDecimal id = (BigDecimal)key;

            if(section == null) {
                return new Link(name, name);
            } else {
                ItemResolver resolver = section.getItemResolver();
                return new Link(name, resolver.generateItemURL (
                                    state, id, name, section, t.getContext()
                                ));
            }
        }
    }

}
