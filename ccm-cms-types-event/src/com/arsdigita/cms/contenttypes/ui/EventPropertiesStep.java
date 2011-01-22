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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.EventGlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Authoring step to view/edit the simple attributes of the Event content type (and
 * its subclasses). The attributes edited are 'name', 'title', 'lead', 
 * 'start date', 'starttime', end date', 'end time','event date' (literal descr. of date),
 * 'location', 'main contributor', 'event type', 'map link', and
 * 'cost'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 **/
public class EventPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public EventPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new EventPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
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

        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.name").localize(), Event.NAME);
        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.title").localize(), Event.TITLE);
        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.lead").localize(), Event.LEAD);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(EventGlobalizationUtil.globalize("cms.contenttypes.ui.launch_date"),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
                            if (page.getLaunchDate() != null) {
                                return DateFormat.getDateInstance(DateFormat.LONG, DispatcherHelper.getNegotiatedLocale()).format(page.getLaunchDate());
                            } else {
                                return (String) EventGlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }
        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.start_time").localize(), Event.START_DATE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    public String format(DomainObject item,
                            String attribute,
                            PageState state) {
                        Event e = (Event) item;

                        if (e.getStartDate() != null) {

                            if (e.getStartTime() == null) {
                                return DateFormat.getDateInstance(DateFormat.LONG, DispatcherHelper.getNegotiatedLocale()).format(e.getStartDate());
                            } else {
                                Date startDateTime = new Date(e.getStartDate().getTime() + e.getStartTime().getTime());
                                return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, DispatcherHelper.getNegotiatedLocale()).format(startDateTime);

                            }

                        } else {
                            return  (String) EventGlobalizationUtil.globalize("cms.ui.unknown").localize();
                        }
                    }
                });

        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.end_time").localize(), Event.END_DATE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    public String format(DomainObject item,
                            String attribute,
                            PageState state) {
                        Event e = (Event) item;
                        if (e.getEndDate() != null) {

                            if (e.getEndTime() == null) {
                                return DateFormat.getDateInstance(DateFormat.LONG, DispatcherHelper.getNegotiatedLocale()).format(e.getEndDate());
                            } else {
                                Date endDateTime = new Date(e.getEndDate().getTime() + e.getEndTime().getTime());
                                return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, DispatcherHelper.getNegotiatedLocale()).format(endDateTime);

                            }

                        } else {
                            return  (String) EventGlobalizationUtil.globalize("cms.ui.unknown").localize();
                        }
                    }
                });
        if (!Event.getConfig().getHideDateDescription()) {
            sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.date_description").localize(), Event.EVENT_DATE);
        }
        sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.location").localize(), Event.LOCATION);

        if (!Event.getConfig().getHideMainContributor()) {
            sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.main_contributor").localize(), Event.MAIN_CONTRIBUTOR);
        }
        if (!Event.getConfig().getHideEventType()) {
            sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.event_type").localize(), Event.EVENT_TYPE);
        }
        if (!Event.getConfig().getHideLinkToMap()) {
            sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.link_to_map").localize(), Event.MAP_LINK);
        }
        if (!Event.getConfig().getHideCost()) {
            sheet.add((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.cost").localize(), Event.COST);
        }
        return sheet;
    }
}
