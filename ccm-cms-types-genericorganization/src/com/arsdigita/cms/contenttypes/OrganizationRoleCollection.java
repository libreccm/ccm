package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

public class OrganizationRoleCollection extends DomainCollection {

    //public static final String versionId = "$Id: OrganizationRoleCollection.java 001 2009-05-28 12:40:00Z jensp $";

    public OrganizationRoleCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    @Override
    public DomainObject getDomainObject() {
        return new OrganizationRole(m_dataCollection.getDataObject());
    }

    public OrganizationRole getOrganizationRole() {
        return (OrganizationRole) getDomainObject();
    }
}