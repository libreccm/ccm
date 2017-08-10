/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.london.terms.portation.conversion.core.web;


import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.core.Resource;
import com.arsdigita.london.terms.portation.modules.core.core.ResourceType;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;
import com.arsdigita.web.Application;

import java.util.List;

/**
 * Class for converting all trunk-{@link Application}s into
 * ng-{@link CcmApplication}s as preparation for a successful export of all
 * trunkclasses into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/2/17
 */
public class CcmApplicationConversion {
    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.ResourceType}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link ResourceType}s focusing on keeping all
     * the associations in tact.
     */
    public static void convertAll() {
        System.err.printf("\tFetching ccm applications from database...");
        List<Application> trunkApplications = Application
                .getAllApplicationObjects();
        System.err.println("done.");

        System.err.printf("\tConverting ccm applications...\n");
        // create ccm applications
        createCcmApplicationsAndSetAssociations(trunkApplications);
        setRingAssociations(trunkApplications);
        System.err.printf("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@code Application} and restores
     * the associations to other classes.
     *
     * @param trunkApplications List of all {@link Application}s
     *                          from this old trunk-system.
     */
    private static void createCcmApplicationsAndSetAssociations(
            List<Application> trunkApplications) {
        long processed = 0;

        for (Application trunkApplication : trunkApplications) {
            // create applications
            CcmApplication ccmApplication = new CcmApplication
                    (trunkApplication);

            // set resource type
            com.arsdigita.kernel.ResourceType trunkResourceType = trunkApplication
                    .getResourceType();
            if (trunkResourceType != null) {
                ResourceType resourceType = NgCoreCollection
                        .resourceTypes
                        .get(trunkResourceType.getID().longValue());
                ccmApplication.setResourceType(resourceType);
            }

            //System.err.println(String.format(
            //        "ccm application id: %d", ccmApplication.getObjectId()));
            processed++;
        }
        System.err.printf("\t\tCreated %d ccm applications.\n", processed);
    }

    /**
     * Method for setting the parent {@link Resource} on the one side and the
     * sub-{@link Resource}s on the other side. Attribute of class
     * {@link Resource}.
     *
     * @param trunkApplications List of all {@link Application} from the old
     *                          trunk-system.
     */
    private static void setRingAssociations(List<Application> trunkApplications) {
        for (Application trunkApplication : trunkApplications) {
            CcmApplication ccmApplication = NgCoreCollection
                    .ccmApplications
                    .get(trunkApplication.getID().longValue());

            // set parent Resource and opposed association
            CcmApplication parentResource = null;

            Application trunkParent = trunkApplication
                    .getParentApplication();
            if (trunkParent != null) {
                parentResource = NgCoreCollection
                        .ccmApplications
                        .get(trunkParent.getID().longValue());

                ccmApplication.setParent(parentResource);
                parentResource.addChild(ccmApplication);
            }
        }
    }
}
