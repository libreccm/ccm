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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 * This instantiator is primarily intended for other instantiators
 * (such as ACSObjectInstantiator) to provide the default behavior
 * of instiating a domain class using reflection.
 * 
 * @version $Id: ReflectionInstantiator.java 2089 2010-04-17 07:55:43Z pboy $
 */
public class ReflectionInstantiator extends DomainObjectInstantiator {

    private static Logger s_log = Logger.getLogger(ReflectionInstantiator.class);

    private Constructor m_constructor;

    /**
     * keyed off of class name.
     **/
    private static Map s_instantiators = new HashMap();

    private static Class[] s_dataArgs = new Class[]{DataObject.class};

    public static ReflectionInstantiator
        getInstantiator(String domainClassName)
    {
        ReflectionInstantiator instantiator = null;
        synchronized (ReflectionInstantiator.class) {
            if (s_instantiators.containsKey(domainClassName)) {
                instantiator =
                    (ReflectionInstantiator) s_instantiators.get(domainClassName);
            } else {
                try {
                    instantiator = new ReflectionInstantiator(domainClassName);
                } catch (Exception e) {
                    s_log.warn("cannot create reflection instantiator for " + domainClassName, e);
                    // no class with specified name and public constructor
                    // of form Constructor(DataObject).  Just return null.
                }
                s_instantiators.put(domainClassName, instantiator);
            }
        }
        return instantiator;
    }

    private ReflectionInstantiator(String domainClassName)
        throws LinkageError,
               ExceptionInInitializerError,
               ClassNotFoundException,
               NoSuchMethodException,
               SecurityException
    {
        Class javaClass = Class.forName(domainClassName);
        m_constructor = javaClass.getConstructor(s_dataArgs);        
        // We should add a check that the domain class is indeed
        // a subclass of DomainObject
    }

    /**
     * Construct a DomainObject given a data object.  Called from
     * DomainObjectFactory.newInstance() as the last step of
     * instantiation.
     *
     * @param dataObject The data object from which to construct a domain
     * object.
     *
     * @return A domain object for this data object, or null if unable to create.
     */
    protected DomainObject doNewInstance(DataObject dataObject) {
        try {
            return (DomainObject)
                m_constructor.newInstance(new DataObject[]{dataObject});
        } catch (InstantiationException ex) {
            return reportError(dataObject, ex);
        } catch (IllegalAccessException ex) {
            return reportError(dataObject, ex);
        } catch (InvocationTargetException ex) {
            return reportError(dataObject, ex);
        }
    }

    private DomainObject reportError(DataObject dobj, Throwable ex) {
        s_log.error("Construtor " + m_constructor +
                    " could not create a new DomainObject " +
                    "for DataObject: " + dobj, ex);
        return null;
    }
}
