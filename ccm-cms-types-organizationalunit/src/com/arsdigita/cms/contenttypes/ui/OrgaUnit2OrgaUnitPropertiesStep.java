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
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrgaUnit2OrgaUnitPropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_ou2ouParam = new BigDecimalParameter("orgaunit2orgaunit");
    private OrgaUnit2OrgaUnitSelectionModel m_ou2ouModel = new OrgaUnit2OrgaUnitSelectionModel(m_ou2ouParam);

    public OrgaUnit2OrgaUnitPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setOrgaUnit2OrgaUnitSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("orgaUnit2OrgaUnitEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    protected void setOrgaUnit2OrgaUnitSelectionModel() {
        setOrgaUnit2OrgaUnitSelectionModel(new OrgaUnit2OrgaUnitSelectionModel(this.m_ou2ouParam));
    }

    protected void setOrgaUnit2OrgaUnitSelectionModel(OrgaUnit2OrgaUnitSelectionModel model) {
        this.m_ou2ouModel = model;
    }

    protected OrgaUnit2OrgaUnitSelectionModel getOrgaUnit2OrgaUnitSelectionModel() {
        return this.m_ou2ouModel;
    }

    protected BigDecimalParameter getOU2OUParam() {
        return this.m_ou2ouParam;
    }

    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new OrgaUnit2OrgaUnitTable(this.m_itemModel, this.m_ou2ouModel));
        return container;
    }

    public FormSection getEditSheet() {
        return new OrgaUnit2OrgaUnitPropertyForm(this.m_itemModel, this.m_ou2ouModel);
    }

    @Override
    public void register(Page page) {
        super.register(page);
        page.addComponentStateParam(this, m_ou2ouParam);
    }
}
