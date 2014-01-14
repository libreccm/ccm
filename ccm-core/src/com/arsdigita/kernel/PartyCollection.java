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
import com.arsdigita.persistence.Filter;

/**
 * Represents a collection of parties.
 *
 * @author Phong Nguyen 
 * @version 1.0
 **/
public class PartyCollection extends ACSObjectCollection {

    /**
     * Constructor.
     *
     * @see ACSObjectCollection#ACSObjectCollection(DataCollection)
     **/
    public PartyCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns the URI for this party.
     *
     * @return the URI for this party.
     **/
    public String getURI() {
        return (String) m_dataCollection.get("uri");
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>Party</code>.
     *
     * @return a <code>Party</code> for the current position in the
     * collection.
     *
     * @see ACSObjectCollection#getDomainObject()
     * @see Party
     * @see com.arsdigita.domain.DomainObject
     **/
    public Party getParty() {
        return (Party) getDomainObject();
    }

    /**
     *
     * Filter this party collection to parties whose name or email
     * contain the given search string.
     **/
    public void filter(String searchString) {
        Filter f = m_dataCollection.getFilterFactory()
            .or()
            .addFilter("lower(displayName) like '%' || :s || '%'")
            .addFilter("lower(primaryEmail) like '%' || :s || '%'");
        f.set("s", searchString.toLowerCase());
        m_dataCollection.addFilter(f);
    }

    /**
     *
     * Get the primary email address of this party.
     **/
    public EmailAddress getPrimaryEmail() {
        String email = (String) m_dataCollection.get("primaryEmail");
        if (email==null) {
            return null;
        }
        return new EmailAddress((String) m_dataCollection.get("primaryEmail"));
    }
}
