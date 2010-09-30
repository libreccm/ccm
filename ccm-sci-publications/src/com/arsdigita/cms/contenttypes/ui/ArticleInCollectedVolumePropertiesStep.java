package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumePropertiesStep
        extends PublicationPropertiesStep {

    public ArticleInCollectedVolumePropertiesStep(ItemSelectionModel itemModel,
                                                  AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getArticleInCollectedVolumePropertySheet(
            ItemSelectionModel itemModel) {
         DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationPropertiesStep.
                getPublicationPropertySheet(itemModel);

         sheet.add(PublicationGlobalizationUtil.globalize(
                 "publications.ui.article_in_collected_volume.pages_from"),
                 ArticleInCollectedVolume.PAGES_FROM);

         sheet.add(PublicationGlobalizationUtil.globalize(
                 "publications.ui.article_in_collected_volume.pages_to"),
                 ArticleInCollectedVolume.PAGES_TO);

         sheet.add(PublicationGlobalizationUtil.globalize(
                 "publications.ui.article_in_collected_volume.chapter"),
                 ArticleInCollectedVolume.CHAPTER);

         return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new ArticleInCollectedVolumePropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.article_in_collected_volume.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getArticleInCollectedVolumePropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }
}
