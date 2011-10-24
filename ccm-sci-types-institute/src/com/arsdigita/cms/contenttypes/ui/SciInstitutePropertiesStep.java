package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstitutePropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciInstitutePropertiesStep(final ItemSelectionModel itemModel,
                                      final AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getSciInstitutePropertySheet(
            final ItemSelectionModel itemModel) {

        final DomainObjectPropertySheet sheet =
                                        (DomainObjectPropertySheet) GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(itemModel);

        sheet.add(SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.shortdesc"),
                  SciInstitute.INSTITUTE_SHORT_DESCRIPTION);

        return sheet;
    }

    @Override
    public void addBasicProperties(final ItemSelectionModel itemModel,
                                   final AuthoringKitWizard parent) {

        final SimpleEditStep basicProperties =
                             new SimpleEditStep(itemModel,
                                                parent,
                                                EDIT_SHEET_NAME);

        final BasicPageForm editBasicSheet = new SciInstitutePropertyForm(
                itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(getSciInstitutePropertySheet(
                itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.edit_basic_properties").localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(final ItemSelectionModel itemModel,
                            final AuthoringKitWizard parent) {
        addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                   parent),
                SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.contacts"));
    }
}
