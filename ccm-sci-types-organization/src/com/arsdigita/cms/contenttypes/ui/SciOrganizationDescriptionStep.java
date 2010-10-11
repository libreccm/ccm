package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationDescriptionStep extends SimpleEditStep {

    private String EDIT_ORGANIZATION_DESC_SHEET_NAME = "editOrganizationDesc";
    private String UPLOAD_ORGANIZATION_DESC_SHEET_NAME =
                   "uploadOrganizationDesc";

    public SciOrganizationDescriptionStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciOrganizationDescriptionStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent,
                                          String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editDescForm =
                new SciOrganizationDescriptionEditForm(itemModel);
        add(EDIT_ORGANIZATION_DESC_SHEET_NAME,
                (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.edit_desc").localize(),
                new WorkflowLockedComponentAccess(editDescForm, itemModel),
                editDescForm.getSaveCancelSection().getCancelButton());

        SciOrganizationDescriptionUploadForm uploadDescForm =
                new SciOrganizationDescriptionUploadForm(itemModel);
        add(UPLOAD_ORGANIZATION_DESC_SHEET_NAME,
                (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.upload_desc").localize(),
                new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
                uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciOrganizationEditDescSheet(itemModel));
    }

    public static Component getSciOrganizationEditDescSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.desc"),
                SciOrganization.ORGANIZATION_DESCRIPTION);

        return sheet;
    }
}
