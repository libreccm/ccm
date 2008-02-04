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

import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.SurveyProcessListener;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import java.math.BigDecimal;
import com.arsdigita.formbuilder.PersistentForm;

public class SurveyForm extends MetaForm {

    private SurveySelectionModel m_survey;
    private RequestLocal m_response;
    public BigDecimal m_responseID;

    public static final BigDecimal THE_PUBLIC_USER = new BigDecimal(-200);
    private static org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(SurveyForm.class.getName());

    
    public SurveyForm(SurveySelectionModel survey, RequestLocal response) {
	super("SurveyForm");
	m_survey = survey;
	m_response = response;
    }
     public SurveyForm(SurveySelectionModel survey) {
	 super("SurveyForm");
	m_response = null;
    }
    public Form buildForm(PageState state) {
	// Get the survey to view
	Survey survey = m_survey.getSelectedSurvey(state);
	
	// Get the persistent form of the survey
	PersistentForm persistentForm = survey.getForm();
	
	// Create the form
	Form form = (Form)persistentForm.createComponent();
	
	
	// Add a submit button
	form.add(new Submit("submit"), BlockStylable.CENTER);
	// Add a hidden input with the survey id

	
	Hidden hiddenID = new Hidden(new BigDecimalParameter(SurveyProcessListener.SURVEY_ID_NAME));
	hiddenID.setDefaultValue(survey.getID());
	form.add(hiddenID);

	// Sometimes this form is being called for use in the admin interface
	// It doesn't need a response_id then
	BigDecimal responseID = null;
	if ( m_response != null ) {
	    // Get the response id from the request local
	   responseID = (BigDecimal) m_response.get(state);
	} 
	    
	Hidden hiddenResponseID = new Hidden(new BigDecimalParameter(SurveyProcessListener.RESPONSE_ID));
	hiddenResponseID.setDefaultValue(responseID);	  
	form.add(hiddenResponseID);
	
	// Only call the SurveyProcessListener when you really need it after instantiating this
	// Add the Simple Survey process listener
	//form.addProcessListener(new SurveyProcessListener());
	return form;
    }        

}

