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
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.dispatcher.TestUtils;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.HttpServletDummyRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This class contains the test pattern used to test all persistent
 * factory objects of the Form Builder. The test pattern
 * is to populate the persistent factory and the reference Bebop component
 * with the same data. Then we create a Bebop Component with the factory
 * and assert that its XML be identical with that of the reference Component.
 * Then the factory is saved and resurrected and the XML comparison is repeated.
 *
 * @author Peter Marklund
 * @version $Id: PersistentComponentTestCase.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public abstract class PersistentComponentTestCase extends BaseTestCase {


    private final static Logger s_log =
        Logger.getLogger(PersistentComponentTestCase.class.getName());

    /**
     * JUnit requires this constructor
     */
    public PersistentComponentTestCase(String name) {
        super(name);
        TestUtils.setRequest(new HttpServletDummyRequest());
    }

    /**
     * Does all testing of the persistent component. Currently only the
     * XML footprint of the components is tested.
     */
    public void testComponent() {

        // Workaround for "(root cause: Request context does not subclass KernelRequestContext:
        // com.arsdigita.formbuilder.test.DummyRequestContext)"
        com.arsdigita.util.URLRewriter.clearParameterProviders();

        logDebug("test case started");

        // Start with the primary use-case

        PersistentComponent pcf = createPrimaryPersistentFactory();

        if ( pcf == null ) {
            return;
        }

        logDebug("primary use-case started");
        runTest(createPrimaryPersistentFactory(),
                createPrimaryReferenceComponent(),
                createPrimaryReferenceComponent());
        logDebug("primary use-csae ended successfully");

        // Run all secondary use-cases
        Map persistentMap = createSecondaryPersistentFactories();
        Map referenceMapBefore = createSecondaryReferenceComponents();
        Map referenceMapAfter = createSecondaryReferenceComponents();

        Iterator persistentIter = persistentMap.keySet().iterator();
        while(persistentIter.hasNext()) {

            String testKey = (String)persistentIter.next();

            logDebug("secondary use-case " + testKey + " started");
            runTest((PersistentComponent)persistentMap.get(testKey),
                    (Component)referenceMapBefore.get(testKey),
                    (Component)referenceMapAfter.get(testKey));

            logDebug("secondary use-case " + testKey + " ended successfully");
        }

        logDebug("test case ended successfully");
    }

    /**
     * We prefix with the name of the sub class to be able to see which test case
     * is being run in the log
     */
    private void logDebug(String message) {
        s_log.debug(this.getClass().getName() + " " + message);
    }

    /**
     * We are comparing the
     * XML footprint of the persistent component with the corresponding
     * standard Bebop component before and after persising to the database
     * and assert that they be identical. The reason we need to supply two
     * reference components is that when generating XML the reference component
     * gets modified so that we need to use a fresh one for the second
     * comparison. We are also comparing the default values after resurrection.
     * I use serialization for default values.
     */
    private void runTest(PersistentComponent persistentFactory,
                         Component referenceComponentBefore,
                         Component referenceComponentAfter) {

        if ( persistentFactory == null ||
             referenceComponentBefore == null ||
             referenceComponentAfter == null ) {
            return;
        }

        // Compare XML before persisting
        assertXMLEqual(persistentFactory.createComponent(),
                       referenceComponentBefore,
                       " should have identical XML before persisting");

        // Persist and retrieve new instance
        persistentFactory.save();
        BigDecimal id = persistentFactory.getID();
        persistentFactory =
            (PersistentComponent)
            FormBuilderUtil.instantiateObjectOneArg(persistentFactory.getClass().getName(),
                                                    id);

        // Compare XML of the resurrected instance
        assertXMLEqual(persistentFactory.createComponent(),
                       referenceComponentAfter,
                       " should have identical XML after persisting");

        // Compare the default values if this is a Widget
        // The reason I am not using Widget.getDefaultValue() is that it returns
        // a String
        // I am excluding the OptionGroups. Option does not have an equals method
        // If the Options are not equal this would be caught in the XML comparison
        Component persistentComponent = persistentFactory.createComponent();
        if (persistentComponent instanceof com.arsdigita.bebop.form.Widget
            && !(persistentComponent instanceof com.arsdigita.bebop.form.OptionGroup)) {

            Object persistentDefaultValue =
                ((Widget)persistentComponent).getParameterModel().getDefaultValue();

            if (persistentDefaultValue != null) {
                Object referenceDefaultValue =
                    ((Widget)referenceComponentAfter).getParameterModel().getDefaultValue();

                logDebug("comparing default values of " + persistentDefaultValue.toString() +
                         " and " + referenceDefaultValue.toString());

                assertTrue(persistentDefaultValue.equals(referenceDefaultValue));
            }
        }
        // Check that persistent attributes are intact (not in XML)
        checkPersistenceAttributes(persistentFactory);
    }

    /**
     * Returns a PersistentComponent setup in a standard way reflecting the
     * primary use-case. Every test case needs to at least include this use-case.
     * The component created by this factory will be compared with the factory returned
     * by createPrimaryReferenceComponent()
     */
    protected abstract PersistentComponent createPrimaryPersistentFactory();

    /**
     * This method creates a new reference component and populates setup in a standard
     * way reflecting the primary use-case. Every test case needs to at least include
     * this use-case.
     */
    protected abstract Component createPrimaryReferenceComponent();

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

        return useCaseMap;
    }

    /**
     * Returns a map with use-case names as keys and reference components as values.
     * Each reference component is populated with different
     * data that reflects a secondary use-case.
     */
    protected Map createSecondaryReferenceComponents() {

        // Intentionally empty for optional implementation by sub classes
        HashMap useCaseMap = new HashMap();

        return useCaseMap;
    }

    /**
     * Some components have attributes that the corresponding Bebop component
     * does not have. Those should be checked (after the component has been
     * retrieved from the database) in this method.
     */
    protected void checkPersistenceAttributes(PersistentComponent factory) {

        // Intentionally empty to be optionally implemented by sub classes
    }

    /**
     * Assert that the persistent component and the reference component have
     * identical XML footprint.
     */
    public static void assertXMLEqual(Component persistentComponent,
                                      Component referenceComponent,
                                      String message) {

        junit.framework.Assert.assertTrue(persistentComponent.toString() + " object and " +
                                          "reference object " + referenceComponent.toString() +
                                          message,
                                          XMLComparator.haveEqualXML(referenceComponent, persistentComponent));
    }
}
