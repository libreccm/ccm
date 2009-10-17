/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.portal;

import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;

/**
 * <p>Collection of Portals.</p>
 * <p>
 * Describes a set of portal domain objects.
 * </p>
 * @see Portal
 * @author Justin Ross
 * @version $Id: PortalCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PortalCollection extends ACSObjectCollection {

    protected PortalCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the current item as a Portal domain object.
     *
     * @return a Portal domain object.
     * @post return != null
     */
    public Portal getPortal() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Portal portal = Portal.retrieve(dataObject);

        Assert.exists(portal, "portal");

        return portal;
    }
}
