/*
 * BaseContactEditPersonPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.BaseContact;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.util.BaseAddressGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class BaseContactEditPersonPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(BaseContactPropertyForm.class);

    private BaseContactPersonPropertiesStep m_step;

    public static final String SURNAME = Person.SURNAME;
    public static final String GIVENNAME = Person.GIVENNAME;
    public static final String TITLEPRE = Person.TITLEPRE;
    public static final String TITLEPOST = Person.TITLEPOST;

    /**
     * ID of the form
     */
    public static final String ID = "BaseContactEditPerson";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public BaseContactEditPersonPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public BaseContactEditPersonPropertyForm(ItemSelectionModel itemModel, BaseContactPersonPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.person.surname").localize()));
	ParameterModel surnameParam = new StringParameter(SURNAME);
        surnameParam.addParameterListener( new NotNullValidationListener( ) );
	surnameParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
	TextField surname = new TextField(surnameParam);
	add(surname);

	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.person.givenname").localize()));
	ParameterModel givennameParam = new StringParameter(GIVENNAME);
        givennameParam.addParameterListener( new NotNullValidationListener( ) );
	givennameParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
	TextField givenname = new TextField(givennameParam);
	add(givenname);

    	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepre").localize()));
	ParameterModel titlepreParam = new StringParameter(TITLEPRE);
	titlepreParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
	TextField titlepre = new TextField(titlepreParam);
	add(titlepre);

    	add(new Label((String)BaseAddressGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepost").localize()));
	ParameterModel titlepostParam = new StringParameter(TITLEPOST);
	titlepostParam.addParameterListener( new StringInRangeValidationListener(0, 1000) );
	TextField titlepost = new TextField(titlepostParam);
	add(titlepost);
    }

    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);
       
        if(baseContact.getPerson() != null) {
            data.put(SURNAME, baseContact.getPerson().getSurname());
            data.put(GIVENNAME, baseContact.getPerson().getGivenName());
            data.put(TITLEPRE, baseContact.getPerson().getTitlePre());
            data.put(TITLEPOST, baseContact.getPerson().getTitlePost());
        }
    }

    public void submitted(FormSectionEvent fse) {
	if (m_step != null && 
	    getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
	    m_step.cancelStreamlinedCreation(fse.getPageState());
	}
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);

	if (baseContact.getPerson() != null &&
	    getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
	    baseContact.getPerson().setSurname((String)data.get(SURNAME));
	    baseContact.getPerson().setGivenName((String)data.get(GIVENNAME));
	    baseContact.getPerson().setTitlePre((String)data.get(TITLEPRE));
	    baseContact.getPerson().setTitlePost((String)data.get(TITLEPOST));
            
	    baseContact.getPerson().save();
	}
	
	if (m_step != null) {
	    m_step.maybeForwardToNextStep(fse.getPageState());
	}
    }
}