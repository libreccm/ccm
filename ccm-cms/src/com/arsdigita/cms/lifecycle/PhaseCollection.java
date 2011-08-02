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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

/**
 * This class contains a collection for Phases
 *
 * @see DomainCollection
 * @see DataCollection
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #8 $ $Date: 2004/08/17 $
 * @version $Id: PhaseCollection.java 2090 2010-04-17 08:04:14Z pboy $
 **/
public class PhaseCollection extends DomainCollection {

    /**
     * Constructor.
     */
    public PhaseCollection(DataCollection dataCollection) {
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
     */
    public DomainObject getDomainObject() {
        return new Phase(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>ContentItem</code> for the current position in
     * the collection.
     */
    public Phase getPhase() {
        return (Phase) getDomainObject();
    }
}
