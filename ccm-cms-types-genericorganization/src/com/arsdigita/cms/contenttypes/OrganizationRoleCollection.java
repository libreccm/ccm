package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

/**
 * Collection containing all roles associated to an organization.
 *
 * @author Jens Pelzetter
 */
public class OrganizationRoleCollection extends DomainCollection {

    //public static final String versionId = "$Id: OrganizationRoleCollection.java 001 2009-05-28 12:40:00Z jensp $";

    /**
     * Creates an object of this class from an DataCollection object.
     *
     * @param dataCollection
     */
    public OrganizationRoleCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    @Override
    public DomainObject getDomainObject() {
        return new OrganizationRole(m_dataCollection.getDataObject());
    }

    /**
     * @return the item at the current position
     */
    public OrganizationRole getOrganizationRole() {
        return (OrganizationRole) getDomainObject();
    }
}