/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class VolumeInSeriesCollection extends DomainCollection {

    public static final String LINK_VOLUME_OF_SERIES = "link.volumeOfSeries";
    public static final String VOLUME_OF_SERIES = "volumeOfSeries";
    public static final Logger s_log =
                               Logger.getLogger(VolumeInSeriesCollection.class);

    public VolumeInSeriesCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINK_VOLUME_OF_SERIES);
    }

    public Integer getVolumeOfSeries() {
        return (Integer) m_dataCollection.get(LINK_VOLUME_OF_SERIES);
    }

    public void setVolumeOfSeries(Integer volumeOfSeries) {
        DataObject link = (DataObject) this.get("link");

        link.set(VOLUME_OF_SERIES, volumeOfSeries);
    }
  
    public Publication getPublication() {
        final PublicationBundle bundle = (PublicationBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getPrimaryInstance();
    }
    
      public Publication getPublication(final String language) {
        final PublicationBundle bundle = (PublicationBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (Publication) bundle.getInstance(language);
    }
}
