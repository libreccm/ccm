package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsPropertiesStep
        extends PublicationPropertiesStep {

    public InProceedingsPropertiesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getInProceedingsPropertySheet(
            ItemSelectionModel itemModel) {
          DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationWithPublisherPropertiesStep.
                getPublicationPropertySheet(itemModel);

          sheet.add(PublicationGlobalizationUtil.globalize(
                  "publications.ui.inproceedings.pages_from"),
                  InProceedings.PAGES_FROM);

          sheet.add(PublicationGlobalizationUtil.globalize(
                  "publications.ui.inproceedings.pages_to"),
                  InProceedings.PAGES_TO);

          return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet = new InProceedingsPropertyForm(itemModel,
                                                                     this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getInProceedingsPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publications.basic_properties").localize()),
                basicProperties);
    }
}
