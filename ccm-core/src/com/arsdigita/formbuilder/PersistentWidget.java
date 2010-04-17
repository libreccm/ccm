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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.parameters.PersistentParameterListener;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;


/**
 * This class is responsible for persisting attributes common to
 * Bebop Widgets. Sub classes of this abstract class manage
 * persistence of different Bebop Widgets (TextArea etc.).
 *
 * @author Peter Marklund
 * @version $Id: PersistentWidget.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public abstract class PersistentWidget extends PersistentComponent {

    private static final Logger s_log =
        Logger.getLogger(PersistentWidget.class.getName());

    private Class DEFAULT_VALUE_CLASS = String.class;

    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String REQUIRED = "widgetRequired";

    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Sub classes can create a new widget domain object with
     * this constructor.
     */
    public PersistentWidget(String objectType) {
        super(objectType);
    }

    public PersistentWidget(ObjectType type) {
        super(type);
    }

    public PersistentWidget(DataObject obj) {
        super(obj);
    }

    /**
     * Sub classes may use this constructor to retrieve an existing
     * Widget domain object with an id and object type.
     */
    public PersistentWidget(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    protected void setup(String parameterName) {
        setParameterName(parameterName);
    }

    /*
    protected void beforeDelete() {
        clearValidationListeners();
        super.beforeDelete();
    }
    */

    // *** Attribute Methods

    public void setParameterName(String parameterName) {
        set("parameterName", parameterName);
    }

    /**
     * Will return null if no value has been set.
     */
    public String getParameterName() {
        return (String)get("parameterName");
    }

    public void addValidationListener(PersistentParameterListener parameterListener) {
        add("listeners", parameterListener);
    }

    /**
     * Return a collection with all PersistentParameterListeners of this widget.
     */
    public Collection getValidationListeners() {

        ArrayList listenerList = new ArrayList();

        DataAssociationCursor listenerCursor =
            ((DataAssociation)get("listeners")).cursor();

        while(listenerCursor.next()) {

            DataObject listenerObject = listenerCursor.getDataObject();
            BigDecimal listenerID = (BigDecimal)listenerObject.get("id");
            String factoryClassName = (String)listenerObject.get("defaultDomainClass");

            PersistentParameterListener listenerFactory =
                (PersistentParameterListener)FormBuilderUtil.instantiateObjectOneArg(factoryClassName, listenerID);

            listenerList.add(listenerFactory);
        }

        listenerCursor.close();

        return listenerList;
    }

    /**
     * Removes all validation listeners associated with this widget
     */
    public void clearValidationListeners() {
        clear("listeners");
    }

    /**
     * The class name of the parameter model to be used for the widget
     */
    public void setParameterModel(String parameterModel) {
        set("parameterModel", parameterModel);
    }

    /**
     * Will return null if no value has been set.
     */
    public String getParameterModel() {
        return (String)get("parameterModel");
    }

    /**
     * You have to make sure that the defaulValue is an instance
     * of the Class returned by the method getValueClass() (for
     * example this is a String for TextAreas and a java.util.Date for
     * Dates).
     */
    public void setDefaultValue(Object defaultValue) {

        // Check that the object has the right type for this Widget
        if (!(getValueClass().isAssignableFrom(defaultValue.getClass()))) {

            throw new IllegalArgumentException("object " + defaultValue.toString() +
                                               " supplied to " + this.toString() +
                                               " setDefaultValue() must be an instanceof " + getValueClass().getName());
        }

        // We need to be able to convert the object to a String
        String stringValue = null;

        if (defaultValue instanceof String) {

            // A String - the easiest case
            stringValue = (String)defaultValue;

        } else if (defaultValue instanceof java.io.Serializable) {

            try {
                // Serialize the object
                java.io.ByteArrayOutputStream byteStream =
                    new java.io.ByteArrayOutputStream();

                java.io.ObjectOutputStream objectStream =
                    new java.io.ObjectOutputStream(byteStream);

                try {
                    objectStream.writeObject(defaultValue);

                    //                stringValue = new String(byteStream.toByteArray(), "ISO-8859-1");
                    stringValue = new String(new Base64().encode(byteStream.toByteArray()));


                    s_log.debug("setDefaultValue serializing object " + defaultValue.toString() +
                                " to " + stringValue);

                } finally {
                    try {
                        objectStream.close();
                    } catch(Exception e) {
                        s_log.error("Problem closing ObjectOutputStream.", e);
                    }
                }

            } catch (java.io.IOException e) {
                throw new UncheckedWrapperException(e);
            }

        } else {

            // We do not support objects that are not strings and
            // that are not serializable
            throw new IllegalArgumentException("object " + defaultValue.toString() +
                                               " does not implement Serializable which is required for a non-String default" +
                                               " value to be persisted by the Form Builder");
        }

        set(DEFAULT_VALUE, stringValue);
    }

    /**
     * Get the default value of the widget. Java serialization is used to persist objects
     * so that the default value object must implement Serializable. Will return null if
     * no value has been set.
     */
    public Object getDefaultValue() {

        // Get the serialized default value object
        Object defaultValue = get(DEFAULT_VALUE);

        if (defaultValue == null) {
            return null;
        }

        if (!(getValueClass().getName().equals("java.lang.String"))) {
            defaultValue = deserializeDefaultValue((String)defaultValue);
        }

        return defaultValue;
    }

    /**
     * Set this 
     */
    public void setRequired( boolean required ) {
        setComponentAttribute( REQUIRED, new Boolean( required ).toString() );
    }

    public boolean isRequired() {
        String required = getComponentAttribute( REQUIRED );
        return !Boolean.FALSE.toString().equals( required );
    }

    /**
     *  This provides a method to take a default value and deserialize
     *  it in to its original object.  This is useful if the defaultValue
     *  is actually something like a serialized java.util.Date
     */
    public static Object deserializeDefaultValue(String defaultValue) {
        Object actualDefault = null;
        try {
            s_log.debug("getDefaultValue de-serializing string " + (String)defaultValue);
            
            // This is a serialized object - resurrect it
            // from the String
            java.io.ByteArrayInputStream byteStream =
                new java.io.ByteArrayInputStream(new Base64().decode(defaultValue.getBytes()));
            
            //((String)defaultValue).getBytes("ISO-8859-1"));
            
            java.io.ObjectInputStream objectStream =
                new java.io.ObjectInputStream(byteStream);
            
            try {
                actualDefault = objectStream.readObject();
            } finally {
                try {
                    objectStream.close();
                } catch(IOException e) {
                    s_log.error("Problem closing ObjectInputStream.", e);
                }
            }
            
        } catch (Exception e) {
            // an IOException or a ClassNotFoundException
            throw new UncheckedWrapperException(e);
        }

        return actualDefault;
    }


    /**
     * Most Widgets have String as their value class which is what is
     * returned by this default implementation. The PersistentDate
     * class has a different implementation.
     */
    protected Class getValueClass() {

        return DEFAULT_VALUE_CLASS;
    }

    /**
     * Get the value of this widget in the current request. This is the
     * preferred method for retrieving this value. Override this method to
     * customise this behaviour.
     */
    public Object getValue(FormData data) {
        return data.get(getParameterName());
    }

    //*** Attribute metadata

    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList list = super.getAttributeMetaData();

        list.add(new AttributeMetaData("parameterName", "HTML parameter name", true)); // required
        list.add(new AttributeMetaData(DEFAULT_VALUE, "Default value"));

        return list;
    }

    //*** Internal Helper Methods
    protected ParameterModel instantiateParameterModel() {

        if (getParameterModel() == null) {
            return null;
        } else {
            return (ParameterModel)
                FormBuilderUtil.instantiateObjectOneArg(getParameterModel(), getParameterName());
        }
    }

    protected void copyValuesToWidget(Widget widget) {

        // Set the ParameterModel if any has been specified
        if (getParameterModel() != null) {

            ParameterModel model = instantiateParameterModel();
            widget.setParameterModel(model);
        }

        // Set the default value if any has been set
        if (get(DEFAULT_VALUE) != null) {
            widget.setDefaultValue(getDefaultValue());
        }

        // Add any validation listeners
        addValidationListeners(widget);
    }

    private void addValidationListeners(Widget widget) {

        Iterator listenerIter = getValidationListeners().iterator();
        while (listenerIter.hasNext()) {
            PersistentParameterListener persistentListener =
                (PersistentParameterListener)listenerIter.next();

            widget.addValidationListener(persistentListener.createListener());
        }
    }
}
