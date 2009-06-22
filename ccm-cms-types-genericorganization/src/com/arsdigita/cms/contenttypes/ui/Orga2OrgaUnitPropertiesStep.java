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
 * PropertiesStep for adding organizational units to an organization.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Orga2OrgaUnitPropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_o2ouParam = new BigDecimalParameter(("orga2orgaunit"));
    private Orga2OrgaUnitSelectionModel m_o2ouModel = new Orga2OrgaUnitSelectionModel(m_o2ouParam);

    /**
     * Creates a new instance of the PropertiesStep.
     *
     * @param itemModel ItemSelectionModel to use
     * @param parent Parent component.
     */
    public Orga2OrgaUnitPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this.m_itemModel = itemModel;
        this.m_parent = parent;
        setOrga2OrgaUnitSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("orga2OrgaUnitEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    /**
     * Sets the custom SelectionModel to use.
     */
    protected void setOrga2OrgaUnitSelectionModel() {
        setOrga2OrgaUnitSelectionModel(new Orga2OrgaUnitSelectionModel(m_o2ouParam));
    }

    /**
     * Sets the custom SelectionModel to use.
     *
     * @param model The {@see Orga2OrgaUnitSelectionModel} instance to use.
     */
    protected void setOrga2OrgaUnitSelectionModel(Orga2OrgaUnitSelectionModel model) {
        this.m_o2ouModel = model;
    }

    /**
     * Gets the {@see Orga2OrgaUnitSelectionModel} used.
     * @return The Orga2OrgaUnitSelectionModel used.
     */
    protected Orga2OrgaUnitSelectionModel getOrga2OrgaUnitSelectionModel() {
        return this.m_o2ouModel;
    }

    /**
     *
     * @return
     */
    protected BigDecimalParameter getO2OUParam() {
        return this.m_o2ouParam;
    }

    /**
     * Returns the displaying component.
     *
     * @return The displying Component.
     */
    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new Orga2OrgaUnitTable(m_itemModel, m_o2ouModel));
        return container;
    }

    /**
     * Returns the edit sheet used.
     *
     * @return The edit sheet used.
     */
    public FormSection getEditSheet() {
        return new Orga2OrgaUnitPropertyForm(this.m_itemModel, this.m_o2ouModel);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_o2ouParam);
    }
}
