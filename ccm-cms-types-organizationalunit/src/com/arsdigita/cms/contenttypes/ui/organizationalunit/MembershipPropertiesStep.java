package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipPropertiesStep extends ResettableContainer {

    private static final Logger logger = Logger.getLogger(MembershipPropertiesStep.class);
    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_membershipParam = new BigDecimalParameter("membership");
    private MembershipSelectionModel m_membershipModel; // = new MembershipSelectionModel(m_membershipParam);

    public MembershipPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        logger.debug("Creating MembershipPropertiesStep");
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setMembershipSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("membershipEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    protected void setMembershipSelectionModel() {
        setMembershipSelectionModel(new MembershipSelectionModel(this.m_membershipParam));
    }

    protected void setMembershipSelectionModel(MembershipSelectionModel membershipModel) {
        this.m_membershipModel = membershipModel;
    }

    protected MembershipSelectionModel getMembershipSelectionModel() {
        return this.m_membershipModel;
    }

    protected BigDecimalParameter getMembershipParam() {
        return this.m_membershipParam;
    }

    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new MembershipTable(m_itemModel, m_membershipModel));
        return container;
    }

    public FormSection getEditSheet() {
        MembershipPropertyForm propertyForm =  new MembershipPropertyForm(m_itemModel, m_membershipModel);
        propertyForm.setPropertiesStep(this);
        return propertyForm;
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_membershipParam);
    }
}
