package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteDescriptionStep extends SimpleEditStep {

    private String EDIT_INSTITUTE_DESC_SHEET_NAME = "editDepartmentDesc";
    private String UPDATE_INSTITUTE_DESC_SHEET_NAME = "updateDepartmentDesc";

    public SciInstituteDescriptionStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciInstituteDescriptionStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editDescFrom = new SciInstituteDescriptionEditForm(
                itemModel);
        add(EDIT_INSTITUTE_DESC_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.desc.edit"),
            new WorkflowLockedComponentAccess(editDescFrom, itemModel),
            editDescFrom.getSaveCancelSection().getCancelButton());

        final SciInstituteDescriptionUploadForm updateDescForm =
                                                new SciInstituteDescriptionUploadForm(
                itemModel);
        add(UPDATE_INSTITUTE_DESC_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.desc.upload"),
            new WorkflowLockedComponentAccess(updateDescForm, itemModel),
            updateDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciInstituteEditDescSheet(itemModel));
    }

    public static Component getSciInstituteEditDescSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(
                SciInstituteGlobalizationUtil.globalize("sciinstitute.ui.desc"),
                  SciInstitute.INSTITUTE_DESCRIPTION);

        return sheet;
    }
}
