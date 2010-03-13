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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.io.IOException;
import org.apache.log4j.Logger;

/** Displays target url that redirects and description */
public class LinkDisplayTable extends LinkTable {

    private static final Logger s_log = Logger.getLogger(LinkDisplayTable.class);

    /** Uses LinkTable.java and changes the display components **/
    ItemSelectionModel m_itemModel;
    LinkSelectionModel m_linkModel;

    private TableColumn m_titleCol;
    private TableColumn m_descCol;

    /**
     * Constructor. Creates a <code>LinkDisplayTable</code> given an
     * <code>ItemSelectionModel</code>  and a
     * <code>LinkSelectionModel</code>, which track the current item
     * and link.
     *
     * @param item The <code>ItemSelectionModel</code> for the current page.
     * @param link The <code>LinkSelectionModel</code> to track the
     * current link
     */
    public LinkDisplayTable(ItemSelectionModel item, LinkSelectionModel link) {
        super(item, link);
        m_itemModel = item;
        m_linkModel = link;
        setDefaultCellRenderer(new LinkDisplayTableCellRenderer());
        addTableActionListener(new LinkDisplayTableActionListener());
    }

    /**
     * Sets up the columns to display.
     */
    protected void addColumnModel() {
        s_log.debug("Setting columns to be display");
        TableColumnModel model = getColumnModel();
        m_titleCol = new TableColumn(0, "Title");
        m_descCol = new TableColumn(2, "Description");

        model.add(m_titleCol);
        model.add(m_descCol);
    }

    /**
     * TableCellRenderer class for LinkDisplayTable
     */
    private class LinkDisplayTableCellRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            Link link = (Link)value;
            
            if (column == m_titleCol.getModelIndex()) {
                return new ControlLink(link.getTitle());
            } else if (column == m_descCol.getModelIndex()) {
                return new Label(link.getDescription());
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        } 
    }

    /**
     * TableActionListener class for LinkDisplayTable
     */
    private class LinkDisplayTableActionListener implements TableActionListener {

        private Link getLink(TableActionEvent e) {
            String id = (String)e.getRowKey();
            Assert.exists(id);
            Link link;
            try {
                link = new Link(new BigDecimal(id));
            } catch(DataObjectNotFoundException ex ) {
                throw new RuntimeException(ex.getMessage());
            }
            return link;
        }

        public void cellSelected(TableActionEvent e) {
           int col = e.getColumn().intValue();
           PageState state = e.getPageState();
           Link link = getLink(e);

           if (col== m_titleCol.getModelIndex()) {
               
               String url = link.getInternalOrExternalURI(state);
               s_log.debug("Redirecting to : " + url);
               try {
                   DispatcherHelper.sendRedirect(state.getRequest(), state.getResponse(), url);
               } catch (IOException ex) {
                   throw new UncheckedWrapperException("Failed to redirect", ex);
               }

           } 
        }
        
        public void headSelected(TableActionEvent e) {}
    }
}

