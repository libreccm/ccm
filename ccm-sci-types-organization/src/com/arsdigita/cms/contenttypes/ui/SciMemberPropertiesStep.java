/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Step for editing the basic properties of an {@link SciMember}.
 *
 * @author Jens Pelzetter
 * @see SciMember
 * @see GenericPerson
 */
public class SciMemberPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public SciMemberPropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editSheet;
        editSheet = new SciMemberPropertyForm(itemModel, this);
        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.edit_basic_properties").localize(),
                            new WorkflowLockedComponentAccess(editSheet,
                                                              itemModel),
                            editSheet.getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(getSciMemberPropertySheet(itemModel));

        segmentedPanel.addSegment(new Label((String) SciOrganizationGlobalizationUtil.
                globalize("scimember.ui.basic_properties").localize()),
                                  basicProperties);

        segmentedPanel.addSegment(new Label((String) SciOrganizationGlobalizationUtil.
                globalize("scimember.ui.organizations").localize()),
                                  new SciMemberSciOrganizationsStep(itemModel,
                                                                    parent));


        setDisplayComponent(segmentedPanel);
    }

    public static Component getSciMemberPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet;

        sheet = (DomainObjectPropertySheet) GenericPersonPropertiesStep.
                getGenericPersonPropertySheet(itemModel);

        return sheet;
    }
}
