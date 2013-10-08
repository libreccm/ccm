/*
 * Copyright (c) 2011, 2013 Jens Pelzetter
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_TARGET = "table_col_target";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel itemModel;

    public PublicPersonalProfileNavigationTable(final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.empty")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.target")),
                TABLE_COL_EDIT));

        colModel.add(new TableColumn(
                1,
                new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.targetitem")),
                TABLE_COL_TARGET));

        colModel.add(new TableColumn(
                2,
                new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.remove")),
                TABLE_COL_DEL));

        setModelBuilder(new PublicPersonalProfileNavigationTableModelBuilder(
                itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new TargetCellRenderer());
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class PublicPersonalProfileNavigationTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public PublicPersonalProfileNavigationTableModelBuilder(
                final ItemSelectionModel itemModel) {
            super();
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.
                    getSelectedObject(state);
            return new PublicPersonalProfileNavigationTableModel(table, profile);
        }

    }

    private class PublicPersonalProfileNavigationTableModel
            implements TableModel {

        private final Table table;
        private final DataCollection linkCollection;
        private final PublicPersonalProfileNavItemCollection navItems;

        public PublicPersonalProfileNavigationTableModel(final Table table,
                                                         final PublicPersonalProfile profile) {
            this.table = table;
            linkCollection = RelatedLink.getRelatedLinks(
                    profile, PublicPersonalProfile.LINK_LIST_NAME);
            navItems = new PublicPersonalProfileNavItemCollection();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            return linkCollection.next();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final RelatedLink link = new RelatedLink(linkCollection.getDataObject());
            final String key = link.getTitle();
            PublicPersonalProfileNavItem navItem;

            navItem = navItems.getNavItem(key, GlobalizationHelper.
                    getNegotiatedLocale().getLanguage());
            navItems.reset();
            if ((navItem == null) && (Kernel.getConfig().languageIndependentItems())) {
                navItem = navItems.getNavItem(key, GlobalizationHelper.LANG_INDEPENDENT);
            }
            navItems.reset();

            switch (columnIndex) {
                case 0:
                    if (navItem.getGeneratorClass() == null) {
                        return new Label(navItem.getLabel());
                    } else {
                        return new Label(String.format("%s (auto)", navItem.getLabel()));
                    }
                case 1:
                    final ContentItem targetItem = link.getTargetItem();
                    if (targetItem instanceof PublicPersonalProfile) {
                        return null;
                    } else {
                        return targetItem;
                    }
                case 2:
                    return new Label(
                            PublicPersonalProfileGlobalizationUtil.globalize(
                            "publicpersonalprofile.ui.nav.remove"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            final RelatedLink link = new RelatedLink(linkCollection.getDataObject());
            return link.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

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
                                      final int col) {
            return (Component) value;
        }
    }

    private class TargetCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public TargetCellRenderer() {
            super();
        }

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (value == null) {
                return new Label("");
            } else {
                final ContentPage item = (ContentPage) value;

                final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(
                        state);

                final boolean canEdit = securityManager.canAccess(
                        state.getRequest(),
                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                        item);

                if (canEdit) {
                    final ContentSection section = item.getContentSection();
                    final ItemResolver resolver = section.getItemResolver();

                    final Link link = new Link(item.getTitle(),
                                               resolver.generateItemURL(
                            state, item, section, item.getVersion()));

                    return link;
                } else {
                    return new Label(item.getTitle());
                }
            }
        }
    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {
        
        public DeleteCellRenderer() {
            //Nothing
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int col) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.
                    getSelectedObject(
                    state);

            final boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    profile);

            if (canDelete) {
                final ControlLink link = new ControlLink((Component) value);
                link.setConfirmation(PublicPersonalProfileGlobalizationUtil.
                        globalize("publicpersonalprofile.ui.nav.remove.confirm"));
                return link;
            } else {
                return (Component) value;
            }
        }
    }

    private class ActionListener implements TableActionListener {

        public ActionListener() {
            //Nothing
        }

        @Override
        public void cellSelected(final TableActionEvent event) {
            final TableColumn column = getColumnModel().get(event.getColumn().intValue());

            if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                final BigDecimal linkId = new BigDecimal(event.getRowKey().toString());
                final RelatedLink link = new RelatedLink(linkId);
                link.delete();
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing to do here.
        }

    }
}
