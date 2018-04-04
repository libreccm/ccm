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
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.Date;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/5/18
 */
public class Phase implements Portable {
    private long phaseId;
    private Date startDateTime;
    private Date endDateTime;
    private String listener;
    private boolean started;
    private boolean finished;
    @JsonIdentityReference(alwaysAsId = true)
    private Lifecycle lifecycle;
    @JsonIdentityReference(alwaysAsId = true)
    private PhaseDefinition phaseDefinition;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkPhase the trunk object
     */
    public Phase(final com.arsdigita.cms.lifecycle.Phase trunkPhase) {
        this.phaseId = trunkPhase.getID().longValue();

        this.startDateTime = trunkPhase.getStartDate();
        this.endDateTime = trunkPhase.getEndDate();

        this.listener = trunkPhase.getListenerClassName();

        this.started = trunkPhase.hasBegun();
        this.finished = trunkPhase.hasEnded();

        //this.lifecycle
        //this.phaseDefinition

        NgCmsCollection.phases.put(this.phaseId, this);
    }

    public long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(final long phaseId) {
        this.phaseId = phaseId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(final Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(final Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(final String listener) {
        this.listener = listener;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(final boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(final Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public PhaseDefinition getPhaseDefinition() {
        return phaseDefinition;
    }

    public void setPhaseDefinition(final PhaseDefinition phaseDefinition) {
        this.phaseDefinition = phaseDefinition;
    }
}
