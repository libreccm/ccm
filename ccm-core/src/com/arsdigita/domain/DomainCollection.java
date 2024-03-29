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

import com.arsdigita.persistence.DataCollection;


/**
 * This is the base class that all other persistent collection classes
 * would extend. It provides navigation methods that delegate to a
 * contained <code>DataCollection</code>.
 *
 * @see com.arsdigita.persistence.DataCollection
 *
 * @author Phong Nguyen
 * @version $Id: DomainCollection.java 2089 2010-04-17 07:55:43Z pboy $
 */
public class DomainCollection extends DomainQuery {

    protected final DataCollection m_dataCollection;

    /**
     * Constructor.
     * @see com.arsdigita.persistence.DataCollection
     *
     * @param dataCollection
     **/
    public DomainCollection(DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection = dataCollection;
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     * @see DomainObject
     *
     * @return 
     **/
    public DomainObject getDomainObject() {
        return
            DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
    }
}
