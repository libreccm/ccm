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
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkMembershipPropertiesStep extends ResettableContainer {

    private static final Logger s_log = Logger.getLogger(ResearchNetworkMembershipPropertiesStep.class);
    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_membershipParam = new BigDecimalParameter("membership");
    private ResearchNetworkMembershipSelectionModel m_membershipModel;

    public ResearchNetworkMembershipPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        s_log.debug("Creating ResearchNetworkMembershipPropertiesStep");
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setMembershipSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("researchNetworkMembershipEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    protected void setMembershipSelectionModel() {
        setMembershipSelectionModel(new ResearchNetworkMembershipSelectionModel(this.m_membershipParam));
    }

    protected void setMembershipSelectionModel(ResearchNetworkMembershipSelectionModel membershipModel) {
        this.m_membershipModel = membershipModel;
    }

    protected ResearchNetworkMembershipSelectionModel getMembershipSelectionModel() {
        return this.m_membershipModel;
    }

    protected BigDecimalParameter getMembershipParam() {
        return this.m_membershipParam;
    }

    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new ResearchNetworkMembershipTable(m_itemModel, m_membershipModel));
        return container;
    }

    public FormSection getEditSheet() {
        ResearchNetworkMembershipPropertyForm propertyForm = new ResearchNetworkMembershipPropertyForm(m_itemModel, m_membershipModel);
        propertyForm.setPropertiesStep(this);
        return propertyForm;
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_membershipParam);
    }
}
