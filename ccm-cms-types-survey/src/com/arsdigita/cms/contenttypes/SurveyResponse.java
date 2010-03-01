package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
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

    /** PDL property name for user */
    public static final String USER = "user";
    /** PDL property name for entryDate */
    public static final String ENTRY_DATE = "entryDate";
    /** PDL property name for answers */
    public static final String ANSWERS = "answers";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SurveyResponse";

    /**
     * Default constructor. This creates a new SurveyResponse. There can't be a
     * SurveyResponse without a proper Survey object
     *
     * @param survey The <code>survey</code> for this SurveyResponse.
     **/
    public SurveyResponse(BigDecimal surveyID, User user) {
        this(BASE_DATA_OBJECT_TYPE);

        // Set unneeded but manadatory fields from ContentItem
        setName("SurveyResponse-for-Survey-" + surveyID);

        // Save the date
        setEntryDate();

        // Save the user, if any
        setUser(user);

        // Save, so I can add answers afterwards. If I don't save here, the
        // persistence can't save this and the SurveyAnswers because of missing
        // arguments.
        save();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
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
    public BigDecimal getSurveyResponseID() {
        return getID();
    }

    private void setEntryDate() {
        set(ENTRY_DATE, new Date());
    }

    public Date getEntryDate() {
        return (Date) get(ENTRY_DATE);
    }

    private void setUser(User user) {
        set(USER, user);
    }

    public User getUser() {
        return (User) get(USER);
    }

    public void addAnswer(int order, String key, String value) {
        SurveyAnswer answer = new SurveyAnswer(getSurveyResponseID(), order, key, value);
        add(ANSWERS, answer);
    }

    public SurveyAnswerCollection getAnswers() {
        return new SurveyAnswerCollection((DataCollection) get(ANSWERS));
    }

    /* Methods **************************************************/
    public boolean hasAnswers() {
        return !this.getAnswers().isEmpty();
    }
}
