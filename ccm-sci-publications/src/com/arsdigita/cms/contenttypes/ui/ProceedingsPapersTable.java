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
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsCollection;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPapersTable
        extends Table
        implements TableActionListener {

    private final Logger s_log = Logger.getLogger(
            ProceedingsPapersTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public ProceedingsPapersTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.procreedings.no_papers")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.proceedings.paper")),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.proceedings.paper.remove")),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                2,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.procedings.paper.up")),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                3,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.proceedings.paper.down")),
                TABLE_COL_DOWN));

        setModelBuilder(
                new ProceedingsPapersTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class ProceedingsPapersTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public ProceedingsPapersTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Proceedings proceedings = (Proceedings) m_itemModel
                                                    .getSelectedObject(state);
            return new ProceedingsPapersTableModel(table,
                                                   state,
                                                   proceedings);
        }
    }

    private class ProceedingsPapersTableModel implements TableModel {

        private Table m_table;
        private InProceedingsCollection m_papers;
        private InProceedings m_paper;

        private ProceedingsPapersTableModel(Table table,
                                            PageState state,
                                            Proceedings proceedings) {
            m_table = table;
            m_papers = proceedings.getPapers();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_papers != null) && m_papers.next()) {
                m_paper = m_papers.getPaper();
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
                    return m_paper.getTitle();
                case 1:
                    return new Label(PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.remove"));
                case 2:
                    return new Label(PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.up"));
                case 3:
                    return new Label(PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.down"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_paper.getID();
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
            Proceedings proceedings = (Proceedings) m_itemModel.getSelectedItem(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    proceedings);

            if (canEdit) {
                InProceedings inProceedings;
                try {
                    inProceedings = new InProceedings((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                ContentSection section = inProceedings.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(String.format("%s (%s)",
                                                   value.toString(),
                                                   inProceedings.getLanguage()),
                                     resolver.generateItemURL(state,
                                                              inProceedings,
                                                              section,
                                                              inProceedings.
                        getVersion()));

                return link;
            } else {
                InProceedings inProceedings;
                try {
                    inProceedings = new InProceedings((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }


                Label label = new Label(
                        String.format("%s (%s)",
                                      value.toString(),
                                      inProceedings.getLanguage()));
                return label;
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
                                      int column) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Proceedings proceedings = (Proceedings) m_itemModel.getSelectedItem(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    proceedings);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                        "publications.ui.proceedings.paper.confirm_remove"));
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class UpCellRenderer
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
            if (0 == row) {
                s_log.debug("Row is first row in table, don't show up link");
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink(value.toString());
                return link;
            }
        }
    }

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

            Proceedings proceedings =
                        (Proceedings) m_itemModel.getSelectedObject(
                    state);
            InProceedingsCollection papers =
                                    proceedings.getPapers();

            if ((papers.size() - 1)
                == row) {
                s_log.debug("Row is last row in table, don't show down link");
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink(value.toString());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        InProceedings paper =
                      new InProceedings(
                new BigDecimal(event.getRowKey().toString()));

        Proceedings proceedings =
                    (Proceedings) m_itemModel.getSelectedItem(state);

        InProceedingsCollection papers =
                                proceedings.getPapers();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            proceedings.removePaper(paper);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            papers.swapWithPrevious(paper);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            papers.swapWithNext(paper);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Noting to do
    }
}
