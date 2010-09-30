package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;


/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedPropertiesStep extends PublicationPropertiesStep {

    public UnPublishedPropertiesStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getUnPublishedPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationPropertiesStep.
                getPublicationPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.place"),
                UnPublished.PLACE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization"),
                UnPublished.ORGANIZATION);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.number"),
                UnPublished.NUMBER);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.number_of_pages"),
                UnPublished.NUMBER_OF_PAGES);



        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet = new UnPublishedPropertyForm(itemModel,
                                                                   this);

           basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getUnPublishedPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }
}
