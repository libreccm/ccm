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
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public SeriesPropertiesStep(ItemSelectionModel itemModel,
                                AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addBasicProperties(itemModel, parent);
        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getSeriesPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.name"),
                  Series.NAME);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.title"),
                  Series.TITLE);
           sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.series.abstract"),
                  Series.ABSTRACT,
                  new PublicationPropertiesStep.PreFormattedTextFormatter());


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

    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet = new SeriesPropertyForm(itemModel, this);

        basicProperties.add(
                EDIT_SHEET_NAME,
                PublicationGlobalizationUtil.globalize(
                        "publications.ui.series.edit_basic_sheet"),
                new WorkflowLockedComponentAccess(editBasicSheet,itemModel),
                editBasicSheet.getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(getSeriesPropertySheet(itemModel));

        segmentedPanel.addSegment(new Label(PublicationGlobalizationUtil.
                globalize("publications.ui.series.basic_properties")),
                                  basicProperties);
    }

    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new SeriesEditshipStep(itemModel, parent),
                "publications.ui.series.editors");
        addStep(new SeriesVolumesStep(itemModel, parent),
                "publications.ui.series.volumes");
    }

    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                labelKey).localize()),
                step);
    }
}
