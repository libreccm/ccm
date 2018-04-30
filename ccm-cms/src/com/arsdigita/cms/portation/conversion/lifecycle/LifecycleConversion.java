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
package com.arsdigita.cms.portation.conversion.lifecycle;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.lifecycle.Lifecycle;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/26/18
 */
public class LifecycleConversion extends AbstractConversion {
    private static LifecycleConversion instance;

    static {
        instance = new LifecycleConversion();
    }

    /**
     * Retrieves all
     * trunk-{@link com.arsdigita.cms.lifecycle.Lifecycle}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Lifecycle}s focusing on keeping all
     * the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("lifecycles");
        List<com.arsdigita.cms.lifecycle.Lifecycle> trunkLifecycles = com
                .arsdigita.cms.lifecycle.Lifecycle.getAllObjects();

        ExportLogger.converting("lifecycles");
        createLifecyclesAndSetAssociations(trunkLifecycles);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code Lifecycle} and restores
     * the associations to other classes.
     *
     * @param trunkLifecycles List of all
     *                        {@link com.arsdigita.cms.lifecycle.Lifecycle}s
     *                        from this old trunk-system.
     */
    private void createLifecyclesAndSetAssociations(final List<com.arsdigita
            .cms.lifecycle.Lifecycle> trunkLifecycles) {
        int processed = 0;
        for (com.arsdigita.cms.lifecycle.Lifecycle trunkLifecycle :
                trunkLifecycles) {

            // create lifecycle
            Lifecycle lifecycle = new Lifecycle(trunkLifecycle);

            // set lifecycle definition
            lifecycle.setLifecycleDefinition(NgCmsCollection
                    .lifecycleDefinitions
                    .get(trunkLifecycle
                            .getLifecycleDefinition()
                            .getID()
                            .longValue()));

            processed++;
        }
        ExportLogger.created("lifecycles", processed);
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static LifecycleConversion getInstance() {
        return instance;
    }
}
