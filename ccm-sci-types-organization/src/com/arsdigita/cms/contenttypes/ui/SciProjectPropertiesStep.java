package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
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
                  SciProject.BEGIN);
        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.end"),
                  SciProject.END);
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
                "sciorganization.ui.project.basic_properties").
                localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        /*addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
        parent),
        "cms.contenttypes.ui.orgaunit.contact");
        addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
        parent),
        "cms.contenttypes.ui.orgaunit.persons");*/

        super.addSteps(itemModel, parent);

        addStep(new SciProjectSubprojectsStep(itemModel,
                                              parent),
                "cms.contenttypes.ui.project.subprojects");

    }
}
