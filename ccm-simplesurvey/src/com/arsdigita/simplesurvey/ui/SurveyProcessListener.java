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
package com.arsdigita.simplesurvey.ui;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.simplesurvey.Response;
import com.arsdigita.simplesurvey.Survey;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The process lister that processes a survey response entered by a user.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: SurveyProcessListener.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class SurveyProcessListener 
    implements FormProcessListener {

    public static final String SURVEY_ID_NAME = "__ss_survey_id__";
    public static final String RESPONSE_ID = "__ss_response_id__";
    
    public static final BigDecimal THE_PUBLIC_USER = new BigDecimal(-200);
    private static final String KNOWLEDGE_TEST = "knowledge_test";
    protected RequestLocal m_persistentForm = new RequestLocal();
    private RequestLocal m_nameQuestionMap = new RequestLocal();
    private RequestLocal  m_response;

    private static org.apache.log4j.Logger s_log = 
        Logger.getLogger(SurveyProcessListener.class.getName());

    public SurveyProcessListener(RequestLocal response) {
	m_response = response;
    }
    public SurveyProcessListener() {
	m_response = null;
    }
    
    public void process(FormSectionEvent event) {

        FormData formData = event.getFormData();
        PageState ps = event.getPageState();

        BigDecimal surveyID = (BigDecimal)formData.get(SURVEY_ID_NAME);
	BigDecimal responseID = (BigDecimal)formData.get(RESPONSE_ID);
	m_response.set(ps, responseID);

        Survey survey = (Survey) FormBuilderUtil.instantiateObjectOneArg(Survey.class.getName(), surveyID);
	Response response = null;
	try {
	    response = (Response) DomainObjectFactory.newInstance( new OID(Response.class.getName(), responseID));
	} catch (DataObjectNotFoundException ex) {
	    //	s_log.warn("Can't create this object" + responseID);
	}
	
	//Let's not save the data twice in the case of a double-click
	if ( response.questionsAnswered() ) {
	    return;
	}
	
        m_persistentForm.set(ps, survey.getForm());

        // Get the responding user
        User user = (User)Kernel.getContext().getParty();

	// Use the generic user "The Public" if the user is not registered
	if ( user == null) {
	    try {
		user = User.retrieve(THE_PUBLIC_USER);
	    } catch ( DataObjectNotFoundException e ) {
		s_log.error("Public User does not exist.");
	    }
	}

        // Iterate over the widget parameters and insert the answers to the survey response
        Iterator parameterIter = formData.getParameters().iterator();
        while (parameterIter.hasNext()) {
	    s_log.warn("Found some formData");
            ParameterData parameterData = (ParameterData)parameterIter.next();

            String parameterName = (String)parameterData.getName();

	    Object parameterValue = parameterData.getValue();
	    if (parameterValue instanceof java.lang.String[]) {
		// This is a multi-answer question - iterate over the
		// answer values and add them one by one
		String[] valueArray = (String[])parameterValue;
		for (int i = 0; i < valueArray.length; ++i) {
		    addAnswer(response, ps, valueArray[i], parameterName);
	
		}
	    } else {
		// Single answer question
	
		addAnswer(response, ps, parameterValue, parameterName);
	    }
        }
        // Save the survey response to the database
        response.save();	
	saveScore(survey, response);
    }

    private void saveScore(Survey survey, Response response) {

	String query;
	if ( survey.getQuizType().equals(KNOWLEDGE_TEST) ) {
	    query = "com.arsdigita.simplesurvey.saveScore";
	} else {
	    query = "com.arsdigita.simplesurvey.saveAssessmentScore";
	}
      	DataOperation dao = SessionManager.getSession().retrieveDataOperation(query);
        dao.setParameter("responseID", response.getID());
        dao.execute();
    }

    private void addAnswer(Response surveyResponse, 
			   PageState ps, 
			   Object parameterValue, 
			   String parameterName) {

	s_log.debug("formData name " + parameterName + " value " + parameterValue);
            
	Question question = getQuestion(ps, parameterName);

	if (question != null ) {

	    PersistentLabel persistentLabel = question.getLabel();
	    PersistentWidget persistentWidget = question.getWidget();

	    surveyResponse.addAnswer(persistentLabel, persistentWidget, getStringValue(parameterValue));
	}
    }

    private String getStringValue(Object parameterValue) {

	return parameterValue == null ? "" : parameterValue.toString();
    }

    protected Question getQuestion(PageState ps, String parameterName) {

        if (m_nameQuestionMap.get(ps) == null) {
            
            // Populate the parameter name label id map
            synchronized (this) {

                Map nameQuestionMap = new HashMap();

                s_log.debug("initializing the parameter name persistent label map");

                PersistentForm persistentForm = (PersistentForm)m_persistentForm.get(ps);
                DataAssociationCursor componentCursor = persistentForm.getComponents();
                PersistentLabel lastPersistentLabel = null;
                while (componentCursor.next()) {

                    PersistentComponent factory = (PersistentComponent) DomainObjectFactory.newInstance(componentCursor.getDataObject());

                    s_log.debug("iterating, component " + factory.toString());

                    // If this is a PersistentLabel save its id
                    if (factory instanceof com.arsdigita.formbuilder.PersistentLabel) {
                        
                        lastPersistentLabel = (PersistentLabel)factory;
                    }

                    // Add the previous label id if this is a PersistentWidget
                    if (factory instanceof com.arsdigita.formbuilder.PersistentWidget) {

                        s_log.debug("adding to map " + ((PersistentWidget)factory).getParameterName() + 
                                    " mapped to " + lastPersistentLabel);

			Question question = new Question(lastPersistentLabel,
							 (PersistentWidget)factory);
			
                        nameQuestionMap.put(((PersistentWidget)factory).getParameterName(), question);
                    }
                }

                m_nameQuestionMap.set(ps, nameQuestionMap);
            }
        }

        s_log.debug("fetching label for parameter name " + parameterName);

        Question question = (Question)((Map)m_nameQuestionMap.get(ps)).get(parameterName);

        s_log.debug("returning " + question);

        return question;
    }
}
