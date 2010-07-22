/*
 * Copyright (c) 2010 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 * Collection class for the GenericOrganizationalUnit -> Contact relation.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactCollection extends DomainCollection {

    public static final String ORDER = "link.contact_order asc";
    public static final String CONTACT_TYPE = "link.contact_type";
    public static final String CONTACT_ORDER = "link.contact_order";

    public GenericOrganizationalUnitContactCollection(
            DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(ORDER);
    }

    // Get the contact type of the link
    public String getContactType() {
        return (String) m_dataCollection.get(CONTACT_TYPE);
    }

    // Get the contact order of the link
    public String getContactOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(CONTACT_ORDER)).
                toString();

        if (retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }

        return retVal;
    }

    public GenericContact getContact() {
        return new GenericContact(m_dataCollection.getDataObject());
    }
}
