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

import com.arsdigita.persistence.DataCollection;

/**
 * Represents a collection of parties.
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 **/
public class ExampleMessageCollection extends ACSObjectCollection {

    public static final String versionId = "$Id: ExampleMessageCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor.
     *
     * @see com.arsdigita.domain.ACSObjectCollection#ACSObjectCollection(DataCollection)
     **/
    public ExampleMessageCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns the URI for this Message.
     *
     * @return the URI for this Message.
     **/
    public String getSubject() {
        return (String) m_dataCollection.get("subject");
    }

    /**
     * Returns the URI for this Message.
     *
     * @return the URI for this Message.
     **/
    public String getBody() {
        return (String) m_dataCollection.get("message");
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>Message</code>.
     *
     * @return a <code>Message</code> for the current position in the
     * collection.
     *
     * @see ACSObjectCollection#getDomainObject()
     * @see Message
     * @see com.arsdigita.domain.DomainObject
     **/
    public ExampleMessage getMessage() {
        return (ExampleMessage) getDomainObject();
    }

}
