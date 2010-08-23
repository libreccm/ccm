package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumePropertiesStep
        extends PublicationWithPublisherPropertiesStep {

    public CollectedVolumePropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    @Override
    protected void addBasicProperties(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new CollectedVolumePropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getPublicationPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        super.addSteps(itemModel,
                parent);

        addStep(new CollectedVolumeArticlesStep(itemModel, parent),
                "publications.ui.collected_volume_articles");
    }
}
