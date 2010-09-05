/*
 * Copyright (C) 2010 Sören Bernstein, for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 * Form to edit the properties of a person.
 *
 * @author: Jens Pelzetter
 */
public class GenericPersonPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(GenericPersonPropertyForm.class);
    private GenericPersonPropertiesStep m_step;
    public static final String PERSON = GenericPerson.PERSON;
    public static final String SURNAME = GenericPerson.SURNAME;
    public static final String GIVENNAME = GenericPerson.GIVENNAME;
    public static final String TITLEPRE = GenericPerson.TITLEPRE;
    public static final String TITLEPOST = GenericPerson.TITLEPOST;
    public static final String BIRTHDATE = GenericPerson.BIRTHDATE;
    public static final String DESCRIPTION = GenericPerson.DESCRIPTION;
    public static final String ID = "Person_edit";

    public GenericPersonPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public GenericPersonPropertyForm(ItemSelectionModel itemModel, GenericPersonPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.surname").localize()));
        ParameterModel surnameParam = new StringParameter(SURNAME);
        TextField surname = new TextField(surnameParam);
        add(surname);

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.givenname").localize()));
        ParameterModel givennameParam = new StringParameter(GIVENNAME);
        TextField givenname = new TextField(givennameParam);
        add(givenname);

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepre").localize()));
        ParameterModel titlepreParam = new StringParameter(TITLEPRE);
        TextField titlepre = new TextField(titlepreParam);
        add(titlepre);

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepost").localize()));
        ParameterModel titlepostParam = new StringParameter(TITLEPOST);
        TextField titlepost = new TextField(titlepostParam);
        add(titlepost);

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.birthdate").localize()));
        ParameterModel birthdateParam = new DateParameter(BIRTHDATE);
        com.arsdigita.bebop.form.Date birthdate = new com.arsdigita.bebop.form.Date(birthdateParam);
        Calendar today = new GregorianCalendar();
        birthdate.setYearRange(1900, today.get(Calendar.YEAR));
        add(birthdate);

        add(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.description").localize()));
        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
        TextArea description = new TextArea(descriptionParam);
        description.setCols(50);
        description.setRows(5);
        add(description);

    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        GenericPerson person = (GenericPerson) super.initBasicWidgets(fse);

        data.put(SURNAME, person.getSurname());
        data.put(GIVENNAME, person.getGivenName());
        data.put(TITLEPRE, person.getTitlePre());
        data.put(TITLEPOST, person.getTitlePost());
        data.put(BIRTHDATE, person.getBirthdate());
        data.put(DESCRIPTION, person.getDescription());
    }

    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        GenericPerson person = (GenericPerson) super.processBasicWidgets(fse);

        if (person != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            person.setSurname((String) data.get(SURNAME));
            person.setGivenName((String) data.get(GIVENNAME));
            person.setTitlePre((String) data.get(TITLEPRE));
            person.setTitlePost((String) data.get(TITLEPOST));
            person.setBirthdate((Date) data.get(BIRTHDATE));
            person.setDescription((String)data.get(DESCRIPTION));

            person.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
					