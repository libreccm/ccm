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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.util.Date;

/**
 * Collection class for the GenericOrganizationalUnit -> Person relation.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonCollection extends DomainCollection {

    public static final String LINK_PERSON_ROLE = "link.role_name";
    public static final String LINK_STATUS = "link.status";
    public static final String PERSON_ROLE = "role_name";
    public static final String STATUS = "status";

    public GenericOrganizationalUnitPersonCollection(
            DataCollection dataCollection) {
        super(dataCollection);
        dataCollection.addOrder("name asc");
    }

    /**
     * Gets the name of the role of this orgaunit-person link
     * @return
     */
    public String getRoleName() {
        return (String) m_dataCollection.get(LINK_PERSON_ROLE);
    }

    public void setRoleName(final String roleName) {
        DataObject link = (DataObject) this.get("link");

        link.set(PERSON_ROLE, roleName);
    }

    public String getStatus() {
        return (String) m_dataCollection.get(LINK_STATUS);
    }

    public void setStatus(final String status) {
        DataObject link = (DataObject) this.get("link");

        link.set(STATUS, status);
    }

    public GenericPerson getPerson() {
        /*return (GenericPerson) DomainObjectFactory.newInstance(m_dataCollection.
         getDataObject());*/
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericPerson) bundle.getPrimaryInstance();
    }

    public OID getOID() {
        return m_dataCollection.getDataObject().getOID();
    }

    public String getSurname() {
        /*return (String) m_dataCollection.getDataObject().get(
         GenericPerson.SURNAME);*/
        return getPerson().getSurname();
    }

    public String getGivenName() {
        /*return (String) m_dataCollection.getDataObject().get(
         GenericPerson.GIVENNAME);*/
        return getPerson().getGivenName();
    }

    public String getTitlePre() {
        /*return (String) m_dataCollection.getDataObject().get(
         GenericPerson.TITLEPRE);*/
        return getPerson().getTitlePre();
    }

    public String getTitlePost() {
        /*return (String) m_dataCollection.getDataObject().get(
         GenericPerson.TITLEPOST);*/
        return getPerson().getTitlePost();
    }

    public Date getBirthdate() {
        /*return (Date) m_dataCollection.getDataObject().get(
         GenericPerson.BIRTHDATE);*/
        return getPerson().getBirthdate();
    }

    public String getGender() {
        /*return (String) m_dataCollection.getDataObject().get(
         GenericPerson.GENDER);*/
        return getPerson().getGender();
    }

    public GenericPerson getAlias() {
        /*return (DataObject) m_dataCollection.getDataObject().get(
         GenericPerson.ALIAS);*/
        return getPerson().getAlias();
    }
}
