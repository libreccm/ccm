package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contenttypes.ui.SurveyPersistentProcessListener;
import com.arsdigita.cms.contenttypes.ui.SurveyProcessListener;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentForm;

import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.kernel.User;
import java.util.Date;

/**
 * A survey content type that represents a survey. This is partially based on
 * the simplesurvey application and CT FormItem.
 *
 * @author Sören Bernstein
 * 
 */
public class Survey extends ContentPage {

    /**  PDL property name for formSection */
    public static final String SURVEY_ID = "survey_id";
    /**  PDL property name for formSection */
    public static final String FORM = "form";
    /**  PDL property name for surveyResponses */
    public static final String RESPONSES = "responses";
    /**  PDL property name for startDate */
    public static final String START_DATE = "startDate";
    /**  PDL property name for endDate */
    public static final String END_DATE = "endDate";
    /**  PDL property name for responsesPublic */
    public static final String RESPONSES_PUBLIC = "responsesPublic";
    /**  PDL property name for responsesAnonym */
    public static final String RESPONSES_ANONYM = "responsesAnonym";
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
            // Preset the responsesPublic
            if (getResponsesPublic() == null) {
                setResponsesPublic(false);
            }
            */
            /*
            // Preset the responsesAnonym
            if (getResponsesAnonym() == null) {
                setResponsesAnonym(false);
            }
            */
        }

        super.beforeSave();
    }

    /* accessors *****************************************************/
    public void setForm(PersistentForm persistentForm) {
//        persistentForm.addProcessListener(new SurveyPersistentProcessListener());
//        PersistentHidden survey_id = PersistentHidden.create(SURVEY_ID);
//        survey_id.setDefaultValue(getSurveyID());
//        persistentForm.addComponent(survey_id);
        set(FORM, persistentForm);
    }

    public PersistentForm getForm() {
        return new PersistentForm((DataObject) get(FORM));
    }

    public BigDecimal getSurveyID() {
        return (BigDecimal) get(SURVEY_ID);
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

    public Boolean getResponsesAnonym() {
        return (Boolean) get(RESPONSES_ANONYM);
    }

    public void setResponsesAnonym(Boolean responsesAnonym) {
        set(RESPONSES_ANONYM, responsesAnonym);
    }

    public SurveyResponse addResponse(User user) {
        SurveyResponse surveyResponse =  new SurveyResponse(user);
        addResponse(surveyResponse);
        return surveyResponse;
    }
    
    protected void addResponse(SurveyResponse surveyResponse) {
        add(RESPONSES, surveyResponse);
    }

    public SurveyResponseCollection getResponses() {
        return new SurveyResponseCollection ((DataCollection) get(RESPONSES));
    }

    public SurveyResponseCollection getResponses(User user) {
        return new SurveyResponseCollection ((DataCollection) get(RESPONSES), user);
    }

    /* methods ****************************************************************/
    public boolean hasResponses() {
        return !this.getResponses().isEmpty();
    }

    public boolean hasResponses(User user) {
        return !this.getResponses(user).isEmpty();
    }

    public boolean isActive() {
        Date currentDate = new Date();
        return currentDate.compareTo(getStartDate()) > 0 && currentDate.compareTo(getEndDate()) > 0;
    }

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
