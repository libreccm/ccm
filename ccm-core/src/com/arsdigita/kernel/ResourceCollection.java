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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <p>A collection of Resources.  This class includes methods to
 * filter and order the results of retrieving resources.</p>
 *
 * @see com.arsdigita.kernel.Resource
 * @see com.arsdigita.kernel.ResourceType
 * @see com.arsdigita.kernel.ResourceTypeCollection
 * 
 * @author Jim Parsons &lt;<a href="mailto:jparsons@redhat.com">jparsons@redhat.com</a>&gt;
 * @version $Id: ResourceCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ResourceCollection extends ACSObjectCollection {

    private static final Logger s_log = Logger.getLogger
        (ResourceCollection.class);

    public ResourceCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public void filterToResourceType(String type) {
        m_dataCollection.addEqualsFilter(ACSObject.OBJECT_TYPE, type);
    }


    /**
     * Orders by the title of the application type.
     **/
    public void orderByTypeTitle() {
        m_dataCollection.addOrder("resourceType.title");
    }

    public void orderByTitle() {
        m_dataCollection.addOrder("title");
    }

    public void orderByParentTitle() {
        m_dataCollection.addOrder("parentResource.title");
    }

    /**
     * Get the current item as an Resource domain object.
     *
     * @return a Resource domain object.
     * @post return != null
     */
    public Resource getResource() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Resource resource =
            Resource.retrieveResource(dataObject);

        Assert.exists(resource, "resource");

        return resource;
    }

    /**
     * Get the title for the resource for the current row.
     *
     * @return the title of this resource.
     * @post return != null
     */
    public String getTitle() {
        String title = (String)m_dataCollection.get("title");

        Assert.exists(title, "title");

        return title;
    }

    /**
     * Returns the title of the parent resource.
     *
     * @return The title of the parent resource.
     **/
    public String getParentTitle() {
        return (String) m_dataCollection.get("parentResource.title");
    }

    public String getDescription() {
        return (String) m_dataCollection.get("description");
    }

}
