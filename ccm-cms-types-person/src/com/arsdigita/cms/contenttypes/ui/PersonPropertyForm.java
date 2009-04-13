package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.util.PersonGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.domain.DomainCollection;
import org.apache.log4j.Logger;

/**
 * Form to edit the properties of a person.
 *
 * @author: Jens Pelzetter
 */
public class PersonPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    private static final Logger s_log = Logger.getLogger(PersonPropertyForm.class);

    private PersonPropertiesStep m_step;

    public static final String PERSON = Person.PERSON;
    public static final String SURNAME = Person.SURNAME;
    public static final String GIVENNAME = Person.GIVENNAME;
    public static final String TITLEPRE = Person.TITLEPRE;
    public static final String TITLEPOST = Person.TITLEPOST;

    public static final String ID = "Person_edit";

    public PersonPropertyForm(ItemSelectionModel itemModel) {
	this(itemModel,null);
    }
    
    public PersonPropertyForm(ItemSelectionModel itemModel, PersonPropertiesStep step) {
	super(ID, itemModel);
	m_step = step;
	addSubmissionListener (this);
    }

    protected void addWidgets() {
	super.addWidgets ();
	
	add(new Label((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.person.surname").localize()));
	ParameterModel surnameParam = new StringParameter(SURNAME);
	TextField surname = new TextField (surnameParam);
	add(surname);

	add(new Label((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.person.givenname").localize()));
	ParameterModel givennameParam = new StringParameter(GIVENNAME);
	TextField givenname = new TextField (givennameParam);
	add(givenname);

	add(new Label((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepre").localize()));
	ParameterModel titlepreParam = new StringParameter(TITLEPRE);
	TextField titlepre = new TextField (titlepreParam);
	add(titlepre);

	add(new Label((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepost").localize()));
	ParameterModel titlepostParam = new StringParameter(TITLEPOST);
	TextField titlepost = new TextField (titlepostParam);
	add(titlepost);
    }

    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
	Person person = (Person)super.initBasicWidgets(fse);

	data.put(SURNAME,person.getSurname());
	data.put(GIVENNAME, person.getGivenName());
	data.put(TITLEPRE, person.getTitlePre());
	data.put(TITLEPOST, person.getTitlePost());
    }

    public void submitted(FormSectionEvent fse) {
	if (m_step != null && 
	    getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
	    m_step.cancelStreamlinedCreation(fse.getPageState());
	}
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();

	Person person = (Person)super.processBasicWidgets(fse);

	if (person != null &&
	    getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
	    person.setSurname ((String)data.get(SURNAME));
	    person.setGivenName ((String)data.get(GIVENNAME));
	    person.setTitlePre ((String)data.get(TITLEPRE));
	    person.setTitlePost ((String)data.get(TITLEPOST));
	    
	    person.save();
	}
	
	if (m_step != null) {
	    m_step.maybeForwardToNextStep(fse.getPageState());
	}
    }
}
					