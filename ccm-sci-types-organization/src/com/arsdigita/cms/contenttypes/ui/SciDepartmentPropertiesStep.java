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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Step for editing a {@link SciDepartment}.
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 */
public class SciDepartmentPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciDepartmentPropertiesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getSciDepartmentPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet =
                                  (DomainObjectPropertySheet) GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.department.shortdescription"),
                  SciDepartment.DEPARTMENT_SHORT_DESCRIPTION);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new SciDepartmentPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.edit_basic_properties").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getSciDepartmentPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.basic_properties").localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        //super.addSteps(itemModel, parent);

        SciOrganizationConfig config;
        config = SciDepartment.getConfig();

        if (!config.getDepartmentAddContactHide()) {
            addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                       parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.contacts"));
        }

        /*
        if (!config.getDepartmentAddPersonHide()) {
            addStep(new SciDepartmentMemberStep(itemModel,
                                                parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.members"));
        }
         */

        /*
        if (!config.getDepartmentAddSubDepartmentHide()) {
            addStep(new SciDepartmentSubDepartmentsStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.subdepartments"));
        }         
         */
        
        if (!config.getDepartmentSetSuperDepartmentHide()) {
            addStep(new SciDepartmentSuperDepartmentStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.superdepartment"));
        }

        /*
        if (!config.getDepartmentAddProjectHide()) {
            addStep(new SciDepartmentProjectsStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.projects"));
        }         
         */

        if (!config.getDepartmentSetOrganizationHide()) {
            addStep(new SciDepartmentOrganizationStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.department.organization"));
        }

        /*
        ContentTypeCollection contentTypes = ContentType.getAllContentTypes();
        contentTypes.addFilter("associatedObjectType = :type").set(
                "type",
                "com.arsdigita.cms.contenttypes.Publication");
        if ((!config.getDepartmentPublicationsHide())
            && (contentTypes.size() > 0)) {
            /*
             * Must add this step manually since the basic class is not
             * SimpleEditStep...
             */
           /* getSegmentedPanel().addSegment(new Label(SciOrganizationGlobalizationUtil.
                    globalize("sciorganization.ui.department.publications")),
                                           new SciDepartmentPublicationsStep(
                    itemModel, parent));
        }*/
    }
}
