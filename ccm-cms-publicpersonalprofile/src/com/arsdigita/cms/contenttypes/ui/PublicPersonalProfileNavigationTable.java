package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
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
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationTable
        extends Table
        implements TableActionListener {

    private static final Logger logger = Logger.getLogger(
            PublicPersonalProfileNavigationTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;
    private SimpleEditStep editStep;

    public PublicPersonalProfileNavigationTable(ItemSelectionModel itemModel,
                                                   SimpleEditStep editStep) {
        super();
        this.itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(new Label(PublicPersonalProfileGlobalizationUtil.
                globalize("publicpersonalprofile.ui.nav.empty")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.target").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new PublicPersonalProfileNavigationTableModelBuilder(
                itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class PublicPersonalProfileNavigationTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public PublicPersonalProfileNavigationTableModelBuilder(
                ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new PublicPersonalProfileNavigationTableModel(table, state);
        }
    }

    private class PublicPersonalProfileNavigationTableModel
            implements TableModel {

        private Table table;
        private String[] mockNav = new String[]{"Allgemein", "Beruflich",
                                                "Forschung", "Lehre", "Projekte",
                                                "Publikationen"};
        private int index = -1;
        private String mockNavItem;

        public PublicPersonalProfileNavigationTableModel(Table table,
                                                            PageState state) {
            this.table = table;
        }

        @Override
        public int getColumnCount() {
            return mockNav.length;
        }

        @Override
        public boolean nextRow() {
            if (index < (mockNav.length - 1)) {
                index++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return mockNav[index];
                case 1:
                    return PublicPersonalProfileGlobalizationUtil.globalize(
                            "publicpersonalprofile.ui.nav.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return index;
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
            //com.arsdigita.cms.SecurityManager securityManager = Utilities.getSecurityManager(state);

            return new Label((String) value);
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
            /*Publication publication = (Publication) m_itemModel.
            getSelectedObject(state);
            
            boolean canDelete = securityManager.canAccess(
            state.getRequest(),
            SecurityManager.DELETE_ITEM,
            publication);
            
            if (canDelete) {
            ControlLink link = new ControlLink(value.toString());
            link.setConfirmation((String) PublicationGlobalizationUtil.
            globalize(
            "publications.ui.authors.author.confirm_remove").
            localize());
            return link;
            } else {*/
            Label label = new Label(value.toString());
            return label;
            //}
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());


        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do here.
    }
}
