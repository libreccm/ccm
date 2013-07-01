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
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.contenttypes.Agenda;
import com.arsdigita.cms.contenttypes.util.AgendaGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

import com.arsdigita.globalization.GlobalizationHelper;

import java.text.DateFormat;
import java.util.Date;

/**
 * Authoring step to edit the simple attributes of the Agenda content type (and
 * its subclasses). The attributes edited are 'name', 'title', ' date' and
 * 'reference code'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class AgendaPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet for this step */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor. 
     * 
     * @param itemModel
     * @param parent 
     */
    public AgendaPropertiesStep(ItemSelectionModel itemModel,
                                AuthoringKitWizard parent ) {
        super(itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new AgendaPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent(getAgendaPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the Agenda specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the agenda content type.
     **/
    public static Component getAgendaPropertySheet(ItemSelectionModel
                                                   itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"), Agenda.TITLE);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.name"),  Agenda.NAME );

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.summary"),
                  Agenda.SUMMARY);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.agenda_date"),
                  Agenda.AGENDA_DATE,
                  new DateTimeAttributeFormatter());
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.location"),
                  Agenda.LOCATION);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.attendees"),
                  Agenda.ATTENDEES);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.subject_items"),
                  Agenda.SUBJECT_ITEMS);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.contact_info"),
                  Agenda.CONTACT_INFO);
        sheet.add(AgendaGlobalizationUtil
                  .globalize("cms.contenttypes.ui.agenda.creation_date"),
                  Agenda.CREATION_DATE,
                  new DateAttributeFormatter() );

        return sheet;
    }

	/**
     * Private class which implements an AttributeFormatter interface for 
     * Agenda's date values.
     * Its format(...) class returns a string representation for either a
     * false or a true value.
     */
    private static class DateAttributeFormatter 
                         implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public DateAttributeFormatter() {
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
 
            if ( obj != null && obj instanceof Agenda) {
                
                Agenda agenda = (Agenda) obj;
                Object field = agenda.get(attribute);

                if( field != null ) {
                    // Note: No type safety here! We relay that it is
                    // attached to a date property!
                    return DateFormat.getDateInstance(
                                         DateFormat.LONG, 
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

	/**
     * Private class which implements an AttributeFormatter interface for 
     * boolean values.
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
 
            if ( obj != null && obj instanceof Agenda) {
                
                Agenda agenda = (Agenda) obj;
                Object field = agenda.get(attribute);

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
