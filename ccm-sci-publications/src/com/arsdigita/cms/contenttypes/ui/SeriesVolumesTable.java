/*
 * Copyright (c) 2010 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.VolumeInSeriesCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SeriesVolumesTable extends Table {

    private static final Logger LOGGER = Logger.getLogger(SeriesVolumesTable.class);
    private static final String TABLE_COL_EDIT = "table_col_edit";
    @SuppressWarnings("PMD.LongVariable")
    private static final String TABLE_COL_EDIT_ASSOC = "table_col_edit_assoc";
    private static final String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel m_itemModel;
    private final SimpleEditStep editStep;

    public SeriesVolumesTable(final ItemSelectionModel itemModel,
                              final SimpleEditStep editStep) {
        super();
        m_itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.none")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.name").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.volume_of_series").localize()));
        colModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.edit_assoc").localize(),
                TABLE_COL_EDIT_ASSOC));
        colModel.add(new TableColumn(
                3,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.volumes.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new SeriesVolumesTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(2).setCellRenderer(new EditAssocCellRenderer());
        colModel.get(3).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class SeriesVolumesTableModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel m_itemModel;

        public SeriesVolumesTableModelBuilder(final ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final Series series = (Series) m_itemModel.getSelectedObject(state);
            return new SeriesVolumesTableModel(table, series);
        }

    }

    private class SeriesVolumesTableModel implements TableModel {

        private final Table table;
        private final VolumeInSeriesCollection volumesCollection;
        private Publication publication;

        public SeriesVolumesTableModel(final Table table,
                                       final Series series) {
            this.table = table;
            volumesCollection = series.getVolumes();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((volumesCollection != null) && volumesCollection.next()) {
                publication = volumesCollection.getPublication();
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
                    return publication.getTitle();
                case 1:
                    return volumesCollection.getVolumeOfSeries();
                case 2:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.series.volumes.edit_assoc").localize();
                case 3:
                    return PublicationGlobalizationUtil.globalize(
                            "publication.ui.series.volumes.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return publication.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditCellRenderer() {
            super();
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
            final SecurityManager securityManager = CMS.getSecurityManager(state);
            final Series series = (Series) m_itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    series);

            if (canEdit) {
                Publication volume;
                try {
                    volume = new Publication((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    LOGGER.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                final ContentSection section = volume.getContentSection();//CMS.getContext().getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(String.format("%s",
                                                         value.toString()),
                                           resolver.generateItemURL(state,
                                                                    volume,
                                                                    section,
                                                                    volume.getVersion()));

                return link;
            } else {
                final Publication volume;
                try {
                    volume = new Publication((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    LOGGER.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                final Label label = new Label(String.format("%s",
                                                            value.toString()));
                return label;
            }
        }

    }

    private class EditAssocCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditAssocCellRenderer() {
            super();
        }

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final SecurityManager securityManager =
                                  CMS.getSecurityManager(state);
            final Series series = (Series) m_itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    series);

            if (canEdit) {
                return new ControlLink(value.toString());
            } else {
                return new Label(value.toString());
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int col) {
            final SecurityManager securityManager = CMS.getSecurityManager(state);
            final Series series = (Series) m_itemModel.getSelectedObject(state);

            final boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    series);

            if (canDelete) {
                final ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.series.volumes.remove.confirm").
                        localize());
                return link;
            } else {
                final Label label = new Label(value.toString());
                return label;
            }
        }

    }

    private class ActionListener implements TableActionListener {

        public ActionListener() {
            //Nothing
        }

        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final Publication publication = new Publication(new BigDecimal(event.getRowKey().toString()));

            final Series series = (Series) m_itemModel.getSelectedObject(state);

            final TableColumn column = getColumnModel().get(event.getColumn().intValue());

            final VolumeInSeriesCollection volumes = series.getVolumes();

            if (TABLE_COL_EDIT_ASSOC.equals(column.getHeaderKey().toString())) {
                while (volumes.next()) {
                    if (volumes.getPublication(publication.getLanguage()).equals(publication)) {
                        break;
                    }
                }

                ((SeriesVolumesStep) editStep).setSelectedPublication(publication);
                ((SeriesVolumesStep) editStep).setSelectedVolume(volumes.getVolumeOfSeries());

                volumes.close();

                editStep.showComponent(state,
                                       SeriesVolumesStep.ADD_VOLUME_SHEET_NAME);
            } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                series.removeVolume(publication);
            }
        }

        public void headSelected(final TableActionEvent event) {
            //Nothing to do.
        }

    }
}
