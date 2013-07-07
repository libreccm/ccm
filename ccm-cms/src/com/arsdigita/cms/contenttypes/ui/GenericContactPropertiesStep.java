/*
 * Copyright (C) 2009-2013 Sören Bernstein, University of Bremen. All Rights Reserved.
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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 * Central entry point into the AuthoringStep for the basic properties of a 
 * basic contact (GenericContact). This class has to be specified in the 
 * content type's definition XML. 
 * 
 * It uses a segmented Panel to provide several components in one editing step.
 * Handles the basic contentpage properties (title, name) and additional 
 * basic properties
 * - Person
 * - Address
 * - Various contact entries.
 * 
 * @author quasi <quasi@barkhof.uni-bremen.de>
 */
public class GenericContactPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(
                                         GenericContactPropertiesStep.class);

    /** Name of the this edit sheet (Don't know if this this really needed.
     *  It has the same value in almost all PropertiesStep classes)          */
    public static final String EDIT_BASIC_SHEET_NAME = "editBasic";

    /**
     * Constructor for the PropertiesStep.
     *
     * @param itemModel
     * @param parent
     */
    public GenericContactPropertiesStep(ItemSelectionModel itemModel, 
                                        AuthoringKitWizard parent) {

        // Construct a new SimpleEditComponent with basic funtionality
        super(itemModel, parent);

        setDefaultEditKey(EDIT_BASIC_SHEET_NAME);
        /* Create a SegmentPanel containing all the various editing steps    */
        SegmentedPanel segmentedPanel = createEditSheet(itemModel, parent);

        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);
    }

    /**
     * Build the segmented panel. It contains 4 components:
     * - basic contentPage properties (title/name)
     * - attached person
     * - attached address
     * - contact entries for this contact
     * 
     * @param itemModel
     * @param parent
     * @return 
     */
    protected SegmentedPanel createEditSheet(ItemSelectionModel itemModel, 
                                             AuthoringKitWizard parent) {

        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        /* The different parts of information are displayed in seperated 
         * segments each containing a SimpleEditStep */
        /* Well, not so simple anymore... */

        /* A new SimpleEditStep for basic properties                         */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, 
                                                            parent, 
                                                            EDIT_BASIC_SHEET_NAME);

        /* Create the edit component for this SimpleEditStep and the 
         * corresponding link */
        BasicPageForm editBasicSheet = new GenericContactPropertyForm(itemModel, 
                                                                      this);
        basicProperties.add(
                EDIT_BASIC_SHEET_NAME, 
                ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.genericcontact.edit_basic_properties"), 
                new WorkflowLockedComponentAccess(
                        editBasicSheet, 
                        itemModel), 
                        editBasicSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(getContactPropertySheet(itemModel));

        /* Add the basic properties SimpleEditStep to the segmented panel with
         * provided title                                                     */
        segmentedPanel.addSegment(new 
                Label(ContenttypesGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.genericcontact.basic_properties")),
                basicProperties);


        // If not disabled via registry, add the ui for attaching a person
        if (!GenericContact.getConfig().getHidePerson()) {

            GenericContactPersonPropertiesStep personProperties = new 
                    GenericContactPersonPropertiesStep(itemModel, parent);
        // Add step to segmented panel with the provided title
            segmentedPanel.addSegment(new 
                    Label(ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.genericcontact.person")), 
                    personProperties);

        }


        // If not disabled via registry, add the ui for attaching an address
        if (!GenericContact.getConfig().getHideAddress()) {

            GenericContactAddressPropertiesStep addressProperties = new 
                    GenericContactAddressPropertiesStep(itemModel, parent);
        // Add step to segmented panel with the provided title
            segmentedPanel.addSegment(new 
                    Label(ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.genericcontact.address")), 
                    addressProperties);

        }

        // Add UI for adding several contact entries.
        GenericContactEntriesPropertiesStep contactEntries = new 
                GenericContactEntriesPropertiesStep(itemModel, parent);
        // Add step to segmented panel with the provided title
        segmentedPanel.addSegment(new 
                Label(ContenttypesGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.genericcontact.contactEntry")), 
                contactEntries);

        return segmentedPanel;
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of a contact. (@see GenericContactPropertyForm).
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the contact.
     */
    public static Component getContactPropertySheet(ItemSelectionModel itemModel) {


        /* The DisplayComponent for the Basic Properties */
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), 
                  "title");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), 
                  "name");

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize(
                                        "cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        return sheet;
    }
}
