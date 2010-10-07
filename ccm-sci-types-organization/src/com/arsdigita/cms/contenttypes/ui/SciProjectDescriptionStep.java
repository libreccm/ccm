package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionStep extends SimpleEditStep {

    private String EDIT_PROJECT_DESC_SHEET_NAME = "editProjectDesc";

    public SciProjectDescriptionStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDescriptionStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent,
                                     String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editDescForm = new SciProjectEditDescForm(itemModel);
        add(EDIT_PROJECT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.edit_desc").localize(),
            new WorkflowLockedComponentAccess(editDescForm, itemModel),
            editDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciProjectEditDescSheet(itemModel));

    }

    public static Component getSciProjectEditDescSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizaztion.ui.project.desc"),
                SciProject.PROJECT_DESCRIPTION);

        return sheet;
    }
}
