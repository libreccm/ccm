/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.lucene;


/**
 * @deprecated moved to com.arsdigita.search.lucene
 * @see com.arsdigita.search.lucene.TypeSpecificFilter
 **/
public class TypeSpecificFilter 
    extends com.arsdigita.search.lucene.TypeSpecificFilter {

    /**
     * Creates a new lucene <code>Filter</code> that filters search results
     * based on whether the "type-specific field" matches the terms supplied in
     * the <code>typeSpecificInfo</code> argument.
     *
     * @param typeSpecificInfo the object type to filter on
     *
     **/
    public TypeSpecificFilter(String typeSpecificInfo) {
        super(typeSpecificInfo);
    }
}
