package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectOrganizationsStep extends SimpleEditStep {

    private String PROJECT_ADD_ORGA_SHEET_NAME = "projectAddOrga";

    public SciProjectOrganizationsStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectOrganizationsStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent,
                                       String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addOrgaForm =
                      new SciProjectOrganizationsAddForm(itemModel);
        add(PROJECT_ADD_ORGA_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.addOrga").localize(),
            new WorkflowLockedComponentAccess(addOrgaForm, itemModel),
            addOrgaForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                new SciProjectOrganizationsTable(itemModel));
    }
}
