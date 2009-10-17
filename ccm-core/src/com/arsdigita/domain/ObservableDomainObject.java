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
package com.arsdigita.domain;

// Support for Persistent Objects.
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataObserver;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * ObservableDomainObject extends DomainObject with an
 * observer pattern on all modification methods, allowing
 * objects to register to receive a callback on those
 * operations.
 *
 * @author Joseph Bank
 * @version $Id: ObservableDomainObject.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public abstract class ObservableDomainObject extends DomainObject {

    public static final String versionId = 
        "$Id: ObservableDomainObject.java 287 2005-02-22 00:29:02Z sskracic $" +
        "by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ObservableDomainObject.class);

    private Collection m_observers;

    protected ObservableDomainObject(DataObject dobj) {
        super(dobj);
        initializeGlobalObservers();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName The name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>.
     *
     * @see DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ObservableDomainObject(String typeName) {
        super(typeName);
        initializeGlobalObservers();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type The <code>ObjectType</code> of the contained
     * <code>DataObject</code>.
     *
     * @see DomainObject#DomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ObservableDomainObject(ObjectType type) {
        super(type);
        initializeGlobalObservers();
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @see DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public ObservableDomainObject(OID oid) throws DataObjectNotFoundException {
        super(oid);
        initializeGlobalObservers();
    }


    /**
     * Sets up any global observers.
     **/

    private void initializeGlobalObservers() {
        GlobalObserverManager gom = GlobalObserverManager.getManager();
        for (Iterator it = gom.getObservers(); it.hasNext(); ) {
            GlobalObserver go = (GlobalObserver) it.next();
            if (go.shouldObserve(this)) {
                addObserver(go);
            }
        }
    }


    /**
     * Adds a new observer.
     *
     * @param doo the new observer
     */
    public void addObserver(DomainObjectObserver doo) {
        if (m_observers == null) {
            m_observers = new ArrayList();
        }
        m_observers.add(doo);
        getDataObject().addObserver(new ProxyObserver(doo));
    }

    /**
     * Return an iterator of all the current observers for this Domain Object
     */
    public Iterator getObservers() {
        if (m_observers == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return m_observers.iterator();
    }

    private class ProxyObserver extends DataObserver {
        // the last observer method to run
        private boolean m_before = false;
        private boolean m_wasNew = false;

        private DomainObjectObserver m_obs;

        ProxyObserver(DomainObjectObserver obs) { m_obs = obs; }

        public void set(DataObject object, String property, Object previous,
                        Object value) {
            m_obs.set(ObservableDomainObject.this, property, previous, value);
        }

        public void add(DataObject object, String property, DataObject value) {
            m_obs.add(ObservableDomainObject.this, property, value);
        }

        public void remove(DataObject object, String property,
                           DataObject value) {
            m_obs.remove(ObservableDomainObject.this, property, value);
        }

        public void clear(DataObject object, String property) {
            m_obs.clear(ObservableDomainObject.this, property);
        }

        public void beforeSave(DataObject object) {
            if (m_before) {
                if (m_wasNew && !isNew()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("suppressing duplicate before save: "
                                    + getOID());
                    }
                    return;
                }
            }
            m_before = true;
            m_wasNew = isNew();

            m_obs.beforeSave(ObservableDomainObject.this);
        }

        public void afterSave(DataObject object) {
            if (!m_before) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("suppressing duplicate after save: "
                                + getOID());
                }
                return;
            }
            m_before = false;

            m_obs.afterSave(ObservableDomainObject.this);
        }

        public void beforeDelete(DataObject object) {
            if (m_before) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("suppressing duplicate before delete: "
                                + getOID());
                }
                return;
            }
            m_before = true;

            m_obs.beforeDelete(ObservableDomainObject.this);
        }

        public void afterDelete(DataObject object) {
            if (!m_before) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("suppressing duplicate after delete: "
                                + getOID());
                }
                return;
            }
            m_before = false;

            m_obs.afterDelete(ObservableDomainObject.this);
        }

        public int hashCode() { return m_obs.hashCode(); }

        public boolean equals(Object other) {
            if (other instanceof ProxyObserver) {
                return m_obs.equals(((ProxyObserver) other).m_obs);
            }

            return false;
        }

        public String toString() {
            return "proxy for " + m_obs;
        }
    }
}
