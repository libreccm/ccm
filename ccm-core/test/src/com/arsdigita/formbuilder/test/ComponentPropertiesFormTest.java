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

import com.arsdigita.util.UncheckedWrapperException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 * Test case for the ComponentPropertiesForm class.
 *
 * @author Peter Marklund
 * @version $Id: ComponentPropertiesFormTest.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class ComponentPropertiesFormTest
    extends com.arsdigita.tools.junit.framework.BaseTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(ComponentPropertiesFormTest.class.getName());

    /**
     * JUnit needs this constructor
     */
    public ComponentPropertiesFormTest(String name) {
        super(name);
    }

    public void testComponentPropertiesForm() {

        // Workaround for "(root cause: Request context does not subclass KernelRequestContext:
        // com.arsdigita.formbuilder.test.DummyRequestContext)"
        com.arsdigita.util.URLRewriter.clearParameterProviders();

        // Construct a new instance
        try {

            // Assertions:
            // There is a default constructor
            // The object implements AttributeMetaDataProvider
            // The object is either a DomainObject or implements FormProcessListener

            // Get the class
            String className = "com.arsdigita.formbuilder.PersistentTextArea";
            Class classObject = Class.forName(className);

            // Create a new instance with the default constructor
            Object objectInstance = classObject.newInstance();

            // Properties that will be extracted from the FormData
            String attributeName1 = "parameterName";
            String attributeValue1 = "parameterName value";
            String attributeName2 = "cols";
            String attributeValue2 = "30";

            // Set the attributes
            invokeSetMethod(objectInstance, attributeName1, attributeValue1);
            invokeSetMethod(objectInstance, attributeName2, attributeValue2);

            // Save the domain object
            Method saveMethod = classObject.getMethod("save", new Class[] {});
            saveMethod.invoke(objectInstance, new Object[] {});

        } catch (Exception e) {

            throw new UncheckedWrapperException(e);
        }

    }

    private void invokeSetMethod(Object object, String attributeName, String attributeValue)
        throws IllegalAccessException, java.lang.reflect.InvocationTargetException {

        Object valueObject = attributeValue;

        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {

            Method method = methods[i];

            // Upper case the first letter of the attribute name
            String setMethodName = "set" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);

            if (method.getName().equals(setMethodName)) {

                Class[] parameterTypes = method.getParameterTypes();

                s_log.debug("method name " + method.getName() + " parameterType " + parameterTypes[0].getName());

                // Unfortunately the reflection API doesn't do data type conversion
                // so we need to handle this ourselves
                String typeName = parameterTypes[0].getName();
                if (typeName.equals("int")) {
                    valueObject = new Integer(attributeValue);
                }

                method.invoke(object, new Object[] {valueObject});
            }
        }
    }
}
