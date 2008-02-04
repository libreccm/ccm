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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * Exposes all methods in {@link DomainService}.
 *
 * @author Stanislav Freidin
 * @author Gavin Doughtie
 * @version $Id: DomainServiceInterfaceExposer.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class DomainServiceInterfaceExposer extends DomainService {

    public static final String versionId = "$Id: DomainServiceInterfaceExposer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    /**
     *
     * Get a property of the specified domain object.
     *
     * @see DomainObject#get(String)
     **/
    public static Object get(DomainObject domainObject, String attr) {
        return DomainService.get(domainObject, attr);
    }

    /**
     * Get the underlying {@link DataObject} of the {@link DomainObject}
     **/
    public static DataObject getDataObject(DomainObject domainObject) {
        return DomainService.getDataObject(domainObject);
    }

    /**
     *
     * Get the data collection encapsulated by a domain collection.
     *
     * @see DomainObject#get(String)
     **/
    public static DataCollection
        getDataCollection (DomainCollection domainCollection)
    {
        return DomainService.getDataCollection(domainCollection);
    }

    // These methods modify domain objects and should be used cautiously.

    /**
     *
     * Set a property of the DomainObjects DataObject.
     *
     * @see DomainObject#set(String, Object)
     **/
    public static void set(DomainObject domainObject, String attr, Object value) {
        DomainService.set(domainObject, attr, value);
    }

    /**
     *
     * Set an association DomainObjects DataObject.
     *
     * @see DomainObject#set(String, Object)
     **/
    public static void setAssociation(DomainObject domainObject,
                                      String attr, DomainObject dobj) {
        DomainService.setAssociation(domainObject, attr, dobj);
    }

    /**
     *
     * Adds data object specified by <i>value</i> to the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DataObject)
     */
    public static DataObject add(DomainObject domainObject,
                           String roleName, DataObject value) {
        return DomainService.add(domainObject, roleName, value);
    }

    /**
     *
     * Adds domain object specified by <i>value</i> to the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#add(String, DomainObject)
     */
    public static DataObject add(DomainObject domainObject,
                           String roleName, DomainObject value) {
        return DomainService.add(domainObject, roleName, value);
    }

    /**
     *
     * Removes data object specified by <i>value</i> from the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DataObject)
     */
    public static void remove(DomainObject domainObject,
                              String roleName, DataObject value) {
        DomainService.remove(domainObject, roleName, value);
    }

    /**
     *
     * Removes domain object specified by <i>value</i> from the
     * specified role (with multiplicity>1) of the specified domain object.
     *
     * @see DomainObject#remove(String, DomainObject)
     */
    public static void remove(DomainObject domainObject,
                              String roleName, DomainObject value) {
        DomainService.remove(domainObject, roleName, value);
    }

    /**
     *
     * Clears specified role (with multiplicity > 1) of specified
     * domain object.
     *
     * @see DomainObject#clear(String)
     */
    public static void clear(DomainObject domainObject, String roleName) {
        DomainService.clear(domainObject, roleName);
    }

}
