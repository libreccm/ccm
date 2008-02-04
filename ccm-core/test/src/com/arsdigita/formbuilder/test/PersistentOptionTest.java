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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentOption;

/**
 * Test the PersistentOption class with the test pattern defined in the
 * PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentOptionTest.java 741 2005-09-02 10:21:19Z sskracic $
 *
 */
public class PersistentOptionTest extends PersistentComponentTestCase {

    public static final String versionId = "$Id: PersistentOptionTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Properties of the Option
    private String m_parameterValue = "test_option_name";
    private String m_label = "test option label";

    /**
     * JUnit needs this constructor
     */
    public PersistentOptionTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentOption factory =
            PersistentOption.create(m_parameterValue, m_label);

        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        Option option = new Option(m_parameterValue, m_label);

        return option;
    }

    // We do not override the checkPersistenceAttributes() method since we keep
    // no persistence specific attributes for the Option
}
