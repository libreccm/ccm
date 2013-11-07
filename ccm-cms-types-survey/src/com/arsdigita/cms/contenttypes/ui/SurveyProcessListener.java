package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.bebop.FormData;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.contenttypes.Survey;
import com.arsdigita.cms.contenttypes.SurveyResponse;
import java.util.Iterator;


import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * A process listener to save the responses from a user to the database.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class SurveyProcessListener implements FormProcessListener {

    public static final String SURVEY_ID = Survey.SURVEY_ID;
//    public static final String RESPONSE_ID = "__ss_response_id__";
//    protected RequestLocal m_persistentForm = new RequestLocal();
    private RequestLocal m_response;
    private static org.apache.log4j.Logger s_log =
            Logger.getLogger(SurveyProcessListener.class.getName());

    public SurveyProcessListener(RequestLocal response) {
        m_response = response;
    }

    public SurveyProcessListener() {
        m_response = null;
    }

    public void process(FormSectionEvent event) {

        int numQuestions = 0;
        Survey survey = null;
        SurveyResponse surveyResponse = null;
        User user = null;

        // Get the form data
        FormData formData = event.getFormData();

        // Read the needed information to create a new response and create
        // a new instance of SurveyResponse to store this information
        BigDecimal surveyID = new BigDecimal((String) formData.getParameter(SURVEY_ID).getValue());

        try {

            // Try to get the corresponding Survey object
            survey = new Survey(surveyID);

        } catch (DataObjectNotFoundException ex) {

            // Strange, there is no survey with this id. Someone is messing around
            s_log.warn("Can't find survey object with ID " + surveyID + ". Someone is messing around.");

            // Abort processing
            return;
        }

        // If this survey isn't anonymous
        if (!survey.getResponsesAnonym()) {

            // Get the current user
            user = (User) Kernel.getContext().getParty();
        }

        // Create the new SurveyResponse object
        surveyResponse = survey.addResponse(user);

        // Process the answers by iteration over the form widget parameters
        Iterator parameterIterator = formData.getParameters().iterator();
        while (parameterIterator.hasNext()) {

            ParameterData parameterData = (ParameterData) parameterIterator.next();
            String parameterName = parameterData.getName().toString();

            // Skip some unneeded Parameters, p.ex. submit button, survey_id
            if (parameterName.startsWith("submit") ||
                    parameterName.startsWith(SURVEY_ID) ||
                    parameterName.startsWith("form.")) {
                continue;
            }

            addAnswer(surveyResponse, ++numQuestions, parameterName, parameterData.getValue());

        }
    }

    private void addAnswer(SurveyResponse surveyResponse, int questionNumber, String name, Object value) {

        // Test if value is a string array
        if (value instanceof String[]) {
            // This is a multi-answer question, so iterate over the answers
            for (int i = 0; i < ((String[]) value).length; i++) {
                addAnswer(surveyResponse, questionNumber, name, ((String[]) value)[i]);
            }
        } else {
            // Create new SurveyAnswer object
            surveyResponse.addAnswer(questionNumber, name, (String) value);
        }
    }
}
