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
package com.arsdigita.domain;

import com.arsdigita.persistence.metadata.Property;

/**
 * <p>This interface is used to control traversal of domain
 * objects. Whenever a property is encountered, the {@link
 * #processProperty} method will be called to determine whether or not
 * to continue processing the object.  The most important use for this
 * is to prevent the needless (and potentially infinite) traversal of
 * associations between objects, but it can also be used to filter out
 * certain attributes.</p>
 *
 * <p>Instances of this class need to be registered using the
 * DomainObjectTraversal.registerAdapter method.</p>
 *
 * @see com.arsdigita.domain.DomainObjectTraversal
 * @see com.arsdigita.domain.SimpleDomainObjectTraversalAdapter
 * @version $Id: DomainObjectTraversalAdapter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface DomainObjectTraversalAdapter {
    /**
     * Invoked to determine whether to process a property.
     * Should return true to allow processing to commence,
     * false to prevent it.
     *
     * @param obj the domain object currently being processed
     * @param path the path to the current domain object from
     * the root object being traversed
     * @param prop the property about to be processed
     * @param context the context in which the object is traversed
     * @return true if the property should be processed
     */
    public boolean processProperty(DomainObject obj,
                                   String path,
                                   Property prop,
                                   String context);
}
