/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainService;
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

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.reviewed"),
                  ArticleInCollectedVolume.REVIEWED, new ReviewedFormatter());

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

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        super.addSteps(itemModel, parent);

        addStep(new ArticleInCollectedVolumeCollectedVolumeStep(itemModel,
                                                                parent),
                (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.collectedVolume").
                localize());

    }

    private static class ReviewedFormatter
            extends DomainService
            implements DomainObjectPropertySheet.AttributeFormatter {

        public ReviewedFormatter() {
            super();
        }

        @Override
        public String format(DomainObject obj, String attribute, PageState state) {
            if ((get(obj, attribute) != null)
                && (get(obj, attribute) instanceof Boolean)
                && ((Boolean) get(obj, attribute) == true)) {
                return (String) PublicationGlobalizationUtil.globalize(
                        "publications.ui.articleInCollectedVolume.reviewed.yes").localize();
            } else {
                return (String) PublicationGlobalizationUtil.globalize(
                        "publications.ui.articleInCollectedVolume.reviewed.no").localize();
            }
        }
    }

    @Override
    protected boolean isSeriesStepEnabled() {
        return false;
    }
}
