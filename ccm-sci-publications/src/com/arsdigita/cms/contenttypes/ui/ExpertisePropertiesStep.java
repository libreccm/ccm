package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertisePropertiesStep extends PublicationPropertiesStep {

    public ExpertisePropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getExpertisePropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) getPublicationPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.place"),
                  Expertise.PLACE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.organization"),
                  Expertise.ORGANIZATION);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.number_of_pages"),
                  Expertise.NUMBER_OF_PAGES);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.orderer"),
                  Expertise.ORDERER);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new ExpertisePropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getExpertisePropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }
}
