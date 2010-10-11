package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
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
                getGenericOrganizationalUnitPropertySheet(itemModel));

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
                    "cms.contenttypes.ui.orgaunit.contact");
        }

        if (!config.getDepartmentAddPersonHide()) {
            addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
                                                                      parent),
                    "cms.contenttypes.ui.orgaunit.persons");
        }

        if (!config.getDepartmentAddSubDepartmentHide()) {
            addStep(new SciDepartmentSubDepartmentsStep(itemModel, parent),
                    "sciorganization.ui.department.subdepartments");
        }

        if (!config.getDepartmentSetSuperDepartmentHide()) {
            addStep(new SciDepartmentSuperDepartmentStep(itemModel, parent),
                    "sciorganization.ui.department.superdepartment");
        }

        if (!config.getDepartmentAddProjectHide()) {
            addStep(new SciDepartmentProjectsStep(itemModel, parent),
                    "sciorganization.ui.department.projects");
        }

        if (!config.getDepartmentSetOrganizationHide()) {
            addStep(new SciDepartmentOrganizationStep(itemModel, parent),
                    "sciorganization.ui.department.organization");
        }
    }
}
