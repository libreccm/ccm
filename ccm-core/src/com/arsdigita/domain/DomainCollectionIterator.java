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
package com.arsdigita.domain;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Wraps a {@link DomainCollection} as an {@link Iterator}
 *
 * NOTE:
 * This iterator reads an entire DomainCollection into memory and stores in an internal container.
 * Asside from being inefficient & preventing the use of filters, this can potentially cause data aliasing problems
 * in pre 5.3 core. Should only be used for passing to systems that don't directly support DomainCollections.
 *
 * @author Stanislav Freidin
 * @version $Id: DomainCollectionIterator.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class DomainCollectionIterator implements Iterator {

    private List m_collection = new LinkedList();
    private Iterator m_iterator;

    /**
     * Wrap the specified <code>DomainCollection</code> in an iterator
     */
    public DomainCollectionIterator(DomainCollection c) {

        try {
            while (c.next()) {
                m_collection.add(c.getDomainObject());
            }

            m_iterator = m_collection.iterator();
        } finally {
            c.close();
        }

    }

    /**
     * Return true if there are more rows in the collection, false otherwise
     */
    public boolean hasNext() {
        return m_iterator.hasNext();
    }

    /**
     * Return the current domain object
     */
    public Object next() {
        return m_iterator.next();
    }

    /**
     * Not Implemented
     */
    public void remove() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
