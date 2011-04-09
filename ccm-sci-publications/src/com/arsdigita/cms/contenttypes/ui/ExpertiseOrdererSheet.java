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
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertiseOrdererSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public ExpertiseOrdererSheet(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.orderer.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.orderer").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.orderer.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new ExpertiseOrganizationSheetModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class ExpertiseOrganizationSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public ExpertiseOrganizationSheetModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Expertise expertise = (Expertise) itemModel.getSelectedObject(state);
            return new ExpertiseOrganizationSheetModel(table, state, expertise);
        }
    }

    private class ExpertiseOrganizationSheetModel implements TableModel {

        private Table table;
        private GenericOrganizationalUnit orga;
        private boolean done;

        public ExpertiseOrganizationSheetModel(final Table table,
                                               final PageState state,
                                               final Expertise expertise) {
            this.table = table;
            orga = expertise.getOrderer();
            if (orga == null) {
                done = false;
            } else {
                done = true;
            }
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

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

        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return orga.getTitle();
                case 1:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.expertise.orderer.remove").localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(final int columnIndex) {
            return orga.getID();
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
            Label label = new Label(value.toString());
            return label;
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
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            Expertise expertise = (Expertise) itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    expertise);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publication.ui.expertise.orderer.remove.confirm").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        Expertise expertise = (Expertise) itemModel.getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            expertise.setOrganization(null);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }
}
