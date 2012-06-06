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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Time;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TimeParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Event;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.util.EventGlobalizationUtil;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Form to edit the basic properties of an <code>Event</code> object. Used by
 * <code>EventPropertiesStep</code> authoring kit step.
 * <br />
 * This form can be extended to create forms for Event subclasses.
 **/
public class EventPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    /** Name of this form */
    public static final String ID = "event_edit";
    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(EventPropertyForm.class);
    private EventPropertiesStep m_step;
    /** event date parameter name */
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String EVENT_DATE = "eventDate";
    /**  location parameter name */
    public static final String LOCATION = "location";
    /**  lead parameter name */
    public static final String LEAD = "lead";
    /** Main contributor parameter name */
    public static final String MAIN_CONTRIBUTOR = "main_contributor";
    /** Event type parameter name */
    public static final String EVENT_TYPE = "event_type";
    /** Map link parameter name */
    public static final String MAP_LINK = "map_link";
    /** cost parameter name */
    public static final String COST = "cost";

    /* DateWidgets have to be accessible later on */
    private com.arsdigita.bebop.form.Date m_startDate;
    private com.arsdigita.bebop.form.Date m_endDate;

    /**
     * Creates a new form to edit the Event object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Event to
     * work on
     **/
    public EventPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the Event object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Event to
     * work on
     * @param step The EventPropertiesStep which controls this form.
     **/
    public EventPropertyForm(ItemSelectionModel itemModel, EventPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    @Override
    protected void addWidgets() {
        super.addWidgets();

        /* Summary (lead) */
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.lead").localize()));
        ParameterModel leadParam = new StringParameter(LEAD);
        if(Event.getConfig().isLeadTextOptional()) {
            leadParam.addParameterListener(new NotNullValidationListener());
        }
        TextArea lead = new TextArea(leadParam);
        lead.setCols(50);
        lead.setRows(5);
        add(lead);
        /* Start date and time */
        ParameterModel eventStartDateParam = new DateParameter(START_DATE);
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.start_date").localize()));
        eventStartDateParam.addParameterListener(new NotNullValidationListener());
        // Use bebop date instead of java.util.date
        m_startDate = new com.arsdigita.bebop.form.Date(eventStartDateParam);
        // Set the upper und lower boundary of the year select box
        m_startDate.setYearRange(Event.getConfig().getStartYear(),
                GregorianCalendar.getInstance().get(Calendar.YEAR) + Event.getConfig().getEndYearDelta());
        add(m_startDate);

        ParameterModel eventStartTimeParam = new TimeParameter(START_TIME);
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.start_time").localize()));
        if(Event.getConfig().isStartTimeOptional()) {
            eventStartTimeParam.addParameterListener(new NotNullValidationListener());
        }
        Time startTime = new Time(eventStartTimeParam);
        add(startTime);

        /* End date and time */
        ParameterModel eventEndDateParam = new DateParameter(END_DATE);
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.end_date").localize()));
        // Use bebop date instead of java.util.date
        m_endDate = new com.arsdigita.bebop.form.Date(eventEndDateParam);
        m_endDate.setYearRange(Event.getConfig().getStartYear(),
                GregorianCalendar.getInstance().get(Calendar.YEAR) + Event.getConfig().getEndYearDelta());
        add(m_endDate);

        ParameterModel eventEndTimeParam = new TimeParameter(END_TIME);
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.end_time").localize()));
        Time endTime = new Time(eventEndTimeParam);
        add(endTime);


        /* optional additional / literal date description */
        if (!Event.getConfig().getHideDateDescription()) {
            add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.date_description").localize()));
            ParameterModel eventDateParam = new StringParameter(EVENT_DATE);
            //eventDateParam
            //    .addParameterListener(new NotNullValidationListener());
            if (Event.getConfig().getUseHtmlDateDescription()) {
                CMSDHTMLEditor eventDate = new CMSDHTMLEditor(eventDateParam);
                eventDate.setCols(40);
                eventDate.setRows(8);
                add(eventDate);
            } else {
                eventDateParam.addParameterListener(new StringInRangeValidationListener(0, 100));
                TextArea eventDate = new TextArea(eventDateParam);
                eventDate.setCols(50);
                eventDate.setRows(2);
                add(eventDate);
            }
        }


        /* extensive description of location */
        add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.location").localize()));
        ParameterModel locationParam = new StringParameter(LOCATION);
        //locationParam
        //    .addParameterListener(new NotNullValidationListener());
        CMSDHTMLEditor location = new CMSDHTMLEditor(locationParam);
        location.setCols(40);
        location.setRows(8);
        add(location);


        /* optional: main contributor */
        if (!Event.getConfig().getHideMainContributor()) {
            add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.main_contributor").localize()));
            ParameterModel mainContributorParam =
                    new StringParameter(MAIN_CONTRIBUTOR);
            //mainContributorParam
            //    .addParameterListener(new NotNullValidationListener());
            CMSDHTMLEditor mainContributor = new CMSDHTMLEditor(mainContributorParam);
            mainContributor.setCols(40);
            mainContributor.setRows(10);
            add(mainContributor);
        }


        /* optional: event type */
        if (!Event.getConfig().getHideEventType()) {
            add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.event_type").localize()));
            ParameterModel eventTypeParam = new StringParameter(EVENT_TYPE);
            //eventTypeParam
            //    .addParameterListener(new NotNullValidationListener());
            TextField eventType = new TextField(eventTypeParam);
            eventType.setSize(30);
            eventType.setMaxLength(30);
            add(eventType);
        }


        /* optional: link to map */
        if (!Event.getConfig().getHideLinkToMap()) {
            add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.link_to_map").localize()));
            ParameterModel mapLinkParam = new StringParameter(MAP_LINK);
            //mapLinkParam
            //    .addParameterListener(new NotNullValidationListener());
            TextArea mapLink = new TextArea(mapLinkParam);
            mapLink.setCols(40);
            mapLink.setRows(2);
            add(mapLink);
        }


        /* optional: costs */
        if (!Event.getConfig().getHideCost()) {
            add(new Label((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.cost").localize()));
            ParameterModel costParam = new TrimmedStringParameter(COST);
            TextField cost = new TextField(costParam);
            cost.setSize(30);
            cost.setMaxLength(30);
            add(cost);
        }

    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        super.validate(e);
        
        FormData d = e.getFormData();
        java.util.Date startDate = d.getDate(START_DATE);
        java.util.Date endDate = d.getDate(END_DATE);

        if (endDate != null) {

            if (startDate == null || startDate.compareTo(endDate) > 0) {
                throw new FormProcessException((String) EventGlobalizationUtil.globalize("cms.contenttypes.ui.event.end_date_after_start_date").localize());
            }
        }
    }

    /** Form initialisation hook. Fills widgets with data.
     * @param fse 
     */
    @Override
    public void init(FormSectionEvent fse) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        Event event = (Event) super.initBasicWidgets(fse);

        // Start date should always be set
        java.util.Date startDate = event.getStartDate();
        if (startDate == null) {
            // new Date is initialised to current time
            startDate = new java.util.Date();
        }
        m_startDate.addYear(startDate);

        // End date can be null
        java.util.Date endDate = event.getEndDate();
        if (endDate != null) {
            m_endDate.addYear(endDate);
        }

        data.put(LEAD, event.getLead());
        data.put(START_DATE, startDate);
        data.put(START_TIME, event.getStartTime());
        data.put(END_DATE, event.getEndDate());
        data.put(END_TIME, event.getEndTime());
        if (!Event.getConfig().getHideDateDescription()) {
            data.put(EVENT_DATE, event.getEventDate());
        }
        data.put(LOCATION, event.getLocation());
        if (!Event.getConfig().getHideMainContributor()) {
            data.put(MAIN_CONTRIBUTOR, event.getMainContributor());
        }
        if (!Event.getConfig().getHideEventType()) {
            data.put(EVENT_TYPE, event.getEventType());
        }
        if (!Event.getConfig().getHideLinkToMap()) {
            data.put(MAP_LINK, event.getMapLink());
        }
        if (!Event.getConfig().getHideCost()) {
            data.put(COST, event.getCost());
        }
    }

    /** Cancels streamlined editing. */
    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Event object.
     * @param fse 
     */
    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Event event = (Event) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (event != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            event.setStartDate((java.util.Date) data.get(START_DATE));
            event.setStartTime((java.util.Date) data.get(START_TIME));
            event.setEndDate((java.util.Date) data.get(END_DATE));
            event.setEndTime((java.util.Date) data.get(END_TIME));
            //date_description
            if (!Event.getConfig().getHideDateDescription()) {
                event.setEventDate((String) data.get(EVENT_DATE));
            }

            if (!Event.getConfig().getHideMainContributor()) {
                event.setMainContributor((String) data.get(MAIN_CONTRIBUTOR));
            }
            if (!Event.getConfig().getHideEventType()) {
                event.setEventType((String) data.get(EVENT_TYPE));
            }
            if (!Event.getConfig().getHideLinkToMap()) {
                event.setMapLink((String) data.get(MAP_LINK));
            }
            event.setLocation((String) data.get(LOCATION));
            event.setLead((String) data.get(LEAD));
            if (!Event.getConfig().getHideCost()) {
                event.setCost((String) data.get(COST));
            }
            event.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
