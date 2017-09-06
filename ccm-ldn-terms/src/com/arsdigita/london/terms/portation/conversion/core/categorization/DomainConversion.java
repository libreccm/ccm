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
package com.arsdigita.london.terms.portation.conversion.core.categorization;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.categorization.Domain;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainOwnership;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.web.Application;

import java.util.List;

/**
 * Class for converting all trunk-{@link com.arsdigita.london.terms.Domain}s
 * into ng-{@link Domain}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
public class DomainConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.london.terms.Domain}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Domain}s focusing on keeping all the
     * associations in tact.
     */
    public static void convertAll() {
        System.err.printf("\tFetching domains from database...");
        List<com.arsdigita.london.terms.Domain> trunkDomains = com
                .arsdigita.london.terms.Domain.getAllObjectDomains();
        System.err.println("done.");

        System.err.printf("\tConverting domains and domain ownerships...\n");
        createDomainsAndSetAssociations(trunkDomains);
        System.err.println("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@code Domain} and restores
     * the associations to other classes.
     *
     * @param trunkDomains List of all {@link com.arsdigita.london.terms.Domain}s
     *                     from this old trunk-system.
     */
    private static void createDomainsAndSetAssociations(
            List<com.arsdigita.london.terms.Domain> trunkDomains) {
        long processedDomains = 0, processedDomainOwnerships = 0;

        for(com.arsdigita.london.terms.Domain trunkDomain : trunkDomains) {
            // create domains
            Domain domain = new Domain(trunkDomain);

            // set root (category) association
            com.arsdigita.categorization.Category trunkModel = trunkDomain
                    .getModel();
            if (trunkModel != null) {
                Category root = com.arsdigita.portation.conversion
                        .NgCoreCollection
                        .categories
                        .get(trunkModel.getID().longValue());
                domain.setRoot(root);
            }

            // create domain ownerships
            DomainCollection useContexts = trunkDomain.getUseContexts();
            processedDomainOwnerships += createDomainOwnerships(domain,
                    useContexts);

            processedDomains++;
        }
        System.err.printf("\t\tCreated %d domains and %d domain ownerships.\n",
                processedDomains, processedDomainOwnerships);
    }

    /**
     * Method for creating {@link DomainOwnership}s between {@link Domain}s
     * and {@link CcmApplication}s which is an association-class and has not
     * been existent in this old system.
     *
     * @param domain The {@link Domain}
     * @param useContexts A collection containing the {@code owner}s of the
     *                    {@link Domain} and its {@code context}
     *
     * @return Number of how many {@link DomainOwnership}s have been processed.
     */
    private static long createDomainOwnerships(Domain domain,
                                               DomainCollection useContexts) {
        long processed = 0;

        while (useContexts.next()) {
            final DomainObject obj = DomainObjectFactory
                    .newInstance((DataObject) useContexts
                            .getDomainObject()
                            .get("categoryOwner"));
            if (obj instanceof Application) {
                CcmApplication owner = NgCoreCollection
                        .ccmApplications
                        .get(((Application) obj)
                                .getID()
                                .longValue());
                String context = (String) useContexts
                        .getDomainObject()
                        .get("useContext");

                if (domain != null && owner != null) {
                    // create domain ownerships
                    DomainOwnership domainOwnership = new DomainOwnership(domain,
                            owner, context);

                    // set opposed associations
                    domain.addOwner(domainOwnership);
                    owner.addDomain(domainOwnership);

                    processed++;
                }
            }
        }
        return processed;
    }


}
