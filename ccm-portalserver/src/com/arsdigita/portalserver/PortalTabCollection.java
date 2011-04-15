/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

    /**
     * This class represents the Domain Object Collection for 
     * <code> PortalTab </code>. 
     *
     * @author Jim Parsons
     */

public class PortalTabCollection extends DomainCollection {

    protected PortalTabCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the ID for the PortalTab for the current row.
     *
     * @return the id of this PortalTab.
     * @post return != null
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)m_dataCollection.get("id");

     // Assert.assertNotNull(id);
        Assert.exists(id);

        return id;
    }

    /**
     * Get the current item as a domain object.
     *
     * @return the domain object for the current row.
     * @post return != null
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getPortalTab();

     // Assert.assertNotNull(domainObject);
        Assert.exists(domainObject);

        return domainObject;
    }

    /**
     * Get the current item as a PortalTab domain object.
     *
     * @return a Portal domain object.
     * @post return != null
     */
    public PortalTab getPortalTab() {
        DataObject dataObject = m_dataCollection.getDataObject();

        PortalTab portalTab = PortalTab.retrieveTab(dataObject);

     // Assert.assertNotNull(portalTab);
        Assert.exists(portalTab);

        return portalTab;
    }

    /**
     * Get the title for the PortalTab for the current row.
     *
     * @return the title of this PortalTab.
     * @post return != null
     */
    public String getTitle() {
        String title = (String)m_dataCollection.get("title");

     // Assert.assertNotNull(title);
        Assert.exists(title);

        return title;
    }
}
