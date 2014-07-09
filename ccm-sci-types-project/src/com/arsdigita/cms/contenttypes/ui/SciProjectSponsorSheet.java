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
import com.arsdigita.cms.ui.ControlButton;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

public class SciProjectSponsorSheet extends Table {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_ASSOC = "table_col_edit_assoc";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private final String SELECTED_PROJECT = "selected_project_sponsor_association_project";
    private final String SELECTED_SPONSOR = "selected_project_sponsor_association_sponsor";
    private ItemSelectionModel itemModel;
    private SimpleEditStep editStep;

    public SciProjectSponsorSheet(final ItemSelectionModel itemModel,
                                  final SimpleEditStep editStep) {
        super();
        this.itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.sponsor_none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_name"),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_fundingcode")));
        columnModel.add(new TableColumn(
                2,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_edit_assoc"),
                TABLE_COL_EDIT_ASSOC));
        columnModel.add(new TableColumn(
                3,
                SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor_remove"),
                TABLE_COL_DEL));
        columnModel.add(new TableColumn(
                4,
                SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.sponsor.up"),
                TABLE_COL_UP));
        columnModel.add(new TableColumn(
                5,
                SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.sponsor.down"),
                TABLE_COL_DOWN));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(2).setCellRenderer(new EditAssocCellRenderer());
        columnModel.get(3).setCellRenderer(new DeleteCellRenderer());
        columnModel.get(4).setCellRenderer(new UpCellRenderer());
        columnModel.get(5).setCellRenderer(new DownCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
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

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((sponsors != null) && sponsors.next()) {
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
                    return sponsors.getTitle();
                case 1:
                    return sponsors.getFundingCode();
                case 2:
                    return new Label(SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.sponsor.edit_assoc"));
                case 3:
                    return new Label(SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.sponsor.remove"));
                default:
                    return null;
            }
        }

        @Override
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

    private class EditAssocCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int col) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final SciProject project = (SciProject) itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    project);

            if (canEdit) {
                final ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                final Label label = new Label(value.toString());
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
                link.setConfirmation(SciProjectGlobalizationUtil.globalize(
                        "sciproject.ui.sponsor.remove.confirm"));
                return link;
            } else {
                return new Label("");
            }
        }

    }

    private class UpCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            if (0 == row) {
                final Label label = new Label();
                return label;
            } else {
                final ControlLink link = new ControlLink("up");
                return link;
            }
        }

    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            final SciProject project = (SciProject) itemModel.getSelectedObject(state);
            final SciProjectSponsorCollection sponsors = project.getSponsors();

            if ((sponsors.size() - 1) == row) {
                final Label label = new Label();
                return label;
            } else {
                final ControlLink link = new ControlLink("down");
                return link;
            }
        }

    }

    private class ActionListener implements TableActionListener {

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final GenericOrganizationalUnit sponsor = new GenericOrganizationalUnit(new BigDecimal(
                    event.getRowKey().toString()));
            final SciProject project = (SciProject) itemModel.getSelectedObject(state);
            final SciProjectSponsorCollection sponsors = project.getSponsors();

            final TableColumn column = getColumnModel().get(event.getColumn().intValue());

            if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                //Nothing yet
            } else if (TABLE_COL_EDIT_ASSOC.equals(column.getHeaderKey().toString())) {

                while (sponsors.next()) {
                    if (sponsors.getSponsor().equals(sponsor)) {
                        break;
                    }
                }

                ((SciProjectSponsorStep) editStep).setSelectedSponsor(sponsor);
                ((SciProjectSponsorStep) editStep).setSelectedSponsorFundingCode(sponsors.
                        getFundingCode());

                editStep.showComponent(state,
                                       SciProjectSponsorStep.SCIPROJECT_SPONSOR_STEP);

                sponsors.close();

            } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                Assert.exists(sponsor, GenericOrganizationalUnit.class);

                project.removeSponsor(sponsor);
            } else if (TABLE_COL_UP.equals(column.getHeaderKey().toString())) {
                project.swapWithPreviousSponsor(sponsor);
            } else if (TABLE_COL_DOWN.equals(column.getHeaderKey().toString())) {
                project.swapWithNextSponsor(sponsor);
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing
        }

    }
}