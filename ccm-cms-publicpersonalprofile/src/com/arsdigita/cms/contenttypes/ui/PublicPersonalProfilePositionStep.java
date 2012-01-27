package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicPersonalProfilePositionStep extends SimpleEditStep {

    private String EDIT_POSITION_SHEET_NAME = "editPosition";

    public PublicPersonalProfilePositionStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicPersonalProfilePositionStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent,
                                             final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editPositionForm = new PublicPersonalProfilePositionEditForm(
                itemModel);
        add(EDIT_POSITION_SHEET_NAME,
            (String) PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.position.edit").localize(),
            new WorkflowLockedComponentAccess(editPositionForm, itemModel),
            editPositionForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getPublicPersonalProfilePositionSheet(itemModel));
    }

    public static Component getPublicPersonalProfilePositionSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.position"),
                  PublicPersonalProfile.POSITION);

        return sheet;
    }
}
