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

import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/5/18
 */
public class PhaseDefinition implements Portable {
    private long definitionId;
    private LocalizedString label;
    private LocalizedString description;
    private long defaultDelay;
    private long defaultDuration;
    private String defaultListener;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkPhaseDefinition the trunk object
     */
    public PhaseDefinition(final com.arsdigita.cms.lifecycle.PhaseDefinition
                                   trunkPhaseDefinition) {
        this.definitionId = trunkPhaseDefinition.getID().longValue();

        this.label = new LocalizedString();
        this.description = new LocalizedString();
        final Locale locale = Locale.getDefault();
        this.label.addValue(locale,
                trunkPhaseDefinition.getLabel());
        this.description.addValue(locale,
                trunkPhaseDefinition.getDescription());

        this.defaultDelay = trunkPhaseDefinition
                .getDefaultDelay().longValue();
        final Integer delay = trunkPhaseDefinition.getDefaultDuration();
        if (delay != null)
            this.defaultDuration = delay.longValue();

        this.defaultListener = trunkPhaseDefinition.getDefaultListener();

        NgCmsCollection.phaseDefinitions.put(this.definitionId, this);
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

    public long getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(final long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public long getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(final long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public String getDefaultListener() {
        return defaultListener;
    }

    public void setDefaultListener(final String defaultListener) {
        this.defaultListener = defaultListener;
    }
}
