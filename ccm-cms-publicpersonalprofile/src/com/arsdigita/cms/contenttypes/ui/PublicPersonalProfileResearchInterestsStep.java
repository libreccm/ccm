/*
 * Copyright (c) 2011 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
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
