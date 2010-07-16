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
public class Project2PersonPropertiesStep extends ResettableContainer {

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private BigDecimalParameter m_project2PersonParam =
            new BigDecimalParameter("project2Person");
    private Project2PersonSelectionModel m_project2PersonModel =
            new Project2PersonSelectionModel(m_project2PersonParam);

    public Project2PersonPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        m_itemModel = itemModel;
        m_parent = parent;
        setProject2PersonSelectionModel();
        add(getDisplayComponent());

        Form form = new Form("project2PersonEditForm");
        form.add(getEditSheet());

        WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
        edit.add(form);
        add(edit);
    }

    protected void setProject2PersonSelectionModel() {
        setProject2PersonSelectionModel(
                new Project2PersonSelectionModel(m_project2PersonParam));
    }

    protected void setProject2PersonSelectionModel(
            Project2PersonSelectionModel model) {
        m_project2PersonModel = model;
    }

    protected Project2PersonSelectionModel getProject2PersonSelectionModel() {
        return m_project2PersonModel;
    }

    protected BigDecimalParameter getProject2PersonParameter() {
        return m_project2PersonParam;
    }

    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        container.add(new Project2PersonTable(m_itemModel,
                m_project2PersonModel));
        return container;
    }

    public FormSection getEditSheet() {
        return new Project2PersonPropertyForm(m_itemModel,
                m_project2PersonModel);
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_project2PersonParam);
    }
}
