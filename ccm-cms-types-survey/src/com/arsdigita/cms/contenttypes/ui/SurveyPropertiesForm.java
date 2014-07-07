/*
 * Copyright (C) 2012 Univerity of Bremen. All Rights Reserved.
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
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;


import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Survey;

import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * 
 * 
 */
public class SurveyPropertiesForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private SurveyPropertiesStep m_step;
    public static final String DESCRIPTION = Survey.DESCRIPTION;
    public static final String START_DATE = Survey.START_DATE;
    public static final String END_DATE = Survey.END_DATE;
    public static final String RESPONSES_PUBLIC = Survey.RESPONSES_PUBLIC;
    public static final String RESPONSES_ANONYM = Survey.RESPONSES_ANONYM;
    public static final String RESULTS_DURING_SURVEY = Survey.RESULTS_DURING_SURVEY;
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

        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        descriptionParam.addParameterListener(new StringInRangeValidationListener(0, 4000));
        TextArea description = new TextArea(descriptionParam);
        description.setLabel(SurveyGlobalizationUtil
                             .globalize("cms.contenttypes.ui.survey.description"));
        description.setRows(20);
        description.setCols(60);
        add(description);

        Date startDate = new Date(START_DATE);
        startDate.setLabel(SurveyGlobalizationUtil
                           .globalize("cms.contenttypes.ui.survey.start_date"));
        add(startDate);

        Date endDate = new Date(END_DATE);
        endDate.setLabel(SurveyGlobalizationUtil
                         .globalize("cms.contenttypes.ui.survey.end_date"));
        add(endDate);

        ParameterModel responsesPublicParam = new BooleanParameter(RESPONSES_PUBLIC);
        responsesPublicParam.addParameterListener(new NotNullValidationListener());
        RadioGroup responsesPublic = new RadioGroup(responsesPublicParam);
        Option rp1 = new Option("true", new Label(SurveyGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.survey.Yes")));
        Option rp2 = new Option("false", new Label(SurveyGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.survey.No")));
        responsesPublic.setLabel(SurveyGlobalizationUtil.globalize(
                "cms.contenttypes.ui.survey.should_quiz_responses_be_public"));
        responsesPublic.addOption(rp1);
        responsesPublic.addOption(rp2);
        add(responsesPublic);

        ParameterModel resultsDuringSurveyParam = new BooleanParameter(RESULTS_DURING_SURVEY);
        resultsDuringSurveyParam.addParameterListener(new NotNullValidationListener());
        RadioGroup resultsDuringSurvey = new RadioGroup(resultsDuringSurveyParam);
        Option rds1 = new Option("true", new Label(SurveyGlobalizationUtil.globalize(
                                 "cms.contenttypes.ui.survey.Yes")));
        Option rds2 = new Option("false", new Label(SurveyGlobalizationUtil.globalize(
                                 "cms.contenttypes.ui.survey.No")));
        resultsDuringSurvey.setLabel(SurveyGlobalizationUtil.globalize(
                     "cms.contenttypes.ui.survey.show_results_during_survey"));
        resultsDuringSurvey.addOption(rds1);
        resultsDuringSurvey.addOption(rds2);
        add(resultsDuringSurvey);

        ParameterModel responsesAnonymParam = new BooleanParameter(RESPONSES_ANONYM);
        responsesAnonymParam.addParameterListener(new NotNullValidationListener());
        RadioGroup responsesAnonym = new RadioGroup(responsesAnonymParam);
        Option ra1 = new Option("true", new Label(SurveyGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.survey.Yes")));
        Option ra2 = new Option("false", new Label(SurveyGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.survey.No")));
        responsesAnonym.addOption(ra1);
        responsesAnonym.addOption(ra2);
        responsesAnonym.setLabel(SurveyGlobalizationUtil.globalize(
                "cms.contenttypes.ui.survey.should_quiz_responses_be_anonym"));
        add(responsesAnonym);

    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        Survey survey = (Survey) super.initBasicWidgets(e);

        data.put(DESCRIPTION, survey.getDescription());
        data.put(START_DATE, survey.getStartDate());
        data.put(END_DATE, survey.getEndDate());
        if (survey.getResponsesPublic() != null) {
            data.put(RESPONSES_PUBLIC, survey.getResponsesPublic().booleanValue());
        }
        if (survey.getResultsDuringSurvey() != null) {
            data.put(RESULTS_DURING_SURVEY, survey.getResultsDuringSurvey().booleanValue());
        }
        if (survey.getResponsesAnonym() != null) {
            data.put(RESPONSES_ANONYM, survey.getResponsesAnonym().booleanValue());
        }
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        Survey survey = (Survey) super.processBasicWidgets(e);

        if ((survey != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            survey.setDescription((String) data.get(DESCRIPTION));
            survey.setStartDate((java.util.Date) data.get(START_DATE));
            survey.setEndDate((java.util.Date) data.get(END_DATE));
            survey.setResponsesPublic((Boolean) data.get(RESPONSES_PUBLIC));
            survey.setResultsDuringSurvey((Boolean) data.get(RESULTS_DURING_SURVEY));
            survey.setResponsesAnonym((Boolean) data.get(RESPONSES_ANONYM));

            survey.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if ((m_step != null) && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}
