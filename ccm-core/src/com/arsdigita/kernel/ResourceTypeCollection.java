/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p>A collection of ResourceTypes.  This class includes methods
 * to filter and order the results of retrieving resource
 * types.</p>
 *
 * @see com.arsdigita.kernel.ResourceType
 * @see com.arsdigita.kernel.ResourceCollection
 * @author Jim Parsons
 */
public class ResourceTypeCollection extends DomainCollection {

    private static final Logger s_log = Logger.getLogger
        (ResourceTypeCollection.class);

    public ResourceTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }


    public void orderByTitle() {
        m_dataCollection.addOrder("title");
    }

    public DomainObject getDomainObject() {
        return getResourceType();
    }


    public ResourceType getResourceType() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Assert.exists(dataObject, "dataObject");

        ResourceType resourceType =
            ResourceType.retrieveResourceType(dataObject);

        return resourceType;
    }

    public String getResourceObjectType() {
        return (String) m_dataCollection.get("objectType");
    }

    public String getTitle() {
        return (String) m_dataCollection.get("title");
    }

    public BigDecimal getID() {
        return (BigDecimal)m_dataCollection.get("id");
    }

}
