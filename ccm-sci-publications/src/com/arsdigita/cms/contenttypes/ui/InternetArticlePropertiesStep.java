/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class InternetArticlePropertiesStep extends PublicationPropertiesStep {

    public InternetArticlePropertiesStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getInternetArticlePropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationPropertiesStep.
                getPublicationPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.place"),
                InternetArticle.PLACE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.organization"),
                "organization.title");

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number"),
                InternetArticle.NUMBER);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number_of_pages"),
                InternetArticle.NUMBER_OF_PAGES);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.edition"),
                InternetArticle.EDITION);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.issn"),
                InternetArticle.ISSN);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.publication_date"),
                InternetArticle.PUBLICATION_DATE);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new InternetArticlePropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.edit_basic_sheet").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getInternetArticlePropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").localize()),
                basicProperties);
    }
}
