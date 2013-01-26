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
package com.arsdigita.simplesurvey.ui.admin;



import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.parameters.NotWhiteSpaceValidationListener;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;

import com.arsdigita.formbuilder.PersistentForm;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.Poll;
// import com.arsdigita.simplesurvey.SimpleSurveyUtil;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * 
 * 
 */
public class PropertiesForm extends Form {
    
    private SurveySelectionModel m_survey;
    private Class m_type;


    private TextField m_surveyName;
    private TextArea m_description;
    private Date m_startDate;
    private Date m_endDate;
    private RadioGroup m_responsesPublic;
    private RadioGroup m_quizType;

    public PropertiesForm(SurveySelectionModel survey,
			  Class type) {

	super("properties" + type.getName());
	
	m_survey = survey;
	m_type = type;

        m_surveyName = new TextField("surveyName");
	m_surveyName.addValidationListener(new NotWhiteSpaceValidationListener());
        m_description = new TextArea("description");

        
        m_startDate = new Date("startDate");
        m_endDate = new Date("endDate");

        add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.name")));
        add(m_surveyName);
	
	add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.description")));
        m_description.setRows(20);
        m_description.setCols(60);
        add(m_description);

        add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.start_date")));
        add(m_startDate);

        add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.end_date")));
        add(m_endDate);


	
	add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.should_quiz_responses_be_public")));
	m_responsesPublic = new RadioGroup("responsesPublic");
	Option o1 = new Option("true", new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.Yes")));
        Option o2 = new Option("false", new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.No")));
	m_responsesPublic.addOption(o1);
	m_responsesPublic.addOption(o2);
       	add(m_responsesPublic);				      
	
	// There can be 2 kinds of quizzes: the knowledge test kind of quiz and the personality assessment kind
	add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.what_type_of_quiz_is_this")));
	m_quizType = new RadioGroup("quizType");
	Option o3 = new Option("knowledge_test", new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.knowledge_test_quiz")));
        Option o4 = new Option("personal_assessment", new Label(GlobalizationUtil.globalize("simplesurvey.ui.admin.personal_assessment_quiz")));
	m_quizType.addOption(o3);
	m_quizType.addOption(o4);
       	add(m_quizType);				      

	add(new Submit("submit"), BlockStylable.CENTER);
        addInitListener(new SurveyInitListener());
        addProcessListener(new PropertiesFormProcessListener());

    }
    


    /**
     * 
     */
    private class SurveyInitListener implements FormInitListener {

        /**
         * 
         * @param e
         * @throws FormProcessException 
         */
        public void init(FormSectionEvent e)  throws FormProcessException {

            PageState state = e.getPageState();

            if (m_survey.isSelected(state)) {

                Survey survey = m_survey.getSelectedSurvey(state);
                PersistentForm form = survey.getForm();
                m_surveyName.setValue(state, form.getAdminName());
                m_description.setValue(state, form.getDescription());
                m_startDate.setValue(state, survey.getStartDate());
                m_endDate.setValue(state, survey.getEndDate());
                m_quizType.setValue(state, survey.getQuizType());
                if ( survey.responsesArePublic() ) {
                    m_responsesPublic.setValue(state, "true");
                } else {
                    m_responsesPublic.setValue(state,"false");
                }

            } else {

                m_surveyName.setValue(state, "");
                m_description.setValue(state, "");
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.add(Calendar.DATE, 0);
                java.util.Date startDate = startCalendar.getTime();
                Calendar endCalendar = new GregorianCalendar();        
                endCalendar.add(Calendar.DATE, 15);
                java.util.Date endDate = endCalendar.getTime();

                m_startDate.setValue(state, startDate);
                m_endDate.setValue(state, endDate);
                m_responsesPublic.setValue(state, "true");
                m_quizType.setValue(state, "knowledge_test");
            }
        }
    }



    /**
     * 
     */
    private class PropertiesFormProcessListener implements FormProcessListener {

        /**
         * 
         * @param e
         * @throws FormProcessException 
         */
        public void process(FormSectionEvent e) throws FormProcessException {

            PageState state = e.getPageState();

            Survey survey;
            PersistentForm form;
	    
            if (m_survey.isSelected(state)) {
                survey = m_survey.getSelectedSurvey(state);
                form = survey.getForm();
            } else {
                survey = m_type.equals(Survey.class) ? new Survey() : new Poll();

                // PackageInstance is old style application, no longer used. 
                // survey.setPackageInstance(SimpleSurveyUtil.getPackageInstance(state));

                form = new PersistentForm();
                survey.setForm(form);
            }
	    
            form.setAdminName((String)m_surveyName.getValue(state));
            form.setHTMLName(getHTMLName((String)m_surveyName.getValue(state)));
            form.setDescription((String)m_description.getValue(state));
            form.save();

            survey.setStartDate((java.util.Date)m_startDate.getValue(state));
            survey.setEndDate((java.util.Date)m_endDate.getValue(state));
            survey.setResponsesPublic(new Boolean((String) m_responsesPublic.getValue(state)));
            survey.setQuizType((String) m_quizType.getValue(state));
            survey.save();
        }

        /**
         * 
         * @param surveyName
         * @return 
         */
        private String getHTMLName(String surveyName) {
            String htmlName = surveyName.trim().toLowerCase();
            htmlName = htmlName.replace(' ', '_');

            return htmlName;
        }

    }
}
