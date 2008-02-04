/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;


import java.math.BigDecimal;

/**
 *
 * The MetaObject class maintains the meta-information
 * required by the formbuilder UI for creating and
 * editing the attributes of persistent objects.
 *
 */
public class MetaObject extends ACSObject {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.MetaObject";

    public static final String TYPE_ID = "typeId";
    public static final String PRETTY_NAME = "prettyName";
    public static final String PRETTY_PLURAL = "prettyPlural";
    public static final String CLASS_NAME = "className";
    public static final String PROPERTIES_FORM = "propertiesForm";

    /**
     * Default constructor. This creates a new meta object.
     */
    public MetaObject() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor used by subclasses when creating a new
     * meta object.
     *
     * @param typeName the base data object type
     */
    public MetaObject(String typeName) {
        super(typeName);
    }

    /**
     * Constructor used by subclasses when creating a new
     * meta object.
     *
     * @param type the base data object type
     */
    public MetaObject(com.arsdigita.persistence.metadata.ObjectType type) {
        super(type);
    }

    /**
     * Constructor. Used to instantiate a meta object
     * from a previously retrieved data object.
     *
     * @param obj the data object
     */
    public MetaObject(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. Used to retrieve a meta object
     * from the data base.
     *
     * @param id the id of the object to retrieve
     */
    public MetaObject(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Used by subclasses to retrieve
     * a meta object with a different base data
     * object type.
     *
     * @param id the oid of the object to retrieve
     */
    public MetaObject(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    /**
     * Creates a new meta object, initialising all
     * the default attributes
     *
     * @param type the <code>ObjectType</code> related to the meta object
     * @param prettyName the pretty name of the object
     * @param prettyPlural the plural of the pretty name
     * @param className the fully qualified java class name of the object
     * whose meta information is being maintained
     * @param propertiesForm the name of a subclass of
     * {@link com.arsdigita.formbuilder.ui.PropertiesForm} used to create
     * and edit the object.
     */
    public static MetaObject create(BebopObjectType type,
                                    String prettyName,
                                    String prettyPlural,
                                    String className,
                                    String propertiesForm) {
        MetaObject o = new MetaObject();
        o.setup(type, prettyName, prettyPlural, className, propertiesForm);
        return o;
    }

    /**
     * This method is intended to be called by static
     * <code>create</code> methods to setup any required
     * attributes when creating a new meta object.
     *
     * @param type the <code>BebopObjectType</code> related to the meta object
     * @param prettyName the pretty name of the object
     * @param prettyPlural the plural of the pretty name
     * @param className the fully qualified java class name of the object
     * whose meta information is being maintained
     * @param propertiesForm the name of a subclass of
     * {@link com.arsdigita.formbuilder.ui.PropertiesForm} used to create
     * and edit the object.
     */
    protected void setup(BebopObjectType type,
                         String prettyName,
                         String prettyPlural,
                         String className,
                         String propertiesForm) {
        set(TYPE_ID, type.getID());
        set(PRETTY_NAME, prettyName);
        set(PRETTY_PLURAL, prettyPlural);
        set(CLASS_NAME, className);
        set(PROPERTIES_FORM, propertiesForm);
    }

    public static MetaObject retrieve(DataObject obj) {
        return new MetaObject(obj);
    }

    /**
     * Returns the meta object associated with a particular
     * class.
     *
     * @throws com.arsdigita.domainDataObjectNotFoundException if no meta object
     * could be found for this specified class
     * @param name the class to find the meta object for
     */
    public static MetaObject findByClass(BebopObjectType type,
                                         Class name)
        throws DataObjectNotFoundException {

        return findByClassName(type, name.getName());
    }

    /**
     * Returns the meta object associated with a particular
     * class.
     *
     * @throws com.arsdigita.domainDataObjectNotFoundException if no meta object
     * could be found for this specified class
     * @param name the name of the class to find the meta object for
     */
    public static MetaObject findByClassName(BebopObjectType type,
                                             String name)
        throws DataObjectNotFoundException {

        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        types.addEqualsFilter(TYPE_ID, type.getID());
        types.addEqualsFilter(CLASS_NAME, name);

        if (types.next()) {
            DataObject obj = types.getDataObject();
            MetaObject t = new MetaObject(obj);
            types.close();
            return t;
        } else {
            throw new DataObjectNotFoundException("no such class '" + name + "' registered");
        }
    }

    /**
     * Retrieves a collection of all the meta objects
     * related to a particular object type
     *
     * @param className the name of the object type to
     * retrieve objects for.
     * @throws com.arsdigita.domain.DataObjectNotFoundException if there
     * is no object type matching the className parameter
     */
    public static MetaObjectCollection getWidgets(String app,
                                                  String className)
        throws DataObjectNotFoundException {

        return getWidgets(BebopObjectType.findByClassName(app, className));
    }

    /**
     * Retrieves a collection of all the meta objects
     * related to a particular object type
     *
     * @param className the class of the object type to
     * retrieve objects for.
     * @throws com.arsdigita.domain.DataObjectNotFoundException if there
     * is no object type matching the class parameter
     */
    public static MetaObjectCollection getWidgets(String app,
                                                  Class type)
        throws DataObjectNotFoundException {

        return getWidgets(BebopObjectType.findByClass(app, type));
    }


    /**
     * Retrieves a collection of all the meta objects
     * related to a particular object type
     *
     * @param type the object type to
     * retrieve objects for.
     */
    public static MetaObjectCollection getWidgets(BebopObjectType type) {
        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        types.addEqualsFilter(TYPE_ID, type.getID());

        return new MetaObjectCollection(types);
    }

    /**
     * Sets the object type for the meta object
     *
     * @param type the new object type
     */
    public void setType(BebopObjectType type) {
        set(TYPE_ID, type.getID());
    }

    /**
     * Retrieves teh object type for the meta object
     *
     * @throws com.arsdigita.domain.DataObjectNotFoundException if the
     * object type could not be retrieved
     */
    public BebopObjectType getType()
        throws DataObjectNotFoundException {
        return new BebopObjectType((BigDecimal)get(TYPE_ID));
    }


    /**
     * Sets the pretty name for the meta object
     *
     * @param name the new pretty name
     */
    public void setPrettyName(String name) {
        set(PRETTY_NAME, name);
    }

    /**
     * Gets the pretty name for the meta object
     *
     * @return the pretty name
     */
    public String getPrettyName() {
        return (String)get(PRETTY_NAME);
    }

    /**
     * Sets the pretty plural name for the meta object
     *
     * @param name the new pretyy plural name
     */
    public void setPrettyPlural(String name) {
        set(PRETTY_PLURAL, name);
    }

    /**
     * Gets the pretty plural name for the meta object
     */
    public String getPrettyPlural() {
        return (String)get(PRETTY_PLURAL);
    }

    /**
     * Sets the widget class that this meta object
     * represents
     *
     * @param type the class of the widget
     */
    public void setWidgetClass(Class type) {
        set(CLASS_NAME, type.getName());
    }

    /**
     * Gets the class represented by this meta
     * object
     */
    public Class getWidgetClass()
        throws ClassNotFoundException {
        return Class.forName((String)get(CLASS_NAME));
    }

    /**
     * Sets the widget class name for the meta object
     *
     * @param name the name of the widget class
     */
    public void setWidgetClassName(String name) {
        set(CLASS_NAME, name);
    }

    /**
     * Gets the widget class name for the meta object
     */
    public String getWidgetClassName() {
        return (String)get(CLASS_NAME);
    }

    /**
     * Sets the class used to create and edit instances
     * of the widget represented by this meta object.
     * The class should be a subclass of
     * {@link com.arsdigita.formbuilder.ui.PropertiesEditor}
     * or
     * {@link com.arsdigita.formbuilder.ui.PropertiesForm}
     *
     * @param type the class for the form
     */
    public void setPropertiesForm(Class type) {
        set(PROPERTIES_FORM, type.getName());
    }

    /**
     * Gets the class used to create and edit instances
     * of the widget represented by this meta object.
     */
    public Class getPropertiesForm()
        throws ClassNotFoundException {
        return Class.forName((String)get(PROPERTIES_FORM));
    }

    /**
     * Sets the class name used to create and edit instances
     * of the widget represented by this meta object.
     * The class should be a subclass of
     * {@link com.arsdigita.formbuilder.ui.PropertiesEditor}
     * or
     * {@link com.arsdigita.formbuilder.ui.PropertiesForm}
     *
     * @param name the fully qualified class name
     */
    public void setPropertiesFormName(String name) {
        set(PROPERTIES_FORM, name);
    }

    /**
     * Gets the class used to create and edit instances
     * of the widget represented by this meta object.
     */
    public String getPropertiesFormName() {
        return (String)get(PROPERTIES_FORM);
    }
}
