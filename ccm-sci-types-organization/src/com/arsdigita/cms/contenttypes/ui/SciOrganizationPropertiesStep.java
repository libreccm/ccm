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
                    "cms.contenttypes.ui.orgaunit.contact");
        }

        if (!config.getOrganizationAddPersonHide()) {
            addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
                                                                      parent),
                    "cms.contenttypes.ui.orgaunit.persons");
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
