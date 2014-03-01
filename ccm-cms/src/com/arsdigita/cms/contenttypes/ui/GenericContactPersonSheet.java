/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GenericContactPersonSheet extends Table implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel m_itemModel;

    /**
     * Constructor.
     *
     * @param itemModel
     */
    public GenericContactPersonSheet(final ItemSelectionModel itemModel) {

        super();
        m_itemModel = itemModel;

        setEmptyView(
            new Label(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.genericcontact.emptyPerson")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            new Label(ContenttypesGlobalizationUtil.globalize(
            //   "cms.contenttypes.ui.contact.person").localize(),
            "cms.contenttypes.ui.genericcontact.person")),
            TABLE_COL_EDIT));
        colModel.add(new TableColumn(
            1,
            new Label(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.genericcontact.delete_person")),
            TABLE_COL_DEL));

        setModelBuilder(new GenericContactPersonSheetModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    /**
     *
     */
    private class GenericContactPersonSheetModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericContactPersonSheetModelBuilder(
            ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);
            return new GenericContactPersonSheetModel(table,
                                                      state,
                                                      contact);
        }

    }

    private class GenericContactPersonSheetModel implements TableModel {

        private final Table m_table;
        private final GenericPerson m_person;
        private boolean m_done;

        public GenericContactPersonSheetModel(final Table table,
                                              final PageState state,
                                              final GenericContact contact) {
            m_table = table;
            m_person = contact.getPerson();
            if (m_person == null) {
                m_done = false;
            } else {
                m_done = true;
            }
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if (m_done) {
                ret = true;
                m_done = false;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_person.getFullName();
                case 1:
                    return ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.genericcontact.delete_person").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return m_person.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                contact);

            if (canEdit) {
                GenericPerson person;

                try {
                    person = new GenericPerson((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = person.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();

                return new Link(value.toString(),
                                resolver.generateItemURL(state,
                                                         person,
                                                         section,
                                                         person.getVersion()));

            } else {

                return new Label(value.toString());

            }
        }

    }

    private class DeleteCellRenderer
        extends LockableImpl
        implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(
                state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.DELETE_ITEM,
                contact);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.person"
                    + ".confirm_remove"));
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }

    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(
            state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            contact.unsetPerson();
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }

}
