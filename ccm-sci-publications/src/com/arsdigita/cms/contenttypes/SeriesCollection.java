package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesCollection extends DomainCollection {

    public SeriesCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public Series getSeries() {
        return new Series(m_dataCollection.getDataObject());
    }

}
