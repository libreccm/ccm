package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.contenttypes.ui.SurveyPersistentProcessListener;
import com.arsdigita.cms.contenttypes.ui.SurveyPersistentProcessListener;
import com.arsdigita.cms.contenttypes.ui.SurveyProcessListener;
import com.arsdigita.cms.contenttypes.ui.SurveyProcessListener;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.cms.formbuilder.FormUnavailableException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;

import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.formbuilder.PersistentSubmit;
import com.arsdigita.formbuilder.ui.BaseAddObserver;
import com.arsdigita.formbuilder.ui.PlaceholdersInitListener;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.Date;

import com.arsdigita.formbuilder.actions.ConfirmEmailListener;
import com.arsdigita.formbuilder.ui.FormBuilderXMLRenderer;
import com.arsdigita.formbuilder.util.FormBuilderUtil;

/**
 * A survey content type that represents a survey. This is partially based on
 * the simplesurvey application and CT FormItem.
 *
 * @author Sören Bernstein
 * 
 */
public class Survey extends ContentPage implements XMLGenerator {

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

            // Preset the responsesPublic
            if (getResponsesPublic() == null) {
                setResponsesPublic(false);
            }
            // Preset the responsesAnonym
            if (getResponsesAnonym() == null) {
                setResponsesAnonym(false);
            }
        }

        super.beforeSave();
    }

    // This will be called during publish
    @Override
    public boolean copyProperty(CustomCopy src,
            Property property,
            ItemCopier copier) {
        if (property.getName().equals(FORM)) {
            PersistentForm pForm = ((Survey) src).getForm();

            // Add hideden field with survey id
            PersistentHidden survey_id = PersistentHidden.create(SURVEY_ID);
            survey_id.setDefaultValue(((Survey) src).getSurveyID().toString());
            pForm.addComponent(survey_id);

            // Add a submit button
            PersistentSubmit submit = PersistentSubmit.create("submit");
            pForm.addComponent(submit);
            setAssociation(FORM, (new FormCopier()).copyForm(pForm));
            return true;
        }

        return super.copyProperty(src, property, copier);
    }

    /* accessors *****************************************************/
    public PersistentForm getForm() {
        return new PersistentForm((DataObject) get(FORM));
    }

    public BigDecimal getSurveyID() {
        return getID();
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
        SurveyResponse surveyResponse = new SurveyResponse(getSurveyID(), user);
        addResponse(surveyResponse);
        return surveyResponse;
    }

    protected void addResponse(SurveyResponse surveyResponse) {
        add(RESPONSES, surveyResponse);
    }

    public SurveyResponseCollection getResponses() {
        return new SurveyResponseCollection((DataCollection) get(RESPONSES));
    }

    public SurveyResponseCollection getResponses(User user) {
        return new SurveyResponseCollection((DataCollection) get(RESPONSES), user);
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
        return currentDate.compareTo(getStartDate()) > 0 && currentDate.compareTo(getEndDate()) < 0;
    }

    public void generateXML(PageState state, Element parent, String useContext) {

        PersistentForm form = getForm();
        Component c = instantiateForm(form, "itemAdminSummary".equals(useContext));

        // Fake the page context for the item, since we
        // have no access to the real page context.
        Page p = new Page("dummy");
        p.add(c);
        p.lock();

        PageState fake;
        try {
//            if ("itemAdminSummary".equals(useContext)) {
            // Chop off all the parameters to stop bebop stategetting confused
//                fake = p.process(new NoParametersHttpServletRequest(
//                        state.getRequest()), state.getResponse());
//            } else {
            // Really serving the user page, so need the params when
            // processing the form
            fake = p.process(state.getRequest(), state.getResponse());
//            }
        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

//        Traversal t = new VisibleTraverse(fake);
//        t.preorder(c);


        // Simply embed the bebop xml as a child of the cms:item tag
        Element element = parent.newChildElement("cms:item", CMS.CMS_XML_NS);
//        generateXMLBody(fake, element, form);
        String action = form.getAction();
        if (action == null) {
            final URL requestURL = Web.getContext().getRequestURL();

            if (requestURL == null) {
                action = state.getRequest().getRequestURI();
            } else {
                action = requestURL.getRequestURI();
            }
        }

        element.addAttribute(FormBuilderUtil.FORM_ACTION, action);

        FormBuilderXMLRenderer renderer =
                new FormBuilderXMLRenderer(element);
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setRevisitFullObject(true);
        renderer.setWrapObjects(false);

        renderer.walk(this, SimpleXMLGenerator.ADAPTER_CONTEXT);

        // then, if the component is actually a form, we need
        // to print out any possible errors
        // Ideally we could do this as part of the "walk" but for now
        // that does not work because we don't pass in the page state
        // although that can always we updated.
//        if (c instanceof Form) {
//            Element infoElement =
//                    element.newChildElement(FormBuilderUtil.FORMBUILDER_FORM_INFO,
//                    FormBuilderUtil.FORMBUILDER_XML_NS);
//            Form f = (Form) c;

//            Traversal infoTraversal =
//                    new ComponentTraverse(state, ((Form) c).getFormData(state),
//                    infoElement);
//            infoTraversal.preorder(f);
//        }

        // we need to generate the state so that it can be part of the form
        // and correctly included when the form is submitted.  We could
        // do this by iterating through the form data but it does not
        // seem like a good idea to just cut and paste the code out
        // of the PageState class
        fake.setControlEvent(c);
        fake.generateXML(element.newChildElement(FormBuilderUtil.FORMBUILDER_PAGE_STATE,
                FormBuilderUtil.FORMBUILDER_XML_NS));
    }

    protected Component instantiateForm(PersistentForm persistentForm, boolean readOnly) {

        try {

            persistentForm.setComponentAddObserver(new BaseAddObserver());
            Form form = (Form) persistentForm.createComponent();
            form.addInitListener(new PlaceholdersInitListener());
            form.addProcessListener(new SurveyProcessListener());
            form.setMethod(Form.GET);

            if (readOnly) {
                Traversal t = new Traversal() {

                    public void act(Component c) {
                        try {
                            Widget widget = (Widget) c;
                            widget.setDisabled();
                            widget.setReadOnly();
                        } catch (ClassCastException ex) {
                            // Nada
                        }
                    }
                };
                t.preorder(form);
            }

            return form;
        } catch (FormUnavailableException ex) {
            return new Label("This form is temporarily unavailable");
        }
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
