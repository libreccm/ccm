package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.ui.util.GlobalizationUtil;
import com.arsdigita.util.Assert;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationViewRoles extends ResettableContainer {

    /* Ids for the editing panels */
    /**
     * Identifier for the table of roles
     */
    public static final String ROLES_TABLE = "rolesTable";
    /**
     * Identifier for the role edit form
     */
    public static final String ROLES_EDIT = "rolesEdit";
    /**
     * Identifier for the role delete form
     */
    public static final String ROLES_DELETE = "rolesDelete";

    /* class attributes */
    /**
     * Identifier for the data table
     */
    public static final String DATA_TABLE = "dataTable";
    /**
     * Identifier for the action link
     */
    public static final String ACTION_LINK = "actionLink";
    /**
     * The authoring wizard
     */
    protected AuthoringKitWizard m_wizard;
    /**
     * ItemSelectionModel for the organization
     */
    protected ItemSelectionModel m_selectionOrganization;
    /**
     * ItemSelection for the role
     */
    protected ItemSelectionModel m_selectionRole;
    /**
     * ItemSelectionModel for moving the role position (currently not used)
     */
    protected ItemSelectionModel m_moveRole;
    /**
     * Move parameter (not used yet)
     */
    protected BigDecimalParameter m_moveParameter;

    /* Visual components doing the word */
    /**
     * Table with all roles associated with the organization.
     */
    protected RoleTable m_roleTable;
    /**
     * Form for editing a role
     */
    protected RoleEditForm m_roleEdit;
    /**
     * Form for deleting a role
     */
    protected RoleDeleteForm m_roleDelete;
    /**
     * Begin link
     */
    protected ActionLink m_beginLink;
    private Label m_moveRoleLabel;
    private String m_typeIdStr;

    /**
     * Constructor.
     *
     * @param selOrga
     * @param wizard
     */
    public GenericOrganizationViewRoles(ItemSelectionModel selOrga, AuthoringKitWizard wizard) {
        super();
        m_selectionOrganization = selOrga;
        m_wizard = wizard;
        m_typeIdStr = wizard.getContentType().getID().toString();
        Assert.exists(m_selectionOrganization, ItemSelectionModel.class);

        add(buildRoleTable(), true);
        add(buildRoleEdit(), false);
        add(buildRoleDelete(), false);
    }

    /**
     * Builds the table of roles.
     *
     * @return The table of roles.
     */
    protected Container buildRoleTable() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(ROLES_TABLE + m_typeIdStr);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");

        m_moveParameter = new BigDecimalParameter("moveRole");
        m_moveRole = new ItemSelectionModel(OrganizationRole.class.getName(), OrganizationRole.BASE_DATA_OBJECT_TYPE, m_moveParameter);
        m_roleTable = new RoleTable(m_selectionOrganization, m_moveRole);
        m_roleTable.setClassAttr(DATA_TABLE);

        m_selectionRole = new ItemSelectionModel(OrganizationRole.class.getName(), OrganizationRole.BASE_DATA_OBJECT_TYPE, m_roleTable.getRowSelectionModel());

        m_roleTable.setRoleModel(m_selectionRole);

        Label emptyView = new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.no_roles_yet"));
        m_roleTable.setEmptyView(emptyView);

        m_moveRoleLabel = new Label("Role Name");
        c.add(m_moveRoleLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        m_beginLink = new ActionLink((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.move_to_beginning").localize());
        c.add(m_beginLink);

        m_beginLink.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();
                GenericOrganization orga = (GenericOrganization) m_selectionOrganization.getSelectedObject(state);

                m_moveRole.setSelectedKey(state, null);
            }
        });

        m_moveRole.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                PageState state = e.getPageState();
                if (m_moveRole.getSelectedKey(state) == null) {
                    m_beginLink.setVisible(state, false);
                    m_moveRoleLabel.setVisible(state, false);
                } else {
                    m_beginLink.setVisible(state, true);
                    m_moveRoleLabel.setVisible(state, true);
                    m_moveRoleLabel.setLabel((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.move_role_name").localize() + ((OrganizationRole) m_moveRole.getSelectedObject(state)).getRolename(), state);
                }
            }
        });

        m_roleTable.addTableActionListener(new TableActionListener() {

            public void cellSelected(TableActionEvent e) {
                PageState state = e.getPageState();

                TableColumn col = m_roleTable.getColumnModel().get(e.getColumn().intValue());
                String colName = (String) col.getHeaderValue();

                if (RoleTable.COL_DEL.equals(colName)) {
                    onlyShowComponent(state, ROLES_DELETE + m_typeIdStr);
                } else if (RoleTable.COL_EDIT.equals(colName)) {
                    onlyShowComponent(state, ROLES_EDIT + m_typeIdStr);
                }
            }

            public void headSelected(TableActionEvent e) {
                //Nothing
            }
        });

        c.add(m_roleTable);

        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds the edit form.
     *
     * @return The edit form.
     */
    protected Container buildRoleEdit() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(ROLES_EDIT + m_typeIdStr);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");

        c.add(new Label(new PrintListener() {

            public void prepare(PrintEvent event) {
                PageState state = event.getPageState();
                Label label = (Label) event.getTarget();

                if (m_selectionRole.getSelectedKey(state) == null) {
                    label.setLabel((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.add_role").localize());
                } else {
                    label.setLabel((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.edit_role").localize());
                }
            }
        }));

        m_roleEdit = new RoleEditForm(m_selectionOrganization, m_selectionRole, this);
        c.add(m_roleEdit);

        c.add(buildViewAllLink());
        c.add(buildAddLink());

        return c;
    }

    /**
     * Builds the delete form
     *
     * @return The delete form.
     */
    protected Container buildRoleDelete() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(ROLES_DELETE + m_typeIdStr);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");

        c.add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.delete_role")));
        m_roleDelete = new RoleDeleteForm(m_selectionOrganization, m_selectionRole);
        m_roleDelete.addSubmissionListener(new FormSubmissionListener() {

            public void submitted(FormSectionEvent e) throws FormProcessException {
                PageState state = e.getPageState();
                onlyShowComponent(state, ROLES_TABLE + m_typeIdStr);
            }
        });
        c.add(m_roleDelete);

        c.add(buildViewAllLink());

        return c;
    }

    /**
     * Builds the view all roles link.
     *
     * @return The ViewAllLink.
     */
    protected ActionLink buildViewAllLink() {
        ActionLink viewAllLink = new ActionLink((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.view_all_roles").localize());
        viewAllLink.setClassAttr(ACTION_LINK);
        viewAllLink.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onlyShowComponent(e.getPageState(), ROLES_TABLE + m_typeIdStr);
            }
        });

        return viewAllLink;
    }

    /**
     * Builds the add link.
     *
     * @return The add link.
     */
    protected ActionLink buildAddLink() {
        ActionLink addLink = new ActionLink((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorgnization.add_new_role").localize()) {

            @Override
            public boolean isVisible(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = (ContentItem) m_selectionOrganization.getSelectedObject(state);

                return (super.isVisible(state) && sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM, item));
            }
        };

        addLink.setClassAttr(ACTION_LINK);
        addLink.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                m_selectionRole.clearSelection(state);
                onlyShowComponent(state, ROLES_EDIT + m_typeIdStr);
            }
        });

        return addLink;
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_moveParameter);
        p.setVisibleDefault(m_beginLink, false);
        p.setVisibleDefault(m_moveRoleLabel, false);

    }

    /**
     * 
     * @return The typeIdStr.
     */
    public String getTypeIdStr() {
        return m_typeIdStr;
    }
}