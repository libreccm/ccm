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
 * Defines the public methods of Data Objects.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 */

public interface DataObject {

    String versionId = "$Id: DataObject.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Returns the type of this persistent object.
     *
     * @return The type of this persistent object.
     **/

    ObjectType getObjectType();

    /**
     * Returns the unique id of this persistent object.
     *
     * @return The id of this object.
     **/

    OID getOID();

    /**
     * Returns the value of the specified property.
     *
     * @param propertyName The property name.
     *
     * @return The property value.
     **/

    Object get(String propertyName);

    /**
     * Sets the specified property to <i>value</i>.
     *
     * @param propertyName The property name.
     * @param value The desired value.
     **/

    void set(String propertyName, Object value);

    /**
     * Returns the Session object from which this object was created or
     * retrieved.
     *
     * @return This object's Session.
     **/

    Session getSession();

    /**
     * Returns true if this persistent object is newly created.
     *
     * @return True if the object is newly created.
     **/

    boolean isNew();

    /**
     * Returns true if this persistent object has been deleted from
     * the database.  This does a database hit to check.
     *
     * @return True if the object has been deleted
     **/

    boolean isDeleted();

    /**
     * Returns true if the object exists in a committed state in the
     * database. This does not mean that all changes to this object have been
     * either written to disk or committed.
     *
     * @return True if the object exists in a committed state in the database.
     **/

    boolean isCommitted();

    /**
     * Returns true if this persistent object has been disconnected from
     * the transaction context. If true, the object can still be read, but
     * any attempt to update any of the object's attributes will cause an
     *  exception to be thrown.
     *
     * @return True if the object has been disconnected
     **/

    boolean isDisconnected();

    /**
     * Disconnects this DataObject from the current transaction. This allows
     * the data object to be used in multiple transactions.
     *
     * @see #isDisconnected()
     **/

    void disconnect();

    /**
     * Returns true if this persistent object has been modified since it was
     * retrieved.
     *
     * @return True if the object has been modified, false otherwise.
     **/

    boolean isModified();

    /**
     * Returns true of the property specified by <i>name</i> has been modified
     * since this object was retrieved.
     *
     * @return True if the property has been modified, false otherwise.
     **/

    boolean isPropertyModified(String name);

    /**
     *  Returns true if this persistent object is in a valid state.
     *  An invalid DataObject usually results from using a data object that was
     *  retrieved during a transaction that has been rolled back.
     *
     * @return True if the object has been modified, false otherwise.
     **/

    boolean isValid();

    /**
     * Deletes this persistent object.
     *
     * @post isDeleted()
     **/

    void delete();


    /**
     * Specializes this persistent object by turning it into a subtype of this
     * object's current type.
     *
     * @param subtype The subtype to which to specialize.
     *
     * @pre subType.isASuperType(getObjectType()) || subtype.equals(getObjectType())
     *
     * @post subtype.equals(getObjectType())
     **/

    void specialize(ObjectType subtype);

    /**
     * Specializes this persistent object by turning it into a subtype of this
     * object's current type. In addition to the local precondition, also
     * has pre and post conditions of specialize(ObjectType).
     *
     * @param subtypeName The name of the subtype to which to specialize.
     *
     * @pre SessionManager.getMetadataRoot().getObjectType(subtypeName) != null
     **/

    void specialize(String subtypeName);

    /**
     * Persists any changes made to this persistent object.
     *
     * @post !(isNew() || isModified())
     **/

    void save();


    /**
     * Adds an observer.
     *
     * @param observer The observer to add to this DataObject.
     **/

    void addObserver(DataObserver observer);


}
