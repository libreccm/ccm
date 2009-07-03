package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.cms.contenttypes.ui.*;
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
public class MembershipStatusPropertiesStep extends ResettableContainer {

    private Logger logger = Logger.getLogger(MembershipStatusPropertiesStep.class);

    private AuthoringKitWizard m_parent;

    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_statusParam = new BigDecimalParameter(("membershipstatus"));
    private MembershipStatusSelectionModel m_statusModel = new MembershipStatusSelectionModel(m_statusParam);

    public MembershipStatusPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        logger.debug("Invoking MembershipStatusPropertiesStep(itemModel, parent)...");
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setMembershipStatusSelectionModel();
        add(getDisplayComponent());

        Form form = form = new Form("membershipStatusEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
        logger.debug("finished.");
    }

    protected void setMembershipStatusSelectionModel() {
        setMembershipStatusSelectionModel(new MembershipStatusSelectionModel(m_statusParam));
    }

    protected void setMembershipStatusSelectionModel(MembershipStatusSelectionModel statusModel) {
        this.m_statusModel = statusModel;
    }

    protected MembershipStatusSelectionModel getMembershipStatusSelectionModel() {
        return this.m_statusModel;
    }

    protected BigDecimalParameter getStatusParam() {
        return this.m_statusParam;
    }

    protected Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new MembershipStatusTable(this.m_itemModel, this.m_statusModel));
        return container;
    }

    protected FormSection getEditSheet() {
        return new MembershipStatusPropertyForm((this.m_itemModel), this.m_statusModel);
    }

    @Override
    public void register(Page p) {
        this.logger.info("registering...");
        super.register(p);
        p.addComponentStateParam(this, this.m_statusParam);
        this.logger.info("finished...");
    }
}
