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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This class represents a phase in Publication Lifecycle for a Content Item.
 * A phase can only be associated with one lifecycle.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: 1.1 $ $DateTime: 2004/08/17 23:15:09 $
 */

public class Phase extends ACSObject {

    public static final String versionId = "$Id: Phase.java 1583 2007-05-25 15:32:13Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Phase";

    // Attributes
    public static final String LABEL = "label";
    public static final String START_DATE_TIME = "startDateTime";
    public static final String END_DATE_TIME = "endDateTime";
    public static final String LISTENER = "listener";
    public static final String HAS_BEGUN = "hasBegun";
    public static final String HAS_ENDED = "hasEnded";

    // Associations
    public static final String LIFECYCLE = "lifecycle";
    public static final String DEFINITION = "definition";

    private static final String PHASE_ID = "phaseId";
    private static final String OBJECT_ID = "objectId";
    private static final String OBJECT_TYPE = "objectType";

    private static final Logger s_log = Logger.getLogger(Phase.class);

    // temporarily stored during delete cycle
    private PhaseDefinition m_definition = null;
    private boolean m_updateCycleTimes = true;
    
    /**
     * If this constructor is used, the phase definition needs to be
     * set with the <code>setPhaseDefinition</code> method.
     */
    protected Phase() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * A new Phase has neither begun nor ended.
     **/
    protected void initialize() {
        super.initialize();

        if (isNew()) {
            set(HAS_BEGUN, Boolean.FALSE);
            set(HAS_ENDED, Boolean.FALSE);

            // Set a default start date since this is a required field.
            setStartDate(new Date());
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
    public Phase(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>Phase.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Phase(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Phase(DataObject obj) {
        super(obj);
    }

    protected Phase(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this phase. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Fetches the label of the phase, which is the same as the
     * PhaseDefinition label
     *
     * @return The label of this phase
     */
    public String getLabel()  {
        PhaseDefinition definition = getPhaseDefinition();
        if ( definition == null ) {
            return "No Label";
        } else {
            return definition.getLabel();
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
     * Set the time when the phase should become active.
     *
     * @param time The start time (milliseconds since Jan 1, 1970)
     * @deprecated use setStartDate(new Date(time))
     */
    public void setStartTime(Long time) {
        if (time == null) {
            setStartDate(null);
        } else {
            setStartDate(new Date(time.longValue()));
        }
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
     * Set the time when the phase should end.
     *
     * @deprecated use setEndDate(new Date(time))
     * @param time The end time (milliseconds since Jan 1, 1970), null
     *             if this phase never ends.
     */
    public void setEndTime(Long time) {
        if (time == null) {
            setEndDate(null);
        } else {
            setEndDate(new Date(time.longValue()));
        }
    }

    /**
     *  Set the end date
     *  @param date The end date
     */
    public void setEndDate(Date date) {
        set(END_DATE_TIME, date);
    }

    /**
     * Get the lifecycle listener associated with this phase.
     *
     * @return The class name of the lifecycle listener
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
                s_log.error("Listener instantiation error", e);
                throw new PublishingException(
                                              "Error instantiating lifecycle listener: " + listenerClassName, e);
            }
        }

        return listener;
    }


    /**
     * Associate a listener to be run when this phase becomes active.
     *
     * @param listener The class name of the lifecycle listener
     */
    public void setListenerClassName(String listener) {
        set(LISTENER, listener);
    }

    /**
     * @return true if the phase has begun. False otherwise.
     */
    public boolean hasBegun() {
        return ((Boolean) get(HAS_BEGUN)).booleanValue();
    }

    protected void setHasBegun(boolean hasBegun) {
        set(HAS_BEGUN, new Boolean(hasBegun));
    }


    /**
     * @return true if the phase has ended. False otherwise.
     */
    public boolean hasEnded() {
        return ((Boolean) get(HAS_ENDED)).booleanValue();
    }


    /**
     * Fetches the lifecycle to which this phase belongs.
     *
     * @return The associated lifecycle
     */
    public Lifecycle getLifecycle() {
        DataObject lifecycle = (DataObject) get(LIFECYCLE);
        if ( lifecycle == null ) {
            return null;
        } else {
            return new Lifecycle(lifecycle);
        }
    }

    /**
     * Update the associated Lifecycle.
     * Every Phase needs to be associated with a Lifecycle.  If this phase
     * does not belong to any lifecycle, then this phase should be removed by
     * calling the delete() method.
     *
     * @param lifecycle The associated lifecycle
     */
    protected void setLifecycle(Lifecycle lifecycle) {
        setAssociation(LIFECYCLE, lifecycle);
    }

    /**
     * Fetches the definition of this phase.
     *
     * @return The phase definition
     */
    public PhaseDefinition getPhaseDefinition() {
        DataObject definition = (DataObject) get(DEFINITION);
        if ( definition == null ) {
            return null;
        } else {
            return new PhaseDefinition(definition);
        }
    }

    /**
     * Update the definition of this phase
     * Every Phase needs to be associated with a definition.  If this phase
     * is not assocaited to any definition, then this phase should be removed by
     * calling the delete() method.
     *
     * @param definition The phase definition
     */
    protected void setPhaseDefinition(PhaseDefinition definition) {
        setAssociation(DEFINITION, definition);
    }


    public void delete (boolean updateCycleTimes) {
    	m_updateCycleTimes = updateCycleTimes;
    	delete();
    	
    }
    
    public void beforeDelete () {
    	s_log.debug("About to delete phase " + getID());
        super.beforeDelete();
    	Lifecycle cycle = getLifecycle();
        if ( cycle != null ) {
            PhaseDefinition definition = getPhaseDefinition();
            PhaseDefinitionCollection cyclePhaseDefinitions = cycle.getLifecycleDefinition().getPhaseDefinitions();
            cyclePhaseDefinitions.addEqualsFilter(PhaseDefinition.ID, definition.getID());
            if (cyclePhaseDefinitions.isEmpty()) {
            	s_log.debug("Phase Definition " + definition.getID() + ": " + definition.getLabel() + " is a custom phase definition. I will delete it after deleting phase " + getID());
            	// this is a custom definition - delete it once phase has been deleted
            	m_definition = definition;
            }
            
            
            
        }
    }
    /**
     * Delete this phase, updating the start and end times of the
     * associated lifecycle.
     */
    public void delete() {
        Lifecycle cycle = getLifecycle();
        super.delete();
        if ( cycle != null && m_updateCycleTimes) {
            cycle.updateStartEndTimes();
        }
    }

    public void afterDelete() {
    	if (m_definition != null) {
    		m_definition.delete();
    	}
    }
    
    protected void afterSave() {
        super.afterSave();
        Lifecycle cycle = getLifecycle();
        if ( cycle != null ) {
            cycle.updateStartEndTimes();
        }
    }

    /**
     * Starts this phase if the start date is past the current time
     * or is undefined (in which case it is set to the current time).
     * Fires the listener associated with this phase's definition.
     **/
    public void start() {
        if ( !hasBegun() ) {

            Date now = new Date();
            Date startDate = getStartDate();

            if ( startDate == null ) {
                startDate = now;
            }

            // If the phase should be active, mark as 'started' and
            // fire any lifecycle listeners.
            if ( startDate.before(now) ||
                 startDate.equals(now) ) {
                fireListeners();
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
                ssn.retrieveQuery("com.arsdigita.cms.getObjectPhaseMappings");
            Filter f = dq.addEqualsFilter(PHASE_ID, getID());

            // A phase may be associated with more than one object.
            // For each object, fire a lifecycle PHASE event.
            while ( dq.next() ) {

                BigDecimal objectId = (BigDecimal) dq.get(OBJECT_ID);
                String objectType = (String) dq.get(OBJECT_TYPE);

                LifecycleEvent event =
                    new LifecycleEvent(LifecycleEvent.PHASE, getStartDate(),
                                       getEndDate(), objectType, objectId);

                // Fire the listener and mark the phase as 'started'.
                try {
                    listener.begin(event);
                    setHasBegun(true);
                    save();
                } catch (Exception e) {

                    s_log.error("Error firing listeners", e);
                    dq.close();
                    throw new PublishingException(e.getMessage());
                }
            }
        }
    }


    /**
     * Stop the phase.
     */
    protected void stop() {
        set(HAS_ENDED, Boolean.TRUE);
    }

}
