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
package com.arsdigita.formbuilder;


// Bebop components that we generate here
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
// We use different parameter models depending on data type
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.bebop.event.ParameterListener;

import com.arsdigita.bebop.parameters.NotEmptyValidationListener;

// We need reflection sometimes
import java.lang.reflect.Method;

import java.util.List;
import java.util.Iterator;

// We use a process listener that will invoke any set methods of the
// attribute provider object
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormData;

// For loading class etc.
import com.arsdigita.formbuilder.util.FormBuilderUtil;

// Domain objects get special treatment in that they are saved

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class can generate a Bebop Form given a PersistentComponent.
 * The Form Builder uses
 * this class for its own admin UI to generate forms for the various persistent
 * components. It is unclear how useful this class will be applicable outside
 * the Form Builder since its functionality is still restricted.
 *
 * @author Peter Marklund
 * @version $Id: FormSectionGenerator.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public class FormSectionGenerator {

    public static final String versionId = "$Id: FormSectionGenerator.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(FormSectionGenerator.class.getName());

    private PersistentComponent m_component;

    private boolean m_isAdd = true;

    public FormSectionGenerator(PersistentComponent component) {

        m_component = component;
    }

    public FormSection generateFormSection() {

        return generateFormSection(true, true);
    }

    /**
     * Generate a form section from the contained object with attribute
     * metadata
     *
     * @param addProcessListener A process listener will be added if this is true.
     *                           The process listener will attempt to use set-methods
     *                           to set all attributes (this is the listener returned by
     *                           getSetProcessListener()). If this form section is for a
     *                           domain object, the domain object will be saved after the
     *                           set-methods have been invoked.
     */
    public FormSection generateFormSection(boolean addProcessListener, boolean isAdd) {

        FormSection formSection = new FormSection();

        m_isAdd = isAdd;

        // Loop over the attributes and add appropriate widgets to the Form
        AttributeMetaDataList attributeList = m_component.getAttributeMetaData();
        attributeList.setIteratorAtStart();
        while(attributeList.hasNext()) {

            // Get the attribute
            AttributeMetaData attribute = attributeList.next();

            // Get the attribute properties
            String parameterName = attribute.getParameterName();
            String label = attribute.getLabel();
            boolean isRequired = attribute.isRequired();
            AttributeType attributeType = attribute.getAttributeType();

            // Create and add the label component
            String labelText = label == null ? parameterName : label;
            // Indicate if the attribute is required in the label
            if (isRequired) {
                labelText = labelText + " (required) ";
            }
            formSection.add(new Label(labelText));

            // Add an appropriate component
            // Use provided parameter model or look at the set method to figure one
            // out
            ParameterModel parameterModel  = attribute.getParameterModel();
            if (parameterModel == null) {
                parameterModel = getModelFromReflection(parameterName);
            }
            TextField textField = new TextField(parameterModel);

            if (isRequired) {
                textField.addValidationListener(new NotEmptyValidationListener());
            }

            // Add other validation listeners
            if (attributeType != null) {
                addValidationListeners(textField, attributeType.getValidationListeners());
            }

            // Set the default value
            setDefaultValue(textField, parameterName);

            formSection.add(textField);

        }

        if (addProcessListener) {

            // Add a process listener that sets the attributes
            formSection.addProcessListener(getSetProcessListener());
            formSection.addProcessListener(getDomainObjectSaveListener());
        }

        return formSection;
    }

    /**
     * Returns a process listener that can be used to set the attributes of
     * the object for which the form section is generated. Note that if this
     * is a domain object this process listener will not save that domain object.
     */
    public FormProcessListener getSetProcessListener() {

        return new FormProcessListener() {

                public void process(FormSectionEvent event) {

                    FormData formData = event.getFormData();

                    // Iterate over the attributes and set them
                    AttributeMetaDataList attributeList = m_component.getAttributeMetaData();
                    attributeList.setIteratorAtStart();
                    while(attributeList.hasNext()) {

                        AttributeMetaData attribute = attributeList.next();

                        String parameterName = attribute.getParameterName();

                        Object parameterValue = formData.get(parameterName);

                        // Only invoke set method if a value was supplied
                        if (parameterValue != null && !parameterValue.toString().trim().equals("")) {

                            Method setMethod = getParameterMethod(parameterName, true);

                            if (setMethod != null) {

                                try {
                                    FormBuilderUtil.invokeMethod(setMethod,
                                                                 m_component,
                                                                 new Object[] {parameterValue});

                                } catch (IllegalArgumentException e) {
                                    // This usually happens if the class of the provided
                                    // default value of a PersistentWidget does not match.
                                    // In this case we don't set a default value
                                }
                            }
                        }
                    }
                }
            };
    }

    //*** Internal Helper Methods

    private void setDefaultValue(Widget widget, String parameterName) {

        Method getMethod = getParameterMethod(parameterName, false);

        Object defaultValue =  FormBuilderUtil.invokeMethod(getMethod,
                                                            m_component,
                                                            new Object[] {});

        if (defaultValue != null) {
            widget.setDefaultValue(defaultValue);
        }
    }

    private ParameterModel getModelFromReflection(String parameterName) {

        // Get the Method
        Method parameterMethod = getParameterMethod(parameterName, true);

        // Get the argument types
        Class[] parameterTypes = parameterMethod.getParameterTypes();

        // Get the name of the first type
        String typeName = parameterTypes[0].getName();

        // Decide on a ParameterModel based on the type name
        if (typeName.equals("int")) {
            return new IntegerParameter(parameterName);
        } else if (typeName.equals("java.lang.String") || typeName.equals("java.lang.Object")) {
            return new StringParameter(parameterName);
        } else {
            throw new IllegalStateException("Trying to generate a Form with an attribute of type " +
                                            typeName + ". This type is currently not supported");
        }
    }

    /**
     * May return null
     */
    private Method getParameterMethod(String parameterName, boolean isSet) {

        Method returnMethod = null;

        // Simply loop over the methods and look for a matching set method name
        Method[] methods = m_component.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {

            Method method = methods[i];
            String methodPrefix = "";
            if (isSet) {
                methodPrefix = "set";
            } else {
                methodPrefix = "get";
            }
            String setMethodName = methodPrefix + parameterName.substring(0,1).toUpperCase() + parameterName.substring(1);

            if (method.getName().equals(setMethodName)) {
                returnMethod = method;
                break;
            }
        }

        return returnMethod;
    }

    private FormProcessListener getDomainObjectSaveListener() {

        return new FormProcessListener() {
            public void process(FormSectionEvent event) {
                m_component.save();
            }
        };
    }

    private void addValidationListeners(Widget widget, List validationListeners) {

        Iterator validationIter = validationListeners.iterator();
        while (validationIter.hasNext()) {

            widget.addValidationListener((ParameterListener)validationIter.next());
        }
    }
}
