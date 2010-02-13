/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.lifecycle;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Definition for a publication lifecycle. Associated with each cycle
 * definition is a set of phase definition. Each phase definition can only
 * be associated with one cycle definition. To remove the a phase definition
 * from this cycle definition, call the <code>delete()</code> method on
 * that particular phase definition
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #14 $ $Date: 2004/08/17 $
 * @version $Id: LifecycleDefinition.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class LifecycleDefinition extends ACSObject {

    private static Logger s_log = Logger.getLogger(LifecycleDefinition.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.LifecycleDefinition";

    protected static final String LABEL = "label";
    protected static final String DESCRIPTION = "description";
    protected static final String DEFAULT_LISTENER = "defaultListener";

    protected static final String PHASE_DEFINITIONS = "phaseDefinitions";

    /**
     * Default constructor. This creates a new life cycle definition.
     */
    public LifecycleDefinition() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     */
    public LifecycleDefinition(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>LifecycleDefinition.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public LifecycleDefinition(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public LifecycleDefinition(DataObject obj) {
        super(obj);
    }

    protected LifecycleDefinition(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this definition. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getLabel() {
        return (String) get(LABEL);
    }

    public void setLabel(String label) {
        set(LABEL, label);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDefaultListener() {
        return (String) get(DEFAULT_LISTENER);
    }

    public void setDefaultListener(String listener) {
        set(DEFAULT_LISTENER, listener);
    }

    /**
     * Add a phase definition to this lifecycle definition.
     * To save this phase
     * definition, you need to call <code>save()</code> method on the
     * returned PhaseDefinition.
     */
    public PhaseDefinition addPhaseDefinition() {
        return addPhaseDefinition("Phase", null, null, null, null);
    }

    /**
     * Add a phase definition to this lifecycle definition.  To save this phase
     * definition, you need to call <code>save()</code> method on the
     * returned PhaseDefinition.
     *
     * @param label Name of this phase definition
     * @param description Decription of this phase definition,
     *   Pass in <code>null</code> if there is no description
     * @param delay Default delay for the start of this phase definition
     *   in minutes relative to the publish date. Pass in
     *   <code>null</code> or <code>0</code> is this phase definition
     *   begins at the publish date.
     * @param duration Duration of this phase definition in minutes.
     *   Pass <code>null</code> if this phase definition never ends.
     * @param defaultListener Default Listener class  of this phase definition.
     */
    public PhaseDefinition addPhaseDefinition(String label, 
                                              String description,
                                              Integer delay,
                                              Integer duration,
                                              String defaultListener) {

        PhaseDefinition pd = new PhaseDefinition();
        pd.setLifecycleDefinition(this);
        pd.setLabel(label);

        if ( description != null ) {
            pd.setDescription(description);
        }

        if ( delay != null ) {
            pd.setDefaultDelay(delay);
        }

        if ( duration != null ) {
            pd.setDefaultDuration(duration);
        }

        if ( defaultListener != null ) {
            pd.setDefaultListener(defaultListener);
        }

        return pd;
    }

    /**
     * Get the phase definitions for this lifecycle definition.  To remove the
     * phase definition from this lifecycle definition, you need to explicitly
     * call the <code>delete()</code> method on that particular phase
     * definition.
     */
    public PhaseDefinitionCollection getPhaseDefinitions() {
        DataAssociationCursor dac = ((DataAssociation) get(PHASE_DEFINITIONS)).cursor();
        return new PhaseDefinitionCollection(dac);
    }

    /**
     * Creates and returns a cycle using this lifecycle definition. To save
     * this lifecycle, you need to call <code>save()</code> method on the
     * returned Lifecycle.
     */
    public Lifecycle createLifecycle() {
        Lifecycle lifecycle = new Lifecycle();
        lifecycle.setLifecycleDefinition(this);
        lifecycle.setListenerClassName(getDefaultListener());

        return lifecycle;
    }

    /**
     * Creates and returns a cycle using this lifecycle definition; then,
     * populates the lifecycle with phases and saves it.
     *
     * @param startDate the date when the lifecycle should start, or
     *   null if the cycle should start immediately
     * @param listenerClassName the classname of the listener for the
     *   new lifecycle
     */
    public Lifecycle createFullLifecycle(Date startDate, String listenerClassName) {
        // Use the current time if startDate is null.
        if ( startDate == null ) {
            startDate = new Date();
        }
        
        Lifecycle lifecycle = createLifecycle();
        lifecycle.setListenerClassName(listenerClassName);
        lifecycle.save();

        // Add phases.
        PhaseDefinitionCollection phaseDefs = getPhaseDefinitions();
        while ( phaseDefs.next() ) {
            PhaseDefinition phaseDef = phaseDefs.getPhaseDefinition();
            // Minutes
            Integer delay = phaseDef.getDefaultDelay();
            // Minutes
            Integer duration = phaseDef.getDefaultDuration();

            // Add a phase, setting the start and end date.
            Phase phase = lifecycle.addPhase(phaseDef);
            long begin = startDate.getTime() + (delay.longValue() * 60l * 1000l);
            phase.setStartDate(new Date(begin));
            if ( duration != null ) {
                long end = begin + (duration.longValue() * 60l  * 1000l);
                phase.setEndDate(new Date(end));
            }
            phase.save();

        }
        lifecycle.save();

        return lifecycle;
    }

    /**
     * Creates and returns a cycle using this lifecycle definition; then,
     * populates the lifecycle with phases and saves it.
     *
     * @param listenerClassName the classname of the listener for the
     *   new lifecycle
     */
    public Lifecycle createFullLifecycle(String listenerClassName) {
        return createFullLifecycle(null, listenerClassName);
    }
}
