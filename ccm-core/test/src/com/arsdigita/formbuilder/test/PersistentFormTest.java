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
import com.arsdigita.bebop.Form;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import org.apache.log4j.Logger;

/**
 * Test the PersistentForm class with the test pattern defined in
 * the PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentFormTest.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class PersistentFormTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentFormTest.class.getName());

    // Common attributes of the forms
    protected String m_htmlName = "test_form";
    protected String m_action = "test_action_url";

    // We reuse the test logic in the form section test through composition
    // and delegation
    private PersistentFormSectionTest m_formSectionTest =
        new PersistentFormSectionTest("Form Section Test");

    /**
     * JUnit needs this constructor
     */
    public PersistentFormTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentForm persistentForm = PersistentForm.create(m_htmlName);

        // Add all components that we add to the form section
        m_formSectionTest.setFormSectionData(persistentForm);

        // Add the form section
        persistentForm.addComponent
            (m_formSectionTest.createPrimaryPersistentFactory());

        // Set the action attribute
        persistentForm.setAction(m_action);

        return persistentForm;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        Form form = new Form(m_htmlName);

        m_formSectionTest.addComponentsToReference(form);

        // Add the form section
        form.add(m_formSectionTest.createPrimaryReferenceComponent());

        form.setAction(m_action);

        return form;
    }

    /**
     * Some components have attributes that the corresponding Bebop component
     * does not have. Those should be checked (after the component has been
     * retrieved from the database) in this method.
     */
    protected void checkPersistenceAttributes(PersistentComponent factory) {

        m_formSectionTest.checkPersistenceAttributes(factory);
    }
}
