/*
 * Copyright (c) 2014 Jens Pelzetter
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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciPublicationsDramaticArtsGlobalisationUtil;
import com.arsdigita.cms.contenttypes.SciPublicationsMovie;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieProductionCompanySheet
    extends Table
    implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final SciPublicationsDramaticArtsGlobalisationUtil globalisationUtil
                                                                   = new SciPublicationsDramaticArtsGlobalisationUtil();
    private ItemSelectionModel itemModel;

    public SciPublicationsMovieProductionCompanySheet(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(globalisationUtil.globalize(
            "publications.dramaticarts.ui.movie.production_company.none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            0,
            globalisationUtil.globalize("publications.dramaticarts.ui.movie.production_company"),
            TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
            1,
            globalisationUtil.globalize(
                "publications.dramaticarts.ui.movie.production_company.remove"),
            TABLE_COL_DEL));

        setModelBuilder(new ModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            final SciPublicationsMovie movie = (SciPublicationsMovie) itemModel.getSelectedObject(
                state);
            return new SheetModel(table, state, movie);
        }

    }

    private class SheetModel implements TableModel {

        private Table table;
        private GenericOrganizationalUnit productionCompany;
        private boolean done;

        public SheetModel(final Table table,
                          final PageState state,
                          final SciPublicationsMovie movie) {
            this.table = table;
            productionCompany = movie.getProductionCompany();
            if (productionCompany == null) {
                done = false;
            } else {
                done = true;
            }

        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if (done) {
                ret = true;
                done = false;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return productionCompany.getTitle();
                case 1:
                    return new Label(globalisationUtil.globalize(
                        "publications.dramaticarts.ui.movie.production_company.remove"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return productionCompany.getID();
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

            final SciPublicationsMovie movie = (SciPublicationsMovie) itemModel.getSelectedObject(
                state);

            final boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                              com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                              movie);

            if (canEdit) {
                final GenericOrganizationalUnit company;
                try {
                    company = new GenericOrganizationalUnit((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                final ContentSection section = company.getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(value.toString(),
                                           resolver.generateItemURL(state,
                                                                    company,
                                                                    section,
                                                                    company.getVersion()));

                return link;
            } else {
                return new Label(value.toString());
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

            final SciPublicationsMovie movie = (SciPublicationsMovie) itemModel.getSelectedObject(
                state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                movie);

            if (canEdit) {
                final ControlLink link = new ControlLink((Label) value);
                link.setConfirmation(globalisationUtil.globalize(
                    "publications.dramaticarts.ui.movie.production_company.remove.confirm"));
                return link;
            } else {
                return (Label) value;
            }
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final SciPublicationsMovie movie = (SciPublicationsMovie) itemModel.getSelectedObject(state);

        final TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            //Nothing
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            movie.setProductionCompany(null);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
