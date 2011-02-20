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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesEditshipTable extends Table implements TableActionListener {

    private static final Logger s_log =
                                Logger.getLogger(SeriesEditshipTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_EDITSHIP = "table_col_edit_editship";
    private final String TABLE_COL_DEL = "table_col_del";
    //private final String TABLE_COL_UP = "table_col_up";
    //private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;
    private SimpleEditStep editStep;

    public SeriesEditshipTable(ItemSelectionModel itemModel,
                               SimpleEditStep editStep) {
        super();
        m_itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.name").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.from").localize()));
        colModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.to").localize()));
        colModel.add(new TableColumn(
                3,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.edit").localize(),
                TABLE_COL_EDIT_EDITSHIP));
        colModel.add(new TableColumn(
                4,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.series.editship.remove").localize(),
                TABLE_COL_DEL));
        /* Just in the case someone want's to sort editships manually..." */
        /* colModel.add(new TableColumn(
        5,
        PublicationGlobalizationUtil.globalize(
        "publications.ui.series.editship.up").localize(),
        TABLE_COL_UP));
        colModel.add(new TableColumn(
        6,
        PublicationGlobalizationUtil.globalize(
        "publications.ui.series.editship.down").localize(),
        TABLE_COL_DOWN));*/

        setModelBuilder(new SeriesEditshipTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(3).setCellRenderer(new EditEditshipCellRenderer());
        colModel.get(4).setCellRenderer(new DeleteCellRenderer());
        //colModel.get(5).setCellRenderer(new UpCellRenderer());
        //colModel.get(6).setCellRenderer(new DownCellRenderer());
    }

    private class SeriesEditshipTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public SeriesEditshipTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Series series =
                   (Series) m_itemModel.getSelectedObject(state);
            return new SeriesEditshipTableModel(table, state, series);
        }
    }

    private class SeriesEditshipTableModel implements TableModel {

        private final int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private EditshipCollection m_editshipCollection;
        private GenericPerson m_editor;

        private SeriesEditshipTableModel(
                Table table,
                PageState state,
                Series series) {
            m_table = table;
            m_editshipCollection = series.getEditors();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_editshipCollection != null) && m_editshipCollection.next()) {
                m_editor = m_editshipCollection.getEditor();
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
                    return m_editor.getFullName();
                case 1:
                    return m_editshipCollection.getFrom();
                case 2:
                    return m_editshipCollection.getTo();
                case 3:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.series.editship.edit").localize();
                case 4:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.series.editship.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_editor.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Series series = (Series) m_itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    series);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class EditEditshipCellRenderer
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
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Series series = (Series) m_itemModel.getSelectedObject(state);

            boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    series);

            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.series.editship.remove.confirm").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    /*
    private class UpCellRenderer
    extends LockableImpl
    implements TableCellRenderer {

    @Override
    public Component getComponent(
    Table table,
    PageState state,
    Object value,
    boolean isSelected,
    Object key,
    int row,
    int col) {

    if (0 == row) {
    s_log.debug("Row is first row in table, don't show up link");
    Label label = new Label("");
    return label;
    } else {
    ControlLink link = new ControlLink("up");
    return link;
    }
    }
    }*/

    /*
    private class DownCellRenderer
    extends LockableImpl
    implements TableCellRenderer {

    @Override
    public Component getComponent(
    Table table,
    PageState state,
    Object value,
    boolean isSelected,
    Object key,
    int row,
    int col) {

    Series = (Series) m_itemModel.
    getSelectedObject(state);
    EditshipCollection editors = series.getEditors();

    if ((editors.size() - 1)
    == row) {
    s_log.debug("Row is last row in table, don't show down link");
    Label label = new Label("");
    return label;
    } else {
    ControlLink link = new ControlLink("down");
    return link;
    }
    }
    }*/
    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        GenericPerson editor =
                      new GenericPerson(new BigDecimal(event.getRowKey().
                toString()));

        Series series = (Series) m_itemModel.getSelectedObject(state);

        EditshipCollection editors = series.getEditors();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if(TABLE_COL_EDIT_EDITSHIP.equals(column.getHeaderKey().toString())) {
            while(editors.next()) {
                if(editors.getEditor().equals(editor)) {
                    break;
                }
            }

            ((SeriesEditshipStep)editStep).setSelectedEditor(editor);
            ((SeriesEditshipStep)editStep).setSelectedEditorDateFrom(editors.getFrom());
            ((SeriesEditshipStep)editStep).setSelectedEditorDateTo(editors.getTo());

            editStep.showComponent(state, SeriesEditshipStep.ADD_EDITOR_SHEET_NAME);
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            series.removeEditor(editor);
        }
        /*
        else if(TABLE_COL_UP.equals(column.getHeaderKey().toString())) {
        editors.swapWithPrevious(editor);
        } else if(TABLE_COL_DOWN.equals(column.getHeaderKey().toString())) {
        authors.swapWithNext(editor);
        }
         */
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do here.
    }
}
