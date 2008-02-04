/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.filters;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;

import com.arsdigita.util.Assert;

/**
 * A filter spec for supplying a list of object types
 * to the object type filter type. There are two flags
 * can alter the semantics of the filter. The first 
 * specifies whether the list of an inclusion (white)
 * or exclusion (black) list.
 */
public class ObjectTypeFilterSpecification extends FilterSpecification {

    public final static String TYPES = "types";
    public final static String EXCLUSION = "exclude";
    
    /**
     * Creates a new filter restricting results to a single
     * object type.
     * @param typeName the object type name
     */
    public ObjectTypeFilterSpecification(String typeName) {
        this(lookupTypes(new String[] {typeName}));
    }

    /**
     * Creates a new filter restricting results to a set
     * object types.
     * @param typeName the object type names
     */
    public ObjectTypeFilterSpecification(String[] typeNames) {
        this(lookupTypes(typeNames));
    }

    /**
     * Creates a new filter restricting results to a single
     * object type.
     * @param type the object type
     */
    public ObjectTypeFilterSpecification(ObjectType type) {
        super(new Object[] { TYPES, new ObjectType[] { type } },
              new ObjectTypeFilterType());
    }

    /**
     * Creates a new filter restricting results to a set
     * object type.
     * @param type the object types
     */
    public ObjectTypeFilterSpecification(ObjectType[] types) {
        super(new Object[] { TYPES, types },
              new ObjectTypeFilterType());
    }
    
    /**
     * Returns the set of object types to filter on
     * @return the object type
     */
    public ObjectType[] getTypes() {
        return (ObjectType[])get(TYPES);
    }
    
    /**
     * Sets the flag indicating that the type list is
     * an exclusion list rather than an inclusion list.
     * Default is an inclusion list.
     *
     * @param exclude true to mark as an exclusion list
     */
    public void setExclusion(boolean exclude) {
        set(EXCLUSION, new Boolean(exclude));
    }

    /**
     * Gets the flag indicating that the type list is
     * an exclusion list rather than an inclusion list.
     *
     * @return true if marked as an exclusion list
     */
    public boolean isExclusion() {
        return Boolean.TRUE.equals(get(EXCLUSION));
    }
    
    private static ObjectType[] lookupTypes(String[] typeNames) {
        Assert.exists(typeNames, String[].class);
        ObjectType[] types = new ObjectType[typeNames.length];
        for (int i = 0 ; i < typeNames.length ; i++) {
            types[i] = MetadataRoot.getMetadataRoot().getObjectType(typeNames[i]);
        }
        return types;
    }
}
