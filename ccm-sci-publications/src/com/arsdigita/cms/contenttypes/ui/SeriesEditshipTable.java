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
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */


public class SeriesEditshipTable extends Table implements TableActionListener {

    private static final Logger s_log = Logger.getLogger(SeriesEditshipTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_EDITSHIP = "table_col_edit_editship";
    private final String TABLE_COL_DEL = "table_col_del";
    //private final String TABLE_COL_UP = "table_col_up";
    //private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;
    private SeriesEditshipStep editStep;

    public SeriesEditshipTable(ItemSelectionModel itemModel,
                               SimpleEditStep editStep) {
        super();
        m_itemModel = itemModel;
        this.editStep = (SeriesEditshipStep) editStep;

        setEmptyView(
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.name")),
            TABLE_COL_EDIT));
        colModel.add(new TableColumn(
            1,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.from"))));
        colModel.add(new TableColumn(
            2,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.to"))));
        colModel.add(new TableColumn(
            3,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.edit")),
            TABLE_COL_EDIT_EDITSHIP));
        colModel.add(new TableColumn(
            4,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.remove")),
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

        addTableActionListener(this);
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
            Series series = (Series) m_itemModel.getSelectedObject(state);
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
                    if (m_editshipCollection.getFrom() == null) {
                        return ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                    } else {
                        return DateFormat.getDateInstance(DateFormat.LONG).
                            format(
                                m_editshipCollection.getFrom());
                    }
                case 2:
                    if (m_editshipCollection.getTo() == null) {
                        return ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                    } else {
                        return DateFormat.getDateInstance(DateFormat.LONG).
                            format(
                                m_editshipCollection.getTo());
                    }
                case 3:
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.series.editship.edit"));
                case 4:
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.series.editship.remove"));
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
            SecurityManager securityManager = CMS.getSecurityManager(state);
            Series series = (Series) m_itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.EDIT_ITEM,
                series);

            if (canEdit) {
                GenericPerson editor;
                try {
                    editor = new GenericPerson((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }
                ContentSection section = editor.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(String.format("%s",
                                                   value.toString(),
                                                   editor.getLanguage()),
                                     resolver.generateItemURL(state,
                                                              editor,
                                                              section,
                                                              editor.getVersion()));

                return link;
            } else {
                GenericPerson editor;
                try {
                    editor = new GenericPerson((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }
                Label label = new Label(String.format("%s",
                                                      value.toString(),
                                                      editor.getLanguage()));
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
            SecurityManager securityManager = CMS.getSecurityManager(state);
            Series series = (Series) m_itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.EDIT_ITEM,
                series);

            if (canEdit) {
                ControlLink link = new ControlLink((Label) value);
                return link;
            } else {
                return new Label("");
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
            SecurityManager securityManager = Utilities.getSecurityManager(state);
            Series series = (Series) m_itemModel.getSelectedObject(state);

            boolean canDelete = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.DELETE_ITEM,
                series);

            if (canDelete) {
                ControlLink link = new ControlLink((Label) value);
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                    "publications.ui.series.editship.remove.confirm"));
                return link;
            } else {
                return new Label("");
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

        GenericPerson editor = new GenericPerson(new BigDecimal(event.getRowKey().
            toString()));

        Series series = (Series) m_itemModel.getSelectedObject(state);

        EditshipCollection editors = series.getEditors();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if (TABLE_COL_EDIT_EDITSHIP.equals(column.getHeaderKey().
            toString())) {
            while (editors.next()) {
                if (editors.getEditor().equals(editor)) {
                    break;
                }
            }

            editStep.setSelectedEditor(editor);
            editStep.setSelectedEditorDateFrom(editors.getFrom());
            editStep.setSelectedEditorDateTo(editors.getTo());

            editors.close();

            editStep.showComponent(state,
                                   SeriesEditshipStep.ADD_EDITOR_SHEET_NAME);
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
