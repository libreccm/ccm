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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table for showing subordinate organizational units. Several aspects can 
 * be customized using an implementation of 
 * {@link GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer}. An implementation 
 * of {@link GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer} has to be
 * passed to the constructor of this table by the authoring step containing 
 * the table.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSubordinateOrgaUnitsTable
        extends Table {

    private final Logger logger =
                         Logger.getLogger(
            GenericOrganizationalUnitSubordinateOrgaUnitsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private final ItemSelectionModel itemModel;
    private final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer;

    public GenericOrganizationalUnitSubordinateOrgaUnitsTable(
            final ItemSelectionModel itemModel,
            final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer) {
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
        private final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer;

        public ModelBuilder(
                final ItemSelectionModel itemModel,
                final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer) {
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
        private final GenericOrganizationalUnitSubordinateCollection subordinateOrgaUnits;
        private final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer;

        public Model(final Table table,
                     final PageState state,
                     final GenericOrganizationalUnit orgaunit,
                     final GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer customizer) {
            this.table = table;
            subordinateOrgaUnits = orgaunit.getSubordinateOrgaUnits();
            if ((customizer.getAssocType() != null)
                && !(customizer.getAssocType().isEmpty())) {
                subordinateOrgaUnits.addFilter(String.format(
                        "link.assocType = '%s'",
                        customizer.getAssocType()));
            }
            if ((customizer.getContentType() != null)
                && !(customizer.getContentType().isEmpty())) {
                subordinateOrgaUnits.addFilter(String.format("objectType = '%s'",
                                                             customizer.
                        getContentType()));
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

            if ((subordinateOrgaUnits != null) && subordinateOrgaUnits.next()) {
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return subordinateOrgaUnits.getGenericOrganizationalUnit(
                            GlobalizationHelper.getNegotiatedLocale().
                            getLanguage()).getTitle();
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

        @Override
        public Object getKeyAt(final int columnIndex) {
            return subordinateOrgaUnits.getGenericOrganizationalUnit(
                    GlobalizationHelper.getNegotiatedLocale().getLanguage()).
                    getID();
        }
    }

    private class EditCellRenderer
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
            final com.arsdigita.cms.SecurityManager securityManager = CMS.
                    getSecurityManager(state);
            final GenericOrganizationalUnit subordinateOrgaUnit =
                                            new GenericOrganizationalUnit(
                    (BigDecimal) key);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    subordinateOrgaUnit);

            if (canEdit) {
                final ContentSection section = CMS.getContext().
                        getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link =
                           new Link(
                        String.format("%s (%s)",
                                      value.toString(),
                                      subordinateOrgaUnit.getLanguage()),
                        resolver.generateItemURL(state,
                                                 subordinateOrgaUnit,
                                                 section,
                                                 subordinateOrgaUnit.getVersion()));

                return link;
            } else {
                final Label label = new Label(String.format(
                        "%s (%s)",
                        value.toString(),
                        subordinateOrgaUnit.getLanguage()));
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
            final com.arsdigita.cms.SecurityManager securityManager = CMS.
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
            final GenericOrganizationalUnitSubordinateCollection subOrgaUnits =
                                                                 orgaunit.
                    getSubordinateOrgaUnits();
            if ((customizer.getAssocType() != null)
                && !(customizer.getAssocType().isEmpty())) {
                subOrgaUnits.addFilter(String.format(
                        "link.assocType = '%s'",
                        customizer.getAssocType()));
            }
            if ((customizer.getContentType() != null)
                && !(customizer.getContentType().isEmpty())) {
                subOrgaUnits.addFilter(
                        String.format("objectType = '%s'",
                                      customizer.getContentType()));
            }

            if ((subOrgaUnits.size() - 1) == row) {
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
                    getSelectedObject(
                    state);
            final GenericOrganizationalUnit subOrgaUnit =
                                            new GenericOrganizationalUnit(
                    new BigDecimal((String) event.getRowKey()));
            final GenericOrganizationalUnitSubordinateCollection subOrgaUnits =
                                                                 orgaunit.
                    getSubordinateOrgaUnits();

            final TableColumn column = getColumnModel().get(event.getColumn().
                    intValue());
            final String headerKey = column.getHeaderKey().toString();
            if (TABLE_COL_EDIT.equals(headerKey)) {
                //Nothing now
            } else if (TABLE_COL_DEL.equals(headerKey)) {
                orgaunit.removeSubordinateOrgaUnit(subOrgaUnit);
            } else if (TABLE_COL_UP.equals(headerKey)) {
                subOrgaUnits.swapWithPrevious(subOrgaUnit);
            } else if (TABLE_COL_DOWN.equals(headerKey)) {
                subOrgaUnits.swapWithNext(subOrgaUnit);
            }
        }

        public void headSelected(TableActionEvent e) {
            //Nothing now
        }
    }
}
