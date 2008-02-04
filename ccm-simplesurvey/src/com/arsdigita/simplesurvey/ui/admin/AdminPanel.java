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

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.formbuilder.PersistentDate;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.ui.ControlEditor;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.SurveyForm;
import com.arsdigita.simplesurvey.ui.SurveySelectionModel;
import com.arsdigita.simplesurvey.ui.SurveyProcessListener;
import com.arsdigita.simplesurvey.ui.Question;
//import com.arsdigita.simplesurvey.ui.admin.AnswerValuesForm;
import java.util.Iterator;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.arsdigita.persistence.OID;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class AdminPanel extends SimpleContainer {
    
    public final static String MODE_VIEW = "view";
    public final static String MODE_RESPONSES = "responses";
    public final static String MODE_PROPERTIES = "properties";
    public final static String MODE_WIDGETS = "widgets";
    public final static String MODE_CORRECT_ANSWERS = "correctAnswers";
    public final static String MODE_ANSWER_VALUES = "answerValues";

    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(AdminPanel.class.getName());

    private SurveySelectionModel m_survey;

    private SurveyForm m_view;
    private ControlEditor m_controls;
    private PropertiesForm m_props;
    private ReportPanel m_reports;
    private SurveyForm m_correctAnswerView;
    private AnswerValuesPanel m_answerValuesPanel;
    private FormSelectionModel m_form;
    
    public AdminPanel(SurveySelectionModel survey,
		      Class type) {
	m_survey = survey;

	m_form = new FormSelectionModel(m_survey);

	m_view = new SurveyForm(m_survey, null);
        
	// The admin is just viewing the form, so hitting submit shouldn't actually
	// do anything
	m_view.addProcessListener(new FormProcessListener() {
		public void process(FormSectionEvent e) 
		    throws FormProcessException {

		    fireCompletionEvent(e.getPageState());
		}
	});
	
	//Correct Answers lets admin fill in the correct answers to questions
	m_correctAnswerView = new SurveyForm(m_survey, null);

	// Add an init listener to fill in the existing correct answers
	m_correctAnswerView.addInitListener(new CorrectAnswersInitListener());
	
	// Add a process listener that saves the answers to the form 
	// as the correct answers
	m_correctAnswerView.addProcessListener(new CorrectAnswersProcessListener());
       	
	//AnswerValues lets admin assign point values per multiple choice answer
	m_answerValuesPanel = new AnswerValuesPanel(m_survey);

	m_controls = new ControlEditor(type.equals(Survey.class) ? "Survey" : "Poll",
				      m_form);

	Form form = new Form("controlComplete");
	form.add(new Submit("Done editing"));
	form.addProcessListener(new FormProcessListener() {
		public void process(FormSectionEvent e) 
		    throws FormProcessException {

		    fireCompletionEvent(e.getPageState());
		}
	    });
 	m_controls.add(form);

	m_reports = new ReportPanel(m_survey);
	m_reports.addCompletionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    fireCompletionEvent(e.getPageState());
		}
	    });

	m_props = new PropertiesForm(m_survey,
				     type);

	m_props.addProcessListener(new FormProcessListener() {
		public void process(FormSectionEvent e) 
		    throws FormProcessException {

		    fireCompletionEvent(e.getPageState());
		}
	    });

	add(m_view);
	add(m_correctAnswerView);
	add(m_answerValuesPanel);
	add(m_controls);
	add(m_props);
	add(m_reports);
    }

    public void setDisplayMode(PageState state,
			       String mode) {
	if (MODE_VIEW.equals(mode)) {
	    m_view.setVisible(state, true);
	    m_correctAnswerView.setVisible(state,false);
	    m_controls.setVisible(state, false);
	    m_props.setVisible(state, false);
	    m_reports.setVisible(state, false);
	    m_answerValuesPanel.setVisible(state,false);
	} else if (MODE_RESPONSES.equals(mode)) {
	    m_view.setVisible(state, false);
	    m_correctAnswerView.setVisible(state,false);
	    m_controls.setVisible(state, false);
	    m_props.setVisible(state, false);
	    m_reports.setVisible(state, true);
	    m_answerValuesPanel.setVisible(state,false);
	} else if (MODE_CORRECT_ANSWERS.equals(mode)) {
	    m_view.setVisible(state, false);
	    m_correctAnswerView.setVisible(state,true);
	    m_controls.setVisible(state, false);
	    m_props.setVisible(state, false);
	    m_reports.setVisible(state, false);
	    m_answerValuesPanel.setVisible(state,false);
	} else if (MODE_PROPERTIES.equals(mode)) {
	    m_view.setVisible(state, false);
	    m_correctAnswerView.setVisible(state,false);
	    m_controls.setVisible(state, false);
	    m_props.setVisible(state, true);
	    m_reports.setVisible(state, false);
	    m_answerValuesPanel.setVisible(state,false);
	} else if (MODE_WIDGETS.equals(mode)) {
	    m_view.setVisible(state, false);
	    m_correctAnswerView.setVisible(state,false);
	    m_controls.setVisible(state, true);
	    m_props.setVisible(state, false);
	    m_reports.setVisible(state, false);
	    m_answerValuesPanel.setVisible(state,false);
	} else if (MODE_ANSWER_VALUES.equals(mode)) {
	    m_view.setVisible(state, false);
	    m_correctAnswerView.setVisible(state,false);
	    m_controls.setVisible(state, false);
	    m_props.setVisible(state, false);
	    m_reports.setVisible(state, false);
	    m_answerValuesPanel.setVisible(state,true);
	}
    }
    private class CorrectAnswersInitListener implements FormInitListener {
	
	// Fills in existing correct answers into the SurveyForm for the admin to edit

	private static final String ANSWER_VALUE = "answerValue";
	private static final String WIDGET_ID = "widgetID";
	private static final String PARAMETER_NAME = "parameterName";
	private static final String PARAMETER_MODEL = "parameterModel";
	private static final String DOMAIN_CLASS = "domainClass";

	private CorrectAnswersInitListener() {

	}

	public void init(FormSectionEvent e) throws FormProcessException {
	    PageState ps = e.getPageState();
	    FormData fd = e.getFormData();
	    
	    
	    // Get the surveyID of this survey
	    BigDecimal surveyID = (BigDecimal) m_survey.getSelectedSurvey(ps).getID();

	    DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.simplesurvey.getCorrectAnswers");
	    dq.setParameter("surveyID", surveyID);
       	    String answerValue = null;
	    String parameterName = null;
	    String parameterModel = null;
	    String domainClass = null;
	    BigInteger widgetID = null;
       	    PersistentWidget pw = null;

	    while ( dq.next() ) {
		
		// Get the value of the correct answer and the label (i.e. component) belonging to it
		answerValue = (String) dq.get(ANSWER_VALUE);
       		widgetID = (BigInteger) dq.get(WIDGET_ID);
       		parameterName = (String) dq.get(PARAMETER_NAME);
		parameterModel = (String) dq.get(PARAMETER_MODEL);
		domainClass = (String) dq.get(DOMAIN_CLASS);
		
		if ( domainClass.equals("com.arsdigita.formbuilder.PersistentDate") ) {
		    PersistentDate pd;
		    try {
			 pd = (PersistentDate) DomainObjectFactory.newInstance( new OID("com.arsdigita.formbuilder.Widget",widgetID.intValue()));
		    } catch (DataObjectNotFoundException ex) {
			//s_log.warn("Can't create this object" + widgetID);
		    }
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
		    try {
			Date d = sdf.parse(answerValue);
			fd.put(parameterName, d);
		    } catch ( ParseException ex ) {
			//s_log.warn("Can't parse this date");
		    }
		    
		} else {
		    fd.put(parameterName, answerValue);
		}
	    }
	    dq.close();
      	}
    }
    private class CorrectAnswersProcessListener extends SurveyProcessListener {
	
	//Stores answers as THE correct answers for this particular survey/quiz
	private CorrectAnswersProcessListener() {
	    super();
	}
	
	public void process(FormSectionEvent event) {

	    //s_log.debug("executing process listener");

	    FormData formData = event.getFormData();
	    PageState pageState = event.getPageState();

	    BigDecimal surveyID = (BigDecimal)formData.get(SURVEY_ID_NAME);
	    Survey survey = (Survey)FormBuilderUtil.instantiateObjectOneArg(Survey.class.getName(), surveyID);
	    m_persistentForm.set(pageState, survey.getForm());

	    //Iterate over the widget parameters and insert the answers to the survey response
	    Iterator parameterIter = formData.getParameters().iterator();
	    while (parameterIter.hasNext()) {

		ParameterData parameterData = (ParameterData)parameterIter.next();
		String parameterName = (String)parameterData.getName();
		Object parameterValue = parameterData.getValue();
		if (parameterValue instanceof java.lang.String[]) {
		    // This is a multi-answer question - iterate over the
		    // answer values and add them one by one
		    String[] valueArray = (String[])parameterValue;
		    for (int i = 0; i < valueArray.length; ++i) {
			addCorrectAnswer(pageState, valueArray[i], parameterName);
		    }
		} else {
		    // Single answer question
		    addCorrectAnswer(pageState, parameterValue, parameterName);
		}
	    }
	    fireCompletionEvent(event.getPageState());
       	}
	private void addCorrectAnswer(PageState ps, Object parameterValue, String parameterName) {
	    
	    // Saves the answer to the database if it's not already there, otherwise, updates the database
	    
	    Question question = getQuestion(ps, parameterName);
	    
	    if (question != null && parameterValue != null && parameterValue != "" ) {

		PersistentLabel persistentLabel = question.getLabel();
		PersistentWidget persistentWidget = question.getWidget();
		BigDecimal labelID = persistentLabel.getID();
		BigDecimal widgetID = persistentWidget.getID();
		
		DataOperation dao;

		if ( correctAnswerExists(labelID, widgetID) ) {
		   dao = SessionManager.getSession().retrieveDataOperation("com.arsdigita.simplesurvey.updateCorrectAnswer");
		} else {
		   dao = SessionManager.getSession().retrieveDataOperation("com.arsdigita.simplesurvey.insertCorrectAnswer");
		}
		dao.setParameter("labelID", labelID);
		dao.setParameter("widgetID", widgetID);
		dao.setParameter("value", parameterValue.toString());
		dao.execute();
      	    }
	}
	private boolean correctAnswerExists ( BigDecimal labelID, BigDecimal widgetID ) {
	    DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.simplesurvey.correctAnswerExists");
	    dq.setParameter("labelID", labelID);
	    dq.setParameter("widgetID", widgetID);
	    if ( dq.next() ) {
		dq.close();
		return true;
	    } else {
		dq.close();
		return false;
	    }
	}

    }
}
