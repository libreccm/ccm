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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <p>A collection of Applications.  This class includes methods to
 * filter and order the results of retrieving applications.</p>
 *
 * @see com.arsdigita.web.Application
 * @see com.arsdigita.web.ApplicationType
 * @see com.arsdigita.web.ApplicationTypeCollection
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: ApplicationCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ApplicationCollection extends ACSObjectCollection {
    public static final String versionId =
        "$Id: ApplicationCollection.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (ApplicationCollection.class);

    public ApplicationCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public void filterToApplicationType(String type) {
        m_dataCollection.addEqualsFilter(ACSObject.OBJECT_TYPE, type);
    }

    public void filterToWorkspaceApplications() {
        m_dataCollection.addEqualsFilter
            ("resourceType.isWorkspaceApplication", Boolean.TRUE);
    }

    public void filterToPortalSiteApplications() {
        m_dataCollection.addEqualsFilter
            ("resourceType.isWorkspaceApplication", Boolean.TRUE);
    }

    public void filterToHasFullPageView() {
        m_dataCollection.addEqualsFilter
            ("resourceType.hasFullPageView", Boolean.TRUE);
    }

    public void filterToHasEmbeddedView() {
        m_dataCollection.addEqualsFilter
            ("resourceType.hasEmbeddedView", Boolean.TRUE);
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
     * Get the current item as an Application domain object.
     *
     * @return an Application domain object.
     * @post return != null
     */
    public Application getApplication() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Application application =
            Application.retrieveApplication(dataObject);

        Assert.exists(application, "application");

        return application;
    }

    /**
     * Get the title for the application for the current row.
     *
     * @return the title of this application.
     * @post return != null
     */
    public String getTitle() {
        String title = (String)m_dataCollection.get("title");

        Assert.exists(title, "title");

        return title;
    }

    /**
     * Returns the title of the parent application.
     *
     * @return The title of the parent application.
     **/
    public String getParentTitle() {
        return (String) m_dataCollection.get("parentResource.title");
    }

    public String getDescription() {
        return (String) m_dataCollection.get("description");
    }

    /**
     * Get the primary URL for the current row's application.
     *
     * @return the primary URL of this application.
     * @post return != null
     */
    public String getPrimaryURL() {
        String primaryURL = (String)m_dataCollection.get("primaryURL");

        Assert.exists(primaryURL, "primaryURL");

        return primaryURL;
    }
}
