/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.test;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.PersistentSubmit;
import com.arsdigita.formbuilder.PersistentTextArea;
import com.arsdigita.formbuilder.PersistentTextField;
import org.apache.log4j.Logger;


/**
 * Test the PersistentFormSection class with the test pattern defined in
 * the PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentFormSectionTest.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class PersistentFormSectionTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentFormSectionTest.class.getName());

    // Component attributes
    private String m_labelText1 = "First label Text on position 3";
    private String m_labelText2 = "Second label Text on position 1";

    // Test cases to create component factories and reference components
    private PersistentLabelTest m_labelTest = new PersistentLabelTest("label test");
    private PersistentTextAreaTest m_areaTest = new PersistentTextAreaTest("text area test");
    private PersistentTextFieldTest m_fieldTest = new PersistentTextFieldTest("text field test");
    private PersistentSubmitTest m_submitTest = new PersistentSubmitTest("submit test");

    // Component factories used
    private PersistentLabel m_label1;
    private PersistentTextArea m_textArea;
    private PersistentLabel m_label2;
    private PersistentTextField m_textField;
    private PersistentSubmit m_submit;
    private PersistentTextArea m_textAreaToRemove;

    // Attributes specific to a persistent form section
    // that are not in a Bebop form section
    protected String m_adminName = "Test Form Admin Name";
    protected String m_description = "Purpose of this form is to test if a form can be persisted";

    // Listeners
    protected PersistentProcessListener m_processListener;

    /**
     * JUnit needs this constructor
     */
    public PersistentFormSectionTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentFormSection factory = new PersistentFormSection();
        setFormSectionData(factory);
        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        FormSection reference = new FormSection();

        addComponentsToReference(reference);

        reference.addProcessListener(m_processListener.createProcessListener());

        return reference;
    }

    /**
     * Some components have attributes that the corresponding Bebop component
     * does not have. Those should be checked (after the component has been
     * retrieved from the database) in this method.
     */
    protected void checkPersistenceAttributes(PersistentComponent factory) {

        PersistentFormSection formSectionFactory = (PersistentFormSection)factory;

        assertEquals(m_adminName, formSectionFactory.getAdminName());
        assertEquals(m_description, formSectionFactory.getDescription());
    }

    /**
     * Adds components to the reference FormSection. Also used by the form test case.
     */
    public void addComponentsToReference(FormSection formSection) {

        // We need to distinguish the labels
        Label label1 = (Label)m_labelTest.createPrimaryReferenceComponent();
        label1.setLabel(m_labelText1);

        Label label2 = (Label)m_labelTest.createPrimaryReferenceComponent();
        label2.setLabel(m_labelText2);

        // Add components
        formSection.add(label2);
        formSection.add(m_areaTest.createPrimaryReferenceComponent());

        formSection.add(label1);
        formSection.add(m_fieldTest.createPrimaryReferenceComponent());

        formSection.add(m_submitTest.createPrimaryReferenceComponent());
    }

    /**
     * Set all data of the test PersistentFormSection. Also used by the form test case.
     */
    public void setFormSectionData(PersistentFormSection formSection) {

        formSection.setDescription(m_description);
        formSection.setAdminName(m_adminName);

        initializeComponentFactories();

        addComponentsToFormSection(formSection);

        m_processListener =
            PersistentProcessListener.create("Test Listener", "com.arsdigita.formbuilder.TestProcessListener");
        m_processListener.save();

        formSection.addProcessListener(m_processListener);
    }

    /**
     * Setup the PersistentComponentFactorys that we add to the PersistentFormSection
     */
    private void initializeComponentFactories() {

        // Create two label/widget pairs and a submit button
        // plus an additional text area that will be removed from the form section
        m_label1 = (PersistentLabel)m_labelTest.createPrimaryPersistentFactory();
        m_label1.setLabel(m_labelText1);

        // It should not be necessary to save
        m_textArea = (PersistentTextArea)m_areaTest.createPrimaryPersistentFactory();
        m_textArea.save();

        m_label2 = (PersistentLabel)m_labelTest.createPrimaryPersistentFactory();
        m_label2.setLabel(m_labelText2);
        // It should not be necessary to save

        m_textField = (PersistentTextField)m_fieldTest.createPrimaryPersistentFactory();
        m_textField.save();

        m_submit = (PersistentSubmit)m_submitTest.createPrimaryPersistentFactory();
        m_submit.save();

        m_textAreaToRemove = (PersistentTextArea)m_areaTest.createPrimaryPersistentFactory();
        m_textAreaToRemove.save();
    }

    /**
     * Add all components to the PersistentFormSection.
     */
    private void addComponentsToFormSection(PersistentFormSection formSection) {

        // Try adding the first label to position 2 and make sure
        // this fails with an IllegalArgumentException
        //         try {
        //             formSection.addComponent(m_label1, 2);

        //             // No exception thrown - fail the test
        //             //            fail("Attempted to add a component to out of range position 2 and there was no exception thrown");

        //         } catch (IllegalArgumentException e) {
        //             // This is ok - this is what we wanted
        //         }

        // Now add the label correctly to position 1
        formSection.addComponent(m_label1, 1);

        // Add the text area with at the default position (position 2)
        formSection.addComponent(m_textArea);

        // Insert the submit button at position 3
        formSection.addComponent(m_submit, 3);

        // Insert the second label at position 3
        formSection.addComponent(m_label2, 3);

        // Insert the text field at position 4
        formSection.addComponent(m_textField, 4);

        // Insert another text area at position 2
        formSection.addComponent(m_textAreaToRemove, 1);

        // Remove that same text area
        formSection.removeComponent(m_textAreaToRemove);

        // Move the second label to the first position
        formSection.moveComponent(m_label2, 1);

        // Move the first label to the third position
        formSection.moveComponent(m_label1, 3);
    }

}
