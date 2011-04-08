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
import com.arsdigita.cms.contenttypes.SciMemberSciProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
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
public class SciMemberSciProjectsTable
        extends Table
        implements TableActionListener {

    private static final Logger logger = Logger.getLogger(
            SciMemberSciProjectsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_LINK = "table_col_edit_link";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel itemModel;
    private SciMemberSciProjectsStep step;

    public SciMemberSciProjectsTable(ItemSelectionModel itemModel,
                                          SciMemberSciProjectsStep step) {
        super();
        this.itemModel = itemModel;
        this.step = step;

        setEmptyView(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.projects.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project.role").localize()));
        columnModel.add(new TableColumn(
                2,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project.status").localize()));
        columnModel.add(new TableColumn(
                3,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project.edit").localize(),
                TABLE_COL_EDIT_LINK));
        columnModel.add(new TableColumn(
                4,
                SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.project.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(
                new SciMemberSciProjectsTableModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(3).setCellRenderer(new EditLinkCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciMemberSciProjectsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public SciMemberSciProjectsTableModelBuilder(
                ItemSelectionModel itemModel) {
            SciMemberSciProjectsTable.this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciMember member = (SciMember) itemModel.getSelectedObject(state);
            return new SciMemberSciProjectsTableModel(table,
                                                           state,
                                                           member);
        }
    }

    private class SciMemberSciProjectsTableModel implements TableModel {

        private Table table;
        private SciMemberSciProjectsCollection projects;
        private SciProject project;

        public SciMemberSciProjectsTableModel(Table table,
                                                   PageState state,
                                                   SciMember member) {
            this.table = table;
            this.projects = member.getProjects();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((projects != null) && projects.next()) {
                project = projects.getProject();
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
                    return project.getTitle();
                case 1:
                    RelationAttributeCollection role = new RelationAttributeCollection(
                            "SciProjectRole",
                            projects.getRoleName());
                    if (role.next()) {
                        String roleName = role.getName();
                        role.close();
                        return roleName;
                    } else {
                        return ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknownRole").localize();
                    }
                case 2:
                    RelationAttributeCollection status = new RelationAttributeCollection(
                            "GenericOrganizationalUnitMemberStatus",
                            projects.getStatus());
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
                            "scimember.ui.projects.edit_assoc").localize();
                case 4:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "scimember.ui.projects.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return project.getID();
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
                SciProject project;
                try {
                    project = new SciProject((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    logger.warn(String.format("No object with key '%s' found.",
                                              key),
                                ex);
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(value.toString(),
                                     resolver.generateItemURL(state,
                                                              project,
                                                              section, project.
                        getVersion()));
                return link;
            } else {
                Label label = new Label(value.toString());
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
                        "scimember.ui.project."
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

        SciProject project = new SciProject(new BigDecimal(event.
                getRowKey().toString()));

        SciMember member = (SciMember) itemModel.getSelectedObject(state);

        SciMemberSciProjectsCollection projects = member.
                getProjects();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if (TABLE_COL_EDIT_LINK.equals(
                column.getHeaderKey().toString())) {
            while (projects.next()) {
                if (projects.getProject().equals(project)) {
                    break;
                }
            }
            step.setSelectedProject(projects.getProject());
            step.setSelectedProjectRole(projects.getRoleName());
            step.setSelectedProjectStatus(projects.getStatus());

            projects.close();

            step.showEditComponent(state);
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            member.removeProject(project);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }
}
