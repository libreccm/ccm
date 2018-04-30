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


import com.arsdigita.cms.portation.modules.lifecycle.PhaseDefinition;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/26/18
 */
public class PhaseDefinitionConversion extends AbstractConversion {
    private static PhaseDefinitionConversion instance;

    static {
        instance = new PhaseDefinitionConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.lifecycle.PhaseDefinition}s
     * from the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link PhaseDefinition}s focusing on keeping
     * all the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("phase definitions");
        List<com.arsdigita.cms.lifecycle.PhaseDefinition> trunkPhaseDefinitions
                = com.arsdigita.cms.lifecycle.PhaseDefinition.getAllObjects();

        ExportLogger.converting("phase definitions");
        createPhaseDefinitionsAndSetAssociations(trunkPhaseDefinitions);

        ExportLogger.newLine();
    }

    private void createPhaseDefinitionsAndSetAssociations(final List<com
            .arsdigita.cms.lifecycle.PhaseDefinition> trunkPhaseDefinitions) {
        int processed = 0;
        for (com.arsdigita.cms.lifecycle.PhaseDefinition
                trunkPhaseDefinition : trunkPhaseDefinitions) {

            // create phase definitions
            new PhaseDefinition(trunkPhaseDefinition);

            processed++;
        }
        ExportLogger.created("phase definitions", processed);
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static PhaseDefinitionConversion getInstance() {
        return instance;
    }
}
