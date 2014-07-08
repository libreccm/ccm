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
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Sheet which displays the proceedings associated with a InProceedings.
 *
 * @author Jens Pelzetter
 */
public class InProceedingsProceedingsSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public InProceedingsProceedingsSheet(ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.inProceedings.proceedings.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.inProceedings.proceedings")),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.inProceedings.proceedings.remove")),
                TABLE_COL_DEL));

        setModelBuilder(new InProceedingsProceedingsSheetModelBuilder(
                itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class InProceedingsProceedingsSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public InProceedingsProceedingsSheetModelBuilder(
                ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            InProceedings inProceedings = (InProceedings) itemModel.
                    getSelectedObject(state);
            return new InProceedingsProceedingsSheetModel(table,
                                                          state,
                                                          inProceedings);
        }
    }

    private class InProceedingsProceedingsSheetModel implements TableModel {

        private Table table;
        private Proceedings proceedings;
        private boolean done;

        public InProceedingsProceedingsSheetModel(Table table,
                                                  PageState state,
                                                  InProceedings inProceedings) {
            this.table = table;
            this.proceedings = inProceedings.getProceedings();
            if (proceedings == null) {
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
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return proceedings.getTitle();
                case 1:
                    return new Label(PublicationGlobalizationUtil.globalize(
                            "publications.ui.inProceedings.proceedings.remove"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return proceedings.getID();
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
                                      int column) {
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            InProceedings inProceedings = (InProceedings) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        inProceedings);
            if (canEdit) {
                Proceedings proceedings;
                try {
                    proceedings = new Proceedings((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = proceedings.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            proceedings.getLanguage()),
                              resolver.generateItemURL(state,
                                                       proceedings,
                                                       section,
                                                       proceedings.getVersion()));

                return link;
            } else {
                Proceedings proceedings;
                try {
                    proceedings = new Proceedings((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                Label label =
                      new Label(String.format("%s (%s)",
                                              value.toString(),
                                              proceedings.getLanguage()));
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
            InProceedings inProceedings = (InProceedings) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    inProceedings);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                     "publications.ui.inProceedings.proceedings.confirm_remove"));
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

        InProceedings inProceedings = (InProceedings) itemModel.
                getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            inProceedings.setProceedings(null);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }
}
