/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contenttypes.ldn.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
//import com.arsdigita.cms.ui.authoring.NameValidationListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.cms.contenttypes.ldn.Person;

import org.apache.log4j.Logger;

public class PersonEditForm extends BasicPageForm
    implements FormInitListener, FormProcessListener, FormSubmissionListener {

    private static final Logger s_log =
        Logger.getLogger( PersonEditForm.class );

    private ItemSelectionModel m_itemModel;
    private ItemSelectionModel m_imageModel;
    private TextArea m_description;
    private CMSDHTMLEditor m_bodyText;
    private CMSDHTMLEditor m_contactDetails;

    private PersonPropertiesStep m_step;

    public PersonEditForm(String label, ItemSelectionModel model ) {
        super( label, model );

        m_itemModel = model;
    }

    public PersonEditForm(ItemSelectionModel model) {
        this("personEdit", model);
    }

    public PersonEditForm(ItemSelectionModel model,
                          PersonPropertiesStep step) {
        this("personEdit", model, step);
    }

    public PersonEditForm(String label,
                          ItemSelectionModel model,
                          PersonPropertiesStep step) {
        this(label, model);

        m_step = step;
        addSubmissionListener( this );
    }

    protected void addWidgets() {
        
        // Should be refactored to use super.addWidgets first to add the
        // Standard widgets in a standard way!
        add(new Label("Name:"));
        TextField titleWidget = new TextField(new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setOnFocus("if (this.form." + NAME + ".value == '') { " +
                               " defaulting = true; this.form." + NAME +
                               ".value = urlize(this.value); }");
        titleWidget.setOnKeyUp(
            "if (defaulting) { this.form." + NAME +
            ".value = urlize(this.value) }"
            );
        add(titleWidget);

        add(new Label("URL:"));
        TextField nameWidget = new TextField(new TrimmedStringParameter(NAME));
        // nameWidget.addValidationListener(new NameValidationListener());
        nameWidget.setOnFocus("defaulting = false");
        nameWidget.setOnBlur(
            "if (this.value == '') " +
            "{ defaulting = true; this.value = urlize(this.form." + TITLE +
            ".value) }"
            );
        add(nameWidget);

        m_description = new TextArea(Person.DESCRIPTION);
        m_description.setRows( 10 );
        m_description.setCols( 65 );
        add(new Label("Description:"));
        add(m_description);

        m_bodyText = new CMSDHTMLEditor("bodyText");
        m_bodyText.setRows( 10 );
        add(new Label("Body Text:"));
        add(m_bodyText);

        m_contactDetails = new CMSDHTMLEditor(Person.CONTACT_DETAILS);
        m_contactDetails.setRows( 10 );
        add(new Label("Contact Details:"));
        add(m_contactDetails);

        addProcessListener(this);
        addInitListener(this);

    }

    public void initPersonEdit(FormSectionEvent event) {
        super.initBasicWidgets(event);
        PageState state = event.getPageState();
        if (m_itemModel.isSelected(state)) {
            Person person = (Person)m_itemModel.getSelectedObject(state);
            m_bodyText.setValue(state, person.getBodyText());
            m_description.setValue(state, person.getDescription());
            m_contactDetails.setValue(state, person.getContactDetails());
        }
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        initPersonEdit(event);
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public Person processPersonEdit(FormSectionEvent event) {
        if( s_log.isDebugEnabled() ) {
            ACSObjectSelectionModel objectModel =
                (ACSObjectSelectionModel) m_itemModel;
                                                                                
            Class javaClass = objectModel.getJavaClass();
            String objectType = objectModel.getObjectType();

            s_log.debug( "Starting process Person with model using: " +
                         javaClass.getName() + " " + objectType );
        }

        PageState state = event.getPageState();
        Person person = null;
        person
            = (Person) super.processBasicWidgets(event);
        if (person != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(state)) {

            person.setBodyText((String)m_bodyText.getValue(state));
            person.setDescription((String)m_description.getValue(state));
            person.setContactDetails((String)m_contactDetails.getValue(state));

            if( null != m_step )
                m_step.maybeForwardToNextStep(event.getPageState());
        }

        s_log.debug( "Finished process Person" );

        return person;
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        processPersonEdit(event);
    }
}
