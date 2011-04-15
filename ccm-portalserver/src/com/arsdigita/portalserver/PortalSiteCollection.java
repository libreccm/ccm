/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: PortalSiteCollection.java  pboy $
 */
public class PortalSiteCollection extends ApplicationCollection {

    private static final Logger s_log = Logger.getLogger
        (PortalSiteCollection.class);

    public PortalSiteCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public void filterToPortalSite(BigDecimal id) {
        m_dataCollection.addEqualsFilter("id", id);
    }

    public void filterForArchived() {
        m_dataCollection.addEqualsFilter("isArchived", Boolean.TRUE);
    }

    public void filterForUnarchived() {
        m_dataCollection.addEqualsFilter("isArchived", Boolean.FALSE);
    }

    /**
     * Get the current item as a PortalSite domain object.
     *
     * @return a PortalSite domain object.
     * @post return != null
     */
    public PortalSite getPortalSite() {
        PortalSite portalsite = (PortalSite)getDomainObject();

     // Assert.assertNotNull(portalsite);
        Assert.exists(portalsite);

        return portalsite;
    }
}
