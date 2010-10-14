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

import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Step for editing the basic properties of a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciOrganizationPropertiesStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new SciOrganizationPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getGenericOrganizationalUnitPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.basic_properties").
                localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        //super.addSteps(itemModel, parent);

        SciOrganizationConfig config;
        config = SciOrganization.getConfig();

        if (!config.getOrganizationAddContactHide()) {
            addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                       parent),
                    "sciorganization.ui.organization.contacts");
        }

        if (!config.getOrganizationAddPersonHide()) {
            addStep(new SciOrganizationMemberStep(itemModel,
                                                  parent),
                    "sciorganization.ui.organization_members");
        }

        if (!config.getOrganizationAddDepartmentHide()) {
            addStep(new SciOrganizationDepartmentsStep(itemModel, parent),
                    "sciorganization.ui.organization.departments");
        }

        if (!config.getOrganizationAddProjectHide()) {
            addStep(new SciOrganizationProjectsStep(itemModel, parent),
                    "sciorganization.ui.organization.projects");
        }
    }
}
