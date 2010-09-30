package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPropertiesStep
        extends PublicationWithPublisherPropertiesStep {

    public ProceedingsPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getProceedingsPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) getPublicationPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.organizer_of_conference"),
                  Proceedings.ORGANIZER_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.name_of_conference"),
                  Proceedings.NAME_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.place_of_conference"),
                  Proceedings.PLACE_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_from_of_conference"),
                  Proceedings.DATE_FROM_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_TO_of_conference"),
                  Proceedings.DATE_TO_OF_CONFERENCE);

        return sheet;
    }

    @Override
    protected void addBasicProperties(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new ProceedingsPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getPublicationPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.basic_properties").localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        super.addSteps(itemModel, parent);

        addStep(new ProceedingsPapersStep(itemModel, parent),
                "publications.ui.proceedings.papers");
    }
}
