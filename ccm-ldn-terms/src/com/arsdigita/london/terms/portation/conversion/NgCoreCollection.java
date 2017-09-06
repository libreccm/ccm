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
package com.arsdigita.london.terms.portation.conversion;

import com.arsdigita.london.terms.portation.modules.core.categorization.Domain;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainOwnership;
import com.arsdigita.london.terms.portation.modules.core.core.Resource;
import com.arsdigita.london.terms.portation.modules.core.core.ResourceType;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/28/17
 */
public class NgCoreCollection {
    public static Map<Long, ResourceType> resourceTypes = new HashMap<>();
    public static Map<Long, Resource> resources = new HashMap<>();
    public static Map<Long, CcmApplication> ccmApplications = new HashMap<>();

    public static Map<Long, Domain> domains = new HashMap<>();
    public static Map<Long, DomainOwnership> domainOwnerships = new HashMap<>();


    // if lists need to be sorted in specific way to work with import
    public static ArrayList<CcmApplication> sortedCcmApplications;


    /**
     * Private constructor to prevent the instantiation of this class.
     */
    private NgCoreCollection() {}

    /**
     * Sorts values of resource-map to ensure that the parent-resources will
     * be listed before their childs in the export file.
     *
     * Runs once over the unsorted list and iterates over each their parents
     * to add them to the sorted list. After being added to the sorted list the
     * resource will be removed from the unsorted list and therefore ignored
     * in this foreach run.
     */
    public static void sortCcmApplications() {
        ArrayList<CcmApplication> unsortedCcmApplications =
                new ArrayList<>(ccmApplications.values());
        sortedCcmApplications = new ArrayList<>(unsortedCcmApplications.size());

        int runs = 0;
        for (CcmApplication anUnsorted : unsortedCcmApplications) {
            add(anUnsorted);
            runs++;
        }
        System.err.printf("\t\tSorted ccm applications in %d runs.\n", runs);
    }

    /**
     * Helper method to recursively add all parent resources before their
     * childs.
     *
     * @param ccmApplication the current resource in the unsorted list
     */
    private static void add(CcmApplication ccmApplication) {
        CcmApplication parent = (CcmApplication) ccmApplication.getParent();

        if (parent != null && !sortedCcmApplications.contains(parent)) {
            add(parent);
        }

        if (!sortedCcmApplications.contains(ccmApplication)) {
            sortedCcmApplications.add(ccmApplication);
        }
    }
}
