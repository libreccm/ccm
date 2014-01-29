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
package com.arsdigita.persistence.metadata;


import java.util.Iterator;

/**
 * The CompoundType class represents types that are built up from SimpleTypes
 * and other CompoundTypes. A CompoundType has a set of properties. Each
 * property contained in a CompoundType has an associated DataType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 */

abstract public class CompoundType extends DataType {

    /**
     * Constructs a new and empty CompoundType with the given name. In order
     * to do anything useful with the type you must add any properties it may
     * have.
     *
     * @param name The name of this compound type.
     **/

    public CompoundType
	(com.redhat.persistence.metadata.ObjectType obj) {
        super(obj);
    }


    /**
     * Gets a property that this CompoundType contains. Returns null if no
     * such property exists.
     *
     * @param name The name of the property.
     *
     * @return The property with name <i>name</i>, or null if no such property
     *         exists.
     **/

    public abstract Property getProperty(String name);


    /**
     * Returns true if and only if this CompoundType has a property with the
     * given name.
     *
     * @param name The name of the property for which to check existence.
     *
     * @return True if this CompoundType has a property with the given name.
     *         False otherwise.
     **/

    public abstract boolean hasProperty(String name);


    /**
     * Returns an iterator containing all the Properties this CompoundType
     * contains.
     *
     * @return An iterator containing all the Properties this CompoundType
     *         contains.
     *
     * @see Property
     **/

    public abstract Iterator getProperties();


    /**
     * This method will always return true. It is the implementation of the
     * abstract method that appears in DataType.
     *
     * @return true
     **/

    public boolean isCompound() {
        return true;
    }

}
