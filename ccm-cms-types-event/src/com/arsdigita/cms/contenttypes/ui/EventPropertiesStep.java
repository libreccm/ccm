/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Event;
import com.arsdigita.cms.contenttypes.util.EventGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import java.text.DateFormat;

/**
 * Authoring step to view/edit the simple attributes of the Event content type (and
 * its subclasses). 
 * 
 * The attributes edited are 'name', 'title', 'lead', 
 * 'start date', 'starttime', end date', 'end time','event date' (literal descr. of date),
 * 'location', 'main contributor', 'event type', 'map link', and
 * 'cost'. 
 * 
 * This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class EventPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     * 
     * @param itemModel
     * @param parent 
     */
    public EventPropertiesStep(ItemSelectionModel itemModel,
                                AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new EventPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getEventPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the Event specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     **/
    public static Component getEventPropertySheet(ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        
        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.title"), Event.TITLE);
        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"), Event.NAME);
        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.event.lead"), Event.LEAD);
      if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.event.start_time"), Event.START_DATE,
                   new DateTimeAttributeFormatter() );

        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.event.end_time"), Event.END_DATE,
                   new DateTimeAttributeFormatter() );
        if (!Event.getConfig().getHideDateDescription()) {
            sheet.add( EventGlobalizationUtil
                       .globalize("cms.contenttypes.ui.event.date_description"), 
                       Event.EVENT_DATE);
        }
        sheet.add( EventGlobalizationUtil
                   .globalize("cms.contenttypes.ui.event.location"), 
                   Event.LOCATION);

        if (!Event.getConfig().getHideMainContributor()) {
            sheet.add( EventGlobalizationUtil.globalize(
                       "cms.contenttypes.ui.event.main_contributor"), 
                       Event.MAIN_CONTRIBUTOR);
        }
        if (!Event.getConfig().getHideEventType()) {
            sheet.add( EventGlobalizationUtil.globalize(
                       "cms.contenttypes.ui.event.event_type"), 
                       Event.EVENT_TYPE);
        }
        if (!Event.getConfig().getHideLinkToMap()) {
            sheet.add( EventGlobalizationUtil.globalize(
                       "cms.contenttypes.ui.event.link_to_map"), 
                       Event.MAP_LINK );
        }
        if (!Event.getConfig().getHideCost()) {
            sheet.add( EventGlobalizationUtil.globalize(
                       "cms.contenttypes.ui.event.cost"), 
                       Event.COST );
        }
        return sheet;
    }

    /**
     * Private class which implements an AttributeFormatter interface for 
     * date values.
     * Its format(...) class returns a string representation for either a
     * false or a true value.
     */
    private static class DateTimeAttributeFormatter 
                         implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public DateTimeAttributeFormatter() {
        }

        /**
         * Formatter for the value of an attribute.
         * 
         * It currently relays on the prerequisite that the passed in property
         * attribute is in fact a date property. No type checking yet!
         * 
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization here!
         * 
         * @param obj        Object containing the attribute to format.
         * @param attribute  Name of the attribute to retrieve and format
         * @param state      PageState of the request
         * @return           A String representation of the retrieved boolean
         *                   attribute of the domain object.
         */
        public String format(DomainObject obj, String attribute, PageState state) {
 
            if ( obj != null && obj instanceof Event) {
                
                Event event = (Event) obj;
                Object field = event.get(attribute);

                if( field != null ) {
                    // Note: No type safety here! We relay that it is
                    // attached to a date property!
                    return DateFormat.getDateTimeInstance(
                                         DateFormat.LONG, 
                                         DateFormat.SHORT, 
                                         GlobalizationHelper.getNegotiatedLocale()
                                          )
                                     .format(field);
                } else {
                    return (String)GlobalizationUtil
                                   .globalize("cms.ui.unknown")
                                   .localize();
                }
            }

            return (String) GlobalizationUtil
                            .globalize("cms.ui.unknown")
                            .localize();
        }
    }
}
