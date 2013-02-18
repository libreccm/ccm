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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPublicationsCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id: GenericOrganizationalUnitPublicationsTable.java 1569 2012-04-05
 * 14:10:12Z jensp $
 */
public class GenericOrganizationalUnitPublicationsTable
        extends Table {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public GenericOrganizationalUnitPublicationsTable(
            final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.publications.none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.publications.columns.name").
                localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.publications.columns.remove").
                localize(),
                TABLE_COL_DEL));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());

    }

    private class ModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            return new Model(table, state, orgaunit);
        }

    }

    private class Model implements TableModel {

        private final Table table;
        private final GenericOrganizationalUnitPublicationsCollection publications;

        public Model(final Table table,
                     final PageState state,
                     final GenericOrganizationalUnit orgaunit) {
            this.table = table;
            publications = Publication.getPublications(orgaunit);
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((publications != null) && publications.next()) {
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
                    return publications.getPublication().getTitle();
                case 1:
                    return PublicationGlobalizationUtil.globalize(
                            "genericorganizationalunit.ui.publications.remove").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return publications.getPublication().getID();
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
            final com.arsdigita.cms.SecurityManager securityManager = CMS.
                    getSecurityManager(state);
            final Publication publication = new Publication((BigDecimal) key);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    publication);

            if (canEdit) {
                final ContentSection section = publication.getContentSection();//CMS.getContext().getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(value.toString(),
                                           resolver.generateItemURL(
                        state,
                        publication,
                        section,
                        publication.getVersion()));
                return link;
            } else {
                final Label label = new Label(value.toString());
                return label;
            }
        }

    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

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

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    orgaunit);

            if (canEdit) {
                final ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "genericorganizationalunit.ui.publications.remove.confirm").
                        localize());
                return link;
            } else {
                final Label label = new Label(value.toString());
                return label;
            }
        }

    }

    private class ActionListener implements TableActionListener {

        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final Publication publication = new Publication(new BigDecimal(event.
                    getRowKey().toString()));
            final GenericOrganizationalUnit orgaunit =
                                            (GenericOrganizationalUnit) itemModel.
                    getSelectedObject(state);

            final TableColumn column = getColumnModel().get(event.getColumn().
                    intValue());

            if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                //Nothing to do yet
            } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                Publication.removePublication(orgaunit, publication);
            }

        }

        public void headSelected(final TableActionEvent event) {
            //Nothing
        }

    }
}
