/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

public class SciProjectSponsorSheet extends Table {

    private ItemSelectionModel itemModel;
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";

    public SciProjectSponsorSheet(final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.sponsor_none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_name").localize(),
                TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                0,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final SciProject project = (SciProject) itemModel.getSelectedObject(state);
            return new Model(table, state, project);
        }

    }

    private class Model implements TableModel {

        private final Table table;
        private final SciProjectSponsorCollection sponsors;

        public Model(final Table table, final PageState state, final SciProject project) {
            this.table = table;

            sponsors = project.getSponsors();
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((sponsors != null) && sponsors.next()) {
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return sponsors.getTitle();
                case 1:
                    return SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(final int columnIndex) {
            return sponsors.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final GenericOrganizationalUnit sponsor =
                                            new GenericOrganizationalUnit((BigDecimal) key);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    sponsor);
            if (canEdit) {
                final ContentSection section = sponsor.getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(String.format("%s (%s)",
                                                         value.toString(),
                                                         sponsor.getLanguage()),
                                           resolver.generateItemURL(state,
                                                                    sponsor,
                                                                    section,
                                                                    sponsor.getVersion()));
                return link;
            } else {
                final Label label = new Label(String.format("%s (%s)",
                                                            value.toString(),
                                                            sponsor.getLanguage()));
                return label;
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final GenericOrganizationalUnit sponsor =
                                            new GenericOrganizationalUnit((BigDecimal) key);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    sponsor);
            if (canEdit) {
                final ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciProjectGlobalizationUtil.globalize(
                        "sciproject.ui.sponsor.remove.confirm").localize());
                return link;
            } else {
                return new Label("");
            }
        }

    }

    private class ActionListener implements TableActionListener {

        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final GenericOrganizationalUnit sponsor = new GenericOrganizationalUnit(new BigDecimal(
                    event.getRowKey().toString()));
            final SciProject project = (SciProject) itemModel.getSelectedObject(state);

            final TableColumn column = getColumnModel().get(event.getColumn().intValue());

            if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                //Nothing yet
            } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                Assert.exists(sponsor, GenericOrganizationalUnit.class);

                project.removeSponsor(sponsor);
            }
        }

        public void headSelected(final TableActionEvent event) {
            //Nothing
        }

    }
}