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
import com.arsdigita.cms.contenttypes.Proceedings;
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
public class ProceedingsPropertiesStep
        extends PublicationWithPublisherPropertiesStep {

    public ProceedingsPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getProceedingsPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) getPublicationWithPublisherPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.name_of_conference"),
                  Proceedings.NAME_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.place_of_conference"),
                  Proceedings.PLACE_OF_CONFERENCE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_from_of_conference"),
                  Proceedings.DATE_FROM_OF_CONFERENCE,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject item,
                                 String attribute,
                                 PageState state) {
                Proceedings proceedings = (Proceedings) item;
                if (proceedings.getDateFromOfConference() != null) {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(proceedings.
                            getDateFromOfConference());
                } else {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                }
            }
        });

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_to_of_conference"),
                  Proceedings.DATE_TO_OF_CONFERENCE,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject item,
                                 String attribute,
                                 PageState state) {
                Proceedings proceedings = (Proceedings) item;
                if (proceedings.getDateToOfConference() != null) {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(proceedings.
                            getDateToOfConference());
                } else {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                }
            }
        });

        return sheet;
    }

    @Override
    protected void addBasicProperties(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new ProceedingsPropertyForm(itemModel, this);

        basicProperties.add(
                EDIT_SHEET_NAME,
                PublicationGlobalizationUtil.globalize(
                        "publications.ui.proceedings.edit_basic_sheet"),
                new WorkflowLockedComponentAccess(editBasicSheet,itemModel),
                editBasicSheet.getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(
                getProceedingsPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.proceedings.basic_properties")),
                basicProperties);
    }

    @Override
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        super.addSteps(itemModel, parent);

        addStep(new ProceedingsOrganizerStep(itemModel, parent),
                "publications.ui.proceedings.organizer");

        addStep(new ProceedingsPapersStep(itemModel, parent),
                "publications.ui.proceedings.papers");
    }
}
