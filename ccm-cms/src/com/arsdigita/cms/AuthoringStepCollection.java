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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

/**
 * Describes a set of {@link com.arsdigita.cms.AuthoringStep authoring
 * steps}.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 *
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @version $Id: AuthoringStepCollection.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class AuthoringStepCollection extends DomainCollection {

    /**
     * Constructor.
     *
     **/
    public AuthoringStepCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Set the order of this Collection. This method needs to be called
     * before <code>next()</code> is called on this collection.  */
    public void setOrder(String order) {
        m_dataCollection.clearOrder();
        m_dataCollection.addOrder(order);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     **/
    public DomainObject getDomainObject() {
        return new AuthoringStep(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>AuthoringStep</code> for the current position in
     * the collection.
     *
     **/
    public AuthoringStep getAuthoringStep() {
        return (AuthoringStep) getDomainObject();
    }

}
