package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.CreationComponent;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.ui.util.GlobalizationUtil;

/**
 * Form for creating a new organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationCreate extends GenericOrganizationForm implements FormInitListener, FormProcessListener, FormSubmissionListener, FormValidationListener, CreationComponent {
    private CreationSelector m_parent;
    private ApplyWorkflowFormSection m_workflowSection;

    /**
     * Constructor.
     *
     * @param itemModel
     * @param parent
     */
    public GenericOrganizationCreate(ItemSelectionModel itemModel, CreationSelector parent) {
        super("GenericOrganizationCreate", itemModel);
        m_parent = parent;
        m_workflowSection.setCreationSelector(m_parent);
        m_workflowSection.setContentType(m_itemModel.getContentType());
        addSubmissionListener(this);
        getSaveCancelSection().getSaveButton().setButtonLabel("Create");
    }

    /**
     * Adds all widgets to the form.
     */
    @Override
    protected void addWidgets() {
        m_workflowSection = new ApplyWorkflowFormSection();
        add(m_workflowSection, ColumnPanel.INSERT);
        super.addWidgets();
    }

    public ApplyWorkflowFormSection getWorkflowSection() {
        return m_workflowSection;
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        //Nothing yet
    }

    public void submitted(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();

        if(getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_parent.redirectBack(state);
            throw new FormProcessException((String)GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.submission_canceled").localize());
        }
    }

    public void validate(FormSectionEvent event) throws FormProcessException {
        //Nothing yet
    }

    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final ContentSection section = m_parent.getContentSection(state);

        final GenericOrganization orga = createGenericOrganization(state);

        orga.setOrganizationName((String)data.get(ORGANIZATIONAME));
        orga.setOrganizationNameAddendum((String)data.get(ORGANIZATIONNAMEADDENDUM));
        orga.setDescription((String)data.get(DESCRIPTION));

        final ContentBundle bundle = new ContentBundle(orga);
        bundle.setParent(m_parent.getFolder(state));
        bundle.setContentSection(section);

        m_workflowSection.applyWorkflow(state, orga);

        m_parent.editItem(state, orga);
    }
}
