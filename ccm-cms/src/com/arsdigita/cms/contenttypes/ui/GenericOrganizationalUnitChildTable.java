/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitChildrenCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table for showing the childs of an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitChildTable extends Table implements
        TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;
    private static final Logger s_log =
                                Logger.getLogger(
            GenericOrganizationalUnitChildTable.class);

    public GenericOrganizationalUnitChildTable(
            final ItemSelectionModel itemModel) {
        super();
        this.m_itemModel = itemModel;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.childs.none")));
        TableColumnModel tabModel = getColumnModel();

        /*tabModel.add(new TableColumn(
        0,
        ContenttypesGlobalizationUtil.globalize(
        "cms.contenttypes.ui.genericorgaunit.child.order").localize(),
        TABLE_COL_EDIT));*/
        tabModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.child.name").localize(),
                TABLE_COL_EDIT));
        tabModel.add(new TableColumn(
                1,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.child.action").localize(),
                TABLE_COL_DEL));
        tabModel.add(new TableColumn(
                2,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.child.up").localize(),
                TABLE_COL_UP));
        tabModel.add(new TableColumn(
                3,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.child.down").localize(),
                TABLE_COL_DOWN));


        setModelBuilder(new GenericOrganizationalUnitChildTableModelBuilder(
                itemModel));

        tabModel.get(0).setCellRenderer(new EditCellRenderer());
        tabModel.get(1).setCellRenderer(new DeleteCellRenderer());
        tabModel.get(2).setCellRenderer(new UpCellRenderer());
        tabModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);

    }

    private class GenericOrganizationalUnitChildTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericOrganizationalUnitChildTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);
            return new GenericOrganizationalUnitChildTableModel(table, state,
                                                                orgaunit);
        }
    }

    private class GenericOrganizationalUnitChildTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericOrganizationalUnitChildrenCollection m_childCollection;
        private GenericOrganizationalUnit m_child;

        private GenericOrganizationalUnitChildTableModel(Table table,
                                                         PageState state,
                                                         GenericOrganizationalUnit orgaunit) {
            m_table = table;
            m_childCollection = orgaunit.getOrgaUnitChildren();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_childCollection != null) && m_childCollection.next()) {
                m_child = m_childCollection.getOrgaUnitChild();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int colIndex) {
            switch (colIndex) {
                /*case 0:
                return m_childCollection.getChildrenOrder();*/
                case 0:
                    return m_child.getName();
                case 1:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int colIndex) {
            return m_child.getID();
        }
    }

    private class EditCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            GenericOrganizationalUnit orgaunit =
                                      (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    orgaunit);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class DeleteCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    orgaunit);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) ContenttypesGlobalizationUtil.
                        globalize(
                        "cms.contenttypes.ui.genericorgaunit.confirm_delete").
                        localize());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class UpCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            /*com.arsdigita.cms.SecurityManager securityManager = Utilities.
            getSecurityManager(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
            getSelectedObject(state);*/

            /*GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
            getSelectedObject(state);
            GenericOrganizationalUnitChildrenCollection children =
            orgaunit.getOrgaUnitChildren();*/

            s_log.debug(String.format("row = %d", row));
            //s_log.debug(String.format("children.size = %d", children.size()));

            if (0 == row) {
                s_log.debug("row is first row in table, don't show 'up'-link...");
                return new Label("");
            } else {
                ControlLink link = new ControlLink("up");
                return link;
            }
        }
    }

    private class DownCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            /*com.arsdigita.cms.SecurityManager securityManager = Utilities.
            getSecurityManager(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
            getSelectedObject(state);*/

            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);
            GenericOrganizationalUnitChildrenCollection children =
                                                        orgaunit.
                    getOrgaUnitChildren();


            if ((children.size() - 1) == row) {
                s_log.debug(
                        "Row is last row of table, don't show 'down'-link...");
                return new Label("");
            } else {
                ControlLink link = new ControlLink("down");
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        GenericOrganizationalUnit child = new GenericOrganizationalUnit(new BigDecimal(event.
                getRowKey().toString()));

        GenericOrganizationalUnit parent = (GenericOrganizationalUnit) m_itemModel.
                getSelectedObject(state);

        GenericOrganizationalUnitChildrenCollection children =
                                                    parent.getOrgaUnitChildren();

        TableColumn col = getColumnModel().get(event.getColumn().intValue());

        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            parent.removeOrgaUnitChildren(child);
        } else if (col.getHeaderKey().equals(TABLE_COL_UP)) {
            children.swapWithPrevious(child);
        } else if (col.getHeaderKey().equals(TABLE_COL_DOWN)) {
            children.swapWithNext(child);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
