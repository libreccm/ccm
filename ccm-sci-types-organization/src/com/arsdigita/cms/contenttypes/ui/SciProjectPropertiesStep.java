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
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;

/**
 * Step for editing the basic properties of a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciProjectPropertiesStep(ItemSelectionModel itemModel,
                                    AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getSciProjectPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.begin"),
                  SciProject.BEGIN,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject obj,
                                 String attribute,
                                 PageState state) {
                SciProject project = (SciProject) obj;
                if (project.getBegin() == null) {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(
                            project.getBegin());
                }
            }
        });
        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.end"),
                  SciProject.END,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject obj,
                                 String attribute,
                                 PageState state) {
                SciProject project = (SciProject) obj;
                if (project.getEnd() == null) {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(
                            project.getEnd());
                }
            }
        });
        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.shortdesc"),
                  SciProject.PROJECT_SHORT_DESCRIPTION);
      
        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new SciProjectPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getSciProjectPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.edit_basic_properties").
                localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        SciOrganizationConfig config;
        config = SciProject.getConfig();

        if (!config.getProjectAddContactHide()) {
            addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                       parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.contacts"));
        }

       /* if (!config.getProjectAddPersonHide()) {
            addStep(new SciProjectMemberStep(itemModel,
                                             parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.members"));
        }*/

        if (!config.getProjectSetSuperProjectHide()) {
            addStep(new SciProjectSuperProjectStep(itemModel,
                                                   parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.superproject"));
        }

      /*  if (!config.getProjectAddSubProjectHide()) {
            addStep(new SciProjectSubprojectsStep(itemModel,
                                                  parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.subprojects"));
        }*/

        if (!config.getProjectAddOrganizationHide()) {
            addStep(new SciProjectOrganizationsStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.organizations"));
        }

        /*if (!config.getProjectAddDepartmentHide()) {
            addStep(new SciProjectDepartmentsStep(itemModel, parent),
                    SciOrganizationGlobalizationUtil.globalize(
                    "sciorganization.ui.project.departments"));
        }

        ContentTypeCollection contentTypes = ContentType.getAllContentTypes();
        contentTypes.addFilter("associatedObjectType = :type").set(
                "type",
                "com.arsdigita.cms.contenttypes.Publication");
        if ((!config.getProjectMaterialsHide())
            && (contentTypes.size() > 0)) {
            /*
             * Must add this step manually since the basic class is not
             * SimpleEditStep...
             */
           /* getSegmentedPanel().addSegment(new Label(SciOrganizationGlobalizationUtil.
                    globalize("sciorganization.ui.project.publications")),
                                           new SciProjectPublicationsStep(
                    itemModel, parent));
        }*/
    }
}
