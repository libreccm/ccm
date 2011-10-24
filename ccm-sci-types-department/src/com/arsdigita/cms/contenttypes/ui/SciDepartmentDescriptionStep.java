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
 * @version $Id$
 */
public class SciDepartmentDescriptionStep extends SimpleEditStep {

    private String EDIT_DEPARTMENT_DESC_SHEET_NAME = "editDepartmentDesc";
    private String UPDATE_DEPARTMENT_DESC_SHEET_NAME = "updateDepartmentDesc";

    public SciDepartmentDescriptionStep(final ItemSelectionModel itemModel,
                                        final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentDescriptionStep(final ItemSelectionModel itemModel,
                                        final AuthoringKitWizard parent,
                                        final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editDescFrom = new SciDepartmentDescriptionEditForm(
                itemModel);
        add(EDIT_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.desc.edit").localize(),
            new WorkflowLockedComponentAccess(editDescFrom, itemModel),
            editDescFrom.getSaveCancelSection().getCancelButton());

        final SciDepartmentDescriptionUploadForm updateDescForm =
                                                 new SciDepartmentDescriptionUploadForm(
                itemModel);
        add(UPDATE_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.desc.upload").localize(),
            new WorkflowLockedComponentAccess(updateDescForm, itemModel),
            updateDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciDepartmentEditDescSheet(itemModel));
    }

    public static Component getSciDepartmentEditDescSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);
        
        sheet.add(SciDepartmentGlobalizationUtil.globalize("scidepartment.ui.desc"),
                  SciDepartment.DEPARTMENT_DESCRIPTION);
        
        return sheet;
    }
}
