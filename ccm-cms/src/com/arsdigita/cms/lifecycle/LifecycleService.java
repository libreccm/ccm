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

import com.arsdigita.cms.RickshawPublishAPIUpgrade;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * This class provides method in associating ACSObject to Lifecycle and
 * methods to access the association.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #13 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: LifecycleService.java 287 2005-02-22 00:29:02Z sskracic $ 
 */

public class LifecycleService extends DomainObject{

    private static final Logger s_log = Logger.getLogger(LifecycleService.class);
    
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.LifecycleService";

    public static final String SERVICE_ID = "serviceID";
    public static final String OBJECT_ID = "objectId";
    public static final String LIFECYCLE_ID = "lifecycle.id";
    public static final String LIFECYCLE = "lifecycle";
    public static final String ITEM = "item";
    public static final String ITEM_ID = "item.id";


    private Lifecycle m_lifecycleToDelete = null;

    protected LifecycleService() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    protected LifecycleService(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * @param id The ID of the ACSObject
     */
    protected LifecycleService(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected LifecycleService(DataObject obj) {
        super(obj);
    }

    protected void initialize() {
        super.initialize();

        if (isNew()) {
            try {
                set(SERVICE_ID, Sequences.getNextValue());
            } catch(SQLException se) {
                throw new UncheckedWrapperException("Could not get sequence value",
                                                    se);
            }
        }
    }

    protected BigDecimal getACSObjectID() {
        DataObject dobj = (DataObject) get(ITEM);
        if (dobj != null) {
            return (BigDecimal) dobj.get(ACSObject.ID);
        } else {
            return null;
        }
    }

    /**
     * Get the ACSObject associated with this LifecycleService.
     **/
    protected ACSObject getACSObject() {
        DataObject dobj = (DataObject) get(ITEM);
        if (dobj != null) {
            return (ACSObject) DomainObjectFactory.
                newInstance(dobj);
        } else {
            return null;
        }
    }

    /**
     * Set the ACSObject associated with this LifecycleService.
     **/
    protected void setACSObject(ACSObject object) {
        setAssociation(ITEM, object);
    }

    protected BigDecimal getLifecycleID() {
        DataObject dobj = (DataObject) get(LIFECYCLE);
        if (dobj != null) {
            return (BigDecimal) dobj.get(ACSObject.ID);
        } else {
            return null;
        }
    }

    protected Lifecycle getLifecycle() {
        DataObject dobj = (DataObject) get(LIFECYCLE);
        if (dobj != null) {
            // don't bother using the DomainObjectFactory since we know
            // it's an instance of Lifecycle
            return new Lifecycle(dobj);
        } else {
            return null;
        }
    }    

    protected void setLifecycle(Lifecycle lifecycle) {
        setAssociation(LIFECYCLE, lifecycle);
    }

    /**
     * Set the lifecycle for a versioned object. Apply a tag to the object
     * to mark the current version.
     *
     * @param objthe object
     * @param cycle the lifecycle to be applied
     * @return true is lifecycle service is updated, false if added
     */
    public static boolean setLifecycle(ACSObject obj, Lifecycle cycle) {

        LifecycleService service = getLifecycleService(obj);
        boolean value = true;

        if (service == null) {
            //the object currently does not have a lifecycle
            service = new LifecycleService();
            service.setACSObject(obj);
            value = false;
        }

        service.setLifecycle(cycle);
        service.save();

        return value;
    }

    /**
     * Get the lifecycle for an ACSObject.  Returns null if this object does not
     * have an associated lifecycle.
     */
    public static Lifecycle getLifecycle(ACSObject object) {
        LifecycleService service = getLifecycleService(object);
        if (service != null) {
            return service.getLifecycle();
        } else {
            return null;
        }
    }

    protected static LifecycleService getLifecycleService(ACSObject object) {
        DataCollection coll = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        coll.addEqualsFilter(ITEM_ID, object.getID());

        LifecycleService service = null;
        if (coll.next()) {
            service = new LifecycleService(coll.getDataObject());

            if( coll.next() ) {
                s_log.warn( "Multiple lifecycles for " +
                            object.getOID().toString() );
            }
        }

        coll.close();
        
        return service;
    }
    
    /**
     * Remove the lifecycle for an ACSObject.
     * @return true is lifecycle service is removed,
     *    false if no lifecycle was associated
     */
    public static boolean removeLifecycle(ACSObject object) {
        // we delete the service rather than the lifecycle
        // because the lifecycle could be associated with other objects
        LifecycleService service = getLifecycleService(object);
        if (service != null) {
            service.delete();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Removed LifecycleService for object: " + object);
            }
            return true;
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No LifecycleService to remove for object: " + object);
            }
            return false;
        }
    }

    protected void beforeDelete() {
	super.beforeDelete();
	Lifecycle lifecycle = getLifecycle();
	if (lifecycle != null) {
	    
	    DataCollection coll = SessionManager.getSession().retrieve
                (BASE_DATA_OBJECT_TYPE);
	    coll.addEqualsFilter(LIFECYCLE+"."+ ACSObject.ID, lifecycle.getID());
	    coll.addNotEqualsFilter(SERVICE_ID,(BigDecimal)get(SERVICE_ID));
	    boolean foundReference = false;
	    if (coll.next()) {
		foundReference = true;
	    }
	    coll.close();
	    // check temporary lifecycle references for
	    // unpublish/republish upgrade.
	    coll = SessionManager.getSession().
		retrieve(RickshawPublishAPIUpgrade.UPGRADE_ITEM_LIFECYCLE_MAP_TYPE);
	    coll.addEqualsFilter(RickshawPublishAPIUpgrade.UPGRADE_LIFECYCLE+"."+ ACSObject.ID, 
				 lifecycle.getID());
	    if (coll.next()) {
		foundReference = true;
	    }
	    coll.close();

	    if (!foundReference) {
		m_lifecycleToDelete = lifecycle;
	    }

	}
	
    }
    /**
     * Remove the lifecycle if it doesn't have any remaining
     * <code>LifecycleService</code> components
     */
    protected void afterDelete() {
	if (m_lifecycleToDelete != null) {
	    m_lifecycleToDelete.delete();
	}
        super.afterDelete();
    }

}
