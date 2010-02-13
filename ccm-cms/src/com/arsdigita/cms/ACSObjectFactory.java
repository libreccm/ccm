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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainService;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides static methods to instantiate proper subclasses of {@link
 * com.arsdigita.kernel.ACSObject} domain objects, based on the object
 * type and the Java class name.
 * @see com.arsdigita.kernel.ACSObject
 *
 * @version $Id: ACSObjectFactory.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ACSObjectFactory extends DomainService {

    private static Logger s_log =
        Logger.getLogger(ACSObjectFactory.class);

    private static Class[] s_args = new Class[]{OID.class};
    private static Class[] s_newArgs = new Class[]{String.class};
    private static Class[] s_dataArgs = new Class[]{DataObject.class};

    /**
     * Load a proper subclass of the {@link ACSObject} domain object
     * from the database
     *
     * @param javaClass The Java {@link Class} to be instantiated
     * @param objectType the object type of the object to instantiated; should
     *   extend the <code>ACSObject</code>
     * @param id the primary key for the new object
     * @return the loaded object, or null on failure. It is safe to cast
     *   the return value to the <code>javaClass</code>
     */
    public static ACSObject loadACSObject(final Class javaClass,
                                          final String objectType,
                                          final BigDecimal id) {
        try {
            Constructor constr = javaClass.getConstructor(s_args);
            OID oid = new OID(objectType, id);
            return (ACSObject)constr.newInstance(new OID[]{oid});
        } catch (NoSuchMethodException nsme) {
            throw new UncheckedWrapperException(nsme);
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    /**
     * Load a proper subclass of the {@link ACSObject} domain object
     * from the database
     *
     * @param javaClass The name of the Java class to be instantiated
     * @param objectType the object type of the object to instantiate; should
     *   extend the <code>ACSObject</code>
     * @param id the primary key for the new object
     * @return the loaded object, or null on failure. It is safe to cast
     *   the return value to the <code>javaClass</code>
     */
    public static ACSObject loadACSObject(final String javaClass,
                                          final String objectType,
                                          final BigDecimal id) {
        try {
            return loadACSObject(Class.forName(javaClass), objectType, id);
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        }
    }

    /**
     * "Cast" the {@link ContentItem} to its proper subclass, based on the
     * values in its content type. Return the item itself on failure.
     * <p>
     * Note that this method performs 2 extra database hits: one hit
     * to get the content type, and another hit to get the new item.
     *
     * @param item the {@link ContentItem} to cast
     * @return the proper subclass of {@link ContentItem}, such as
     *   {@link ContentPage}
     */
    public static ContentItem castContentItem(ContentItem item) {

        String javaClass = null, objectType = null;

        ContentType t = item.getContentType();
        if (t != null) {
            // Get the java class/objetc type from the content type
            javaClass =  t.getClassName();
            objectType = t.getAssociatedObjectType();
        } else {
            // Get the most specific object type and use it as the java class
            objectType = item.getSpecificObjectType();
            javaClass = objectType;
        }

        return (ContentItem)loadACSObject(
                                          javaClass, objectType, item.getID()
                                          );
    }

    /**
     * Attempt to retrieve the true object type of the item
     * from the "object_type" column of <code>ACSObject</code>.
     * On failure, return the type which is recorder in the
     * item's OID
     *
     * @param obj the ACSObject
     * @return the true (most specific) object type of the object
     */
    public static ObjectType getSpecificObjectType(ACSObject obj) {
        String typeName = obj.getSpecificObjectType();
        return SessionManager.getMetadataRoot().getObjectType(typeName);
    }

    /**
     * Attempt to "cast" a {@link DataObject} to a proper
     * {@link ACSObject} subclass. Return null if the data object
     * does not represent an <code>ACSObject</code>.
     * <p>
     * This method assumes that the Java class name is the same
     * as the object type name of the object. If this assumption
     * fails, the method will return null.
     * <p>
     * If the assumption turns out to be true, this method
     * will try to instantiate the desired object with the
     * constructor <code>public Foo(DataObject obj)</code>.
     * If the constructor does not exist, the method will
     * return null.
     *
     * <p> The data object <code>obj</code> is specialized to the right
     * subclass if that is necessary.
     *
     * @param obj the {@link DataObject} to cast
     * @return the proper subclass of {@link ACSObject},
     *   or null on failure
     *
     * @see #castContentItem
     */
    public static ACSObject castACSObject(DataObject obj) {
        String objType = obj.getOID().getObjectType().getQualifiedName();

        // Try to extract the object type column
        String javaClassName = (String) obj.get(ACSObject.OBJECT_TYPE);

        if (!javaClassName.equals(objType)) {
            obj.specialize(javaClassName);
        }

        // Try to use the constructor "public Whatever(DataObject obj)"

        try {
            final Class javaClass = Class.forName(javaClassName);

            final Constructor constr = javaClass.getConstructor(s_dataArgs);

            return (ACSObject) constr.newInstance(new DataObject[] {obj});
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new UncheckedWrapperException(nsme);
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    /**
     * Attempt to "cast" an {@link ACSObject} to a proper
     * {@link ACSObject} subclass. Return the original
     * object on failure
     * <p>
     * This method assumes that the Java class name is the same
     * as the object type name of the object. If this assumption
     * fails, the method will return null.
     * <p>
     *
     * @param obj the {@link ACSObject} to cast
     * @return the proper subclass of {@link ACSObject},
     *   or null on failure
     *
     * @see #castContentItem
     */
    public static ACSObject castACSObject(ACSObject obj) {
        String objectTypeName = null;
        ObjectType specificType = null;
        ObjectType currentType = obj.getOID().getObjectType();


        // Extract the most specific object type
        objectTypeName = obj.getSpecificObjectType();
        if (objectTypeName == null) {
            // Just get the type from the OID
            objectTypeName = currentType.getQualifiedName();
        } else {
            specificType =
                SessionManager.getMetadataRoot().getObjectType(objectTypeName);

            // Abort if the object type is unknown
            if (specificType == null) return obj;

            objectTypeName = specificType.getQualifiedName();
        }

        // If the object is already specific, abort
        if (obj.getClass().getName().equals(objectTypeName)) return obj;

        // Try to load the more specific object
        ACSObject newObj = loadACSObject(objectTypeName, objectTypeName, obj.getID());

        if (newObj == null) return obj;

        return newObj;
    }


    /**
     * Attempt to "cast" a {@link DataObject} to a proper
     * {@link ContentItem} subclass. Return null if the data object
     * does not represent a content item.
     * <p>
     * Note that this method performs 2 extra database hits: one hit
     * to get the content type, and another hit to get the new item.
     *
     * @param obj the {@link DataObject} to cast
     * @param useContentType if true, try to access the item's content type
     *   in order to get the Java class name. If false, assume that
     *   the java class name is the same as the object type.
     *
     * @return the proper subclass of {@link ContentItem}, such as
     *   {@link ContentPage}
     */
    public static ContentItem castContentItem(final DataObject obj,
                                              final boolean useContentType) {
        OID oid = obj.getOID();
        ContentType t = null;
        String javaClassName = null;

        String objectType = (String) obj.get(ACSObject.OBJECT_TYPE);

        if (objectType == null) {
            // Get the object type from OID
            objectType = oid.getObjectType().getQualifiedName();
        }

        // Try to figure out the class name by looking at the
        // content type
        if (useContentType) {
            DataObject tobj = (DataObject) obj.get(ContentItem.CONTENT_TYPE);

            if (tobj != null) {
                // Get the content type directly from the object

                t = new ContentType(tobj);
            } else {
                // Ok, try to find the content type through object type

                t = ContentType.findByAssociatedObjectType(objectType);
            }

            if (t != null) {
                javaClassName = t.getClassName();
            }
        }

        // On failure, assume that the java class name is the same
        // as the object type
        if (javaClassName == null) {
            javaClassName = objectType;
        }

        try {
            // Try to use the constructor "public Whatever(DataObject obj)"

            final Class javaClass = Class.forName(javaClassName);
            final Constructor constr = javaClass.getConstructor(s_dataArgs);

            return (ContentItem) constr.newInstance(new DataObject[] {obj});
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        } catch (NoSuchMethodException nsme) {
            try {
                return (ContentItem) loadACSObject
                    (javaClassName, objectType,
                     (BigDecimal) oid.get(ACSObject.ID));
            } catch (ClassCastException ex) {
                s_log.warn("Assumption (type == java class) for " +
                           objectType + " is incorrect, giving up");
                return null;
            }
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    /**
     * Attempt to "cast" a {@link DataObject} to a proper
     * {@link ContentItem} subclass. Return null if the data object
     * does not represent a content item.
     * <p>
     * Note that this method performs 2 extra database hits: one hit
     * to get the content type, and another hit to get the new item.
     *
     * @param obj the {@link DataObject} to cast
     *
     * @return the proper subclass of {@link ContentItem}, such as
     *   {@link ContentPage}
     */
    public static ContentItem castContentItem(DataObject obj) {
        return castContentItem(obj, true);
    }

    /**
     * Attempt to "cast" a {@link DomainObject} to a proper
     * {@link ContentItem} subclass. Return null if the domain object
     * does not represent a content item.
     * <p>
     * Note that this method performs 2 extra database hits: one hit
     * to get the content type, and another hit to get the new item.
     *
     * @param obj the {@link DomainObject} to cast
     *
     * @return the proper subclass of {@link ContentItem}, such as
     *   {@link ContentPage}
     */
    public static ContentItem castContentItem(DomainObject obj) {
        return castContentItem(getDataObject(obj));
    }

    /**
     * Create a proper subclass of {@link ACSObject}. Requires the class
     * designated by <code>javaClassName</code> to have a constructor
     * with a single String parameter. The String parameter should specify
     * the object type of the new object.
     *
     * @param javaClassName The name of the Java {@link Class}
     *   to be instantiated
     * @param objectType the object type of the object to instantiated; should
     *   extend the {@link ACSObject}
     * @return the new {@link ACSObject} on success, null on failure
     */
    public static ACSObject createACSObject(final String javaClassName,
                                            final String objectType) {
        try {
            final Class javaClass = Class.forName(javaClassName);
            final Constructor constr = javaClass.getConstructor(s_newArgs);

            ACSObject obj = (ACSObject) constr.newInstance(new String[] {objectType});

            return obj;
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new UncheckedWrapperException(nsme);
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    /**
     * Instantiate a subclass of {@link ACSObject},
     * blatantly disregarding ObjectType.
     *
     * @param javaClassName The name of the Java {@link Class}
     *   to be instantiated
     *
     * @return the new {@link ACSObject} on success, null on failure
     */
    public static ACSObject createACSObject(String javaClassName) {
        try {
            final Class javaClass = Class.forName(javaClassName);

            final ACSObject obj = (ACSObject) javaClass.newInstance();

            return obj;
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        }
    }

    /**
     * Get a collection of all attributes which comprise the type's
     * primary key
     *
     * @return an array of primary key attribute names
     */
    public static Collection getKeyAttributes(final ObjectType type) {
        final ArrayList keys = new ArrayList(1);

        for (Iterator it = type.getKeyProperties(); it.hasNext();) {
            keys.add(it.next());
        }

        return keys;
    }

    /**
     * Get an array of all attribute names which comprise the type's
     * primary key
     *
     * @return an array of primary key attribute names
     */
    public static Collection getKeyAttributeNames(final ObjectType type) {
        final Collection attrs = getKeyAttributes(type);
        final ArrayList names = new ArrayList(attrs.size());

        for (Iterator i = attrs.iterator(); i.hasNext();) {
            names.add(((Property)i.next()).getName());
        }

        return names;
    }
}
