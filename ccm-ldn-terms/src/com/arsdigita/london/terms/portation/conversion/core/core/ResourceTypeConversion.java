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
package com.arsdigita.london.terms.portation.conversion.core.core;

import com.arsdigita.london.terms.portation.modules.core.core.ResourceType;

import java.util.List;

/**
 * Class for converting all trunk-{@link com.arsdigita.kernel.ResourceType}s
 * into ng-{@link ResourceType}s as preparation for a successful export of
 * all trunk classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/2/17
 */
public class ResourceTypeConversion {
    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.ResourceType}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link ResourceType}s focusing on keeping all
     * the associations in tact.
     */
    public static void convertAll() {
        System.err.printf("\tFetching resource types from database...");
        List<com.arsdigita.kernel.ResourceType> trunkResourceTypes = com
                .arsdigita.kernel.ResourceType.getAllObjectResourceTypes();
        System.err.println("done.");

        System.err.printf("\tConverting domains...\n");
        // create resource types
        int processed = 0;
        for (com.arsdigita.kernel.ResourceType trunkResourceType :
                trunkResourceTypes) {
            new ResourceType(trunkResourceType);
            processed++;
        }
        System.out.printf("\t\tCreated %d resource types.\n", processed);
        System.err.println("\tdone.\n");
    }
}
