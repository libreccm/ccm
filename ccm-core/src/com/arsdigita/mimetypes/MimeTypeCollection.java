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
package com.arsdigita.mimetypes;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;

/**
 * This class contains a collection of MimeTypes.
 *
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @version 1.0
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 */
public class MimeTypeCollection extends DomainCollection {

    /**
     * Constructor.
     *
     **/
    public MimeTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Set the order of this Collection. This method needs to be called
     * before <code>next()</code> is called on this collection.
     *
     */
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     **/
    public DomainObject getDomainObject() {
        return new MimeType(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>MimeType</code> for the current position in
     * the collection.
     *
     **/
    public MimeType getMimeType() {
        return ((MimeType) getDomainObject()).specialize();
    }

}
