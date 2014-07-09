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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublisherPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public PublisherPropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getPublisherPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) 
                getGenericOrganizationalUnitPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.name"),
                  Publisher.PUBLISHER_NAME);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.place"),
                  Publisher.PLACE);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);
        BasicPageForm editBasicSheet = new PublisherPropertyForm(itemModel,
                                                                 this);
        basicProperties.add(
                EDIT_SHEET_NAME,
                PublicationGlobalizationUtil.globalize(
                         "publications.ui.publisher.edit_basic_properties"),
                new WorkflowLockedComponentAccess(editBasicSheet,itemModel),
                editBasicSheet.getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(
                getPublisherPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.basic_properties")),
                basicProperties);
    }
}
