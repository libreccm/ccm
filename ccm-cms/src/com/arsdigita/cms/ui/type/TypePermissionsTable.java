package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.util.LockableImpl;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class TypePermissionsTable extends Table {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final ContentTypeRequestLocal type;

    public TypePermissionsTable(final ContentSectionRequestLocal section,
                                final ContentTypeRequestLocal type) {
        super();

        this.type = type;

        setEmptyView(new Label(GlobalizationUtil.globalize(
                "cms.ui.type.permissions.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(0, GlobalizationUtil.globalize(
                "cms.ui.type.permission"), TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                1,
                GlobalizationUtil.globalize(
                "cms.ui.type.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new TypePermissionsTableModelBuilder());

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());
    }

    private class TypePermissionsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public TypePermissionsTableModelBuilder() {
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new TypePermissionsTableModel(table, state);
        }
    }

    private class TypePermissionsTableModel implements TableModel {

        private Table table;
        private ObjectPermissionCollection permissions;

        public TypePermissionsTableModel(Table table, PageState state) {
            this.table = table;
            ContentType type =
                        ((TypePermissionsTable) table).getType().getContentType(
                    state);

            permissions = PermissionService.getDirectGrantedPermissions(type.
                    getOID());
        }

        public int getColumnCount() {
            if (permissions == null) {
                return 0;
            } else {
                return (int) permissions.size();
            }
        }

        public boolean nextRow() {
            if (permissions == null) {
                return false;
            } else {
                return permissions.next();
            }
        }

        public Object getElementAt(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return permissions.toString();
                case 1:
                    return GlobalizationUtil.globalize("cms.ui.type.permission.remove");
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return columnIndex;
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            return new Label("");
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            return new Label("");
        }
    }

    private ContentTypeRequestLocal getType() {
        return type;
    }
}
