package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationBundleCollection extends DomainCollection {
    
    public PublicationBundleCollection(final DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection.addOrder("name asc");        
    }
    
    public PublicationBundle getPublicationBundle() {
        return new PublicationBundle(m_dataCollection.getDataObject());
    }
    
    public Publication getPublication() {
        final ContentBundle  bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getPrimaryInstance();
    }
    
    public Publication getPublication(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getInstance(language);
    }
    
}
