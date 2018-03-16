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
package com.arsdigita.cms.portation.modules.lifecycle;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/5/18
 */
public class LifecycleDefinition implements Portable {
    private long definitionId;
    private LocalizedString label;
    private LocalizedString description;
    private String defaultListener;
    @JsonIdentityReference(alwaysAsId = true)
    private List<PhaseDefinition> phaseDefinitions;

    public LifecycleDefinition(final com.arsdigita.cms.lifecycle
            .LifecycleDefinition trunkLifecycleDefinition) {
        this.definitionId = trunkLifecycleDefinition.getID().longValue();

        this.label = new LocalizedString();
        this.description = new LocalizedString();
        final Locale locale = Locale.getDefault();
        this.label.addValue(locale, trunkLifecycleDefinition.getLabel());
        this.description
                .addValue(locale, trunkLifecycleDefinition.getDescription());

        this.defaultListener = trunkLifecycleDefinition.getDefaultListener();

        this.phaseDefinitions = new ArrayList<>();

        NgCmsCollection.lifecycleDefinitions.put(this.definitionId, this);
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getDefaultListener() {
        return defaultListener;
    }

    public void setDefaultListener(final String defaultListener) {
        this.defaultListener = defaultListener;
    }

    public List<PhaseDefinition> getPhaseDefinitions() {
        return phaseDefinitions;
    }

    public void setPhaseDefinitions(final List<PhaseDefinition> phaseDefinitions) {
        this.phaseDefinitions = phaseDefinitions;
    }

    public void addPhaseDefinition(final PhaseDefinition phaseDefinition) {
        this.phaseDefinitions.add(phaseDefinition);
    }

    public void removePhaseDefinition(final PhaseDefinition phaseDefinition) {
        this.phaseDefinitions.remove(phaseDefinition);
    }
}
