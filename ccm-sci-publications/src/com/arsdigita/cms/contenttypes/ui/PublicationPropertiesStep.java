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
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainService;
import java.text.DateFormat;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public PublicationPropertiesStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addBasicProperties(itemModel, parent);
        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getPublicationPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.name"),
                  Publication.NAME);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.title"),
                  Publication.TITLE);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.year_of_publication"),
                  Publication.YEAR_OF_PUBLICATION);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.abstract"),
                  Publication.ABSTRACT);
                  //new PreFormattedTextFormatter());
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.misc"),
                  Publication.MISC);

        if (Publication.getConfig().getEnableFirstPublishedProperty()) {
            sheet.add(PublicationGlobalizationUtil.globalize("publications.ui.publication.first_published"),
                      Publication.FIRST_PUBLISHED);
        }

        if (Publication.getConfig().getEnableLanguageProperty()) {
            sheet.add(PublicationGlobalizationUtil.globalize("publications.ui.publication.language"),
                      Publication.LANG);
        }

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(ContenttypesGlobalizationUtil.globalize(
                    "cms.ui.authoring.page_launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {
                public String format(DomainObject item,
                                     String attribute,
                                     PageState state) {
                    ContentPage page = (ContentPage) item;
                    if (page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).
                                format(page.getLaunchDate());
                    } else {
                        return (String) ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }

    protected SegmentedPanel getSegmentedPanel() {
        return segmentedPanel;
    }

    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet = new PublicationPropertyForm(itemModel,
                                                                   this);
        basicProperties.add(EDIT_SHEET_NAME, (String) PublicationGlobalizationUtil.
                globalize("publications.ui.publication.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel), editBasicSheet.
                getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(getPublicationPropertySheet(
                itemModel));

        segmentedPanel.addSegment(new Label((String) PublicationGlobalizationUtil.
                globalize("publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }

    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new PublicationAuthorsPropertyStep(itemModel, parent),
                "publications.ui.publication.authors");
        if (isSeriesStepEnabled()) {
            addStep(new PublicationSeriesPropertyStep(itemModel, parent),
                    "publications.ui.publication.series");
        }
    }

    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                labelKey).localize()),
                step);
    }

    protected boolean isSeriesStepEnabled() {
        return true;
    }

    protected static class PreFormattedTextFormatter
            extends DomainService
            implements DomainObjectPropertySheet.AttributeFormatter {

        public PreFormattedTextFormatter() {
            super();
        }

        public String format(DomainObject obj, String attribute, PageState state) {
            String str = (String) get(obj, attribute);
            if ((str == null) || str.trim().isEmpty()) {
                return (String) GlobalizationUtil.globalize("cms.ui.unknown").
                        localize();
            } else {
                return String.format("<pre>%s</pre>", str);
            }
        }
    }
}
