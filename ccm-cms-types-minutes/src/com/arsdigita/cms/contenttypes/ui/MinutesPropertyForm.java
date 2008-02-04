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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Minutes;
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
    public MinutesPropertyForm( ItemSelectionModel itemModel, MinutesPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     **/
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.reference")));
        ParameterModel minuteNumberParam = new StringParameter(MINUTE_NUMBER);
        TextArea minuteNumber = new TextArea(minuteNumberParam);
        minuteNumber.setCols(40);
        minuteNumber.setRows(3);
        add(minuteNumber);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.description")));
        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        TextArea description = new TextArea(descriptionParam);
        description.setCols(40);
        description.setRows(5);
        add(description);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.action_item")));
        ParameterModel actionItemParam = new StringParameter(ACTION_ITEM);
        TextArea actionItem = new TextArea(actionItemParam);
        actionItem.setCols(40);
        actionItem.setRows(3);
        add(actionItem);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.attendees")));
        ParameterModel attendeesParam = new StringParameter(ATTENDEES);
        TextArea attendees = new TextArea(attendeesParam);
        attendees.setCols(40);
        attendees.setRows(3);
        add(attendees);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.description_of_minutes")));
        ParameterModel descriptionOfMinutesParam =
            new StringParameter(DESCRIPTION_OF_MINUTES);
        TextArea descriptionOfMinutes = new TextArea(descriptionOfMinutesParam);
        descriptionOfMinutes.setCols(40);
        descriptionOfMinutes.setRows(5);
        add(descriptionOfMinutes);

    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Minutes minutes = (Minutes) super.initBasicWidgets(fse);

        data.put(ACTION_ITEM,            minutes.getActionItem());
        data.put(ATTENDEES,              minutes.getAttendees());
        data.put(DESCRIPTION,            minutes.getDescription());
        data.put(DESCRIPTION_OF_MINUTES, minutes.getDescriptionOfMinutes());
        data.put(MINUTE_NUMBER,          minutes.getMinuteNumber());
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Minutes object. */
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
