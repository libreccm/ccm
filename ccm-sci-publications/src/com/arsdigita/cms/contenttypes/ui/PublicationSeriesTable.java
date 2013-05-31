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
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationSeriesTable
        extends Table
        implements TableActionListener {

    private static final Logger LOGGER = Logger.getLogger(PublicationSeriesTable.class);
    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_NUMBER = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel m_itemModel;

    public PublicationSeriesTable(final ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize("publications.ui.series.none")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize("publications.ui.series.title").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize("publications.ui.series.number").localize(),
                TABLE_COL_NUMBER));
        colModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize("publications.ui.series.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(
                new PublicationSeriesTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new NumberCellRenderer());
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());

        LOGGER.info("Adding table action listener...");
        addTableActionListener(this);
    }

    private class PublicationSeriesTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private final ItemSelectionModel m_itemModel;

        public PublicationSeriesTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);
            return new PublicationSeriesTableModel(table, state, publication);
        }

    }

    private class PublicationSeriesTableModel implements TableModel {

        private Table m_table;
        private SeriesCollection m_seriesCollection;
        private Series m_series;

        public PublicationSeriesTableModel(Table table,
                                           PageState state,
                                           Publication publication) {
            m_table = table;
            m_seriesCollection = publication.getSeries();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_seriesCollection != null)
                && m_seriesCollection.next()) {
                m_series = m_seriesCollection.getSeries();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_series.getTitle();
                case 1:
                    return m_series.getTitle();
                case 2:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.series.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_series.getID();
        }

    }

    private class EditCellRenderer
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
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    publication);

            if (canEdit) {
                Series series;
                try {
                    series = new Series((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    LOGGER.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                ContentSection section = series.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            series.getLanguage()),
                              resolver.generateItemURL(state,
                                                       series,
                                                       section,
                                                       series.getVersion()));

                return link;
            } else {
                Series series;
                try {
                    series = new Series((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    LOGGER.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                Label label = new Label(String.format("%s (%s)",
                                                      value.toString(),
                                                      series.getLanguage()));
                return label;
            }
        }
    }
    
    private class NumberCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(final Table table, 
                                      final PageState state, 
                                      final Object value, 
                                      final boolean isSelected, 
                                      final Object key,
                                      final int row, 
                                      final int column) {
            final Publication publication = (Publication) m_itemModel.getSelectedObject(state);
            
            final BigDecimal seriesId = (BigDecimal) key;
            
            final SeriesCollection seriesCol = publication.getSeries();            
            
            String volumeOfSeries = null;            
            while(seriesCol.next()) {
                if (seriesId.equals(seriesCol.getSeries().getID())) {
                    volumeOfSeries = seriesCol.getVolumeOfSeries();
                    break;
                }
            }            
            seriesCol.close();
            
            if (volumeOfSeries == null) {
                return new Label("");
            } else {
                return new Label(volumeOfSeries);
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
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);

            boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    publication);

            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.series.confirm_remove").
                        localize());
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

        LOGGER.info("cellSelected!");

        Series series =
               new Series(new BigDecimal(event.getRowKey().
                toString()));

        Publication publication = (Publication) m_itemModel.getSelectedObject(
                state);

        SeriesCollection seriesCollection = publication.getSeries();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            publication.removeSeries(series);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do here.
    }

}
