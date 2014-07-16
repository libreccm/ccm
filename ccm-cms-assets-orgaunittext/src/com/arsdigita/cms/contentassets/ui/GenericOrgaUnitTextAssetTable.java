/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contentassets.GenericOrgaUnitTextAsset;
import com.arsdigita.cms.contentassets.GenericOrgaUnitTextAssetGlobalizationUtil;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrgaUnitTextAssetTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final GenericOrgaUnitTextAssetStep parent;
    private final ItemSelectionModel itemModel;
    private final ACSObjectSelectionModel selectionModel;

    public GenericOrgaUnitTextAssetTable(final GenericOrgaUnitTextAssetStep parent,
                                         final ACSObjectSelectionModel selectionModel,
                                         final ItemSelectionModel itemModel) {

        super();
        this.parent = parent;
        this.itemModel = itemModel;
        this.selectionModel = selectionModel;

        setEmptyView(new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                "cms.orgaunit.textassets.empty")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                                "cms.orgaunit.textasset.name"))));
        colModel.add(new TableColumn(
                1,
                new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                                "cms.orgaunit.textasset.edit")),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                2,
                new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                                "cms.orgaunit.textasset.delete")),
                TABLE_COL_DEL));

        setModelBuilder(new ModelBuilder(itemModel));

        colModel.get(1).setCellRenderer(new EditCellRenderer());
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());

    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            super();
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);
            return new AssetsTableModel(table, state, orgaunit);
        }

    }

    private class AssetsTableModel implements TableModel {

        private final Table table;
        private final DataCollection textAssets;
        private GenericOrgaUnitTextAsset textAsset;

        public AssetsTableModel(final Table table,
                                final PageState state,
                                final GenericOrganizationalUnit orgaunit) {
            this.table = table;

            textAssets = GenericOrgaUnitTextAsset.getTextAssets(orgaunit);
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((textAssets != null) && textAssets.next()) {
                textAsset = new GenericOrgaUnitTextAsset(textAssets.getDataObject());
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    final GlobalizedMessage name = new GlobalizedMessage(
                            textAsset.getTextAssetName(),
                            "GenericOrgaUnitTextAssetName",
                            new RelationAttributeResourceBundleControl());
                    
                    return new Label(name);
//                    final RelationAttributeCollection names = new RelationAttributeCollection(
//                            "GenericOrgaUnitTextAssetName");
//                    names.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
//                    names.addKeyFilter(textAsset.getTextAssetName());
//                    final String label;
//                    if (names.next()) {
//                        label = names.getName();
//                        names.close();
//                    } else {
//                        label = textAsset.getTextAssetName();
//                    }
//                    return label;

                case 1:
                    return new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                            "cms.orgaunit.textasset.edit"));
                case 2:
                    return new Label(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                            "cms.orgaunit.textasset.delete"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return textAsset.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditCellRenderer() {
            super();
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(), com.arsdigita.cms.SecurityManager.EDIT_ITEM, orgaunit);

            if (canEdit) {
                return new ControlLink((Label) value);
            } else {
                return (Label) value;
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        public DeleteCellRenderer() {
            super();
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(), com.arsdigita.cms.SecurityManager.EDIT_ITEM, orgaunit);

            if (canEdit) {
                final ControlLink link = new ControlLink((Label) value);
                link.setConfirmation(GenericOrgaUnitTextAssetGlobalizationUtil.globalize(
                        "cms.orgaunit.textasset.delete.confirm"));
                return link;
            } else {
                return (Label) value;
            }
        }

    }

    private class ActionListener implements TableActionListener {

        public ActionListener() {
            //Nothing
        }

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final DataObject dataObject = SessionManager.getSession().retrieve(new OID(
                    GenericOrgaUnitTextAsset.BASE_DATA_OBJECT_TYPE,
                    new BigDecimal(event.getRowKey().toString())));
            final GenericOrgaUnitTextAsset textAsset = new GenericOrgaUnitTextAsset(dataObject);

            final GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            final TableColumn col = getColumnModel().get(event.getColumn().intValue());

            if (TABLE_COL_EDIT.equals(col.getHeaderKey().toString())) {
                selectionModel.setSelectedObject(state, textAsset);
                parent.setEditVisible(state);
            } else if (TABLE_COL_DEL.equals(col.getHeaderKey().toString())) {
                textAsset.delete();
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing
        }

    }
}
