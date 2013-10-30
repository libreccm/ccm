package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;
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
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.util.LockableImpl;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AtoZProviderTable extends Table implements TableActionListener {

    //private static final String EDIT = "edit";
    //private static final String DELETE = "delete";
    private final static String TABLE_COL_DELETE = "table_col_delete";
    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final ACSObjectSelectionModel selectedProvider;
    private final ApplicationInstanceAwareContainer parent;

    public AtoZProviderTable(final ACSObjectSelectionModel provider) {
        this(provider, null);
    }

    public AtoZProviderTable(final ACSObjectSelectionModel provider,
                             final ApplicationInstanceAwareContainer parent) {
        super();

        this.parent = parent;
        this.selectedProvider = provider;

        setEmptyView(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.empty")));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0,
                AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.provider_name").localize()));

        columnModel.add(new TableColumn(
                1,
                AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.description").localize()));

        columnModel.add(new TableColumn(
                2,
                AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.type").localize()));

        columnModel.add(new TableColumn(
                3,
                AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.edit").localize(),
                TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                4,
                AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.delete").localize(),
                TABLE_COL_DELETE));

        setModelBuilder(new AtoZTableModelBuilder());

        columnModel.get(3).setCellRenderer(new EditCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final OID providerId = OID.valueOf(event.getRowKey().toString());

        final TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            final AtoZProvider provider = (AtoZProvider) DomainObjectFactory.newInstance(providerId);
            selectedProvider.setSelectedObject(state, provider);
        } else if (TABLE_COL_DELETE.equals(column.getHeaderKey().toString())) {
            final AtoZProvider provider = (AtoZProvider) DomainObjectFactory.newInstance(providerId);
            provider.delete();
            selectedProvider.clearSelection(state);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

    private class AtoZTableModelBuilder extends LockableImpl implements TableModelBuilder {

        public AtoZTableModelBuilder() {
            super();
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new AtoZTableModel(table);
        }
    }

    private class AtoZTableModel implements TableModel {

        private final Table table;
        private final DomainCollection providers;

        public AtoZTableModel(final Table table) {
            this.table = table;
            final AtoZ atoz;
            if (parent == null) {
                atoz = (AtoZ) Kernel.getContext().getResource();
            } else {
                atoz = (AtoZ) parent.getAppInstance();
            }
            providers = atoz.getProviders();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            return providers.next();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return ((AtoZProvider) providers.getDomainObject()).getTitle();
                case 1:
                    return ((AtoZProvider) providers.getDomainObject()).getDescription();
                case 2:
                    return ((AtoZProvider) providers.getDomainObject()).getObjectType().getName();
                case 3:
                    return AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.edit").
                            localize();
                case 4:
                    return AtoZGlobalizationUtil.globalize("atoz.ui.providers_table.delete").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return providers.getDomainObject().getOID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditCellRenderer() {
            super();
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int col) {
            return new ControlLink((String) AtoZGlobalizationUtil.globalize(
                    "atoz.ui.providers_table.edit").localize());
//            state.setControlEvent(table, EDIT, key.toString());
//            try {
//                return new Link(value.toString(), state.stateAsURL());
//            } catch (IOException ex) {
//                throw new UncheckedWrapperException("Failed to create edit link for AtoZ provider.", ex);
//            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        public DeleteCellRenderer() {
            super();
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ControlLink link = new ControlLink((String) AtoZGlobalizationUtil.globalize(
                    "atoz.ui.providers_table.delete").localize());

            link.setConfirmation((String) AtoZGlobalizationUtil.globalize(
                    "atoz.ui.providers_table.delete.confirm").
                    localize());

            return link;


//            state.setControlEvent(table, DELETE, key.toString());
//            try {
//                return new Link(value.toString(), state.stateAsURL());
//            } catch (IOException ex) {
//                throw new UncheckedWrapperException("Failed to create delete link for AtoZ provider.", ex);
//            }
        }

    }
}
