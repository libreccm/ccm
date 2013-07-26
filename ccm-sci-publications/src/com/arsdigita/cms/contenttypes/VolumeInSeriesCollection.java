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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class VolumeInSeriesCollection extends DomainCollection {

    public static final String LINK_VOLUME = "link.volumeOfSeries";
    public static final String VOLUME_OF_SERIES = "volumeOfSeries";
    public static final Logger LOGGER = Logger.getLogger(VolumeInSeriesCollection.class);

    public VolumeInSeriesCollection(final DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINK_VOLUME);
    }

    public String getVolumeOfSeries() {
        return (String) m_dataCollection.get(LINK_VOLUME);
    }

    public void setVolumeOfSeries(final String volumeOfSeries) {
        final DataObject link = (DataObject) this.get("link");

        link.set(VOLUME_OF_SERIES, volumeOfSeries);
    }

    public Publication getPublication() {
        final PublicationBundle bundle = (PublicationBundle) DomainObjectFactory.newInstance(m_dataCollection.
                getDataObject());
        return (Publication) bundle.getPrimaryInstance();
    }

    public Publication getPublication(final String language) {
        final PublicationBundle bundle = (PublicationBundle) DomainObjectFactory.newInstance(m_dataCollection.
                getDataObject());
        return (Publication) bundle.getInstance(language);
    }

}
