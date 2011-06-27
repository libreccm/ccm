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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedOrganizationSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DELETE = "table_col_delete";
    private ItemSelectionModel itemModel;

    public UnPublishedOrganizationSheet(final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization.remove").localize(),
                TABLE_COL_DELETE));

        setModelBuilder(new UnPublishedOrganizationSheetModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class UnPublishedOrganizationSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public UnPublishedOrganizationSheetModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            UnPublished unPublished = (UnPublished) itemModel.getSelectedObject(
                    state);
            return new UnPublishedOrganizationSheetModel(table, state,
                                                         unPublished);
        }
    }

    private class UnPublishedOrganizationSheetModel implements TableModel {

        private Table table;
        private GenericOrganizationalUnit orga;
        private boolean m_done;

        public UnPublishedOrganizationSheetModel(final Table table,
                                                 final PageState state,
                                                 final UnPublished unPublished) {
            this.table = table;
            orga = unPublished.getOrganization();
            if (orga == null) {
                m_done = false;
            } else {
                m_done = true;
            }
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if (m_done) {
                ret = true;
                m_done = false;
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
                            "publications.ui.unpublished.organization.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return orga.getID();
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
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            UnPublished unPublished =
                        (UnPublished) itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        unPublished);
            if (canEdit) {
                GenericOrganizationalUnit organization;
                try {
                    organization = new GenericOrganizationalUnit((BigDecimal)key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            organization.getLanguage()),
                              resolver.generateItemURL(state,
                                                       organization,
                                                       section,
                                                       organization.getVersion()));

                return link;
            } else {
                GenericOrganizationalUnit organization;
                try {
                    organization = new Publisher(
                            (BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                Label label = new Label(
                        String.format("%s (%s)",
                                      value.toString(),
                                      organization.getLanguage()));
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
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            UnPublished unPublished = (UnPublished) itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        SecurityManager.DELETE_ITEM,
                                                        unPublished);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.unpublished.organization.confirm_remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        UnPublished unPublished = (UnPublished) itemModel.getSelectedObject(
                state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());
        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DELETE)) {
            unPublished.setOrganization(null);
        }
    }

    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }
}
