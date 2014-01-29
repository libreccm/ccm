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


/**
 * The DataType class represents the type of a persistently stored datum.
 * There are two flavors of DataTypes. SimpleTypes, and CompoundTypes.
 * CompoundTypes are defined in terms of other DataTypes both simple and
 * compound. Any value returned by the persistence layer must have an
 * associated DataType object.
 *
 * @see SimpleType
 * @see CompoundType
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

abstract public class DataType extends ModelElement {

    

    private com.redhat.persistence.metadata.ObjectType m_type;

    /**
     * Constructs a new DataType with the given name.
     *
     * @param name The semantically meaningful name of the datatype.
     **/

    protected DataType
	(com.redhat.persistence.metadata.ObjectType obj) {
        super(obj.getRoot(), obj.getModel(), obj);
	m_type = obj;
    }

    /**
     * Returns the semantically meaningful name of this datatype.
     *
     * @return the semantically meaningful name of this datatype.
     **/

    public String getName() {
	return m_type.getName();
    }


    /**
     * Returns the fully qualified name of this DataType. The fully qualified
     * name consists of the model name followed by a "." followed by the name
     * of this DataType.
     *
     * @return The fully qualified name of this DataType.
     **/

    public String getQualifiedName() {
	return m_type.getQualifiedName();
    }


    /**
     * Returns true if this DataType is a compound type, false otherwise.
     * Compound types are composed of other data types both simple and
     * compound.
     *
     * @return True if this DataType is a compound type. False otherwise.
     **/

    public boolean isCompound() {
	return m_type.isCompound();
    }

    /**
     * Returns true if this DataType is a simple type, false otherwise. Simple
     * types are not composed of other data types. They are the basic atoms
     * from which compound types can be created.
     *
     * @return True if this DataType is a simple type. False otherwise.
     **/

    public boolean isSimple() {
        return !isCompound();
    }

}
