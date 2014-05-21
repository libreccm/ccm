/*;
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsPublicationCollection extends DomainCollection {

    public SciPublicationsPersonsPublicationCollection(final DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder("name");
    }

    public Publication getPublication() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
            m_dataCollection.getDataObject());

        return (Publication) bundle.getPrimaryInstance();
    }

    public Publication getPublication(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
            m_dataCollection.getDataObject()
        );

        return (Publication) bundle.getInstance(language);
    }

}
