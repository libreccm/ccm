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
        //return (Publication) DomainObjectFactory.newInstance(m_dataCollection.
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getPrimaryInstance();
    }
    
      public Publication getPublication(final String language) {
        //return (Publication) DomainObjectFactory.newInstance(m_dataCollection.
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getInstance(language);
    }

    /*public BigDecimal getID() {
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
     }*/
}
