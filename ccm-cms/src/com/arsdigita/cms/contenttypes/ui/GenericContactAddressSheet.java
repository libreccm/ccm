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
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericContactAddressSheet extends Table implements TableActionListener {

    private static final String TABLE_COL_EDIT = "table_col_edit";
    private static final String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel itemModel;

    public GenericContactAddressSheet(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.contact.emptyAddress")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            new Label(),
            TABLE_COL_EDIT));

        colModel.add(new TableColumn(
            1,
            new Label(),
            TABLE_COL_DEL));

        setModelBuilder(new GenericContactAddressTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    /**
     * 
     */
    private class GenericContactAddressTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public GenericContactAddressTableModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final GenericContact contact = (GenericContact) itemModel.getSelectedObject(state);
            return new GenericContactAddressTableModel(table, state, contact);
        }

    }

    private class GenericContactAddressTableModel implements TableModel {

        private final Table table;
        private final GenericAddress address;
        private boolean done;

        public GenericContactAddressTableModel(final Table table,
                                               final PageState state,
                                               final GenericContact contact) {
            this.table = table;
            address = contact.getAddress();
            if (address == null) {
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
                    return address.getTitle();
                case 1:
                    return ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.contact.delete_address.button_label");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return address.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditCellRenderer() {
            //Nothing
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericContact contact = (GenericContact) itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                contact);

            if (canEdit) {
                final GenericAddress address;

                try {
                    address = new GenericAddress((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                final ContentSection section = address.getContentSection();
                final ItemResolver resolver = section.getItemResolver();

                return new Link(value.toString(),
                                resolver.generateItemURL(state,
                                                         address,
                                                         section,
                                                         address.getVersion()));
            } else {
                return new Label(value.toString());
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericContact contact = (GenericContact) itemModel.getSelectedObject(
                state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                contact);

            if (canEdit) {
                final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
                link.setConfirmation(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.person.confirm_remove"));
                return link;
            } else {
                Label label = new Label("");
                return label;
            }

        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();
        
        final GenericContact contact = (GenericContact) itemModel.getSelectedObject(state);
        
        final TableColumn column = getColumnModel().get(event.getColumn().intValue());
        
        if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            contact.unsetAddress();
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
