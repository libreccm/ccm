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

import com.arsdigita.cms.contenttypes.util.SurveyGlobalizationUtil;

import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;


import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Survey;

import com.arsdigita.cms.ui.authoring.BasicPageForm;

public class SurveyPropertiesForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private SurveyPropertiesStep m_step;
    public static final String DESCRIPTION = Survey.DESCRIPTION;
//    public static final String START_DATE = Survey.START_DATE;
//    public static final String END_DATE = Survey.END_DATE;
    public static final String RESPONSES_PUBLIC = Survey.RESPONSES_PUBLIC;
    /**
     * ID of the form
     */
    public static final String ID = "Survey_edit";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public SurveyPropertiesForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     *
     * @param itemModel
     * @param step
     */
    public SurveyPropertiesForm(ItemSelectionModel itemModel, SurveyPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label((String) SurveyGlobalizationUtil.globalize("cms.contenttypes.survey.ui.admin.description").localize()));
        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        descriptionParam.addParameterListener(new StringInRangeValidationListener(0, 4000));
        TextArea description = new TextArea(descriptionParam);
        description.setRows(20);
        description.setCols(60);
        add(description);

//        add(new Label(SurveyGlobalizationUtil.globalize("simplesurvey.ui.admin.start_date")));
//        add(m_startDate);
//
//        add(new Label(SurveyGlobalizationUtil.globalize("simplesurvey.ui.admin.end_date")));
//        add(m_endDate);

        add(new Label((String) SurveyGlobalizationUtil.globalize("cms.contenttypes.survey.ui.admin.should_quiz_responses_be_public").localize()));
        RadioGroup responsesPublic = new RadioGroup("responsesPublic");
        Option rp1 = new Option("true", new Label((String) SurveyGlobalizationUtil.globalize("cms.contenttypes.survey.ui.Yes").localize()));
        Option rp2 = new Option("false", new Label((String) SurveyGlobalizationUtil.globalize("cms.contenttypes.survey.ui.No").localize()));
        responsesPublic.addOption(rp1);
        responsesPublic.addOption(rp2);
        add(responsesPublic);

    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        Survey survey = (Survey) super.initBasicWidgets(e);

        data.put(DESCRIPTION, survey.getDescription());
//        data.put(START_DATE, survey.getStartDate());
//        data.put(END_DATE, survey.getEndDate());
        data.put(RESPONSES_PUBLIC, survey.getResponsesPublic());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        Survey survey = (Survey) super.processBasicWidgets(e);

        if ((survey != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            survey.setDescription((String) data.get(DESCRIPTION));
//            survey.setStartDate((String)data.get(START_DATE));
//            survey.setEndDate((String)data.get(END_DATE));
            survey.setResponsesPublic((Boolean) data.get(RESPONSES_PUBLIC));

            survey.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if ((m_step != null) && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}
