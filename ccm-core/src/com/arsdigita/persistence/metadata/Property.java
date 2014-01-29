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

import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.pdl.PDL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The Property class represents one property of a CompoundType. Each property
 * is a "field" in the CompoundType. Every Property has an associated
 * DataType. This allows CompoundTypes to be constructed from multiple
 * SimpleTypes and CompoundTypes. In addition to having an associated
 * DataType, each property has an associated multiplicity. There are currently
 * three possible values for the multiplicity of a Property, NULLABLE,
 * REQUIRED, and COLLECTION.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #17 $ $Date: 2004/08/16 $
 **/

public class Property extends Element {


    /**
     * These are the integer type codes for the multiplicity of a Property.
     **/

    /**
     * The NULLABLE multiplicity is for single valued properties that can be
     * set to null.
     **/
    public final static int NULLABLE = 0;

    /**
     * The REQUIRED multiplicity is for single valued properties that cannot
     * be set to null.
     **/
    public final static int REQUIRED = 1;

    /**
     * The COLLECTION multiplicity is for multi valued properties.
     **/
    public final static int COLLECTION = 2;

    /**
     * This is for the outputPDL method to use to display the multiplicity of the
     * Property.
     **/
    private final static String[] s_multiplicityText = {
        "",
        "[1..1]",
        "[0..n]"
    };

    


    static Property
	wrap(com.redhat.persistence.metadata.Property prop) {
	if (prop == null) {
	    return null;
	} else {
	    return new Property(prop);
	}
    }

    static Collection wrap(Collection props) {
	ArrayList result = new ArrayList(props.size());
	for (Iterator it = props.iterator(); it.hasNext(); ) {
	    com.redhat.persistence.metadata.Property prop =
		(com.redhat.persistence.metadata.Property) it.next();
	    if (prop.getName().charAt(0) != '~'
                && prop.getName().indexOf(PDL.LINK) == -1) {
		result.add(wrap(prop));
	    }
	}
	return result;
    }


    com.redhat.persistence.metadata.Property m_prop;

    private Property(com.redhat.persistence.metadata.Property prop) {
        super(prop.getRoot(), prop);
	m_prop = prop;
    }


    /**
     * Returns the container of this property.
     **/

    public CompoundType getContainer() {
        return ObjectType.wrap(m_prop.getContainer());
    }

    /**
     * Returns the name of this Property.
     *
     * @return The name of this Property.
     **/

    public String getName() {
        return m_prop.getName();
    }


    /**
     * Returns the type of this Property.
     *
     * @return The type of this Property.
     **/

    public DataType getType() {
	if (isAttribute()) {
	    return SimpleType.wrap(m_prop.getType());
	} else {
	    return ObjectType.wrap(m_prop.getType());
	}
    }


    /**
     * Returns true if this Property is an attribute, i.e. its DataType is
     * simple.
     *
     * @return True if this Property is an attribute, false otherwise.
     **/

    public boolean isAttribute() {
        return m_prop.getType().getModel().getName().equals("global");
    }


    /**
     * Returns true if this Property is a role, i.e. it's DataType is
     * compound.
     *
     * @return True if this Property is a role, false otherwise.
     **/

    public boolean isRole() {
        return !isAttribute();
    }


    /**
     * Returns the integer type code for the multiplicity of this property.
     *
     * @return An integer that is always one of the type codes defined in this
     *         class.
     **/

    public int getMultiplicity() {
	if (isCollection()) {
	    return COLLECTION;
	} else if (isNullable()) {
	    return NULLABLE;
	} else {
	    return REQUIRED;
	}
    }


    /**
     * Returns true if the multiplicity of this Property is COLLECTION.
     *
     * @return True if the property is a COLLECTION.
     **/

    public boolean isCollection() {
        return m_prop.isCollection();
    }

    /**
     * Returns true if the multiplicity of this Property is NULLABLE.
     *
     * @return True if the multiplicity of this Property is NULLABLE.
     **/

    public boolean isNullable() {
        return m_prop.isNullable();
    }


    /**
     * Returns true if the multiplicity of this Property is REQUIRED.
     *
     * @return True if the multiplicity of this Property is REQUIRED.
     **/

    public boolean isRequired() {
        return !m_prop.isNullable() && !m_prop.isCollection();
    }


    /**
     * Returns true if this property is a component.
     *
     * @return true if this property is a component.
     **/

    public boolean isComponent() {
        return m_prop.isComponent();
    }


    /**
     * Returns the associon that this property plays a role in, or null if
     * this property doesn't play a role in an association.
     *
     * @return An association in which this property is a role, or null.
     **/

    public Association getAssociation() {
	Property p = getAssociatedProperty();
	if (p == null) {
	    return null;
	} else {
	    return new Association(this, p);
	}
    }


    /**
     * Returns the type of the link object used by this association, or null
     * if there is none.
     *
     * @return The type of the link object used by this association, or null.
     **/

    public CompoundType getLinkType() {
	if (m_prop instanceof Link) {
	    return ObjectType.wrap(((Link) m_prop).getLinkType());
	} else {
	    return null;
	}
    }


    /**
     * Returns the associated property or null if there is no associated
     * property.
     **/

    public Property getAssociatedProperty() {
	if (m_prop instanceof Role) {
	    return Property.wrap(((Role) m_prop).getReverse());
	} else {
	    return null;
	}
    }


    /**
     * Returns true if this property refers to the composite of the object
     * type containing this property.
     **/

    public boolean isComposite() {
        Property rev = getAssociatedProperty();
	if (rev == null) {
	    return false;
	} else {
	    return rev.isComponent();
	}
    }


    /**
     * Returns the java class for the object that will be returned when
     * DataObject.get() is called on this Property.
     *
     * @return A java Class object.
     **/

    public Class getJavaClass() {
        if (isRole()) {
            if (isCollection()) {
                return com.arsdigita.persistence.DataAssociation.class;
            } else {
                return com.arsdigita.persistence.DataObject.class;
            }
        } else {
            return ((SimpleType) getType()).getJavaClass();
        }
    }

    public boolean isKeyProperty() {
	return m_prop.isKeyProperty();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String container =
            getContainer() == null ? "" : getContainer().getQualifiedName();
        return sb.append(container).append(".").append(getName()).toString();
    }
}
