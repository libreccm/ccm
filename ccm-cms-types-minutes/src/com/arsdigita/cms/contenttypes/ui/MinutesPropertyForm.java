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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Minutes;
import com.arsdigita.cms.contenttypes.util.MinutesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Form to edit the basic properties of a minutes. This form can be extended to
 * create forms for Minutes subclasses.
 **/
public class MinutesPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private MinutesPropertiesStep m_step;

    /** Minute number parameter name */
    public static final String MINUTE_NUMBER = "minute_number";
    /**  description parameter name */
    public static final String DESCRIPTION_OF_MINUTES = "descriptionOfMinutes";
    /** action item parameter name */
    public static final String ACTION_ITEM = "actionItem";
    /**  attendees parameter name */
    public static final String ATTENDEES = "attendees";
    /**  description parameter name */
    public static final String DESCRIPTION = "description";

    /** Name of this form */
    public static final String ID = "minutes_edit";

    /**
     * Creates a new form to edit the Minutes object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Minutes to work on
     */
    public MinutesPropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the Minutes object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Minutes to work on
     * @param step The MinutesPropertiesStep which controls this form.
     */
    public MinutesPropertyForm( ItemSelectionModel itemModel, 
                                MinutesPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    @Override
    protected void addWidgets() {
        super.addWidgets();

        ParameterModel minuteNumberParam = new StringParameter(MINUTE_NUMBER);
        TextArea minuteNumber = new TextArea(minuteNumberParam);
        minuteNumber.setLabel(MinutesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.minutes.reference"));
        minuteNumber.setCols(40);
        minuteNumber.setRows(3);
        minuteNumber.addValidationListener(new StringLengthValidationListener(100));
        add(minuteNumber);

        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        TextArea description = new TextArea(descriptionParam);
        description.setLabel(GlobalizationUtil
                             .globalize("cms.contenttypes.ui.description"));
        description.setCols(40);
        description.setRows(5);
        description.addValidationListener(new StringLengthValidationListener(4000));
        add(description);

        ParameterModel actionItemParam = new StringParameter(ACTION_ITEM);
        TextArea actionItem = new TextArea(actionItemParam);
        actionItem.setLabel(MinutesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.minutes.action_item"));
        actionItem.setCols(40);
        actionItem.setRows(3);
        actionItem.addValidationListener(new StringLengthValidationListener(4000));
        add(actionItem);

        ParameterModel attendeesParam = new StringParameter(ATTENDEES);
        TextArea attendees = new TextArea(attendeesParam);
        attendees.setLabel(MinutesGlobalizationUtil
                           .globalize("cms.contenttypes.ui.minutes.attendees"));
        attendees.setCols(40);
        attendees.setRows(3);
        attendees.addValidationListener(new StringLengthValidationListener(1000));
        add(attendees);

        ParameterModel descriptionOfMinutesParam =
            new StringParameter(DESCRIPTION_OF_MINUTES);
        TextArea descriptionOfMinutes = new TextArea(descriptionOfMinutesParam);
        descriptionOfMinutes.setLabel(MinutesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.minutes.description_of"));
        descriptionOfMinutes.setCols(40);
        descriptionOfMinutes.setRows(5);
        descriptionOfMinutes.addValidationListener(new StringLengthValidationListener(4000));
        add(descriptionOfMinutes);

    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Minutes minutes = (Minutes) super.initBasicWidgets(fse);

        data.put(ACTION_ITEM,            minutes.getActionItem());
        data.put(ATTENDEES,              minutes.getAttendees());
        data.put(DESCRIPTION,            minutes.getDescription());
        data.put(DESCRIPTION_OF_MINUTES, minutes.getDescriptionOfMinutes());
        data.put(MINUTE_NUMBER,          minutes.getMinuteNumber());
    }

    /** 
     * Cancels streamlined editing. 
     * @param fse
     */
    @Override
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** 
     * Form processing hook. Saves Minutes object. 
     * @param fse
     */
    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        Minutes minutes = (Minutes) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (minutes != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(fse.getPageState())) {

            minutes.setMinuteNumber((String) data.get(MINUTE_NUMBER));
            minutes.setActionItem((String)   data.get(ACTION_ITEM));
            minutes.setAttendees((String)    data.get(ATTENDEES));
            minutes.setDescription((String)  data.get(DESCRIPTION));
            minutes.setDescriptionOfMinutes((String)
                                            data.get(DESCRIPTION_OF_MINUTES));
            minutes.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
