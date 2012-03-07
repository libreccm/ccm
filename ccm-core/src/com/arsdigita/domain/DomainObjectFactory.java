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
package com.arsdigita.domain;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;


import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * This is a factory class to support instantiation of an appropriate domain
 * object class from a given data object. <p> The architecture of the
 * persistence and domain layers intentionally does not impose a simple
 * one-to-one correspondence between data object types and domain object
 * classes. It is possible for multiple domain object classes to encapsulate a
 * given data object. It is also possible that a single domain object class can
 * encapsulate data objects of different types. The primary factory design
 * objectives are:
 *
 * <ol> <li> It should be easy for developers to produce a domain object from a
 * given data object when it cannot be determined at compile time which domain
 * object class should be instantiated.
 *
 * <li> The process of instantiating a domain object given a data object should
 * be as flexible as possible by delegating to custom code, called a
 * <code>DomainObjectInstantiator</code>, that can make use of any properties of
 * the given data object.
 *
 * <li> A data object of type X is not supported by the factory unless some
 * <code>DomainObjectInstantiator</code> has been registered with the factory
 * for type X, and even then, it may be possible for the instantiator code to
 * examine other properties of the data object and decide that the given data
 * object is not supported (presumably because there is no sensible way to
 * choose which domain object class should be instantiated for the particular
 * data object). </ol>
 *
 * @see DomainObjectInstantiator
 * @see com.arsdigita.persistence.DataObject
 * @see DomainObject
 *
 * @author Oumi Mehrotra
 * @version 1.0
 * @version $Id: DomainObjectFactory.java 2170 2011-06-19 22:23:08Z pboy $
 *
 */
public class DomainObjectFactory {

    /**
     * map of registered instantiators, keyed by data object type.
     */
    private static Map s_instantiators = new HashMap();
    /**
     * private logger instance.
     */
    private static final Logger s_log = Logger.getLogger(
            DomainObjectFactory.class);

    /**
     * Instantiates a domain object given a data object.
     *
     * <p>The process of instantiating the domain object involves delegating to
     * a {@link DomainObjectInstantiator}. The instantiator may use any
     * information about the DataObject to produce the appropriate domain
     * object.</p>
     *
     * <p>The result may be null if there is no instantiator registered for the
     * dataObjectType of the specified
     * <code>dataObject</code>, OR if the registered instantiator does not
     * support instantiation of the specified
     * <code>dataObject</code>.</p>
     *
     * @param dataObject The data object from which to instantiate a domain
     * object.
     *
     * @return A domain object that encapuslates the given dataObject. Returns
     * <code>null</code>, if called with the
     * <code>null</code> data object parameter. The result may also be
     * <code>null</code> if no domain object could be instantiated for the given
     * data object.
     *
     * @throws InstantiatorNotFoundException if no Instantiator could be found
     * @throws PersistenceException
     *
     * @see com.arsdigita.persistence.DataObject
     * @see DomainObject
     */
    public static DomainObject newInstance(DataObject dataObject)
            throws PersistenceException, InstantiatorNotFoundException {

        if (dataObject == null) {
            return null;
        }

        if (s_log.isInfoEnabled()) {
            s_log.info("Instantiating " + dataObject.getOID());
        }

        /*
         * A instantiator may delegate to another instantiator depending on the
         * data object. That other instantiator may in turn delegate to a
         * different instantiator, and so on. We get the final instantiator by
         * recursed calls to resolveInstantiator until the returned instantiator
         * doesn't change.
         */

        DomainObjectInstantiator delegator = null;
        DomainObjectInstantiator delegate =
                                 getInstantiator(dataObject.getObjectType());

        if (s_log.isDebugEnabled()) {
            s_log.debug("Initial delegate " + delegate);
        }

        /*
         * keep calling resolveInstantiator until the result doesn't change
         */
        while (delegate != null && delegate != delegator) {
            delegator = delegate;
            delegate = delegator.resolveInstantiator(dataObject);
            if (s_log.isDebugEnabled()) {
                s_log.debug("New delegate " + delegate);
            }
        }

        if (delegate == null) {
            throw new InstantiatorNotFoundException(
                    "No instantiator found for dataObject " + dataObject);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Final delegate " + delegate);
        }
        return delegate.doNewInstance(dataObject);
    }

    /**
     * Instantiate a domain object given an OID. This method is a wrapper around
     * newInstance(DataObject).
     *
     * @param oid the oid of the data object for which to instantiate a domain
     * object.
     *
     * @return A domain object that encapuslates the dataObject identified by
     * <i>oid</i>. The result may be null if no domain object could be
     * instantiated for the specified data object.
     *
     * @throws DataObjectNotFoundException if object could not be retrieved.
     *
     * @see #newInstance(DataObject)
     * @see com.arsdigita.persistence.DataObject
     * @see DomainObject
     */
    public static DomainObject newInstance(OID oid)
            throws InstantiatorNotFoundException {

        if (oid == null) {
            return null;
        }

        Session session = SessionManager.getSession();
        if (session == null) {
            throw new RuntimeException("Could not retrieve a session from "
                                       + "the session manager while instantiating "
                                       + "a class with OID = " + oid.toString());
        }

        DataObject dataObject = session.retrieve(oid);

        if (dataObject == null) {
            throw new DataObjectNotFoundException("Could not retrieve a DataObject with "
                                                  + "OID = " + oid.toString());
        }

        return newInstance(dataObject);

    }

    /**
     * Register an instantiator for the data object type specified by
     * <i>dataObjectType</i>. The registered instantiator will be used by
     * newInstance() for data objects whose type is equal to the type specified
     * by <i>dataObjectType</i>. That is, when
     * <code>newInstance(x)</code> is executued, the specified
     * <i>instantiator</i> will be used if the specified <i>dataObjectType</i>
     * is equal to
     * <code>x.getObjectType()</code>.
     *
     * <p> Any object type that does not have an instantiator registered with
     * this factory is not supported by the factory.
     *
     * <p> If another instantiator was already registered for the specified
     * object type, the previous instantiator is replaced and returned.
     *
     * @param dataObjectType The data object type for which to register the
     * instantiator specified by <i>instantiator</i>
     *
     * @param instantiator The instantiator that will handle data objects of the
     * data object type specified by <i>dataObjectType</i> when newInstance() is
     * called.
     *
     * @pre dataObjectType!= null
     *
     * @return DomainObjectInstantiator The previous instantiator that was
     * registered with this factory for this object type.
     *
     * @throws InstantiatorRegistryException if null == dataObjectType
     *
     * @see #registerInstantiator(String, DomainObjectInstantiator)
     * @see #newInstance(DataObject)
     * @see com.arsdigita.persistence.DataObject
     * @see DomainObject
     */
    public synchronized static DomainObjectInstantiator registerInstantiator(
            ObjectType dataObjectType,
                                                                             DomainObjectInstantiator instantiator) {
        if (null == dataObjectType) {
            throw new InstantiatorRegistryException("null", instantiator);
        }

        return (DomainObjectInstantiator) s_instantiators.put(dataObjectType,
                                                              instantiator);
    }

    /**
     * Wrapper around
     * <code>registerInstantiator(ObjectType, DomainObjectInstantiator)</code>.
     *
     * @param dataObjectType The fully qualified name of the data object type
     * for which to register the specified <i>instantiator</i>. The qualified
     * name is the model name followed by a '.' followed by the object type name
     * (e.g. "com.arsdigita.kernel.Party").
     *
     * @param instantiator The instantiator that will handle data objects of the
     * type specified by <i>typeName</i> when this newInstance() is called.
     *
     * @pre SessionManager.getMetadataRoot().getObjectType(dataObjectType) !=
     * null
     *
     * @return DomainObjectInstantiator The previous instantiator that was
     * registered with this factory for this object type.
     *
     * @throws InstantiatorRegistryException if the dataObjectType does not
     * exist
     *
     * @see #registerInstantiator(ObjectType, DomainObjectInstantiator)
     */
    public static DomainObjectInstantiator registerInstantiator(
            String dataObjectType,
            DomainObjectInstantiator instantiator) {
        MetadataRoot meta = SessionManager.getMetadataRoot();

        ObjectType objectType = meta.getObjectType(dataObjectType);
        if (null == objectType) {
            throw new InstantiatorRegistryException(dataObjectType, instantiator);
        }
        return registerInstantiator(objectType, instantiator);
    }

    /**
     * Get the registered instantiator for the specified object type.
     *
     * @param dataObjectType The data object type whose registered instantiator
     * is to be returned
     *
     * @return The instantiator that is registered for the specified object
     * type.
     *
     * @see #registerInstantiator(ObjectType,DomainObjectInstantiator)
     */
    public synchronized static DomainObjectInstantiator getRegisteredInstantiator(
            ObjectType dataObjectType) {
        return (DomainObjectInstantiator) s_instantiators.get(dataObjectType);
    }

    /**
     * Wrapper around getRegisteredInstantiator(ObjectType).
     *
     * @param dataObjectType The fully qualified name of the data object type
     * whose registered instantiator is to be returned
     *
     * @return The instantiator that is registered for the specified object
     * type. The qualified name is the model name followed by a '.' followed by
     * the object type name (e.g. "com.arsdigita.kernel.Party").
     *
     * @see #getRegisteredInstantiator(ObjectType)
     * @see #registerInstantiator(ObjectType,DomainObjectInstantiator)
     */
    public synchronized static DomainObjectInstantiator getRegisteredInstantiator(
            String dataObjectType) {
        MetadataRoot meta = SessionManager.getMetadataRoot();
        return getRegisteredInstantiator(meta.getObjectType(dataObjectType));
    }

    /**
     * Get the registered or inherited instantiator for the specified object
     * type. That is, get the instantiator that is registered for the specified
     * object type or its closest supertype that has a registered instantiator
     * (or null if there is no supertype that has a registered instantiator).
     *
     * @param dataObjectType The data object type whose registered or inherited
     * instantiator is to be returned
     *
     * @return The registered or inherited instantiator for the specified object
     * type.
     *
     * @see #registerInstantiator(ObjectType,DomainObjectInstantiator)
     */
    public synchronized static DomainObjectInstantiator getInstantiator(
            ObjectType dataObjectType) {
        ObjectType type = dataObjectType;
        if (s_log.isDebugEnabled()) {
            s_log.debug("Initial type " + (type == null ? null : type.getName()));
        }
        while (type != null && !s_instantiators.containsKey(type)) {
            type = type.getSupertype();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Parent type " + (type == null ? null
                                              : type.getName()));
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Final type " + (type == null ? null : type.getName()));
        }
        if (type != null) {
            DomainObjectInstantiator instantiator =
                                     (DomainObjectInstantiator) s_instantiators.
                    get(type);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found instantiator " + instantiator);
            }
            return instantiator;
        } else {
            return null;
        }
    }

    /**
     * Wrapper around getInstantiator(ObjectType).
     *
     * @param dataObjectType The fully qualified name of the data object type
     * whose registered or inherited instantiator is to be returned
     *
     * @return The registered or inherited instantiator for the specified object
     * type. The qualified name is the model name followed by a '.' followed by
     * the object type name (e.g. "com.arsdigita.kernel.Party").
     *
     * @see #getInstantiator(ObjectType)
     * @see #registerInstantiator(ObjectType,DomainObjectInstantiator)
     */
    public synchronized static DomainObjectInstantiator getInstantiator(
            String dataObjectType) {
        MetadataRoot meta = SessionManager.getMetadataRoot();
        return getInstantiator(meta.getObjectType(dataObjectType));
    }

    /**
     * Package scope method for clearing the factory of registered
     * instantiators. <P> It is ONLY for use by Domain TestCases. <P> If you use
     * this method outside that context, YOU WILL BE FLOGGED!!
     */
    static synchronized void resetFactory() {
        s_instantiators = new HashMap();
    }
}
