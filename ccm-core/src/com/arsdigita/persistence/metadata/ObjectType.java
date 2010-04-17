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

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * The ObjectType class is a specialized form of CompoundType that supports
 * inheritence. It also adds the notion of identity by allowing properties to
 * be marked as special "key" properties.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #19 $ $Date: 2004/08/16 $
 * @version $Id: ObjectType.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ObjectType extends CompoundType {

    static ObjectType
	wrap(com.redhat.persistence.metadata.ObjectType type) {
	if (type == null) {
	    return null;
	} else {
	    return new ObjectType(type);
	}
    }

    static Collection wrap(Collection types) {
	ArrayList result = new ArrayList(types.size());
	for (Iterator it = types.iterator(); it.hasNext(); ) {
	    result.add
		(wrap((com.redhat.persistence.metadata.ObjectType)
		      it.next()));
	}
	return result;
    }


    private com.redhat.persistence.metadata.ObjectType m_type;

    private
	ObjectType(com.redhat.persistence.metadata.ObjectType type) {
	super(type);
        m_type = type;
    }


    /**
     * Returns the supertype of this ObjectType or null if this is a base
     * type.
     *
     * @return The supertype of this ObjectType or null if this is a base.
     **/

    public ObjectType getSupertype() {
        return ObjectType.wrap(m_type.getSupertype());
    }


    /**
     * Returns the base type of this ObjectType (which may simply be the
     * current ObjectType).
     *
     * @return The base type of this ObjectType.
     **/

    public ObjectType getBasetype() {
	return ObjectType.wrap(m_type.getBasetype());
    }


    /**
     *  Returns true if the given Property is one of the key Properties on this
     *  ObjectType.
     *
     *  @param p The property to check.
     *  @return true if is a key property.
     */
    public boolean isKeyProperty(Property p) {
	return m_type.isKeyProperty(p.getName());
    }

    /**
     *  Returns true if the given Property name belongs to this Object type,
     *  and is one of the key Properties.
     *
     *  @param name The name of a property to check.
     *  @return true if is a key property.
     */
    public boolean isKeyProperty(String name) {
	return m_type.isKeyProperty(name);
    }

    /**
     * Returns an Iterator containing all the properties that are part of this
     * ObjectType's key.
     *
     * @return An Iterator containing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getKeyProperties() {
	return Property.wrap(m_type.getKeyProperties()).iterator();
    }

    /**
     * Returns an Iterator containing all the properties that are part of this
     * ObjectType. This includes any properties defined in this ObjectType's
     * supertype.
     *
     * @return An Iterator containing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getProperties() {
        return Property.wrap(m_type.getProperties()).iterator();
    }

    /**
     * Returns an Iterator containing all the properties that are defined by
     * this ObjectType directly. This does <i>not</i> include any properties
     * that are defined in this ObjectType's supertype.
     *
     * @return An Iterator contianing instances of the Property class.
     *
     * @see Property
     **/

    public Iterator getDeclaredProperties() {
        return Property.wrap(m_type.getDeclaredProperties()).iterator();
    }


    /**
     * Returns true if this ObjectType contains a Property with the given
     * name. This includes any Properties inherited from the supertype.
     *
     * @param name The name of the property to query for.
     *
     * @return True if this ObjectType contains a Property with the given
     *         name, false otherwise.
     **/

    public boolean hasProperty(String name) {
	return m_type.hasProperty(name);
    }


    /**
     * Returns true if this ObjectType directly defines a Property with the
     * given name. This does <i>not</i> any Properties inherited from the
     * supertype.
     *
     * @param name The name of the property to query for.
     *
     * @return True if this ObjectType contains a directly defined Property
     *         with the given name, false otherwise.
     **/

    public boolean hasDeclaredProperty(String name) {
        return m_type.hasDeclaredProperty(name);
    }


    /**
     * Returns the Property contained by this ObjectType with the given name
     * or null if no such property exists. This includes any properties that
     * may be defined by the supertype.
     *
     * @param name The name of the property to retrieve.
     *
     * @return An instance of Property or null.
     **/

    public Property getProperty(String name) {
	return Property.wrap(m_type.getProperty(name));
    }


    /**
     * Returns the Property directly defined by this ObjectType with the given
     * name or null if no such property exists. This does <i>not</i> include
     * any properties that may be defined by the supertype.
     *
     * @param name The name of the property to retrieve.
     *
     * @return An instance of Property or null.
     **/

    public Property getDeclaredProperty(String name) {
        return Property.wrap(m_type.getDeclaredProperty(name));
    }


    /**
     * @see #isSubtypeOf(ObjectType)
     **/

    public boolean isSubtypeOf(String qualifiedName) {
	return m_type.isSubtypeOf(qualifiedName);
    }

    /**
     * Returns true if this ObjectType is a subtype of <i>type</i>. The
     * definition of the subtype relation is that A is a subtype of B if and
     * only if anywhere in code that B appears, A can appear as well and the
     * code will still function. This means that an object type is a subtype
     * of itself.
     *
     * @param type The candidate supertype.
     *
     * @return True if this ObjectType is a subtype of <i>type</i>.
     **/

    public boolean isSubtypeOf(ObjectType type) {
	return m_type.isSubtypeOf(type.m_type);
    }


    /**
     * Checks if the <code>ObjectType</code> specified by <code>extendedType</code>
     * is a subtype of the <code>ObjectType</code> specified by <code>baseType</code>.
     *
     * @param baseType The base object type.
     * @param extendedType The extended object type.
     *
     * @pre extendedType.isSubtypeOf(baseType)
     *
     * @exception PersistenceException Thrown if <code>extendedType</code>
     * is not a subtype of the <code>ObjectType</code> specified by
     * <code>baseType</code>.
     **/
    public static void verifySubtype(ObjectType baseType,
                                     ObjectType extendedType) {
        if (!extendedType.isSubtypeOf(baseType)) {
            throw new PersistenceException
		("The object type '" + extendedType.getQualifiedName() +
		 "' is not a subtype of the object type '" +
		 baseType.getQualifiedName() + "'");
        }
    }

    public static void verifySubtype(String baseType,
                                     String extendedType) {
        verifySubtype
	    (MetadataRoot.getMetadataRoot().getObjectType(baseType),
	     MetadataRoot.getMetadataRoot().getObjectType(extendedType));
    }

    public static void verifySubtype(String baseType,
                                     ObjectType extendedType) {
        ObjectType baseObjectType =
	    MetadataRoot.getMetadataRoot().getObjectType(baseType);
        Assert.isTrue(baseObjectType != null,
			  "Could not find the ObjectType for the " +
                          "base type.  The base type was: " + baseType + ".");
        verifySubtype(baseObjectType, extendedType);
    }

    public static void verifySubtype(ObjectType baseType,
                                     String extendedType) {
        verifySubtype
	    (baseType,
	     MetadataRoot.getMetadataRoot().getObjectType(extendedType));
    }

}
