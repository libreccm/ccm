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


import com.arsdigita.simplesurvey.util.GlobalizationUtil ; 

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;

import com.arsdigita.simplesurvey.Response;
import com.arsdigita.simplesurvey.Survey;
import com.arsdigita.simplesurvey.ui.ResultsPane;
import org.apache.log4j.Logger;
import java.math.BigDecimal;

/**
 * The page for viewing a Survey.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: ViewPanel.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class ViewPanel extends SimpleContainer {

    private static final Logger s_log =
        Logger.getLogger(ViewPanel.class.getName());
    
    private SurveySelectionModel m_survey;


    private SurveyForm m_surveyForm;
    private Label m_notLive;
    private Label m_alreadySubmitted;
    private SimpleContainer m_confirmation;
    private BigDecimalParameter m_responseID;

    public RequestLocal m_response;
    
    public static final BigDecimal THE_PUBLIC_USER = new BigDecimal(-200);

    public ViewPanel(SurveySelectionModel survey) {
	m_survey = survey;
	
	m_response = new RequestLocal();

	m_notLive = new Label(GlobalizationUtil.globalize("simplesurvey.ui.this_survey_is_not_currently_active"));
	add(m_notLive);

	m_alreadySubmitted = new Label(GlobalizationUtil.globalize("simplesurvey.ui.you_have_already_completed_this_survey"));
	add(m_alreadySubmitted);
	
	//m_confirmation = new Label(GlobalizationUtil.globalize("simplesurvey.ui.thankyou_for_completing_the_survey"));
	m_confirmation = new SimpleContainer();
	m_confirmation.add(new Label(GlobalizationUtil.globalize("simplesurvey.ui.thankyou_for_completing_the_survey")));
       	m_confirmation.add(new ResultsPane(survey, m_response));		   
	add(m_confirmation);

	m_surveyForm = new SurveyForm(m_survey, m_response);
	m_surveyForm.addProcessListener(new SurveyProcessListener(m_response));
        add(m_surveyForm);	
    }

    public void register(Page p) {
	p.setVisibleDefault(m_surveyForm, false);
	
	// Sometimes we need to get the response_id out of the URL. This occurs when
	// the user double clicks on the confirmation page or hits an ActionLink there
	m_responseID = new BigDecimalParameter("response_id");
	p.addGlobalStateParam(m_responseID);

	p.addRequestListener(new RequestListener() {
		public void pageRequested(RequestEvent e) {
		    PageState ps = e.getPageState();
		    
		    Survey survey = m_survey.getSelectedSurvey(ps);
		    if ( survey == null ) {
			//s_log.warn("No survey selected!");
			return;
		    }
		    
		    // Don't display a survey that is not live
		    if ( !survey.isLive() ) {
			m_confirmation.setVisible(ps, false);
			m_surveyForm.setVisible(ps, false);
			m_notLive.setVisible(ps, true);
			m_alreadySubmitted.setVisible(ps, false);
      		    }
       		    
		    BigDecimal responseID = (BigDecimal) ps.getValue(m_responseID);
		    //s_log.warn("HEY responseID=" + responseID);
		    if ( responseID == null ) {
			
			User user = KernelHelper.getCurrentUser(ps.getRequest());
			if ( user == null) {
			    try {
				user = User.retrieve(THE_PUBLIC_USER);
			    } catch ( DataObjectNotFoundException err ) {
				//s_log.error("Public User does not exist.");
			    }
			}
			Response r = Response.create(survey, user);
			m_response.set(ps, r.getID());
			if (m_surveyForm.getFormData(ps).isSubmission()) {

			    // The SurveyForm has just been submitted so show the confirmation page
      			    m_confirmation.setVisible(ps, true);
			    m_surveyForm.setVisible(ps, false);
			    m_notLive.setVisible(ps, false);
			    m_alreadySubmitted.setVisible(ps, false);
			} else {

			    //Show the SurveyForm:
			    //Create the Response object immediately with the form. The ID of the reponse is pass along with the answers when the form is submitted
			    r.save();
			    m_confirmation.setVisible(ps, false);
			    m_surveyForm.setVisible(ps, true);
			    m_notLive.setVisible(ps, false);
			    m_alreadySubmitted.setVisible(ps, false);
			}
		    } else {

			// THe user has completed the form and is on the confirmation page
			// and has either double-clicked or wishes to see the statistics
			m_confirmation.setVisible(ps, true);
			m_surveyForm.setVisible(ps, false);
			m_notLive.setVisible(ps, false);
			m_alreadySubmitted.setVisible(ps, false);
		    }
		}
	    });
	}
}
			     
			     
