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
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciMemberSciOrganizationsCollection;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciMemberSciOrganizationsTable
        extends Table
        implements TableActionListener {

    private static final Logger logger = Logger.getLogger(
            SciMemberSciOrganizationsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_LINK = "table_col_edit_link";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel itemModel;
    private SciMemberSciOrganizationsStep step;

    public SciMemberSciOrganizationsTable(ItemSelectionModel itemModel,
                                          SciMemberSciOrganizationsStep step) {
        super();
        this.itemModel = itemModel;
        this.step = step;

        setEmptyView(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organizations.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.role").localize()));
        columnModel.add(new TableColumn(
                2,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.status").localize()));
        columnModel.add(new TableColumn(
                3,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.edit").localize(),
                TABLE_COL_EDIT_LINK));
        columnModel.add(new TableColumn(
                4,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(
                new SciMemberSciOrganizationsTableModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(3).setCellRenderer(new EditLinkCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciMemberSciOrganizationsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public SciMemberSciOrganizationsTableModelBuilder(
                ItemSelectionModel itemModel) {
            SciMemberSciOrganizationsTable.this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciMember member = (SciMember) itemModel.getSelectedObject(state);
            return new SciMemberSciOrganizationsTableModel(table,
                                                           state,
                                                           member);
        }
    }

    private class SciMemberSciOrganizationsTableModel implements TableModel {

        private Table table;
        private SciMemberSciOrganizationsCollection organizations;
        private SciOrganization organization;

        public SciMemberSciOrganizationsTableModel(Table table,
                                                   PageState state,
                                                   SciMember member) {
            this.table = table;
            this.organizations = member.getOrganizations();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((organizations != null) && organizations.next()) {
                organization = organizations.getOrganization();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return organization.getTitle();
                case 1:
                    RelationAttributeCollection role =
                                                new RelationAttributeCollection(
                            "SciOrganizationRole",
                            organizations.getRoleName());
                    if (role.next()) {
                        String roleName = role.getName();
                        role.close();
                        return roleName;
                    } else {
                        return ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknownRole").localize();
                    }
                case 2:
                    RelationAttributeCollection status =
                                                new RelationAttributeCollection(
                            "GenericOrganizationalUnitMemberStatus",
                            organizations.getStatus());
                    if (status.next()) {
                        String statusName = status.getName();
                        status.close();
                        return statusName;
                    } else {
                        return ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknownStatus").localize();
                    }
                case 3:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "scimember.ui.organizations.edit_assoc").localize();
                case 4:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "scimember.ui.organizations.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return organization.getID();
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
                                      int column) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            SciMember member = (SciMember) itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        member);

            if (canEdit) {
                SciOrganization organization;
                try {
                    organization = new SciOrganization((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    logger.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(String.format("%s (%s)",
                                                   value.toString(),
                                                   organization.getLanguage()),
                                     resolver.generateItemURL(state,
                                                              organization,
                                                              section,
                                                              organization.
                        getVersion()));
                return link;
            } else {
                SciOrganization organization;
                try {
                    organization = new SciOrganization((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    logger.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }
                
                Label label = new Label(String.format("%s (%s)",
                                                      value.toString(),
                                                      organization.getLanguage()));
                return label;
            }
        }
    }

    private class EditLinkCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            SciMember member = (SciMember) itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        member);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
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
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            SciMember member = (SciMember) itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        member);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciOrganizationGlobalizationUtil.
                        globalize(
                        "scimember.ui.organization."
                        + "confirm_remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        SciOrganization organization = new SciOrganization(new BigDecimal(event.
                getRowKey().toString()));

        SciMember member = (SciMember) itemModel.getSelectedObject(state);

        SciMemberSciOrganizationsCollection organizations = member.
                getOrganizations();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if (TABLE_COL_EDIT_LINK.equals(
                column.getHeaderKey().toString())) {
            while (organizations.next()) {
                if (organizations.getOrganization().equals(organization)) {
                    break;
                }
            }
            step.setSelectedOrganization(organizations.getOrganization());
            step.setSelectedOrganizationRole(organizations.getRoleName());
            step.setSelectedOrganizationStatus(organizations.getStatus());

            organizations.close();

            step.showEditComponent(state);
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            member.removeOrganization(organization);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }
}
