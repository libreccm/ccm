/*
 * Copyright (C) 2010 Sören Bernstein
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 * Form to edit the properties of a person.
 *
 * @author: Jens Pelzetter
 */
public class GenericPersonPropertyForm extends BasicPageForm 
                                       implements FormProcessListener, 
                                                  FormInitListener, 
                                                  FormSubmissionListener {

    private static final Logger s_log = 
                         Logger.getLogger(GenericPersonPropertyForm.class);
    private GenericPersonPropertiesStep m_step;
    public static final String PERSON = GenericPerson.PERSON;
    public static final String SURNAME = GenericPerson.SURNAME;
    public static final String GIVENNAME = GenericPerson.GIVENNAME;
    public static final String TITLEPRE = GenericPerson.TITLEPRE;
    public static final String TITLEPOST = GenericPerson.TITLEPOST;
    public static final String BIRTHDATE = GenericPerson.BIRTHDATE;
    public static final String GENDER = GenericPerson.GENDER;
    public static final String DESCRIPTION = GenericPerson.DESCRIPTION;
    public static final String ID = "Person_edit";

    /**
     * Constructor, creates an empty form.
     * 
     * @param itemModel 
     */
    public GenericPersonPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constructor, creates an empty form.
     * @param itemModel
     * @param step 
     */
    public GenericPersonPropertyForm(ItemSelectionModel itemModel, 
                                     GenericPersonPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Add widgets to the Form.
     */
    @Override
    protected void addWidgets() {

        // Add standard widgets title / name .
        super.addWidgets();
        // Add mandatory widgets title/Surname/giben name/name appendix
        mandatoryFieldWidgets(this);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.genericperson.birthdate")));
        ParameterModel birthdateParam = new DateParameter(BIRTHDATE);
        com.arsdigita.bebop.form.Date birthdate = new 
                                 com.arsdigita.bebop.form.Date(birthdateParam);
        Calendar today = new GregorianCalendar();
        birthdate.setYearRange(1900, today.get(Calendar.YEAR));
        add(birthdate);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.genericperson.gender")));
        ParameterModel genderParam = new StringParameter(GENDER);
        SingleSelect gender  = new SingleSelect(genderParam);
        gender.addOption(new Option(
               "", 
               new Label(GlobalizationUtil.globalize("cms.ui.select_one"))));
        gender.addOption(new Option(
               "f", 
                new Label(ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.genericperson.gender.f"))));
        gender.addOption(new Option(
               "m", 
               new Label(ContenttypesGlobalizationUtil.globalize(
                         "cms.contenttypes.ui.genericperson.gender.m"))));
        add(gender);

        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.genericperson.description")));
        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        TextArea description = new TextArea(descriptionParam);
        description.setCols(50);
        description.setRows(5);
        add(description);

    }

    public static void mandatoryFieldWidgets(FormSection form) {
        form.add(new Label(ContenttypesGlobalizationUtil
                           .globalize("cms.contenttypes.ui.genericperson.titlepre")));
        ParameterModel titlepreParam = new StringParameter(TITLEPRE);
        TextField titlepre = new TextField(titlepreParam);
        form.add(titlepre);

        form.add(new Label(ContenttypesGlobalizationUtil
                           .globalize("cms.contenttypes.ui.genericperson.surname")));
        ParameterModel surnameParam = new StringParameter(SURNAME);
        surnameParam.addParameterListener(new NotNullValidationListener());
        TextField surname = new TextField(surnameParam);
        form.add(surname);

        form.add(new Label(ContenttypesGlobalizationUtil
                           .globalize("cms.contenttypes.ui.genericperson.givenname")));
        ParameterModel givennameParam = new StringParameter(GIVENNAME);
        TextField givenname = new TextField(givennameParam);
        form.add(givenname);

        form.add(new Label(ContenttypesGlobalizationUtil
                           .globalize("cms.contenttypes.ui.genericperson.titlepost")));
        ParameterModel titlepostParam = new StringParameter(TITLEPOST);
        TextField titlepost = new TextField(titlepostParam);
        form.add(titlepost);
    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        GenericPerson person = (GenericPerson) super.initBasicWidgets(fse);

        data.put(TITLEPRE, person.getTitlePre());
        data.put(SURNAME, person.getSurname());
        data.put(GIVENNAME, person.getGivenName());
        data.put(TITLEPOST, person.getTitlePost());
        data.put(BIRTHDATE, person.getBirthdate());
        data.put(GENDER, person.getGender());
        data.put(DESCRIPTION, person.getDescription());
    }

    public void submitted(FormSectionEvent fse) {
        if (m_step != null
             && getSaveCancelSection().getCancelButton()
                                      .isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        GenericPerson person = (GenericPerson) super.processBasicWidgets(fse);

        if (person != null
                && getSaveCancelSection().getSaveButton()
                                         .isSelected(fse.getPageState())) {
            person.setTitlePre((String) data.get(TITLEPRE));
            person.setSurname((String) data.get(SURNAME));
            person.setGivenName((String) data.get(GIVENNAME));
            person.setTitlePost((String) data.get(TITLEPOST));
            person.setBirthdate((Date) data.get(BIRTHDATE));
            person.setGender((String) data.get(GENDER));
            person.setDescription((String)data.get(DESCRIPTION));

            person.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
					