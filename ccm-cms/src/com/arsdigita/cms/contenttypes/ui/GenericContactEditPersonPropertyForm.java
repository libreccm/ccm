/*
 * GenericContactEditPersonPropertyForm.java
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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

import com.arsdigita.globalization.GlobalizationHelper;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class GenericContactEditPersonPropertyForm extends BasicPageForm 
                                                  implements FormProcessListener, 
                                                             FormInitListener, 
                                                             FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(GenericContactPropertyForm.class);
    private GenericContactPersonPropertiesStep m_step;
    
    public static final String SURNAME = GenericPerson.SURNAME;
    public static final String GIVENNAME = GenericPerson.GIVENNAME;
    public static final String TITLEPRE = GenericPerson.TITLEPRE;
    public static final String TITLEPOST = GenericPerson.TITLEPOST;
    public static final String CONTACTS_KEY = GenericPersonContactCollection.CONTACTS_KEY;

    /**
     * ID of the form
     */
    public static final String ID = "ContactEditPerson";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactEditPersonPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constructor taking an ItemSelectionModel and an instance of 
     * ContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public GenericContactEditPersonPropertyForm(ItemSelectionModel itemModel, 
                                                GenericContactPersonPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.person.surname")));
        ParameterModel surnameParam = new StringParameter(SURNAME);
        surnameParam.addParameterListener(new NotNullValidationListener());
        surnameParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField surname = new TextField(surnameParam);
        add(surname);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.person.givenname")));
        ParameterModel givennameParam = new StringParameter(GIVENNAME);
        givennameParam.addParameterListener(new NotNullValidationListener());
        givennameParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField givenname = new TextField(givennameParam);
        add(givenname);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.person.titlepre")));
        ParameterModel titlepreParam = new StringParameter(TITLEPRE);
        titlepreParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField titlepre = new TextField(titlepreParam);
        add(titlepre);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.person.titlepost")));
        ParameterModel titlepostParam = new StringParameter(TITLEPOST);
        titlepostParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField titlepost = new TextField(titlepostParam);
        add(titlepost);

        // GenericContact type field
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.person.contact.type")));
        ParameterModel contactTypeParam = new StringParameter(CONTACTS_KEY);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.addValidationListener(new NotNullValidationListener());
        contactType.addOption(new 
                Option("", 
                       new Label(GlobalizationUtil
                                 .globalize("cms.ui.select_one"))));

        // Add the Options to the SingleSelect widget
        GenericContactTypeCollection contacttypes = new GenericContactTypeCollection();
        contacttypes.addLanguageFilter(GlobalizationHelper
                                       .getNegotiatedLocale().getLanguage());

        while (contacttypes.next()) {
            RelationAttribute ct = contacttypes.getRelationAttribute();
            contactType.addOption(new Option(ct.getKey(), ct.getName()));
        }

        add(contactType);
    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel()
                                                  .getSelectedObject(state);

        if (contact.getPerson() != null) {
            data.put(SURNAME, contact.getPerson().getSurname());
            data.put(GIVENNAME, contact.getPerson().getGivenName());
            data.put(TITLEPRE, contact.getPerson().getTitlePre());
            data.put(TITLEPOST, contact.getPerson().getTitlePost());
//            data.put(CONTACTS_KEY, contact.getContactType());
        }
    }

    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel()
                                                  .getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

//            if (contact.getPerson() == null) {
//                contact.setPerson(new GenericPerson());
//                contact.getPerson().setName("Person for " + contact.getName() + "(" + contact.getID() + ")");
//                contact.getPerson().setTitle("Person for " + contact.getName() + "(" + contact.getID() + ")");
//            }

            contact.getPerson().setSurname((String) data.get(SURNAME));
            contact.getPerson().setGivenName((String) data.get(GIVENNAME));
            contact.getPerson().setTitlePre((String) data.get(TITLEPRE));
            contact.getPerson().setTitlePost((String) data.get(TITLEPOST));
//            contact.setContactType((String) data.get(CONTACTS_KEY));

            contact.getPerson().save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
