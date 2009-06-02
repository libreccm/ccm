package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.util.Assert;

/**
 * Form for editing a GenericOrganization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationEditForm extends GenericOrganizationForm implements FormSubmissionListener {

    private SimpleEditStep m_step;

    /**
     * Constructor.
     *
     * @param itemModel
     * @param step
     */
    public GenericOrganizationEditForm(ItemSelectionModel itemModel, SimpleEditStep step) {
        super("GenericOrganizationEditForm", itemModel);
        addSubmissionListener(this);
        m_step = step;
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        super.initBasicWidgets(e);
    }

    public void submitted(FormSectionEvent fse) {
        if (getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        GenericOrganization orga = processBasicWidgets(fse);
        m_step.maybeForwardToNextStep(fse.getPageState());
    }

    /**
     * This function does nothing real usefull yet. It will maybe removed later. 
     * 
     * @param fse
     * @throws FormProcessException
     */
    public void validate(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();

        GenericOrganization orga = (GenericOrganization) m_itemModel.getSelectedObject(state);
        Assert.exists(orga, GenericOrganization.class);

        String newOrganizationName = (String)data.get(GenericOrganizationForm.ORGANIZATIONAME);
        String oldOrganizationName = orga.getOrganizationName();

        boolean valid = true;

        if(!valid) {
            throw new FormProcessException("error");
        }
    }

    private Folder getParentFolder(GenericOrganization orga) {
        ContentItem parent = (ContentItem)orga.getParent();
        while((parent != null)
                && (!(parent instanceof Folder))) {
            parent = (ContentItem)parent.getParent();
        }

        return (Folder)parent;
    }
}
