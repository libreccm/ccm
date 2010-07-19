package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonPropertiesStep extends SimpleEditStep {

    private static final String ADD_PERSON_SHEET_NAME = "addPerson";

    public GenericOrganizationalUnitPersonPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericOrganizationalUnitPersonPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addPersonSheet = new GenericOrganizationalUnitAddPersonForm(itemModel);
        add(ADD_PERSON_SHEET_NAME,
                (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_person").localize(),
                new WorkflowLockedComponentAccess(addPersonSheet, itemModel),
                addPersonSheet.getSaveCancelSection().getCancelButton());

        GenericOrganizationalUnitPersonsTable personsTable = new GenericOrganizationalUnitPersonsTable(itemModel);
        setDisplayComponent(personsTable);
    }

}
