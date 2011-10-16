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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSuperiorCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSuperiorOrgaUnitsTable extends Table {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private final ItemSelectionModel itemModel;
    private final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer;

    public GenericOrganizationalUnitSuperiorOrgaUnitsTable(
            final ItemSelectionModel itemModel,
            final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer) {
        super();
        this.itemModel = itemModel;
        this.customizer = customizer;

        setEmptyView(new Label(customizer.getEmptyViewLabel()));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(0,
                                        customizer.getNameColumnLabel(),
                                        TABLE_COL_EDIT));
        columnModel.add(new TableColumn(1,
                                        customizer.getDeleteColumnLabel(),
                                        TABLE_COL_DEL));
        columnModel.add(new TableColumn(2,
                                        customizer.getUpColumnLabel(),
                                        TABLE_COL_UP));
        columnModel.add(new TableColumn(3,
                                        customizer.getDownColumnLabel(),
                                        TABLE_COL_DOWN));

        setModelBuilder(new ModelBuilder(itemModel, customizer));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());
        columnModel.get(2).setCellRenderer(new UpCellRenderer());
        columnModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;
        private final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer;

        public ModelBuilder(
                final ItemSelectionModel itemModel,
                final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer) {
            this.itemModel = itemModel;
            this.customizer = customizer;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);
            return new Model(table, state, orgaunit, customizer);
        }
    }

    private class Model implements TableModel {

        private final Table table;
        private final GenericOrganizationalUnitSuperiorCollection superiorOrgaUnits;
        private final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer;

        public Model(final Table table,
                     final PageState state,
                     final GenericOrganizationalUnit orgaunit,
                     final GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer customizer) {
            this.table = table;
            superiorOrgaUnits = orgaunit.getSuperiorOrgaUnits();
            if ((customizer.getAssocType() != null)
                && !(customizer.getAssocType().isEmpty())) {
                superiorOrgaUnits.addFilter(String.format("assocType = '%s",
                                                          customizer.
                        getAssocType()));
            }
            this.customizer = customizer;
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((superiorOrgaUnits != null) && superiorOrgaUnits.next()) {
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return superiorOrgaUnits.getTitle();
                case 1:
                    return customizer.getDeleteLabel();
                case 2:
                    return customizer.getUpLabel();
                case 3:
                    return customizer.getDownLabel();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return superiorOrgaUnits.getOID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            final GenericOrganizationalUnit superiorOrgaUnit =
                                            (GenericOrganizationalUnit) DomainObjectFactory.
                    newInstance((OID) key);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    superiorOrgaUnit);

            if (canEdit) {
                final ContentSection section = CMS.getContext().
                        getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(
                        String.format("%s (%s)",
                                      value.toString(),
                                      superiorOrgaUnit.getLanguage()),
                        resolver.generateItemURL(state,
                                                 superiorOrgaUnit,
                                                 section,
                                                 superiorOrgaUnit.getVersion()));
                return link;
            } else {
                final Label label = new Label(String.format(
                        "%s (%s)",
                        value.toString(),
                        superiorOrgaUnit.getLanguage()));
                return label;
            }
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);


            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    orgaunit);

            if (canEdit) {
                final ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(customizer.getConfirmRemoveLabel());
                return link;
            } else {
                final Label label = new Label("");
                return label;
            }
        }
    }

    private class UpCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            if (row == 0) {
                final Label label = new Label("");
                return label;
            } else {
                final ControlLink link =
                                  new ControlLink(customizer.getUpLabel());
                return link;
            }
        }
    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);
            final GenericOrganizationalUnitSuperiorCollection supOrgaUnits =
                                                              orgaunit.
                    getSuperiorOrgaUnits();

            if ((supOrgaUnits.size() - 1) == row) {
                final Label label = new Label("");
                return label;
            } else {
                final ControlLink link =
                                  new ControlLink(customizer.getDownLabel());
                return link;
            }
        }
    }

    private class ActionListener implements TableActionListener {

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);
            final GenericOrganizationalUnit supOrgaUnit =
                                            (GenericOrganizationalUnit) DomainObjectFactory.
                    newInstance((OID) event.getRowKey());
            final GenericOrganizationalUnitSuperiorCollection supOrgaUnits =
                                                              orgaunit.
                    getSuperiorOrgaUnits();

            final TableColumn column = getColumnModel().get(event.getColumn().
                    intValue());
            final String headerKey = column.getHeaderKey().toString();
            if (TABLE_COL_EDIT.equals(headerKey)) {
                //Nothing now
            } else if(TABLE_COL_DEL.equals(headerKey)) {
                orgaunit.removeSuperiorOrgaUnit(supOrgaUnit);
            } else if (TABLE_COL_UP.equals(headerKey)) {
                supOrgaUnits.swapWithPrevious(supOrgaUnit);                
            } else if(TABLE_COL_DOWN.equals(headerKey)) {
                supOrgaUnits.swapWithNext(supOrgaUnit);
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing now
        }
    }
}
