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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentTextArea;
import org.apache.log4j.Logger;

/**
 * Test the PersistentTextArea class with the test pattern defined in
 * PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentTextAreaTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class PersistentTextAreaTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentTextAreaTest.class.getName());

    // Properties of the text area
    private static String m_htmlName = "Test Text Area Name";
    private static int m_nColumns = 30;
    private static int m_nRows = 10;
    private static String m_defaultValue = "Test Text Area Default Value";

    /**
     * JUnit needs this constructor
     */
    public PersistentTextAreaTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentTextArea factory =
            PersistentTextArea.create(m_htmlName);

        factory.setDefaultValue(m_defaultValue);
        factory.setCols(m_nColumns);
        factory.setRows(m_nRows);

        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {
        TextArea textArea = new TextArea(m_htmlName);

        textArea.setDefaultValue(m_defaultValue);
        textArea.setCols(m_nColumns);
        textArea.setRows(m_nRows);

        return textArea;
    }

    // We do not override the checkPersistenceAttributes() method since we keep
    // no persistence specific attributes for the TextArea
}
