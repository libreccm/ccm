package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 */
public class OrganizationPropertiesStep extends GenericOrganizationalUnitPropertiesStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public OrganizationPropertiesStep(final ItemSelectionModel itemModel,
                                            final AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    protected void createEditSheet(final ItemSelectionModel itemModel) {
        final BasicPageForm editSheet = new OrganizationPropertyForm(
                itemModel, this);
        add(EDIT_SHEET_NAME,
            "Edit",
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());
    }

    public static Component getOrganizationPropertySheet(
            final ItemSelectionModel itemModel) {
        final Component sheet = GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(itemModel);

        return sheet;
    }
}
