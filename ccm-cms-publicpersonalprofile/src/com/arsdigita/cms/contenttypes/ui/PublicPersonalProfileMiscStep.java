package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
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
public class PublicPersonalProfileMiscStep extends SimpleEditStep {
    
     private String EDIT_MISC_SHEET_NAME = "editMisc";

    public PublicPersonalProfileMiscStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicPersonalProfileMiscStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent,
                                             final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editPositionForm = new PublicPersonalProfileMiscEditForm(
                itemModel);
        add(EDIT_MISC_SHEET_NAME,
            (String) PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.misc.edit").localize(),
            new WorkflowLockedComponentAccess(editPositionForm, itemModel),
            editPositionForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getPublicPersonalProfilePositionSheet(itemModel));
    }

    public static Component getPublicPersonalProfilePositionSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.misc"),
                  PublicPersonalProfile.MISC);

        return sheet;
    }
    
}
