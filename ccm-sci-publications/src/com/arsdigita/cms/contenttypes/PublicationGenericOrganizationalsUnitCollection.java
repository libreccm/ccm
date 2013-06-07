/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationGenericOrganizationalsUnitCollection
        extends DomainCollection {

    public PublicationGenericOrganizationalsUnitCollection(
            final DataCollection dataCollection) {
        super(dataCollection);

        addOrder("name");
    }

    public GenericOrganizationalUnit getOrganizationalUnit() {
        //return (GenericOrganizationalUnit) DomainObjectFactory.newInstance(
        //        m_dataCollection.getDataObject());
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }
    
    public GenericOrganizationalUnit getOrganizationalUnit(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public BigDecimal getID() {
         return getOrganizationalUnit().getID();
    }
    
    public String getTitle() {
        //return (String) m_dataCollection.getDataObject().get(ContentPage.TITLE);
        return getOrganizationalUnit().getTitle();
    }
    
    /*public String getAddendum() {
        return (String) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.ADDENDUM);
    }*/

    /*public GenericOrganizationalUnitContactCollection getContacts() {
        return new GenericOrganizationalUnitContactCollection(
                (DataCollection) m_dataCollection.get(
                GenericOrganizationalUnit.CONTACTS));
    }

    public GenericOrganizationalUnitPersonCollection getPersons() {
        return new GenericOrganizationalUnitPersonCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.PERSONS));
    }

    public GenericOrganizationalUnitSuperiorCollection getSuperiorOrgaUnits() {
        return new GenericOrganizationalUnitSuperiorCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.SUPERIOR_ORGAUNITS));
    }

    public GenericOrganizationalUnitSubordinateCollection getSubordinateOrgaUnits() {
        return new GenericOrganizationalUnitSubordinateCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.SUBORDINATE_ORGAUNITS));
    }*/
}
