/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.bookmarks.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.portal.PortletSelectionModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.contenttypes.ui.LinkTableModelBuilder;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.portlet.bookmarks.Bookmark;
import com.arsdigita.portlet.bookmarks.BookmarksPortlet;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * Bebop table copied from authoring ui
 *
 * @version $Revision: 1.2 $ $Date: 2007/08/08 09:28:26 $
 * @author Chris Gilbert
 */
public class BookmarksTable extends Table {
    
    private static final Logger s_log = Logger.getLogger(BookmarksTable.class);

    private BookmarkSelectionModel m_linkModel;
   // private ItemSelectionModel m_itemModel;
    private TableColumn m_titleCol;
    private TableColumn m_descCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;

    private RequestLocal m_size;
    private PortletSelectionModel m_portlet;


    protected static final String EDIT_EVENT = "Edit";
    protected static final String DELETE_EVENT = "Delete";
    protected static final String UP_EVENT = "up";
    protected static final String DOWN_EVENT = "down";

    /**
     * Constructor. Creates a <code>BookmarksTable</code> given a
     * <code>PortletSelectionModel</code>  and a
     * <code>BookmarkSelectionModel</code>, which track the current portlet
     * and link.
     *
     * @param item The <code>ItemSelectionModel</code> for the current page.
     * @param link The <code>LinkSelectionModel</code> to track the
     * current link
     */
    public BookmarksTable(BookmarkSelectionModel link, PortletSelectionModel portlet) {
        super();
        m_portlet = portlet;
		setModelBuilder(new BookmarksTableModelBuilder(portlet));

        m_linkModel = link;
        addColumns();

        m_size = new RequestLocal();
       
        Label empty = new Label("There are no links defined yet");
        setEmptyView(empty);
        addTableActionListener(new LinkTableActionListener());
        setRowSelectionModel(m_linkModel);
        setDefaultCellRenderer(new LinkTableRenderer());
    }

    /**
     * Sets up the columns to display.
     */
    protected void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        m_titleCol = new TableColumn(i, "Link");
        m_descCol  = new TableColumn(++i, "Description");
        m_editCol  = new TableColumn(++i, "Edit");
        m_delCol   = new TableColumn(++i, "Delete");
        m_moveUpCol = new TableColumn(++i, "");
        m_moveDownCol = new TableColumn(++i, "");
        model.add(m_titleCol);
        model.add(m_descCol);
        model.add(m_editCol);
        model.add(m_delCol);
        model.add(m_moveUpCol);
        model.add(m_moveDownCol);
        setColumnModel(model);
    }

    /**
     * TableCellRenderer class for LinkTable
     */
    private class LinkTableRenderer implements TableCellRenderer {
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            Bookmark link = (Bookmark)value;
	          boolean isFirst = (row == 0);
              if (m_size.get(state) == null) {
                  m_size.set(state, 
                             new Long(((LinkTableModelBuilder.LinkTableModel) 
                              table.getTableModel(state)).size()));
              }
	          boolean isLast = (row == ((Long)m_size.get(state)).intValue() - 1);
            
            String url = BookmarksPortlet.getURIForBookmark(link, state);//link.getInternalOrExternalURI(state);
			ExternalLink extLink = null;
            if (column == m_titleCol.getModelIndex()) {
            	if (null != url) {
            		extLink = new ExternalLink(link.getTitle(), url);
		            extLink.setTargetFrame(link.getTargetWindow());
            	} else {
            		extLink = new ExternalLink("Broken Link", url);
            		
            	}
                return extLink;
            } else if ( column == m_descCol.getModelIndex()) {
                if ( isSelected ) {
                    return new Label(url == null? "Page has been unpublished" : link.getDescription(), Label.BOLD);
                } else {
                    return new Label(url == null? "Page has been unpublished" : link.getDescription());
                }
            } else if ( column == m_editCol.getModelIndex()) {
                     if (isSelected ) {
                        return new Label(EDIT_EVENT, Label.BOLD);
                    } else {
                        return new ControlLink(EDIT_EVENT);
                    }
                
            } else if ( column == m_delCol.getModelIndex()) {
                    return new ControlLink(DELETE_EVENT);
               
            } else if (column == m_moveUpCol.getModelIndex()) {
                if (!isFirst) {
		                Label upLabel = new Label(UP_EVENT);
		                upLabel.setClassAttr("linkSort");
		                return new ControlLink(upLabel);
                } else {
		                return new Label("");
		            }
            } else if (column == m_moveDownCol.getModelIndex()) {
                if (!isLast) {
		                Label downLabel = new Label(DOWN_EVENT);
		                downLabel.setClassAttr("linkSort");
		                return new ControlLink(downLabel);
                } else {
		                return new Label("");
		            }
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        }
    }

    /**
     * TableActionListener class for LinkTable
     */
    private class LinkTableActionListener implements TableActionListener {
        private Bookmark getLink(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                s_log.debug("row key is a string : " + o);
                id = new BigDecimal((String)o);
            } else {
                id = (BigDecimal)e.getRowKey();
            }

            Assert.exists(id);
            Bookmark link;
            try {
                link = (Bookmark)DomainObjectFactory.newInstance(new OID(Bookmark.BASE_DATA_OBJECT_TYPE,id));
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException(de);
            }
            return link;
        }

        public void cellSelected(TableActionEvent e) {
           int col = e.getColumn().intValue();
           PageState state = e.getPageState();
           Bookmark link = getLink(e);
           Assert.exists(link);

           if (col== m_titleCol.getModelIndex()) {
               // do nothing
           } else if (col == m_editCol.getModelIndex()) {
                    // This selection is passed to the LinkPropertyForm
                    s_log.debug("setting linkModel to :" + link.getTitle());
                    m_linkModel.setSelectedObject(state, link);
                
           } else if (col == m_delCol.getModelIndex()) {
                   try {
                       s_log.debug("About to delete");
                       m_linkModel.clearSelection(state);
                       link.delete();
                       BookmarksPortlet portlet = (BookmarksPortlet)m_portlet.getSelectedPortlet(state);
                       portlet.renumberBookmarks();
                   } catch ( PersistenceException pe) {
                       throw new UncheckedWrapperException(pe);
                   }
               
           } else if (col == m_moveUpCol.getModelIndex() ) {	       
               // move the link up
	             m_linkModel.clearSelection(state);
	             link.swapWithPrevious();
           } else if ( col == m_moveDownCol.getModelIndex() ) {
               // move the link down
	             m_linkModel.clearSelection(state);
	             link.swapWithNext();
           } 
       }

        public void headSelected(TableActionEvent e) {}
    }
}
