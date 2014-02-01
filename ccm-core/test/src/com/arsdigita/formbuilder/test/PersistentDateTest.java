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
import com.arsdigita.bebop.form.Date;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentDate;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Test the PersistentDate class with the test pattern defined in
 * PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentDateTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class PersistentDateTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentDateTest.class.getName());

    // Properties of the text area
    private static String m_htmlName = "Test Date Name";
    private static java.util.Date m_defaultValue = new java.util.Date();

    /**
     * JUnit needs this constructor
     */
    public PersistentDateTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentDate factory =
            PersistentDate.create(m_htmlName);

        // We cannot set a String default value
        try {

            factory.setDefaultValue("test string default value");

            fail("PersistentDate.setDefaultValue() should not accept a String");

        } catch (IllegalArgumentException e) {
            // This is what we wanted
        }

        factory.setDefaultValue(m_defaultValue);

        factory.setYearRange(1974, 2001);

        return factory;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {
        Date date = new Date(m_htmlName);

        date.setDefaultValue(m_defaultValue);

        date.setYearRange(1974, 2001);

        return date;
    }

    /**
     * Returns a map with use-case names as keys and PersistentComponent:s as values.
     * Each factory is populated with different
     * data that reflects a secondary use-case. The components created by these factories
     * will compared with the reference component in the same position in the list returned
     * by createSecondaryReferenceComponents().
     */
    protected Map createSecondaryPersistentFactories() {

        // Intentionally empty for optional implementation by sub classes
        HashMap useCaseMap = new HashMap();

        // Setting the DateParameter class
        // I should ideally be using a sub class of DateParameter here
        PersistentDate date = (PersistentDate)createPrimaryPersistentFactory();
        date.setDateParameter("com.arsdigita.bebop.parameters.DateParameter");
        useCaseMap.put("custom_date_parameter", date);

        return new HashMap();
    }

    /**
     * Returns a map with use-case names as keys and reference components as values.
     * Each reference component is populated with different
     * data that reflects a secondary use-case.
     */
    protected Map createSecondaryReferenceComponents() {

        // Intentionally empty for optional implementation by sub classes
        HashMap useCaseMap = new HashMap();

        // Setting the DateParameter class
        useCaseMap.put("custom_date_parameter", createPrimaryReferenceComponent());

        return new HashMap();
    }

    // We do not override the checkPersistenceAttributes() method since we keep
    // no persistence specific attributes for the Date
}
