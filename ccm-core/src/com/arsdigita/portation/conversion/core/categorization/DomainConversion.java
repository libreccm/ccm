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
package com.arsdigita.portation.conversion.core.categorization;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.categorization.Domain;
import com.arsdigita.portation.modules.core.categorization.DomainOwnership;
import com.arsdigita.portation.modules.core.web.CcmApplication;
import com.arsdigita.web.Application;

/**
 * Class for converting all trunk-{@link com.arsdigita.london.terms.Domain}s
 * into ng-{@link Domain}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
public class DomainConversion extends AbstractConversion {
    private static DomainConversion instance;

    static {
        instance = new DomainConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.london.terms.Domain}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Domain}s focusing on keeping all the
     * associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("domains");
        DataCollection trunkDomains = SessionManager.getSession()
                .retrieve("com.arsdigita.london.terms.Domain");

        ExportLogger.converting("doamins and domain ownerships");
        createDomainsAndSetAssociations(trunkDomains);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code Domain} and restores
     * the associations to other classes.
     *
     * @param trunkDomains List of all {@link com.arsdigita.london.terms.Domain}s
     *                     from this old trunk-system.
     */
    private void createDomainsAndSetAssociations(DataCollection trunkDomains) {
        int processedDomains = 0, processedDomainOwnerships = 0;

        while(trunkDomains.next()) {
            DataObject trunkDomain = trunkDomains.getDataObject();
            if (trunkDomain != null) {
                // create domains
                Domain domain = new Domain(trunkDomain);


                com.arsdigita.categorization.Category trunkModel =
                        (com.arsdigita.categorization.Category) DomainObjectFactory
                                .newInstance((DataObject) trunkDomain
                                        .get("model"));
                if (trunkModel != null) {
                    // set root (category) association
                    Category root = NgCoreCollection
                            .categories
                            .get(trunkModel.getID().longValue());
                    domain.setRoot(root);

                    // create domain ownerships
                    DataCollection useContexts = SessionManager
                            .getSession()
                            .retrieve("com.arsdigita." +
                                    "categorization.UseContext");
                    useContexts.addEqualsFilter(
                            "rootCategory.id", trunkModel.getID());

                    processedDomainOwnerships += createDomainOwnerships(
                            domain, new DomainCollection(useContexts));
                }

                processedDomains++;
            }
        }
        ExportLogger.created("domains", processedDomains);
        ExportLogger.created("domain ownerships", processedDomainOwnerships);
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
    private long createDomainOwnerships(Domain domain,
                                        DomainCollection useContexts) {
        int processed = 0;

        while (useContexts.next()) {
            final DomainObject obj = DomainObjectFactory
                    .newInstance((DataObject) useContexts
                            .getDomainObject()
                            .get("categoryOwner"));
            if (obj instanceof Application) {
                CcmApplication owner = NgCoreCollection
                        .ccmApplications
                        .get(((Application) obj).getID().longValue());
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

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static DomainConversion getInstance() {
        return instance;
    }
}
