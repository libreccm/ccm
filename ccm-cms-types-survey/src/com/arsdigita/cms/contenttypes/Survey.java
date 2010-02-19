package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentForm;

import java.util.Date;

/**
 * A survey content type that represents a survey. This is
 * based on the simplesurvey application.
 *
 * @author Sören Bernstein
 * 
 */
public class Survey extends ContentPage {

    /**  PDL property name for formSection */
    public static final String FORM = "form";
    /**  PDL property name for startDate */
    public static final String START_DATE = "startDate";
    /**  PDL property name for endDate */
    public static final String END_DATE = "endDate";
    /**  PDL property name for responsesPublic */
    public static final String RESPONSES_PUBLIC = "responsesPublic";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Survey";

/*
    private static final SurveyConfig s_config = new SurveyConfig();
    static {
	    s_config.load();
    }

    public static final SurveyConfig getConfig()
    {
	    return s_config;
    }
*/
    
    /**
     * Default constructor. This creates a new (empty) Survey.
     **/
    public Survey() {
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
    public Survey(BigDecimal id) throws DataObjectNotFoundException {
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
    public Survey(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public Survey(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public Survey(String type) {
        super(type);
    }

    /**
     * For new content items, sets the associated content type if it
     * has not been already set.
     */
    /*    @Override
    public void beforeSave() {
    super.beforeSave();

    Assert.exists(getContentType(), ContentType.class);
    }
     */
    /**
     * This will handle the mandatory FormSection. If there is no
     * FormSection set it will create an empty new form and assign it
     * to keep the db happy.
     */
    @Override
    protected void beforeSave() {
        if (isNew()) {
            if (get(FORM) == null) {
                PersistentForm form = new PersistentForm();
                form.setHTMLName(getName());
                form.setAdminName(getName());
                setAssociation(FORM, form);
            }
            /*
            if (getResponsesPublic() == null) {
            setResponsesPublic(false);
            }
             */
        }

        super.beforeSave();
    }

    /* accessors *****************************************************/
    public void setForm(PersistentForm persistentForm) {
        set(FORM, persistentForm);
    }

    public PersistentForm getForm() {
        return new PersistentForm((DataObject) get(FORM));
    }

    public void setStartDate(Date startDate) {
        set(START_DATE, startDate);
    }

    public Date getStartDate() {
        return (Date) get(START_DATE);
    }

    public void setEndDate(Date endDate) {
        set(END_DATE, endDate);
    }

    public Date getEndDate() {
        return (Date) get(END_DATE);
    }

    public Boolean getResponsesPublic() {
        return (Boolean) get(RESPONSES_PUBLIC);
    }

    public void setResponsesPublic(Boolean responsesPublic) {
        set(RESPONSES_PUBLIC, responsesPublic);
    }

    /* Class methods *********************************************************/
    public static Survey retrieve(BigDecimal id)
            throws DataObjectNotFoundException {

        Survey survey = new Survey(id);

        return survey;
    }

    public static Survey retrieve(DataObject obj) {
        Survey survey = new Survey(obj);

        return survey;
    }
    /*
    public SurveyResponseCollection getResponses() {
    return SurveyResponse.retrieveBySurvey(this);
    }

    public SurveyResponseCollection getUserResponses(User user) {
    return SurveyResponse.retrieveBySurvey(this, user);
    }

    public boolean hasUserResponded(User user) {
    SurveyResponseCollection responses = getUserResponses(user);

    if (responses.next()) {
    responses.close();
    return true;
    }
    return false;
    }
     */

    /*
    public DataQuery getLabelDataQuery() {
    String queryName = "com.arsdigita.simplesurvey.GetFormLabels";
    DataQuery dataQuery =
    SessionManager.getSession().retrieveQuery(queryName);
    dataQuery.setParameter("surveyID", getID());

    return dataQuery;
    }

    public boolean isLive() {
    Date currentDate = new Date();

    return getStartDate().compareTo(currentDate) < 0 &&
    getEndDate().compareTo(currentDate) > 0;
    }
     */
    /*
     * Retrieves most recent survey that isn't completed
     */
    public static Survey getMostRecentSurvey() {
        DataCollection surveys = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        surveys.addFilter("startDate <= sysdate and endDate > sysdate");
        surveys.addOrder("startDate desc");

        Survey survey = null;
        if (surveys.next()) {
            survey = new Survey(surveys.getDataObject());
        }
        surveys.close();

        return survey;
    }
}
