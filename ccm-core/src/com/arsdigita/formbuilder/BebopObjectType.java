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
//import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.util.ArrayList;
import java.util.Collection;

import java.math.BigDecimal;

/**
 * This class defines a persistent object, whose
 * subclasses will have meta objects associated
 * with them. It is essentially just here to facilitate
 * the retrieval of groups of meta object
 *
 * There are two object types which are defined,
 * {@link com.arsdigita.formbuilder.PersistentComponent}
 * & {@link com.arsdigita.formbuilder.PersistentProcessListener}.
 */
public class BebopObjectType extends ACSObject {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.ObjectType";

    public static final String TYPE_ID = "id";
    public static final String CLASS_NAME = "className";
    public static final String APP_NAME = "appName";

    public BebopObjectType() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public BebopObjectType(String type) {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public BebopObjectType(com.arsdigita.persistence.metadata.ObjectType type) {
        super(type);
    }

    /**
     * Constructor. Instantiates the object type
     * from an existing data object.
     *
     * @param obj the data object
     */
    public BebopObjectType(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. Instantiates the object type
     * from the specified id
     *
     * @param id the id of the object type
     */
    public BebopObjectType(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. To be used by subclasses for
     * instantiating an object type from a given
     * oid.
     *
     * @param oid the oid of the object type
     */
    public BebopObjectType(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    public static BebopObjectType create(String app,
                                         Class widget) {
        BebopObjectType type = new BebopObjectType();
        type.setAppName(app);
        type.setType(widget);
        return type;
    }

    public static BebopObjectType create(String app,
                                         String widgetClass) {
        BebopObjectType type = new BebopObjectType();
        type.setAppName(app);
        type.setTypeName(widgetClass);
        return type;
    }

    /**
     * Retrieves object type matching the specified
     * class.
     *
     * @param type the class whose object type to retrieve
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     * if there is no object type for the specified class
     */
    // XXX This probably not the most appropriate exception to be throwing
    public static BebopObjectType findByClass(String name,
                                              Class type)
        throws DataObjectNotFoundException {

        return findByClassName(name, type.getName());
    }

    /**
     * Retrieves object type matching the specified
     * class name.
     *
     * @param type the class namewhose object type to retrieve
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     * if there is no object type for the specified class name
     */
    public static BebopObjectType findByClassName(String app,
                                                  String name)
        throws DataObjectNotFoundException {

        Session ssn = SessionManager.getSession();
        DataCollection types = ssn.retrieve(BASE_DATA_OBJECT_TYPE);
        types.addEqualsFilter(CLASS_NAME, name);
        types.addEqualsFilter(APP_NAME, app);

        if (types.next()) {
            DataObject obj = types.getDataObject();
            BebopObjectType t = new BebopObjectType(obj);
            types.close();
            return t;
        } else {
            throw new DataObjectNotFoundException("no such object type " + name);
        }
    }

    /**
     * Retrieves a collection of all the object types
     */
    public static Collection getObjectTypes() {
        Session ssn = SessionManager.getSession();
        DataCollection forms = ssn.retrieve(BASE_DATA_OBJECT_TYPE);

        ArrayList l = new ArrayList();
        while (forms.next()) {
            DataObject obj = forms.getDataObject();
            BebopObjectType t = new BebopObjectType(obj);
            l.add(t);
        }
        return l;
    }


    /**
     * Returns the Class object of the persistent
     * object this object type represents
     */
    public Class getType()
        throws ClassNotFoundException {
        return Class.forName((String)get(CLASS_NAME));
    }

    /**
     * Returns the class name of the persistent
     * object this object type represents
     */
    public String getTypeName() {
        return (String)get(CLASS_NAME);
    }

    public void setType(Class type) {
        set(CLASS_NAME, type.getName());
    }

    public void setTypeName(String typeName) {
        set(CLASS_NAME, typeName);
    }

    public String getAppName() {
        return (String)get(APP_NAME);
    }

    public void setAppName(String name) {
        set(APP_NAME, name);
    }
}
