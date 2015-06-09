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
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Bebop table to display a list of Links associated with a ContentItem.
 *
 * It is the UI for the CMS build in Link asset.
 *
 * It's current prominent use is by the RelatedLink asset authjoring step.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @author Nobuko Asakai (nasakai@redhat.com)
 */
public class LinkTable extends Table {

    private static final Logger s_log = Logger.getLogger(LinkTable.class);
    private LinkSelectionModel m_linkModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_titleCol;
    private TableColumn m_descCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;

    // match columns by (symbolic) index, makes for easier reordering
    private static final int COL_IDX_LINK = 0;   //Link;
    private static final int COL_IDX_DESCR = 1;  //Description;
    private static final int COL_IDX_EDIT = 2;  //Description;
    private static final int COL_IDX_DELETE = 3;  //Description;
    private static final int COL_IDX_MOVE_UP = 4;  //Description;
    private static final int COL_IDX_MOVE_DOWN = 5;  //Description;

    protected static final String EDIT_EVENT = "Edit";
    protected static final String DELETE_EVENT = "Delete";
    protected static final String UP_EVENT = "up";
    protected static final String DOWN_EVENT = "down";

    /**
     * Constructor. Creates a <code>LinkTable</code> given an
     * <code>ItemSelectionModel</code> and a <code>LinkSelectionModel</code>,
     * which track the current item and link.
     *
     * @param item The <code>ItemSelectionModel</code> for the current page.
     * @param link The <code>LinkSelectionModel</code> to track the current link
     */
    public LinkTable(ItemSelectionModel item, LinkSelectionModel link) {

        super();
        m_itemModel = item;
        m_linkModel = link;
        addColumns();

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {
            /**
             *
             */
            public Object initialValue(PageState state) {
                SecurityManager sm = CMS.getSecurityManager(state);
                ContentItem item = (ContentItem) m_itemModel
                        .getSelectedObject(state);
                Boolean val = new Boolean(sm.canAccess(state.getRequest(),
                        SecurityManager.EDIT_ITEM,
                        item));
                return val;
            }
        };

        Label empty = new Label(GlobalizationUtil.globalize(
                "cms.contenttyes.link.ui.table_no_entries"));
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

        m_titleCol = new TableColumn(
                COL_IDX_LINK,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_link"))
        );
        m_descCol = new TableColumn(
                COL_IDX_DESCR,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_descr"))
        );
        m_editCol = new TableColumn(
                COL_IDX_EDIT,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_edit"))
        );
        m_delCol = new TableColumn(
                COL_IDX_DELETE,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_delete"))
        );
        m_moveUpCol = new TableColumn(
                COL_IDX_MOVE_UP,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_move_up"))
        );
        m_moveDownCol = new TableColumn(
                COL_IDX_MOVE_DOWN,
                new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_header_move_down"))
        );

        model.add(m_titleCol);
        model.add(m_descCol);
        model.add(m_editCol);
        model.add(m_delCol);
        model.add(m_moveUpCol);
        model.add(m_moveDownCol);

        setColumnModel(model);
    }

    /**
     * Internal private class TableCellRenderer class for LinkTable
     */
    private class LinkTableRenderer implements TableCellRenderer {

        /**
         *
         * @param table
         * @param state
         * @param value
         * @param isSelected
         * @param key
         * @param row
         * @param column
         * @return
         */
        public Component getComponent(Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {

            Link link = (Link) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state,
                        new Long(((LinkTableModelBuilder.LinkTableModel) table.
                                getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            if (column == m_titleCol.getModelIndex()) {

                if (link.getTargetURI() != null && link.getTargetURI().equals("caption")) {
                    Label label = new Label(link.getTitle());
                    label.setOutputEscaping(false);
                    return label;
                }
                String url = link.getInternalOrExternalURI(state);
                ExternalLink extLink = new ExternalLink(link.getTitle(), url);
                extLink.setTargetFrame("_blank");
                return extLink;

            } else if (column == m_descCol.getModelIndex()) {
//                if (isSelected) {
//                    Label label = new Label(link.getDescription(), Label.BOLD);
//                    label.setOutputEscaping(false);
//                    return label;
//                } else {
                    Label label = new Label(link.getDescription());
                    label.setOutputEscaping(false);
                    return label;
//                }
            } else if (column == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    if (isSelected) {
                        return new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_cell_edit_link"),
                                Label.BOLD);
                    } else {
                        return new ControlLink(new Label(GlobalizationUtil.globalize(
                                "cms.contenttyes.link.ui.table_cell_edit_link")));
                    }
                } else {
                    return new Label(GlobalizationUtil.globalize(
                            "cms.contenttyes.link.ui.table_cell_edit_link"));
                }
            } else if (column == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    return new ControlLink(new Label(GlobalizationUtil.globalize(
                            "cms.contenttyes.link.ui.table_cell_delete_link")));
                } else {
                    return new Label(GlobalizationUtil.globalize(
                            "cms.contenttyes.link.ui.table_cell_delete_link"));
                }
            } else if (column == m_moveUpCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isFirst) {
                    Label upLabel = new Label(GlobalizationUtil.globalize(
                            "cms.contenttyes.link.ui.table_cell_move_up"));
                    upLabel.setClassAttr("linkSort");
                    return new ControlLink(upLabel);
                } else {
                    return new Label("");
                }
            } else if (column == m_moveDownCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isLast) {
                    Label downLabel = new Label(GlobalizationUtil.globalize(
                            "cms.contenttyes.link.ui.table_cell_move_down"));
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label();
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

        private Link getLink(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                s_log.debug("row key is a string : " + o);
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) e.getRowKey();
            }

            Assert.exists(id);
            Link link;
            try {
                link = (Link) DomainObjectFactory.newInstance(new OID(
                        Link.BASE_DATA_OBJECT_TYPE, id));
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException(de);
            }
            return link;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            Link link = getLink(e);
            Assert.exists(link);

            if (col == m_titleCol.getModelIndex()) {
                // do nothing
            } else if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    // This selection is passed to the LinkPropertyForm
                    s_log.debug("setting linkModel to :" + link.getTitle());
                    m_linkModel.setSelectedObject(state, link);
                }
            } else if (col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        s_log.debug("About to delete");
                        m_linkModel.clearSelection(state);
                        link.delete();
                    } catch (PersistenceException pe) {
                        throw new UncheckedWrapperException(pe);
                    }
                }
            } else if (col == m_moveUpCol.getModelIndex()) {
                // move the link up
                m_linkModel.clearSelection(state);
                link.swapWithPrevious();
            } else if (col == m_moveDownCol.getModelIndex()) {
                // move the link down
                m_linkModel.clearSelection(state);
                link.swapWithNext();
            }
        }

        @Override
        public void headSelected(TableActionEvent e) {
        }
    }
}
