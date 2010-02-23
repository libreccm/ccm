package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import java.util.Map;
import java.util.HashMap;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.contenttypes.Survey;
import com.arsdigita.cms.contenttypes.SurveyResponse;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import java.util.Iterator;

import com.arsdigita.formbuilder.util.FormBuilderUtil;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * A process listener to save the responses from a user to the database.
 *
 * @author Sören Bernstein
 */
public class SurveyProcessListener implements FormProcessListener {

    public static final String SURVEY_ID = "SurveyID";
//    public static final String RESPONSE_ID = "__ss_response_id__";
//    public static final BigDecimal THE_PUBLIC_USER = new BigDecimal(-200);
//    private static final String KNOWLEDGE_TEST = "knowledge_test";
//    protected RequestLocal m_persistentForm = new RequestLocal();
//    private RequestLocal m_nameQuestionMap = new RequestLocal();
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

        Survey survey = null;
        SurveyResponse surveyResponse = null;

        // Get the form data
        FormData formData = event.getFormData();

        // Read the needed information to create a new response and create
        // a new instance of SurveyResponse to store this information
        BigDecimal surveyID = (BigDecimal) formData.get(formData.getParameter(SURVEY_ID));

        try {

            // Try to get the corresponding Survey object
            survey = new Survey(surveyID);

        } catch (DataObjectNotFoundException ex) {

            // Strange, there is no survey with this id. Someone is messing aroound
            s_log.warn("Can't find survey object with ID " + surveyID + ". Someone is messing around.");

            // Abort processing
            return;
        }

        // Get the user
        User user = (User) Kernel.getContext().getParty();

        // Create the new SurveyResponse object
        surveyResponse = survey.addResponse();



        // Process the answers by iteration over the form widget parameters
        Iterator parameterIterator = formData.getParameters().iterator();
        while (parameterIterator.hasNext()) {

            ParameterData parameterData = (ParameterData) parameterIterator.next();
            addAnswer(surveyResponse, parameterData.getName(), parameterData.getValue());

        }
    }

    private void addAnswer(SurveyResponse surveyResponse, Object name, Object value) {

        // Test if value is a string array
        if(value instanceof String[]) {
            // This is a multi-answer question, so iterate over the answers
            for (int i = 0; i < ((String[]) value).length; i++) {
                addAnswer(surveyResponse, name, ((String[]) value)[i]);
            }
        } else {
            // Create new SurveyAnswer object
//            surveyResponse.addAnswer(,, (String) value);
        }
    }

    /*
    PageState ps = event.getPageState();

    BigDecimal responseID = (BigDecimal) formData.get(RESPONSE_ID);
    m_response.set(ps, responseID);

    m_persistentForm.set(ps, survey.getForm());

     */
    /*
    private void addAnswer(SurveyResponse surveyResponse,
    PageState ps,
    Object parameterValue,
    String parameterName) {

    s_log.debug("formData name " + parameterName + " value " + parameterValue);

    Question question = getQuestion(ps, parameterName);

    if (question != null) {

    PersistentLabel persistentLabel = question.getLabel();
    PersistentWidget persistentWidget = question.getWidget();

    surveyResponse.addAnswer(persistentLabel, persistentWidget, getStringValue(parameterValue));
    }
    }
     */
    /*
    private String getStringValue(Object parameterValue) {

    return parameterValue == null ? "" : parameterValue.toString();
    }
     */
    /*
    protected Question getQuestion(PageState ps, String parameterName) {

    if (m_nameQuestionMap.get(ps) == null) {

    // Populate the parameter name label id map
    synchronized (this) {

    Map nameQuestionMap = new HashMap();

    s_log.debug("initializing the parameter name persistent label map");

    PersistentForm persistentForm = (PersistentForm) m_persistentForm.get(ps);
    DataAssociationCursor componentCursor = persistentForm.getComponents();
    PersistentLabel lastPersistentLabel = null;
    while (componentCursor.next()) {

    PersistentComponent factory = (PersistentComponent) DomainObjectFactory.newInstance(componentCursor.getDataObject());

    s_log.debug("iterating, component " + factory.toString());

    // If this is a PersistentLabel save its id
    if (factory instanceof com.arsdigita.formbuilder.PersistentLabel) {

    lastPersistentLabel = (PersistentLabel) factory;
    }

    // Add the previous label id if this is a PersistentWidget
    if (factory instanceof com.arsdigita.formbuilder.PersistentWidget) {

    s_log.debug("adding to map " + ((PersistentWidget) factory).getParameterName() +
    " mapped to " + lastPersistentLabel);

    Question question = new Question(lastPersistentLabel,
    (PersistentWidget) factory);

    nameQuestionMap.put(((PersistentWidget) factory).getParameterName(), question);
    }
    }

    m_nameQuestionMap.set(ps, nameQuestionMap);
    }
    }

    s_log.debug("fetching label for parameter name " + parameterName);

    Question question = (Question) ((Map) m_nameQuestionMap.get(ps)).get(parameterName);

    s_log.debug("returning " + question);

    return question;
    }
     */
}
