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
package com.arsdigita.cms.search;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.cms.ContentType;

import com.arsdigita.util.Assert;

/**
 * A filter spec for supplying a list of content types
 * to the content type filter type. 
 */
public class ContentTypeFilterSpecification extends FilterSpecification {

    public final static String TYPES = "types";
    
    /**
     * Creates a new filter restricting results to a single
     * content type.
     * @param typeName the content type name
     */
    public ContentTypeFilterSpecification(String typeName) {
        this(lookupTypes(new String[] {typeName}));
    }

    /**
     * Creates a new filter restricting results to a set
     * content types.
     * @param typeName the content type names
     */
    public ContentTypeFilterSpecification(String[] typeNames) {
        this(lookupTypes(typeNames));
    }

    /**
     * Creates a new filter restricting results to a single
     * content type.
     * @param type the content type
     */
    public ContentTypeFilterSpecification(ContentType type) {
        super(new Object[] { TYPES, new ContentType[] { type } },
              new ContentTypeFilterType());
    }

    /**
     * Creates a new filter restricting results to a set
     * content type.
     * @param type the content types
     */
    public ContentTypeFilterSpecification(ContentType[] types) {
        super(new Object[] { TYPES, types },
              new ContentTypeFilterType());
    }
    
    /**
     * Returns the set of object types to filter on
     * @return the object type
     */
    public ContentType[] getTypes() {
        return (ContentType[])get(TYPES);
    }

    private static ContentType[] lookupTypes(String[] typeNames) {
        Assert.exists(typeNames, String[].class);
        ContentType[] types = new ContentType[typeNames.length];
        for (int i = 0 ; i < typeNames.length ; i++) {
            types[i] = ContentType.findByAssociatedObjectType(typeNames[i]);
        }
        return types;
    }
}
