package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectSponsorCollection extends DomainCollection {

    public SciProjectSponsorCollection(final DataCollection dataCollection) {
        super(dataCollection);

        addOrder("name");
    }

    public GenericOrganizationalUnit getSponsor() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
                m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }

    public GenericOrganizationalUnit getSponsor(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
                m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public BigDecimal getID() {
        return getSponsor().getID();
    }

    public String getTitle() {
        return getSponsor().getTitle();
    }

}
