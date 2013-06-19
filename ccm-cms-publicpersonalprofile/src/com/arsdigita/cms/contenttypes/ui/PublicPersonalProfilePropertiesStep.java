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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfilePropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public PublicPersonalProfilePropertiesStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        super(itemModel, parent);

        //Use a segemented panel to provide an easy for adding addional forms
        //to the step.
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        setDefaultEditKey(EDIT_SHEET_NAME);

        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new PublicPersonalProfilePropertyForm(itemModel,
                                                               this);
        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicPersonalProfileGlobalizationUtil.
                globalize(
                "publicpersonalprofile.ui.profile.edit_basic_properties").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(getPublicPersonalProfilePropertySheet(
                itemModel));

        segmentedPanel.addSegment(new Label((String) PublicPersonalProfileGlobalizationUtil.
                globalize("publicpersonalprofile.ui.profile.basic_properties").
                localize()), basicProperties);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getPublicPersonalProfilePropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.owner"),
                  PublicPersonalProfileBundle.OWNER, new OwnerFormatter());

        sheet.add(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile_url"),
                  PublicPersonalProfile.PROFILE_URL);

        return sheet;
    }

    private static class OwnerFormatter implements
            DomainObjectPropertySheet.AttributeFormatter {

        public String format(DomainObject obj, String attribute, PageState state) {
            PublicPersonalProfile profile = (PublicPersonalProfile) obj;

            GenericPerson owner = profile.getOwner();

            if (owner == null) {
                return "";
            } else {
                return owner.getFullName();
            }
        }
    }
}
