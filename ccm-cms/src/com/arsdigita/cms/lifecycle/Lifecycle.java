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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;


/**
 * This class represents a Publication Lifecycle for a Content Item.
 * Associated with each Lifecycle is a set of phases.  A phase can only be
 * associated with one Lifecycle.
 *
 * @author Jack Chung
 * @author Michael Pih
 * @version $Revision: 1.1 $ $DateTime: 2004/08/17 23:15:09 $
 */

public class Lifecycle extends ACSObject {

    public static final String versionId = "$Id: Lifecycle.java 1585 2007-05-29 13:12:44Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/17 23:15:09 $";

    public final static String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Lifecycle";

    // Attributes
    private final String START_DATE_TIME = "startDateTime";
    private final String END_DATE_TIME = "endDateTime";
    private final String LISTENER = "listener";
    private final String HAS_BEGUN = "hasBegun";
    private final String HAS_ENDED = "hasEnded";

    // Associations
    private final String DEFINITION = "definition";
    private final String PHASES = "phases";

    private static final String CYCLE_ID = "cycleId";
    private static final String OBJECT_ID = "objectId";
    private static final String OBJECT_TYPE = "objectType";

    private static final Logger s_log = Logger.getLogger(Lifecycle.class);

    /**
     * If this constructor is used, the lifecycle definition needs to be
     * set with the <code>setLifecycleDefinition</code> method.
     */
    protected Lifecycle() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * A new Lifecycle has neither begun nor ended.
     **/
    protected void initialize() {
        super.initialize();

        if (isNew()) {
            set(HAS_BEGUN, Boolean.FALSE);
            set(HAS_ENDED, Boolean.FALSE);
        }
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Lifecycle(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Lifecycle.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Lifecycle(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Lifecycle(DataObject obj) {
        super(obj);
    }

    protected Lifecycle(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this lifecycle. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    
    public void beforeDelete() {
    	s_log.debug("About to delete lifecycle " + getID() + ". First delete phases & LifecycleService");
    	super.beforeDelete();
    	DataCollection lifecycleServices = SessionManager.getSession().retrieve(LifecycleService.BASE_DATA_OBJECT_TYPE);
    	lifecycleServices.addEqualsFilter(LifecycleService.LIFECYCLE_ID, getID());
    	while (lifecycleServices.next()) {
    		DataObject service = lifecycleServices.getDataObject();
    		s_log.debug("Deleting lifecycle service " + service.getOID());
    		// we are deleting the lifecycle so delete all connections from cycle to acs objects if more than one 
    		service.delete();
    	}
    	PhaseCollection phases = getPhases();
    	while (phases.next()) {
    		phases.getPhase().delete(false);
    	}
    }
    /**
     * Fetches the label of the lifecycle, which is the same as the
     * label of the associated lifecycle definition.
     *
     * @return The label of this lifecycle
     */
    public String getLabel() {
        LifecycleDefinition def = getLifecycleDefinition();
        if ( def == null ) {
            return null;
        } else {
            return def.getLabel();
        }
    }

    /**
     * Get the start date.
     *
     * @return The start date
     */
    public Date getStartDate() {
        return (Date) get(START_DATE_TIME);
    }

    /**
     *  Set the start date
     *  @param date The start date
     */
    public void setStartDate(Date date) {
        set(START_DATE_TIME, date);
    }

    /**
     * Get the end date
     *
     * @return The end date. If this is null then the lifecycle has no end
     */
    public Date getEndDate() {
        return (Date) get(END_DATE_TIME);
    }

    /**
     *  Set the end date
     *  @param date The end date
     */
    public void setEndDate(Date date) {
        set(END_DATE_TIME, date);
    }

    /**
     * Fetches the class name of the listener associated with this lifecycle.
     *
     * @return The class name of the listener
     */
    public String getListenerClassName() {
        return (String) get(LISTENER);
    }

    /**
     * Get the lifecycle listener associated with this phase.
     *
     * @return The lifecycle listener
     */
    public LifecycleListener getListener() {

        String listenerClassName = getListenerClassName();
        LifecycleListener listener = null;

        if ( listenerClassName != null ) {
            try {
                Class listenerClass = Class.forName(listenerClassName);
                listener = (LifecycleListener) listenerClass.newInstance();
            } catch (Exception e) {
                s_log.error("Error instantiating", e);
                throw new PublishingException
                    ("Error instantiating lifecycle listener: " +
                     listenerClassName, e);
            }
        }

        return listener;
    }

    /**
     * Associate a listener with this lifecycle.
     *
     * @param listener The class name of the lifecycle listener
     */
    public void setListenerClassName(String listener) {
        set(LISTENER, listener);
    }


    /**
     * @return true if the lifecycle has begun. False otherwise.
     */
    public boolean hasBegun() {
        return ((Boolean) get(HAS_BEGUN)).booleanValue();
    }

    protected void setHasBegun(boolean hasBegun) {
        set(HAS_BEGUN, new Boolean(hasBegun));
    }

    /**
     * @return true if the lifecycle has ended. False otherwise.
     */
    public boolean hasEnded() {
        return ((Boolean) get(HAS_ENDED)).booleanValue();
    }

    /**
     * Fetches the definition of publication lifecycle.
     *
     * @return The lifecycle definition
     */
    public LifecycleDefinition getLifecycleDefinition() {
        DataObject definition = (DataObject) get(DEFINITION);
        if (definition == null) {
            return null;
        } else {
            return new LifecycleDefinition(definition);
        }
    }

    /**
     * Update the associated definition of publication lifecycle.
     *
     * @param definition The lifecycle definition
     */
    protected void setLifecycleDefinition(LifecycleDefinition definition) {
        setAssociation(DEFINITION, definition);
    }


    /**
     * Fetches all phases within this lifecycle. To remove the phase
     * from this cycle, you need to explicitly call the <code>delete()</code>
     * method on that particular phase.
     *
     * @return A collection of phases
     */
    public PhaseCollection getPhases() {
        DataAssociationCursor phaseCursor = ((DataAssociation) get(PHASES)).cursor();
        return new PhaseCollection(phaseCursor);
    }


    /**
     * Fetches the phases in this lifecycle that should be currently active.
     *
     * @return A collection of active phases
     */
    public PhaseCollection getActivePhases() {
        return getActivePhases(null);
    }

    /**
     * Fetches the phases in this lifecycle that should be active at the
     * specified time.
     *
     * @param date The specified time.
     * @return A collection of active phases
     */
    public PhaseCollection getActivePhases(Date date) {

        // If no time is specified, use the current time.
        if ( date == null ) {
            date = new Date();
        }

        // Filter phases that should be active.
        DataAssociationCursor dac = ((DataAssociation) get(PHASES)).cursor();
        FilterFactory factory = dac.getFilterFactory();
        Filter filter = factory.and()
            .addFilter(factory.simple("startDateTime <= :startDateTime"))
            .addFilter(factory.or()
                       .addFilter(factory.equals("endDateTime", null))
                       .addFilter("endDateTime >= :endDateTimeValue"));
        filter.set("startDateTime", date);
        filter.set("endDateTimeValue", date);
        dac.addFilter(filter);
        return new PhaseCollection(dac);
    }


    /**
     * Adds a phase which uses the default delay and duration from the
     * phase definition.
     *
     * @param pd The phase definition
     * @return The phase
     */
    public Phase addPhase(PhaseDefinition pd) {
        return addPhase(pd, null);
    }

    /**
     * Adds a phase which uses the supplied start date and calculate the
     * end date using the phase definition default duration.
     *
     * @param pd The phase definition
     * @param startDate Date at which the phase becomes active
     * @return The phase
     */
    public Phase addPhase(PhaseDefinition pd, Date startDate) {
        return addPhase(pd, startDate, null);
    }

    /**
     * Adds a phase which uses the supplied start date and end date.  If end
     * date is null, then this phase will never end.
     *
     * @param pd The phase definition
     * @param startDate Date at which the phase becomes active
     * @param endDate Date at which the phase is no longer active
     * @return The phase
     */
    public Phase addPhase(PhaseDefinition pd, Date startDate, Date endDate) {
        // Check that the phase-definition's cycle-definition belongs
        // to the cycle's cycle-definition.
        BigDecimal cycleDefId = pd.getLifecycleDefinition().getID();
        if ( !cycleDefId.equals(this.getLifecycleDefinition().getID()) ) {
            throw new PublishingException
                ("The phase definition does not correspond to the " +
                 "lifecycle definition.");
        }

        Phase phase = new Phase();
        phase.setPhaseDefinition(pd);
        phase.setLifecycle(this);
        phase.setListenerClassName(pd.getDefaultListener());

        if ( startDate == null ) {
            Integer delay = pd.getDefaultDelay();
            long start = System.currentTimeMillis() + (delay.longValue() * 60l * 1000l);
            startDate = new Date(start);
        }

        if (endDate == null) {
            Integer duration = pd.getDefaultDuration();
            if (duration != null){
                long end = startDate.getTime() + (duration.longValue() * 60l * 1000l);
                endDate = new Date(end);
            }
        }

        phase.setStartDate(startDate);
        phase.setEndDate(endDate);

        return phase;
    }


    /**
     * Adds a custom phase which is not part of the lifecycle definition.
     *
     * @param label the name of this phase.
     * @param startDate start time of this phase
     * @param endDate end time of this phase If end date is null,
     *    then this phase will never end.
     */
    public Phase addCustomPhase(String label, Date startDate, Date endDate) {

        PhaseDefinition pd = new PhaseDefinition();
        pd.setLabel(label);
        pd.save();

        Phase phase = new Phase();
        phase.setPhaseDefinition(pd);
        phase.setLifecycle(this);

        phase.setStartDate(startDate);
        phase.setEndDate(endDate);

        return phase;
    }


    /**
     * Adds a custom phase which is not part of the lifecycle definition.
     *
     * @param label the name of this phase.
     * @param start start time of this phase
     * @param end end time of this phase If end date is null,
     *    then this phase will never end.
     */
    public Phase addCustomPhase(String label, Long start, Long end) {

        PhaseDefinition pd = new PhaseDefinition();
        pd.setLabel(label);
        pd.save();

        Phase phase = new Phase();
        phase.setPhaseDefinition(pd);
        phase.setLifecycle(this);

        phase.setStartTime(start);
        phase.setEndTime(end);

        return phase;
    }

    /**
     * Starts this lifecycle if the start date is past the current time
     * or is undefined (in which case it is set to the current time).
     * Fires the listener associated with this lifecycle's definition,
     * as well as the listener associated with any phases that begin
     * immediately.
     **/
    public void start() {

        if ( !hasBegun() ) {

            Date now = new Date();
            Date startDate = getStartDate();

            if ( startDate == null ) {
                startDate = now;
            }

            // If the lifecycle should be active, mark as 'started' and
            // fire any lifecycle listeners.
            if ( startDate.before(now) ||
                 startDate.equals(now) ) {
                fireListeners();
            }

            // Start the phases.
            PhaseCollection phases = getActivePhases();
            while ( phases.next() ) {
                phases.getPhase().start();
            }

        }
    }


    /**
     * Fetch and fire the lifecycle listener with a new lifecycle event.
     */
    private void fireListeners() {

        LifecycleListener listener = getListener();
        if ( listener != null ) {

            // MP: instead of a DataQuery, this should be an association
            //     once the DomainObjectFactory is implemented.
            //
            // Get objects with this lifecycle.
            Session ssn = SessionManager.getSession();
            DataQuery dq =
                ssn.retrieveQuery("com.arsdigita.cms.getObjectLifecycleMappings");
            Filter f = dq.addEqualsFilter(CYCLE_ID, getID());

            // A phase may be associated with more than one object.
            // For each object, fire a lifecycle PHASE event.
            while ( dq.next() ) {

                BigDecimal objectId = (BigDecimal) dq.get(OBJECT_ID);
                String objectType = (String) dq.get(OBJECT_TYPE);

                LifecycleEvent event =
                    new LifecycleEvent(
                                       LifecycleEvent.LIFECYCLE, getStartDate(),
                                       getEndDate(), objectType, objectId);

                // Fire the listener and mark the phase as 'started'.
                try {
                    listener.begin(event);
                    setHasBegun(true);
                    save();
                } catch (Exception e) {
                    s_log.error("Error in firing listeners", e);
                    dq.close();
                    throw new PublishingException(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Stop the lifecycle.
     */
    protected void stop() {
        set(HAS_ENDED, Boolean.TRUE);
    }


    /**
     * This method is called when the phase is saved.
     *
     * Look through all the the phases and update the start and end times.
     * If no phases are left, startDate and endDate will be null.
     */
    void updateStartEndTimes() {
        // TODO: We may want to register a pdl query that checks
        //       the min of the phase.startDate and
        //       the max of the phase.endDate

        Date origStart = getStartDate();
        Date origEnd = getEndDate();

        // Get the associated phases and rewind to first row.
        PhaseCollection phases = getPhases();

        Date minStartDate = null;
        Date maxEndDate = null;
        boolean hasPhases = false;

        int phaseCount = 0;
        while ( phases.next() ) {
            Phase phase = phases.getPhase();
            Date startDate = phase.getStartDate();
            Date endDate = phase.getEndDate();

            // First phase. Initialize min start time and max end time.
            if ( !hasPhases ) {
                minStartDate = startDate;
                maxEndDate = endDate;
                hasPhases = true;
                continue;
            }

            // Update min start time.
            if (startDate.before(minStartDate)) {
                minStartDate = startDate;
            }

            if (endDate == null) {
                // If endDate is null, then this phase will never
                // end, and so the lifecycle will never end.
                maxEndDate = null;
            } else {
                if (maxEndDate != null) {
                    if (endDate.after(maxEndDate)) {
                        maxEndDate = endDate;
                    }
                } else {
                    // If maxEndDate is null, then at least one of the phases
                    // had a null endDate, so we leave maxEndDate as null.
                }
            }
        }

        // only save the lifecycle if its start or end time has
        // changed.
        if ( ( origStart == null || !origStart.equals(minStartDate) ) ||
             ( (origEnd == null && maxEndDate != null) || 
               (origEnd != null && maxEndDate == null) || 
               (origEnd != null && !origEnd.equals(maxEndDate)) ) ) {
            setStartDate(minStartDate);
            setEndDate(maxEndDate);
            save();
        }
    }

    /*
     * Called from ContentItem.java
     * Using information from any existing phases a new phase is created
     * and in order to reset any hasbegun flag, the old phase is then deleted  
     */
    
    public void reset() {
		
	PhaseCollection existingPhases = this.getPhases();

	Date finalEndDate = null;
	long timeExpired = new Date().getTime() - this.getStartDate().getTime();

	existingPhases.addOrder(Phase.END_DATE_TIME);
	while (existingPhases.next()) {
	    Phase existingPhase = existingPhases.getPhase();
	    PhaseDefinition phaseDefinition = existingPhase.getPhaseDefinition();
	    LifecycleDefinition lifeCycleDef = phaseDefinition.getLifecycleDefinition();
            // will be now for any phaseds that have already started
	    Date startDate = new Date(existingPhase.getStartDate().getTime() + timeExpired);
	    if (existingPhase.getEndDate() != null) {
	        Date endDate = new Date(existingPhase.getEndDate().getTime() + timeExpired);
		Phase phase;
		if (lifeCycleDef != null) {
		    phase = this.addPhase(phaseDefinition, startDate, endDate);
		} else {
			
		    phase = this.addCustomPhase(existingPhase.getLabel(), startDate, endDate);
		    
		}
	    phase.setListenerClassName(existingPhase.getListenerClassName());
		// keep track of phase ends as latest end must be set as lifecycle end
		finalEndDate = endDate;
	    } else {
		if (lifeCycleDef != null) {
		    this.addPhase(phaseDefinition, startDate);
		} else {
		    this.addCustomPhase(existingPhase.getLabel(), startDate, null);
		}
	    }
	    existingPhase.delete();
	}

	this.setStartDate(new Date());
	this.setEndDate(finalEndDate);
    }

    

}
