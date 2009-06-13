package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;

/**
 * Authoring step for adding roles (e.g. chairmen) to an organization.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRolePropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;

    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_roleParam = new BigDecimalParameter("organizationrole");
    private OrganizationRoleSelectionModel m_roleModel = new OrganizationRoleSelectionModel(m_roleParam);

    /**
     * Constructor for creating an new instance of this authoring step.
     *
     * @param itemModel
     * @param parent
     */
    public OrganizationRolePropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setOrganizationRoleSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("organizationRoleEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    /**
     * Creates an new instance of OrganizationRoleSelectionModel and calls
     * setOrganizationRole(OrganizationRoleSelectionModel model) with the new
     * instance.
     */
    protected void setOrganizationRoleSelectionModel() {
        setOrganizationRoleSelectionModel(new OrganizationRoleSelectionModel(m_roleParam));
    }

    /**
     * Sets the OrganizationRoleSelectionModel.
     *
     * @param model
     */
    protected void setOrganizationRoleSelectionModel(OrganizationRoleSelectionModel model) {
        m_roleModel = model;
    }

    /**
     * @return The OrganizationRoleSelectionModel instance of this authoring step.
     */
    protected OrganizationRoleSelectionModel getOrganizationRoleSelectionModel() {
        return m_roleModel;
    }

    /**
     *
     * @return The value of the m_roleParam property.
     */
    protected BigDecimalParameter getRoleParam() {
        return this.m_roleParam;
    }

    /**
     *
     * @return The component displaying the authoring step
     */
    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new OrganizationRoleTable(m_itemModel, m_roleModel));
        return container;
    }

    /**
     *
     * @return The form for editing the roles of the organization
     */
    protected FormSection getEditSheet() {
        return new OrganizationRolePropertyForm(this.m_itemModel, this.m_roleModel);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_roleParam);
    }
}
