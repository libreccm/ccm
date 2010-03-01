package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * A SurveyAnswer object represents a response by a user to
 * a survey at a certain point in time. A survey response consists
 * of a collection of answers to questions in the survey.
 *
 * @author Sören Bernstein
 * 
 * Based on simplesurvey application
 */
public class SurveyAnswer extends ContentItem {

    /** PDL property name for label */
    public static final String QUESTION_NUMBER = "questionNumber";
    /** PDL property name for widget */
    public static final String KEY = "key";
    /** PDL property name for value */
    public static final String VALUE = "value";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SurveyAnswer";

    /**
     * Default constructor. This creates a new (empty) Survey.
     **/
    public SurveyAnswer(BigDecimal surveyResponseID, int questNum, String key, String value) {
        this(BASE_DATA_OBJECT_TYPE);

        // Set unneeded but mandatory fields from content item
        setName("SurveyAnswer-for-SurveyRepsonse-" + surveyResponseID);

        // Set the data
        setQuestionNumber(questNum);
        setKey(key);
        setValue(value);
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
    public SurveyAnswer(BigDecimal id) throws DataObjectNotFoundException {
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
    public SurveyAnswer(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public SurveyAnswer(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public SurveyAnswer(String type) {
        super(type);
    }

    /* accessors *****************************************************/
    private void setQuestionNumber(int questNum) {
        set(QUESTION_NUMBER, questNum);
    }

    public int getQuestionNumber() {
        return ((Integer) get(QUESTION_NUMBER)).intValue();
    }

    private void setKey(String key) {
        set(KEY, key);
    }

    public String getKey() {
        return (String) get(KEY);
    }

    private void setValue(String value) {
        set(VALUE, value);
    }

    public String getValue() {
        return (String) get(VALUE);
    }
}
