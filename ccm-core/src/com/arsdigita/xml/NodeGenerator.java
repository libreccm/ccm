/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.xml;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.metadata.Property;

/**
 * An interface providing an API for converting an object
 * to a new Element. This is useful when rendering objects where
 * the detail is obtained from an external source. In this
 * case the passed in value will be an attribute that enables the 
 * external data to be found and the NodeGenerator can 
 * produce a node representing the retrieved attributes.
 * 
 */
public interface NodeGenerator {
    Element format(DomainObject parent, Property property, Object value);
}
