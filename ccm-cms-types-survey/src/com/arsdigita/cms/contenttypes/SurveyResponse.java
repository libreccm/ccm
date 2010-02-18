package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A SurveyResponse object represents a response by a user to
 * a survey at a certain point in time. A survey response consists
 * of a collection of answers to questions in the survey.
 *
 * @author Sören Bernstein
 *
 * Based on simplesurvey application
 */
public class SurveyResponse extends ContentItem {

    /** PDL property name for survey */
    public static final String SURVEY = "survey";
    /** PDL property name for user */
    public static final String USER = "user";
    /** PDL property name for entryDate */
    public static final String ENTRY_DATE = "entryDate";
    /** PDL property name for answers */
    public static final String ANSWERS = "answers";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SurveyResponse";

    /**
     * Default constructor. This creates a new (empty) Survey.
     **/
    public SurveyResponse() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Address.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public SurveyResponse(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
     *
     * @param id The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    protected SurveyResponse(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public SurveyResponse(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public SurveyResponse(String type) {
        super(type);
    }

    /* accessors *****************************************************/
    public void addAnswer(PersistentLabel label, PersistentWidget widget, String value) {

        SurveyAnswer answer = SurveyAnswer.create(label, widget, value);
        add(ANSWERS, answer);
    }

    public Date getEntryDate() {
        return (Date) get(ENTRY_DATE);
    }

    /* Class methods **********************************************************/
    public static SurveyResponse create(Survey survey, User user) {
        SurveyResponse response = new SurveyResponse();
        response.setup(survey, user);
        return response;
    }

    protected void setup(Survey survey, User user) {
        set(ENTRY_DATE, new Date());

        // XXX hack - see pdl file
        set(USER + "ID", user.getID());
        set(SURVEY + "ID", survey.getID());
    }

    public static SurveyResponse retrieve(DataObject obj) {
        return new SurveyResponse(obj);
    }

    /*
    public static SurveyResponseCollection retrieveBySurvey(Survey survey) {
        DataCollection responses =
                SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        responses.addEqualsFilter(SURVEY + "ID",
                survey.getID());

        return new SurveyResponseCollection(responses);
    }

    public static SurveyResponseCollection retrieveBySurvey(Survey survey, User user) {
        SurveyResponseCollection responses = retrieveBySurvey(survey);

        responses.addEqualsFilter(USER + "ID",
                user.getID());

        return responses;
    }
*/

    public boolean questionsAnswered() {

        // Returns true of questions have been answered on this response
        BigDecimal responseID = this.getID();
//        DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.simplesurvey.questionsAnswered");
//        dq.setParameter("responseID", responseID);
//        dq.next();
//        Boolean questionsAnswered = (Boolean) dq.get(QUESTIONS_ANSWERED);
//        dq.close();
//        return questionsAnswered.booleanValue();

// HACK: Brauche ich diese Funktion?
        return true;

    }
}
