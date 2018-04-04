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
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/5/18
 */
public class Lifecycle implements Portable {
    private long lifecycleId;
    private Date startDateTime;
    private Date endDateTime;
    private String listener;
    private boolean started;
    private boolean finished;
    @JsonIdentityReference(alwaysAsId = true)
    private LifecycleDefinition lifecycleDefinition;
    @JsonIgnore
    private List<Phase> phases;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkLifecycle the trunk object
     */
    public Lifecycle(final com.arsdigita.cms.lifecycle.Lifecycle
                             trunkLifecycle) {
        this.lifecycleId = trunkLifecycle.getID().longValue();

        this.startDateTime = trunkLifecycle.getStartDate();
        this.endDateTime = trunkLifecycle.getEndDate();

        this.listener = trunkLifecycle.getListenerClassName();
        this.started = trunkLifecycle.hasBegun();
        this.finished = trunkLifecycle.hasEnded();

        //this.lifecycleDefinition
        this.phases = new ArrayList<>();

        NgCmsCollection.lifecycles.put(this.lifecycleId, this);
    }

    public long getLifecycleId() {
        return lifecycleId;
    }

    public void setLifecycleId(final long lifecycleId) {
        this.lifecycleId = lifecycleId;
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

    public LifecycleDefinition getLifecycleDefinition() {
        return lifecycleDefinition;
    }

    public void setLifecycleDefinition(final LifecycleDefinition lifecycleDefinition) {
        this.lifecycleDefinition = lifecycleDefinition;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases(final List<Phase> phases) {
        this.phases = phases;
    }

    public void addPhase(final Phase phase) {
        this.phases.add(phase);
    }

    public void removePhase(final Phase phase) {
        this.phases.remove(phase);
    }
}
