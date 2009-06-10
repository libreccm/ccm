package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.PersistenceException;
/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleCollection extends DomainCollection {

    public OrganizationRoleCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    @Override
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }

    @Override
    public DomainObject getDomainObject() {
        return new OrganizationRole(m_dataCollection.getDataObject());
    }

    public OrganizationRole getOrganizationRole() {
        return (OrganizationRole) getDomainObject();
    }

    @Override
    public Filter addFilter(String conditions) {
        return m_dataCollection.addFilter(conditions);
    }

    @Override
    public void clearFilter() {
        m_dataCollection.clearFilter();
    }

    @Override
    public void clearOrder() throws PersistenceException {
        m_dataCollection.clearOrder();
    }
}
