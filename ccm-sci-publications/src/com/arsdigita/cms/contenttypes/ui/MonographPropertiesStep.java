package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class MonographPropertiesStep
        extends PublicationWithPublisherPropertiesStep {

    public MonographPropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getMonographPropertiesStep(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationWithPublisherPropertiesStep.
                getPublicationWithPublisherPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.volume"),
                  Monograph.VOLUME);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.numberOfVolumes"),
                  Monograph.NUMBER_OF_VOLUMES);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.numberOfPages"),
                  Monograph.NUMBER_OF_PAGES);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.edition"),
                  Monograph.EDITION);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new MonographPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.monography.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getPublicationPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").localize()),
                basicProperties);
    }
}
