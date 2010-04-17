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
import com.arsdigita.persistence.DataCollection;

/**
 *
 * This class should be extended by domain object services that
 * need privileged access to the DataObject encapsulated by a
 * DomainObject or the DataCollection encapsulated by a DomainCollection.
 *
 * @see com.arsdigita.persistence.DataObject
 *
 * @author Oumi Mehrotra
 * @version $Id: DomainService.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class DomainService {

    /**
     *
     * Get a property of the specified domain object.
     *
     * @see DomainObject#get(String)
     **/
    protected static Object get(DomainObject domainObject, String attr) {
        return domainObject.get(attr);
    }

    /**
     * Get the underlying {@link DataObject} of the {@link DomainObject}
     **/
    protected static DataObject getDataObject(DomainObject domainObject) {
        return domainObject.getDataObject();
    }

    /**
     *
     * Get the data collection encapsulated by a domain collection.
     *
     * @see DomainObject#get(String)
     **/
    protected static DataCollection
        getDataCollection (DomainCollection domainCollection)
    {
        return domainCollection.m_dataCollection;
    }

    // These methods modify domain objects and should be used cautiously.

    /**
     *
     * Set a property of the DomainObjects DataObject.
     *
     * @see DomainObject#set(String, Object)
     **/
    protected static void set(DomainObject domainObject, String attr, Object value) {
        domainObject.set(attr, value);
    }

    /**
     *
     * Set an association DomainObjects DataObject.
     *
     * @see DomainObject#set(String, Object)
     **/
    protected static void setAssociation(DomainObject domainObject,
                                         String attr, DomainObject dobj) {
        domainObject.set(attr, dobj);
    }

    /**
     * Adds data object specified by <i>value</i> to the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DataObject)
     */
    protected static DataObject add(DomainObject domainObject,
                                    String roleName, DataObject value) {
        return domainObject.add(roleName, value);
    }

    /**
     * Adds domain object specified by <i>value</i> to the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DomainObject)
     */
    protected static DataObject add(DomainObject domainObject,
                                    String roleName, DomainObject value) {
        return domainObject.add(roleName, value);
    }

    /**
     *
     * Removes data object specified by <i>value</i> from the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DataObject)
     */
    protected static void remove(DomainObject domainObject,
                                 String roleName, DataObject value) {
        domainObject.remove(roleName, value);
    }

    /**
     *
     * Removes domain object specified by <i>value</i> from the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DomainObject)
     */
    protected static void remove(DomainObject domainObject,
                                 String roleName, DomainObject value) {
        domainObject.remove(roleName, value);
    }

    /**
     *
     * Clears specified role (with multiplicity > 1) of specified
     * domain object.
     *
     * @see DomainObject#clear(String)
     */
    protected static void clear(DomainObject domainObject, String roleName) {
        domainObject.clear(roleName);
    }

}
