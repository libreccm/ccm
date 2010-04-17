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
package com.arsdigita.formbuilder.util;


import com.arsdigita.formbuilder.util.GlobalizationUtil ;

import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.FormData;


import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.parameters.PersistentParameterListener;


import java.util.Iterator;

// Classes needed when when retrieving a Component DataObject




import com.arsdigita.util.UncheckedWrapperException;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * A collection of utility methods used within the Form Builder service.
 * Some of these methods might be candidates for being moved to a generic ACS
 * utility class.
 *
 * @author Peter Marklund
 * @version $Id: FormBuilderUtil.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class FormBuilderUtil {

    private static final Logger s_log =
        Logger.getLogger(FormBuilderUtil.class);

    public static final String FORMBUILDER_XML_NS = 
        "http://www.arsdigita.com/formbuilder/1.0";
    public static final String FORMBUILDER_FORM_INFO = "formbuilder:formInfo";
    public static final String FORMBUILDER_PAGE_STATE = "formbuilder:pageState";
    public static final String FORM_ACTION = "formAction";

    private static FormBuilderConfig s_config;

    public static FormBuilderConfig getConfig() {
        if (s_config == null) {
            s_config = new FormBuilderConfig();
            s_config.load("ccm-core/formbuilder.properties");
        }
        return s_config;
    }


    public static void addTextFieldToForm(FormSection form, String name, String label) {
        form.add(new Label(label));
        form.add(new TextField(name));
    }

    public static void addTextAreaToForm(FormSection form, String name, String label) {
        form.add(new Label(label));
        form.add(new TextArea(name));
    }

    /*
    /**
     * Return true if testClass implements the interfaceClass interface, false otherwise.
    public static boolean classImplementsInterface(Class testClass, Class interfaceClass) {

        Class[] classInterfaces = testClass.getInterfaces();
        for (int i = 0; i < classInterfaces.length; ++i) {
            if (interfaceClass.getName().equals(classInterfaces[i].getName())) {
                return true;
            }
        }

        return false;
    }

    public static PersistentComponentFactory instantiateFactory(BigDecimal componentID) {

        Session session = SessionManager.getSession();

        String factoryClassName = null;

        // Retrieve the component DataObject
        OID oid = new OID("com.arsdigita.formbuilder.Component", componentID);
        DataObject componentObject = session.retrieve(oid);
        if (componentObject == null) {
            throw new RuntimeException("could not retrieve DataObject with id " + oid.toString());
        }

        // Get the class name of the PersistentComponentFactory from the DataObject
        factoryClassName = (String)componentObject.get("defaultDomainClass");

        return (PersistentComponentFactory)instantiateObjectOneArg(factoryClassName, componentID);
    }

    public static ACSObject instantiateObject(BigDecimal id) {
        s_log.info("instantiate object with id " + id);
        OID oid = new OID(ACSObject.BASE_DATA_OBJECT_TYPE, id);
        Session s = SessionManager.getSession();
        DataObject o = s.retrieve(oid);

        ACSObject obj =
            (ACSObject)FormBuilderUtil.instantiateObject((String)o.get(ACSObject.DEFAULT_DOMAIN_CLASS),
                                                         new Class[] { BigDecimal.class },
                                                         new Object[] { id });
        return obj;
    }
    */

    public static Object invokeMethod(java.lang.reflect.Method method, Object object, Object[] argumentList) {

        try {
            return method.invoke(object, argumentList);

        } catch (Exception e) {
            throw new UncheckedWrapperException(e);
        }

    }

    public static Object instantiateObjectOneArg(String className,
                                                 Object arg) {
        s_log.info("instantiate object class " + className + " arg class " + arg.getClass());
        Class argClass = arg.getClass();
        RuntimeException exn = null;
        while (argClass != null) {
            try {
                return instantiateObject(className,
                                         new Class[] {argClass},
                                         new Object[] {arg});
            } catch (RuntimeException e) {
                // failed, perhaps because there's not an exact
                // match for the argument type.  so try the
                // argument's superclass
                exn = e;
                argClass = argClass.getSuperclass();
            }
        }
        throw exn;
    }

    public static Object instantiateObject(String className) {
        return instantiateObject(className, new Class[] {}, new Object[] {});
    }

    public static Object instantiateObject(String className,
                                           Class[] argumentTypes,
                                           Object[] arguments) {
        s_log.debug("instantiate object class " + className + " args " + argumentTypes);
        Object object;
        Class classObject;
        try {
            classObject = Class.forName(className);

            java.lang.reflect.Constructor constructor =
                classObject.getConstructor(argumentTypes);

            object = constructor.newInstance(arguments);

        } catch (Exception e) {
            // The Exception e is a ClassNotFoundException an
            // InstantiationException or an IllegalAccessException
            // Cannot take action on those Exceptions
            throw new UncheckedWrapperException(e);
        }

        return object;
    }

    public static Class loadClass(String className) {

        Class returnClass = null;
        try {
            returnClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }

        return returnClass;
    }

    /**
     * Check if an integer argument is in range. The limits are inclusive so that an argument
     * on the limit is allowed
     */
    public static void assertArgumentInRange(int argument,
                                             int lowerLimit,
                                             int upperLimit) {

        boolean argumentIsValid = true;

        if (argument < lowerLimit) {
            argumentIsValid = false;
        }

        if (argument > upperLimit) {
            argumentIsValid = false;
        }

        if (!argumentIsValid) {
            throw new IllegalArgumentException("Integer argument with value " + argument +
                                               " is out of range. Should be between " +
                                               lowerLimit + " and " + upperLimit);
        }
    }

    public static boolean isSuccessfulSubmission(FormData formData) {

        return formData.isSubmission() && !formData.getAllErrors().hasNext();
    }

    /**
     * Returns true if the persistent widget has a NotEmptyValidationListener
     * added to it.
     */
    public static boolean isRequired(PersistentWidget widget) {

        // This is kind of ugly - check if the widget has a
        // NotEmptyValidationListener
        Iterator listenerIter = widget.getValidationListeners().iterator();
        while (listenerIter.hasNext()) {
            PersistentParameterListener listener =
                (PersistentParameterListener)listenerIter.next();

            if (listener.getClassName().
                equals("com.arsdigita.bebop.parameters.NotEmptyValidationListener")) {
                return true;
            }
        }

        return false;
    }

    public static void doLogDebug(Logger log, String message) {

        log.debug(message, new Exception());
    }
}
