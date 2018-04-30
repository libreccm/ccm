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
import com.arsdigita.cms.portation.modules.lifecycle.Phase;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/26/18
 */
public class PhaseConversion extends AbstractConversion {
    private static PhaseConversion instance;

    static {
        instance = new PhaseConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.lifecycle.Phase}s from
     * the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Phase}s focusing on keeping all the
     * associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("phases");
        List<com.arsdigita.cms.lifecycle.Phase> trunkPhases = com.arsdigita
                .cms.lifecycle.Phase.getAllObjects();

        ExportLogger.converting("phases");
        createPhasesAndSetAssociations(trunkPhases);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code Phase} and restores
     * the associations to other classes.
     *
     * @param trunkPhases List of all {@link com.arsdigita.cms.lifecycle.Phase}s
     *                    from this old trunk-system.
     */
    private void createPhasesAndSetAssociations(final List<com.arsdigita.cms
            .lifecycle.Phase> trunkPhases) {
        int processed = 0;
        for (com.arsdigita.cms.lifecycle.Phase trunkPhase : trunkPhases) {
            // create phase
            Phase phase = new Phase(trunkPhase);

            // set phase definition
            phase.setPhaseDefinition(NgCmsCollection
                    .phaseDefinitions
                    .get(trunkPhase
                            .getPhaseDefinition()
                            .getID()
                            .longValue()));

            // set lifecycle and opposed association
            Lifecycle lifecycle = NgCmsCollection
                    .lifecycles
                    .get(trunkPhase
                            .getLifecycle()
                            .getID()
                            .longValue());
            phase.setLifecycle(lifecycle);
            lifecycle.addPhase(phase);

            processed++;
        }
        ExportLogger.created("phases", processed);
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static PhaseConversion getInstance() {
        return instance;
    }
}
