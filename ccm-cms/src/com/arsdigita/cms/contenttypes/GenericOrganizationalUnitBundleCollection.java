package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrganizationalUnitBundleCollection extends DomainCollection {
    
    public GenericOrganizationalUnitBundleCollection(final DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection.addOrder("name asc");
    }
    
    public GenericOrganizationalUnitBundle getGenericOrganizationalUnitBundle() {
        return new GenericOrganizationalUnitBundle(m_dataCollection.getDataObject());
    }
    
    public GenericOrganizationalUnit getGenericOrganizationalUnit() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }
    
     public GenericOrganizationalUnit getGenericOrganizationalUnit(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }
    
}
