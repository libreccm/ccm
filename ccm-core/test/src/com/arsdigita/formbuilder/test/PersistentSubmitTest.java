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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentSubmit;

/**
 * Test the PersistentSubmit class with the test pattern defined in the
 * PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentSubmitTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class PersistentSubmitTest extends PersistentComponentTestCase {


    // Properties of the submit
    private String m_htmlName = "submit_test_name";
    private String m_label = "Submit test label";

    /**
     * JUnit needs this constructor
     */
    public PersistentSubmitTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentSubmit factory =
            PersistentSubmit.create(m_htmlName);

        factory.setDefaultValue(m_label);

        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        Submit submit = new Submit(m_htmlName);

        submit.setButtonLabel(m_label);

        return submit;
    }

    // We do not override the checkPersistenceAttributes() method since we keep
    // no persistence specific attributes for the Submit
}
