package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitPublicationsCollection
        extends DomainCollection {

    public GenericOrganizationalUnitPublicationsCollection(
            final DataCollection dataCollection) {
        super(dataCollection);
    }

    public Publication getPublication() {
        return (Publication) DomainObjectFactory.newInstance(m_dataCollection.
                getDataObject());
    }

    public BigDecimal getID() {
         return (BigDecimal) m_dataCollection.getDataObject().get(ACSObject.ID);
    }
    
    public String getTitle() {
        return (String) m_dataCollection.getDataObject().get(ContentPage.TITLE);
    }

    public Integer getYearOfPublication() {
        return (Integer) m_dataCollection.getDataObject().get(
                Publication.YEAR_OF_PUBLICATION);
    }

    public String getAbstract() {
        return (String) m_dataCollection.getDataObject().get(
                Publication.ABSTRACT);
    }

    public String getMisc() {
        return (String) m_dataCollection.getDataObject().get(Publication.MISC);
    }

    public AuthorshipCollection getAuthors() {
        return new AuthorshipCollection((DataCollection) m_dataCollection.
                getDataObject().get(Publication.AUTHORS));
    }

    public SeriesCollection getSeries() {
        return new SeriesCollection((DataCollection) m_dataCollection.
                getDataObject().get(Publication.SERIES));
    }
}
