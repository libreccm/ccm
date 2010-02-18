package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
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

    /** PDL property name for id */
    public static final String ID = "id";
    /** PDL property name for label */
    public static final String LABEL = "label";
    /** PDL property name for widget */
    public static final String WIDGET = "widget";
    /** PDL property name for value */
    public static final String VALUE = "value";
    /** PDL property name for response */
    public static final String RESPONSE = "response";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SurveyAnswer";

    /**
     * Default constructor. This creates a new (empty) Survey.
     **/
    public SurveyAnswer() {
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
    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

/* Class methods ********/
    public static SurveyAnswer create(PersistentLabel label,
            PersistentWidget widget,
            String value) {
        SurveyAnswer answer = new SurveyAnswer();
        answer.setup(label, widget, value);
        return answer;
    }

    protected void setup(PersistentLabel label,
            PersistentWidget widget,
            String value) {
        try {
            set(ID, Sequences.getNextValue("ss_answers_seq"));
        } catch (java.sql.SQLException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
        set(LABEL, label);
        set(WIDGET, widget);
        set(VALUE, value);
    }

}
