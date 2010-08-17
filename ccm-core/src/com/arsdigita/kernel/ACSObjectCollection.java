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
package com.arsdigita.kernel;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

// identity class
import java.math.BigDecimal;

/**
 * Represents a collection of ACSObject domain objects.
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 * @version $Id: ACSObjectCollection.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class ACSObjectCollection extends DomainCollection {

    /**
     * Constructor.
     *
     * @see com.arsdigita.domain.DomainCollection#DomainCollection(DataCollection)
     **/
    public ACSObjectCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Gets the value of the ID property.
     *
     * @return the value of the ID property.
     */
    public BigDecimal getID() {
        return (BigDecimal) m_dataCollection.get("id");
    }

    /**
     * 
     * Gets the denormalized display name of this object.
     *
     * @return the denormalized display name of this object.
     *
     * @see ACSObject#getDisplayName()
     */
    public String getDisplayName() {
        return (String) m_dataCollection.get("displayName");
    }

    /**
     * Gets the value of the objectType property, which is the fully
     * qualified name of the data object type that this domain object
     * had when it was first created.
     * @return the fully qualified name of this object
     */
    public String getSpecificObjectType() {
        return (String) m_dataCollection.get("objectType");
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     * @see com.arsdigita.domain.DomainObject
     * @see com.arsdigita.persistence.DataCollection#getDataObject()
     **/
    public DomainObject getDomainObject() {
        DataObject data = m_dataCollection.getDataObject();
        return DomainObjectFactory.newInstance(data);
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as an <code>ACSObject</code>.
     *
     * @return an <code>ACSObject</code> for the current position in the
     * collection.
     *
     * @see #getDomainObject()
     * @see Party
     * @see com.arsdigita.domain.DomainObject
     **/
    public ACSObject getACSObject() {
        return (ACSObject) getDomainObject();
    }

}
