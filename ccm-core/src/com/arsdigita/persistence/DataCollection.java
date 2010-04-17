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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * The DataCollection interface defines the public methods available on a
 * collection of DataObjects. DataCollections can be used to efficiently
 * iterate over a large set of DataObjects and access the values of their
 * properties. A DataCollection has much of the functionality of a
 * {@link com.arsdigita.persistence.DataQuery}, and can be filtered or sorted
 * in the same way. A typical usage of a DataCollection is:
 *
 *   <pre>
 *   Session ssn = SessionManager.getSession();
 *   DataCollection employees = ssn.retrieve("com.dotcom.Employee");
 *   employees.setFilter("name like '%nut'");
 *   employees.setOrder("name");
 *
 *   while (employees.next()) {
 *       System.out.println("ID: " + employees.get("id"),
 *                          "Name: " + employees.get("id"));
 *   }
 *   </pre>
 *
 * A DataCollection can also be used to fetch complete DataObjects as opposed
 * to simply accessing the property values of those DataObjects. This means of
 * access is less efficient than that described above because a Java object
 * must be instantiated for every DataObject in the DataCollection.
 *
 *   <pre>
 *   Session ssn = SessionManager.getSession();
 *   DataCollection employees = ssn.retrieve("com.dotcom.Employee");
 *   employees.addFilter("name like '%nut'");
 *   employees.addOrder("name");
 *
 *   while (employees.next()) {
 *       DataObject emp = employees.getDataObject();
 *       System.out.println(emp);
 *   }
 *   </pre>
 *
 * @see com.arsdigita.persistence.SessionManager
 * @see com.arsdigita.persistence.Session
 * @see com.arsdigita.persistence.DataObject
 * @see com.arsdigita.persistence.DataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: DataCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface DataCollection extends DataQuery {

    /**
     * Returns a data object for the current position in the collection.
     *
     * @return A DataObject.
     **/

    DataObject getDataObject();

    /**
     * Returns the object type of the data collection.
     *
     * @return The object type of the data collection.
     **/

    ObjectType getObjectType();


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    void setParameter(String parameterName, Object value);


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName);

    /**
     * Tests whether the current collection contains an object.
     *
     * @param oid The oid of the object.
     * @return True if the collection contains the object, false otherwise.
     */
    public boolean contains(OID oid);

    /**
     * Tests whether the current collection contains an object.
     *
     * @param data The dataobject.
     * @return True if the collection contains the object, false otherwise.
     */
    public boolean contains(DataObject data);


}
