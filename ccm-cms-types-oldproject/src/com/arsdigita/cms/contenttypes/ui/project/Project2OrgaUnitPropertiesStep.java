package com.arsdigita.cms.contenttypes.ui.project;

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

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2OrgaUnitPropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_p2ouParam =
            new BigDecimalParameter("project2orgaunit");
    private Project2OrgaUnitSelectionModel m_p2ouModel =
            new Project2OrgaUnitSelectionModel(m_p2ouParam);

    public Project2OrgaUnitPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        m_itemModel = itemModel;
        m_parent = parent;
        setProject2OrgaUnitSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("project2OrgaUnitEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    protected void setProject2OrgaUnitSelectionModel() {
        setProject2OrgaUnitSelectionModel(
                new Project2OrgaUnitSelectionModel(m_p2ouParam));
    }

    protected void setProject2OrgaUnitSelectionModel(
            Project2OrgaUnitSelectionModel model) {
        m_p2ouModel = model;
    }

    protected Project2OrgaUnitSelectionModel getProject2OrgaUnitSelelectioModel() {
        return m_p2ouModel;
    }

    protected BigDecimalParameter getP2OUParameter() {
        return m_p2ouParam;
    }

    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new Project2OrgaUnitTable(m_itemModel, m_p2ouModel));
        return container;
    }

    public FormSection getEditSheet() {
        return new Project2OrgaUnitPropertyForm(m_itemModel, m_p2ouModel);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_p2ouParam);
    }
}
