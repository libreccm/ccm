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
package com.arsdigita.web;

import com.arsdigita.kernel.ResourceTypeCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * <p>A collection of ApplicationTypes.  This class includes methods
 * to filter and order the results of retrieving application
 * types.</p>
 *
 * @see com.arsdigita.web.ApplicationType
 * @see com.arsdigita.web.ApplicationCollection
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: ApplicationTypeCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ApplicationTypeCollection extends ResourceTypeCollection {
    public static final String versionId =
        "$Id: ApplicationTypeCollection.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (ApplicationTypeCollection.class);

    public ApplicationTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public void filterToFullPageViewable() {
        m_dataCollection.addEqualsFilter("hasFullPageView", Boolean.TRUE);
    }

    public void filterToWorkspaceApplication() {
        m_dataCollection.addEqualsFilter("isWorkspaceApplication",
                                         Boolean.TRUE);
    }

    public void filterToEmbeddable() {
        m_dataCollection.addEqualsFilter("hasEmbeddedView", Boolean.TRUE);
    }

    public void orderByTitle() {
        m_dataCollection.addOrder("title");
    }

    public DomainObject getDomainObject() {
        return getApplicationType();
    }

    public ApplicationType getApplicationType() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Assert.exists(dataObject, "dataObject");

        ApplicationType applicationType =
            ApplicationType.retrieveApplicationType(dataObject);

        return applicationType;
    }

    public String getApplicationObjectType() {
        return (String) m_dataCollection.get("objectType");
    }

    public String getTitle() {
        return (String) m_dataCollection.get("title");
    }

    public BigDecimal getID() {
        return (BigDecimal)m_dataCollection.get("id");
    }

}
