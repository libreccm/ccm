/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class PublicPersonalProfileResearchInterestsStep extends SimpleEditStep {

    private String EDIT_RI_SHEET_NAME = "editResearchInterests";

    public PublicPersonalProfileResearchInterestsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicPersonalProfileResearchInterestsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editRiForm =
                            new PublicPersonalProfileResearchInterestsEditForm
                (itemModel);
        add(EDIT_RI_SHEET_NAME, (String) PublicPersonalProfileGlobalizationUtil.
                globalize("publicpersonalprofile.ui.research_interests.edit").
                localize(),
            new WorkflowLockedComponentAccess(editRiForm, itemModel),
            editRiForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getResearchInterestsSheet(itemModel));
    }

    public static Component getResearchInterestsSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.research_interests"),
                  PublicPersonalProfile.INTERESTS);

        return sheet;
    }
}
