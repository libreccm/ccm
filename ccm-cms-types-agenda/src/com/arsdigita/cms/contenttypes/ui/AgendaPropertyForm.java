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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.DateTime;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Agenda;
import com.arsdigita.cms.contenttypes.util.AgendaGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
// Replaced by AgendaGlobalizationUtil for easier localisation per module
// import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of a Agenda . These are name, title, date
 * and reference code. This form can be extended to create forms for Agenda
 * subclasses.
 **/
public class AgendaPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private AgendaPropertiesStep m_step;

    /**  summary parameter name */
    public static final String SUMMARY = Agenda.SUMMARY;
    /** Agenda date parameter name */
    public static final String AGENDA_DATE = Agenda.AGENDA_DATE;
    /**  location parameter name */
    public static final String LOCATION = Agenda.LOCATION;
    /**  attendees parameter name */
    public static final String ATTENDEES = Agenda.ATTENDEES;
    /** subject items parameter name */
    public static final String SUBJECT_ITEMS = Agenda.SUBJECT_ITEMS;
    /** contact info parameter name */
    public static final String CONTACT_INFO = Agenda.CONTACT_INFO;
    /** Creation date parameter name */
    public static final String CREATION_DATE = Agenda.CREATION_DATE;

    /** Name of this form */
    public static final String ID = "Agenda_edit";

    /**
     * Creates a new form to edit the Agenda object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Agenda to
     * work on
     **/
    public AgendaPropertyForm( ItemSelectionModel itemModel ) {
        this(itemModel,null);
    }
    /**
     * Creates a new form to edit the Agenda object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the Agenda to
     * work on
     * @param step The AgendaPropertiesStep which controls this form.
     **/
    public AgendaPropertyForm( ItemSelectionModel itemModel, AgendaPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.summary")));
        ParameterModel summaryParam = new StringParameter(SUMMARY);
        //summaryParam
        //    .addParameterListener(new NotNullValidationListener());
        summaryParam.addParameterListener(new StringInRangeValidationListener(0, 4000));
        TextArea summary = new TextArea(summaryParam);
        summary.setCols(40);
        summary.setRows(5);
        add(summary);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.agenda_date")));
        ParameterModel agendaDateParam = new DateTimeParameter(AGENDA_DATE);
        agendaDateParam
            .addParameterListener(new NotNullValidationListener());
        DateTime agendaDate = new DateTime(agendaDateParam);
        add(agendaDate);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.location")));
        ParameterModel locationParam = new StringParameter(LOCATION);
        //locationParam
        //    .addParameterListener(new NotNullValidationListener());
        locationParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea location = new TextArea(locationParam);
        location.setCols(40);
        location.setRows(3);
        add(location);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.attendees")));
        ParameterModel attendeesParam = new StringParameter(ATTENDEES);
        //attendeesParam
        //    .addParameterListener(new NotNullValidationListener());
        attendeesParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea attendees = new TextArea(attendeesParam);
        attendees.setCols(40);
        attendees.setRows(3);
        add(attendees);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.subject_items")));
        ParameterModel subjectItemsParam = new StringParameter(SUBJECT_ITEMS);
        //subjectItemsParam
        //    .addParameterListener(new NotNullValidationListener());
        subjectItemsParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea subjectItems = new TextArea(subjectItemsParam);
        subjectItems.setCols(40);
        subjectItems.setRows(3);
        add(subjectItems);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.contact_info")));
        ParameterModel contactInfoParam = new StringParameter(CONTACT_INFO);
        //contactInfoParam
        //    .addParameterListener(new NotNullValidationListener());
        contactInfoParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextArea contactInfo = new TextArea(contactInfoParam);
        contactInfo.setCols(40);
        contactInfo.setRows(3);
        add(contactInfo);

        add(new Label(AgendaGlobalizationUtil.globalize("cms.contenttypes.ui.agenda.creation_date")));
        ParameterModel creationDateParam = new DateParameter(CREATION_DATE);
        creationDateParam
            .addParameterListener(new NotNullValidationListener());
        com.arsdigita.bebop.form.Date creationDate
            = new com.arsdigita.bebop.form.Date(creationDateParam);
        add(creationDate);


    }

    /**
     * Form initialisation hook. Fills widgets with data.
     **/
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Agenda agenda = (Agenda) super.initBasicWidgets(fse);

        // set a default creation date, if none set
        java.util.Date creationDate = agenda.getCreationDate();
        if (creationDate == null) {
            // new Date is initialised to current time
            creationDate = new java.util.Date();
        }

        data.put(CREATION_DATE, creationDate);
        data.put(AGENDA_DATE,   agenda.getAgendaDate());
        data.put(SUBJECT_ITEMS, agenda.getSubjectItems());
        data.put(CONTACT_INFO,  agenda.getContactInfo());
        data.put(LOCATION,      agenda.getLocation());
        data.put(ATTENDEES,     agenda.getAttendees());
        data.put(SUMMARY,       agenda.getSummary());
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
 
    /**
     * Form processing hook. Saves Agenda object.
     **/
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Agenda agenda
            = (Agenda) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (agenda != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(fse.getPageState())) {

            agenda.setAgendaDate((java.util.Date)
                                 data.get(AGENDA_DATE));
            agenda.setSubjectItems((String)
                                   data.get(SUBJECT_ITEMS));
            agenda.setContactInfo((String)
                                  data.get(CONTACT_INFO));
            agenda.setLocation((String)
                               data.get(LOCATION));
            agenda.setAttendees((String)
                                data.get(ATTENDEES));
            agenda.setSummary((String)
                              data.get(SUMMARY));
            agenda.setCreationDate((java.util.Date)
                                   data.get(CREATION_DATE));
            agenda.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
