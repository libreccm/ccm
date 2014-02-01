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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentTextField;
import org.apache.log4j.Logger;

/**
 * Test the PersistentTextField class with the test pattern defined in
 * PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentTextFieldTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class PersistentTextFieldTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentTextFieldTest.class.getName());

    // Properties of the text area
    private static String m_htmlName = "Test Text Field Name";
    private static String m_defaultValue = "Test text field Default Value";
    private static int m_size = 20;
    private static int m_maxLength = 50;

    /**
     * JUnit needs this constructor
     */
    public PersistentTextFieldTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentTextField factory =
            PersistentTextField.create(m_htmlName);

        factory.setDefaultValue(m_defaultValue);
        factory.setSize(m_size);
        factory.setMaxLength(m_maxLength);

        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {
        TextField textField = new TextField(m_htmlName);

        textField.setDefaultValue(m_defaultValue);
        textField.setSize(m_size);
        textField.setMaxLength(m_maxLength);

        return textField;
    }

    // We do not override the checkPersistenceAttributes() method since we keep
    // no persistence specific attributes for the TextField
}
