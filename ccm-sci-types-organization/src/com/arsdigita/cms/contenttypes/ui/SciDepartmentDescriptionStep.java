package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentDescriptionStep extends SimpleEditStep {

    private String EDIT_DEPARTMENT_DESC_SHEET_NAME = "editDepartmentDesc";
    private String UPLOAD_DEPARTMENT_DESC_SHEET_NAME = "uploadDepartmentDesc";

    public SciDepartmentDescriptionStep(ItemSelectionModel itemModel,
                                        AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentDescriptionStep(ItemSelectionModel itemModel,
                                        AuthoringKitWizard parent,
                                        String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editDescForm =
                      new SciDepartmentDescriptionEditForm(itemModel);
        add(EDIT_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.edit_desc").localize(),
            new WorkflowLockedComponentAccess(editDescForm, itemModel),
            editDescForm.getSaveCancelSection().getCancelButton());

        SciDepartmentDescriptionUploadForm uploadDescForm =
                                           new SciDepartmentDescriptionUploadForm(
                itemModel);
        add(UPLOAD_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.upload_desc").localize(),
            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
            uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciDepartmentDescSheet(itemModel));
    }

    public static Component getSciDepartmentDescSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.desc"),
                SciDepartment.DEPARTMENT_DESCRIPTION);

        return sheet;
    }
}
